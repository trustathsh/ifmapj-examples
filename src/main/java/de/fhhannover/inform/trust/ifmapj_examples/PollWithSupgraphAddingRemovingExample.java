package de.fhhannover.inform.trust.ifmapj_examples;

/*
 * #%L
 * ====================================================
 *   _____                _     ____  _____ _   _ _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \|  ___| | | | | | |
 *    | | | '__| | | / __| __|/ / _` | |_  | |_| | |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _| |  _  |  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_|   |_| |_|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Fachhochschule Hannover 
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.inform.fh-hannover.de/
 * 
 * This file is part of IfmapJ-examples, version 0.1.5, implemented by the Trust@FHH 
 * research group at the Fachhochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2013 Trust@FHH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.w3c.dom.Document;

import de.fhhannover.inform.trust.ifmapj.IfmapJ;
import de.fhhannover.inform.trust.ifmapj.IfmapJHelper;
import de.fhhannover.inform.trust.ifmapj.binding.IfmapStrings;
import de.fhhannover.inform.trust.ifmapj.channel.ARC;
import de.fhhannover.inform.trust.ifmapj.channel.SSRC;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapErrorResult;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapException;
import de.fhhannover.inform.trust.ifmapj.exception.InitializationException;
import de.fhhannover.inform.trust.ifmapj.identifier.Identifier;
import de.fhhannover.inform.trust.ifmapj.identifier.Identifiers;
import de.fhhannover.inform.trust.ifmapj.identifier.IpAddress;
import de.fhhannover.inform.trust.ifmapj.identifier.MacAddress;
import de.fhhannover.inform.trust.ifmapj.messages.PollResult;
import de.fhhannover.inform.trust.ifmapj.messages.PublishDelete;
import de.fhhannover.inform.trust.ifmapj.messages.Requests;
import de.fhhannover.inform.trust.ifmapj.messages.ResultItem;
import de.fhhannover.inform.trust.ifmapj.messages.SearchResult;
import de.fhhannover.inform.trust.ifmapj.messages.SearchResult.Type;
import de.fhhannover.inform.trust.ifmapj.metadata.LocationInformation;
import de.fhhannover.inform.trust.ifmapj.metadata.StandardIfmapMetadataFactory;


/**
 * Test the behavior of a MAPS when there is a subscription that matches
 * a link which adds/removes a new subgraph to the poll result.
 * 
 */
public class PollWithSupgraphAddingRemovingExample {
	
	private StandardIfmapMetadataFactory mf = IfmapJ.createStandardMetadataFactory();
	private SSRC ssrc;
	private ARC arc;
	private Semaphore mainSem, pollSem;
	
	private IpAddress ip;
	private MacAddress mac;
	Document ipMac;
	Document location;
	
	public PollWithSupgraphAddingRemovingExample() {
		// prepare identifiers
		ip = Identifiers.createIp4("192.168.0.1");
		mac = Identifiers.createMac("aa:bb:cc:dd:ee:ff");
		// prepare metadata
		ipMac = mf.createIpMac("2011-01-26T19:32:52Z", "2011-01-27T19:32:52Z", "trust-dhcp-server");
		List<LocationInformation> l = new ArrayList<LocationInformation>();
		LocationInformation li1 = new LocationInformation("GPS", "N 52.517526 E 13.44");
		LocationInformation li2 = new LocationInformation("ROOM", "326");
		l.add(li1); l.add(li2);
		location = mf.createLocation(l, "2011-01-27T19:32:52Z", "my location sensor");
	}
	
	private class Polling implements Runnable {

		@Override
		public void run() {
			try {
				PollResult pollResult;
				List<SearchResult> results;
			
				// main has to allow us to poll
				pollSem.acquire();
			
				// the searchResult
				System.out.print("[poll-thread] Polling for searchResult ... ");
				pollResult = arc.poll();
				results = pollResult.getResults();
				if (results.size() < 1 || results.get(0).getType() != Type.searchResult)
					System.out.println("Unexpected first PollResult");
				else
					System.out.println("OK");
				IfmapJExamples.sleepSomeTime();
	
				// let the main thread continue
				mainSem.release();
				
				// wait for the main thread to allow us to continue
				pollSem.acquire();
				
				System.out.print("[poll-thread] Polling for updateResult ... ");
				pollResult = arc.poll();
				
				results = pollResult.getResults();
				if (results.size() < 1 || results.get(0).getType() != Type.updateResult)
					System.out.println("Unexpected second PollResult");
				else
					checkContainsExpectedMetadata(results);
				
				IfmapJExamples.sleepSomeTime();
			
				// let the main-thread delete the metadata
				mainSem.release();
			
				// wait until stuff is deleted
				pollSem.acquire();
				
				System.out.print("[poll-thread] Polling for deleteResult ... ");
				pollResult = arc.poll();
				
				results = pollResult.getResults();
				if (results.size() < 1 || results.get(0).getType() != Type.deleteResult)
					System.out.println("Unexpected thrid PollResult");
				else
					checkContainsExpectedMetadata(results);
				
				IfmapJExamples.sleepSomeTime();
			
				// let the main thread end the session
				mainSem.release();
				
				IfmapJExamples.doLastPoll(arc);
				
			} catch (Exception e) {
				System.err.println("[poll-thread] Exception: " + e.getMessage());
				e.printStackTrace();
			}	
		}

		private void checkContainsExpectedMetadata(Collection<SearchResult> results) {
			boolean ipMacOk = false;
			boolean locationOk = false;
			// Check whether we find ip-mac link and mac identifier,
			// both containing a single metadata element
			for (SearchResult sr : results) {
				for (ResultItem ri : sr.getResultItems()) {
					Identifier idents[] = ri.getIdentifier();
					if (idents[1] == null) {
						if (idents[0] instanceof MacAddress && ri.getMetadata().size() == 1) {
							MacAddress mac = (MacAddress)idents[0];
							Document md = ri.getMetadata().iterator().next();
							locationOk = mac.getValue().equals("aa:bb:cc:dd:ee:ff")
							&& md.getFirstChild().getLocalName().equals("location");
						}
					} else {
						MacAddress mac = null;
						IpAddress ip = null;
						if (idents[0] instanceof IpAddress && idents[1] instanceof MacAddress) {
							ip = (IpAddress) idents[0];
							mac = (MacAddress) idents[1];
						} else if (idents[0] instanceof MacAddress && idents[1] instanceof IpAddress) {
							mac = (MacAddress) idents[0];
							ip = (IpAddress) idents[1];
						}
						
						if (mac != null && ip != null && ri.getMetadata().size() == 1) {
							Document md = ri.getMetadata().iterator().next();
							ipMacOk = md.getFirstChild().getLocalName().equals("ip-mac")
							&& ip.getValue().equals("192.168.0.1")
							&& mac.getValue().equals("aa:bb:cc:dd:ee:ff");
						}
					}
				}
			}
			
			if (ipMacOk && locationOk) {
				System.out.println("OK");
			} else {
				System.out.println("Could NOT find expected Metadata");
			}
		}
	}
	

	public void run (String args[]) throws InitializationException {
		
		System.out.println("====== RUNNING POLL WITH SUBGRAPH OPS EXAMPLE ======");
		
		mainSem = new Semaphore(0);
		pollSem = new Semaphore(0);

		// Use basic authentication
		ssrc = IfmapJ.createSSRC(
				Config.BASIC_AUTH_SERVER_URL,
				Config.BASIC_AUTH_USER,
				Config.BASIC_AUTH_PASSWORD,
				IfmapJHelper.getTrustManagers(
				getClass().getResourceAsStream(Config.TRUST_STORE_PATH),
				Config.TRUST_STORE_PASSWORD));
		
		arc = ssrc.getArc();
		
		try {
	
			System.out.print("[main-thread] newSession ... ");
			ssrc.newSession();
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
		
			// Create a subscription beginning from 192.168.0.1
			System.out.print("[main-thread] subscribe ... ");
			ssrc.subscribe(Requests.createSubscribeReq(Requests.createSubscribeUpdate(
					"subscription", // name
					null,			// match-links
					10,				// maxDepth
					null,			// terminal-identifiers,
					null,			// max-size
					null,			// result-filter
					ip)));			// start identifier
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// start the polling thread
			new Thread(new Polling()).start();
		
			// let it go
			pollSem.release();
	
			// continue if the poll thread has done its first polling
			mainSem.acquire();
			
			System.out.print("[main-thread] publishing ip-mac... ");
			ssrc.publish(Requests.createPublishReq(Requests.createPublishUpdate(
					ip, mac, ipMac)));
			System.out.println("OK");
			
			IfmapJExamples.sleepSomeTime();
			System.out.print("[main-thread] publishing location to mac... ");
			ssrc.publish(Requests.createPublishReq(Requests.createPublishUpdate(
					mac, location)));
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
		
			
			// let the poll-thread do it's polling
			pollSem.release();
		
			// wait until the poll-thread is done
			mainSem.acquire();

			// delete ip-mac
			PublishDelete delete = Requests.createPublishDelete(ip, mac, "meta:ip-mac");
			delete.addNamespaceDeclaration(IfmapStrings.STD_METADATA_PREFIX,
					IfmapStrings.STD_METADATA_NS_URI);
			
			System.out.print("[main-thread] deleting ... ");
			// delete the metadata
			ssrc.publish(Requests.createPublishReq(delete));
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
		
			// let the poll-thread get the deleteResult
			pollSem.release();
		
			// wait until it's done
			mainSem.acquire();

			IfmapJExamples.doLastEndSession("[main-thread]", ssrc);
			IfmapJExamples.sleepSomeTime();
		} catch (IfmapException e ) {
			System.err.println(e.getMessage());
		} catch (IfmapErrorResult e) {
			System.err.println(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
