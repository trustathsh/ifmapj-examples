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


import java.util.Collection;

import org.w3c.dom.Document;

import de.fhhannover.inform.trust.ifmapj.IfmapJ;
import de.fhhannover.inform.trust.ifmapj.IfmapJHelper;
import de.fhhannover.inform.trust.ifmapj.channel.ARC;
import de.fhhannover.inform.trust.ifmapj.channel.SSRC;
import de.fhhannover.inform.trust.ifmapj.exception.EndSessionException;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapErrorResult;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapException;
import de.fhhannover.inform.trust.ifmapj.exception.InitializationException;
import de.fhhannover.inform.trust.ifmapj.identifier.Identifiers;
import de.fhhannover.inform.trust.ifmapj.identifier.IpAddress;
import de.fhhannover.inform.trust.ifmapj.identifier.MacAddress;
import de.fhhannover.inform.trust.ifmapj.messages.PollResult;
import de.fhhannover.inform.trust.ifmapj.messages.PublishRequest;
import de.fhhannover.inform.trust.ifmapj.messages.Requests;
import de.fhhannover.inform.trust.ifmapj.messages.SearchResult;
import de.fhhannover.inform.trust.ifmapj.messages.SearchResult.Type;
import de.fhhannover.inform.trust.ifmapj.metadata.StandardIfmapMetadataFactory;

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
