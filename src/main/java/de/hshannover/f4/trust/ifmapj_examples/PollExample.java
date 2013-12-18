/*
 * #%L
 * =====================================================
 *   _____                _     ____  _   _       _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \| | | | ___ | | | |
 *    | | | '__| | | / __| __|/ / _` | |_| |/ __|| |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _  |\__ \|  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_| |_||___/|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Hochschule Hannover
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.f4.hs-hannover.de/
 * 
 * This file is part of ifmapj-examples, version 1.0.0,
 * implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2013 Trust@HsH
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

package de.hshannover.f4.trust.ifmapj_examples;



import java.util.concurrent.Semaphore;

import de.hshannover.f4.trust.ifmapj.IfmapJ;
import de.hshannover.f4.trust.ifmapj.IfmapJHelper;
import de.hshannover.f4.trust.ifmapj.binding.IfmapStrings;
import de.hshannover.f4.trust.ifmapj.channel.ARC;
import de.hshannover.f4.trust.ifmapj.channel.SSRC;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.exception.InitializationException;
import de.hshannover.f4.trust.ifmapj.identifier.Identifiers;
import de.hshannover.f4.trust.ifmapj.identifier.IpAddress;
import de.hshannover.f4.trust.ifmapj.identifier.MacAddress;
import de.hshannover.f4.trust.ifmapj.messages.PollResult;
import de.hshannover.f4.trust.ifmapj.messages.PublishDelete;
import de.hshannover.f4.trust.ifmapj.messages.Requests;
import de.hshannover.f4.trust.ifmapj.messages.SearchResult.Type;
import de.hshannover.f4.trust.ifmapj.metadata.StandardIfmapMetadataFactory;


/**
 * Having some fun with SSRC and ARC and threads.
 * 
 */
public class PollExample {
	
	private StandardIfmapMetadataFactory mf = IfmapJ.createStandardMetadataFactory();
	private IpAddress ip = Identifiers.createIp4("192.168.0.1");
	private MacAddress mac = Identifiers.createMac("aa:bb:cc:dd:ee:ff");
	private SSRC ssrc;
	private ARC arc;
	private Semaphore mainSem, pollSem;
	
	private class Polling implements Runnable {

		@Override
		public void run() {
			try {
				PollResult pollResult;
			
				// main has to allow us to poll
				pollSem.acquire();
			
				// the searchResult
				System.out.print("[poll-thread] Polling for searchResult ... ");
				pollResult = arc.poll();
				arc.closeTcpConnection();
				if ((pollResult.getResults().size() < 1) ||
						pollResult.getResults().get(0).getType() != Type.searchResult)
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
				
				if ((pollResult.getResults().size() < 1) ||
						pollResult.getResults().get(0).getType() != Type.updateResult)
					System.out.println("Unexpected second PollResult");
				else
					System.out.println("OK");
				IfmapJExamples.sleepSomeTime();
			
				// let the main-thread delete the metadata
				mainSem.release();
			
				// wait until stuff is deleted
				pollSem.acquire();
				
				System.out.print("[poll-thread] Polling for deleteResult ... ");
				pollResult = arc.poll();
				arc.closeTcpConnection();
				
				if ((pollResult.getResults().size() < 1) ||
						pollResult.getResults().get(0).getType() != Type.deleteResult) 
					System.out.println("Unexpected third PollResult");
				else
					System.out.println("OK");
				IfmapJExamples.sleepSomeTime();
			
				// let the main thread end the session
				mainSem.release();
				
				IfmapJExamples.doLastPoll(arc);
				
			} catch (Exception e) {
				System.err.println("[poll-thread] Exception: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	

	public void run (String args[]) throws InitializationException {
		
		System.out.println("====== RUNNING POLL EXAMPLE ======");
		
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
		
		ssrc.setGzip(true);
		
		arc = ssrc.getArc();
		
		try {
	
			System.out.print("[main-thread] newSession ... ");
			ssrc.newSession();
			ssrc.closeTcpConnection();
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
					ip)));
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			ssrc.closeTcpConnection();
			
			// start the polling thread
			new Thread(new Polling()).start();
		
			// let it go
			pollSem.release();
	
			// continue if the poll thread has done its first polling
			mainSem.acquire();
			
			System.out.print("[main-thread] publishing... ");
			ssrc.publish(Requests.createPublishReq(Requests.createPublishUpdate(
					ip, mac, mf.createIpMac())));
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
		
			
			// let the poll-thread do it's polling
			pollSem.release();
		
			// wait until the poll-thread is done
			mainSem.acquire();
		
			PublishDelete delete = Requests.createPublishDelete(ip, mac, "meta:ip-mac");
			delete.addNamespaceDeclaration(IfmapStrings.STD_METADATA_PREFIX,
					IfmapStrings.STD_METADATA_NS_URI);
			
			System.out.print("[main-thread] deleting ... ");
			// delete the metadata
			ssrc.publish(Requests.createPublishReq(delete));
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
		
			// let the poll-thread get the delteResult
			pollSem.release();
		
			// wait until it's done
			mainSem.acquire();
			
			IfmapJExamples.doLastEndSession("[main-thread]", ssrc);
			IfmapJExamples.sleepSomeTime();
		} catch (IfmapException e ) {
			System.err.println(e.getMessage());
		} catch (IfmapErrorResult e) {
			System.err.println(e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
