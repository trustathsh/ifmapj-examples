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




import java.util.Collection;

import org.w3c.dom.Document;

import de.hshannover.f4.trust.ifmapj.IfmapJ;
import de.hshannover.f4.trust.ifmapj.IfmapJHelper;
import de.hshannover.f4.trust.ifmapj.channel.ARC;
import de.hshannover.f4.trust.ifmapj.channel.SSRC;
import de.hshannover.f4.trust.ifmapj.exception.EndSessionException;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.exception.InitializationException;
import de.hshannover.f4.trust.ifmapj.identifier.Identifiers;
import de.hshannover.f4.trust.ifmapj.identifier.IpAddress;
import de.hshannover.f4.trust.ifmapj.identifier.MacAddress;
import de.hshannover.f4.trust.ifmapj.messages.PollResult;
import de.hshannover.f4.trust.ifmapj.messages.PublishRequest;
import de.hshannover.f4.trust.ifmapj.messages.Requests;
import de.hshannover.f4.trust.ifmapj.messages.SearchResult;
import de.hshannover.f4.trust.ifmapj.messages.SearchResult.Type;
import de.hshannover.f4.trust.ifmapj.metadata.StandardIfmapMetadataFactory;

/**
 * Demonstrate search, update and delete {@link SearchResult} objects
 * in a {@link PollResult}
 * 
 * @author aw
 *
 */
public class SubscriptionSearchResults {
	
	private SSRC ssrc = null;
	private ARC arc = null;
	private IpAddress ip = Identifiers.createIp4("192.168.0.1");
	private MacAddress mac = Identifiers.createMac("aa:bb:cc:dd:ee:ff");
	private StandardIfmapMetadataFactory mf = IfmapJ.createStandardMetadataFactory();
	
	public void run (String args[]) throws InitializationException {
		
		System.out.println("====== RUNNING SUBSCRIPTION INIT SEARCH ======");
		
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

			PollResult pres = null;
			PublishRequest preq = null;
			Document dummyMd = mf.createCapability("dummy");
			System.out.print("newSession ... ");
			ssrc.newSession();
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
		
			// Publish a ip-mac link from 192.168.0.1 to aa:bb:cc:dd:ee:ff
			preq =  Requests.createPublishReq(Requests.createPublishUpdate(ip, mac, mf.createIpMac()));
			System.out.print("publishing... ");
			ssrc.publish(preq);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			
			// Create a subscription beginning from 192.168.0.1
			System.out.print("[main-thread] subscribe ... ");
			ssrc.subscribe(Requests.createSubscribeReq(Requests.createSubscribeUpdate(
					"my_sub_1234", // name
					null,			// match-links
					10,				// maxDepth
					null,			// terminal-identifiers,
					null,			// max-size
					null,			// result-filter
					ip)));
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			pres = arc.poll();
			
			checkPollResultForSearch(pres);
			preq =  Requests.createPublishReq(Requests.createPublishUpdate(ip, mac, dummyMd));
			preq.addPublishElement(Requests.createPublishUpdate(ip, dummyMd));
			preq.addPublishElement(Requests.createPublishUpdate(mac, dummyMd));
			ssrc.publish(preq);

			pres = arc.poll();
			checkPollResultForUpdate(pres);
			
			ssrc.purgePublisher();
			pres = arc.poll();
			checkPollResultForDelete(pres);
			
			IfmapJExamples.doLastEndSession("[main-thread]", ssrc);
			IfmapJExamples.sleepSomeTime();
			arc.closeTcpConnection();
		} catch (IfmapException e ) {
			System.err.println(e.getMessage());
		} catch (IfmapErrorResult e) {
			System.err.println(e);
		} catch (EndSessionException e) {
			System.err.println(e);
		}
	}

	private void checkPollResultForDelete(PollResult pres) {
		checkSizeAndShow("deleteResult", pres.getResults(), Type.deleteResult);
	}

	private void checkPollResultForUpdate(PollResult pres) {
		checkSizeAndShow("updateResult", pres.getResults(), Type.updateResult);
	}

	private void checkPollResultForSearch(PollResult pres) {
		checkSizeAndShow("searchResult", pres.getResults(), Type.searchResult);
	}
	
	private void checkSizeAndShow(String resultName, Collection<SearchResult> results, Type resType) {
		if (results.size() < 1)
			System.out.println("Unexpected Number of results ("
					+ results.size() + ")");

		for (SearchResult sr : results) {
			if (sr.getType() != resType)
				System.out.println("Unexpected type of result: " +sr.getType() +
						" instead of " + resType);
			else
				IfmapJExamples.printSearchResult(resultName, sr);
		}
	}
}
