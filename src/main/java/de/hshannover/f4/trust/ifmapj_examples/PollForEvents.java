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
 * This file is part of ifmapj-examples, version 1.0.1,
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



import java.util.List;
import java.util.concurrent.Semaphore;

import org.w3c.dom.Document;

import de.hshannover.f4.trust.ifmapj.IfmapJ;
import de.hshannover.f4.trust.ifmapj.binding.IfmapStrings;
import de.hshannover.f4.trust.ifmapj.channel.ARC;
import de.hshannover.f4.trust.ifmapj.channel.SSRC;
import de.hshannover.f4.trust.ifmapj.config.BasicAuthConfig;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.exception.InitializationException;
import de.hshannover.f4.trust.ifmapj.identifier.AccessRequest;
import de.hshannover.f4.trust.ifmapj.identifier.Identifiers;
import de.hshannover.f4.trust.ifmapj.identifier.Identity;
import de.hshannover.f4.trust.ifmapj.identifier.IdentityType;
import de.hshannover.f4.trust.ifmapj.identifier.IpAddress;
import de.hshannover.f4.trust.ifmapj.messages.PollResult;
import de.hshannover.f4.trust.ifmapj.messages.PublishNotify;
import de.hshannover.f4.trust.ifmapj.messages.Requests;
import de.hshannover.f4.trust.ifmapj.messages.SearchResult;
import de.hshannover.f4.trust.ifmapj.messages.SearchResult.Type;
import de.hshannover.f4.trust.ifmapj.messages.SubscribeRequest;
import de.hshannover.f4.trust.ifmapj.messages.SubscribeUpdate;
import de.hshannover.f4.trust.ifmapj.metadata.EventType;
import de.hshannover.f4.trust.ifmapj.metadata.Significance;
import de.hshannover.f4.trust.ifmapj.metadata.StandardIfmapMetadataFactory;


/**
 * 
 */
public class PollForEvents {
	
	private StandardIfmapMetadataFactory mf = IfmapJ.createStandardMetadataFactory();
	private SSRC ssrc;
	private ARC arc;
	private Semaphore mainSem, pollSem;
	
	private IpAddress ip;
	private AccessRequest ar;
	private Identity id;
	
	Document arIp, authenticatedAs, event; 
	
	public PollForEvents() {
		// prepare identifiers
		ip = Identifiers.createIp4("1.0.80.80");
		ar = Identifiers.createAr("test-18067508891:1");
		id = Identifiers.createIdentity(IdentityType.userName, "User1");
		
		// prepare metadata
		authenticatedAs = mf.createAuthAs();
		arIp = mf.createArIp();
		event = mf.createEvent("name", "2011-08-19T09:09:21Z", "discId", new Integer(59), new Integer(50), Significance.important, EventType.policyViolation, null, "info", "http://www.example.org");
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
					System.out.println(" OK");
				IfmapJExamples.sleepSomeTime();
			
				// let the main-thread publish notify an event metadata
				mainSem.release();
			
				// wait until the event was published
				pollSem.acquire();
				
				System.out.print("[poll-thread] Polling for notifyResult ... ");
				pollResult = arc.poll();
				
				results = pollResult.getResults();
				if (results.size() < 1 || results.get(0).getType() != Type.notifyResult)
					System.out.println("Unexpected third PollResult");
				else
					System.out.println(" OK");
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
		
		System.out.println("====== RUNNING POLL FOR EVENTS EXAMPLE ======");
		
		mainSem = new Semaphore(0);
		pollSem = new Semaphore(0);

		// Use basic authentication
		ssrc = IfmapJ.createSsrc(new BasicAuthConfig(
				Config.BASIC_AUTH_SERVER_URL,
				Config.BASIC_AUTH_USER,
				Config.BASIC_AUTH_PASSWORD,
				Config.TRUST_STORE_PATH,
				Config.TRUST_STORE_PASSWORD));
		
		arc = ssrc.getArc();
		
		try {
	
			System.out.print("[main-thread] newSession ... ");
			ssrc.newSession();
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
		
			// Create a subscription beginning from 192.168.0.1
			System.out.print("[main-thread] subscribe ... ");
			SubscribeUpdate subscribeUpd = Requests.createSubscribeUpdate(
					"subscription", // name
					"meta:access-request-ip",			// match-links that matches the published metadata
//					"meta:authenticated-as",			// match-links that does not match the published metadata
					3,				// maxDepth
					null,			// terminal-identifiers,
					null,			// max-size
					"meta:event or meta:access-request-ip",			// result-filter
					ar);			// start identifier
			subscribeUpd.addNamespaceDeclaration(IfmapStrings.STD_METADATA_PREFIX,
					IfmapStrings.STD_METADATA_NS_URI);
			SubscribeRequest subscribeReq = Requests.createSubscribeReq(subscribeUpd);
			ssrc.subscribe(subscribeReq);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// start the polling thread
			new Thread(new Polling()).start();
		
			// let it go
			pollSem.release();
	
			// continue if the poll thread has done its first polling
			mainSem.acquire();
			
			System.out.print("[main-thread] publishing authenticated-as... ");
			ssrc.publish(Requests.createPublishReq(Requests.createPublishUpdate(
					ar, id, authenticatedAs)));
			System.out.println("OK");
			
			IfmapJExamples.sleepSomeTime();
			System.out.print("[main-thread] publishing access-request-ip... ");
			ssrc.publish(Requests.createPublishReq(Requests.createPublishUpdate(
					ar, ip, arIp)));
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
		
			
			// let the poll-thread do it's polling
			pollSem.release();
		
			// wait until the poll-thread is done
			mainSem.acquire();

			// publish notify event metadata
			PublishNotify notify = Requests.createPublishNotify(ip, event);
			
			System.out.print("[main-thread] publishing event... ");
			ssrc.publish(Requests.createPublishReq(notify));
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
		
			// let the poll-thread get the event
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
