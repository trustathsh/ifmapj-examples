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



import de.hshannover.f4.trust.ifmapj.IfmapJ;
import de.hshannover.f4.trust.ifmapj.channel.SSRC;
import de.hshannover.f4.trust.ifmapj.config.BasicAuthConfig;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.exception.InitializationException;
import de.hshannover.f4.trust.ifmapj.identifier.Identifiers;
import de.hshannover.f4.trust.ifmapj.identifier.IpAddress;
import de.hshannover.f4.trust.ifmapj.messages.PublishRequest;
import de.hshannover.f4.trust.ifmapj.messages.PublishUpdate;
import de.hshannover.f4.trust.ifmapj.messages.Requests;
import de.hshannover.f4.trust.ifmapj.messages.SearchRequest;
import de.hshannover.f4.trust.ifmapj.messages.SearchResult;
import de.hshannover.f4.trust.ifmapj.metadata.StandardIfmapMetadataFactory;

/**
 * This example publishes a link with two identical identifiers. In graph theory,
 * an edge which starts and ends at the same vertex is called a loop. We try to
 * publish a metadata such that a loop is created in the MAP graph.
 * 
 * The result though, is probably MAPS dependent. 
 * 
 * This example further shows how code can be simplified by using the "convenience
 * factory methods". This is shown with, for example,
 * createPublishUpdate(ip, ip, mF.createIpMac()), which creates a
 * {@link PublishUpdate} object, containing a Link and a single metadata
 * element (ip-mac metadata).
 */
public class LoopLink {
	
	public void run(String[] args) throws InitializationException {

		System.out.println("====== RUNNING LOOP EXAMPLE ======");
	
		// Use basic authentication.
		SSRC ssrc = IfmapJ.createSsrc(new BasicAuthConfig(
				Config.BASIC_AUTH_SERVER_URL,
				Config.BASIC_AUTH_USER,
				Config.BASIC_AUTH_PASSWORD,
				Config.TRUST_STORE_PATH,
				Config.TRUST_STORE_PASSWORD));
		

		// To create simple requests, we need some factories.
		StandardIfmapMetadataFactory mF = IfmapJ.createStandardMetadataFactory();
		
		IpAddress ip = Identifiers.createIp4("192.168.0.1");
		// Create a PublishUpdate object containing a link. Note: It doesn't
		// really make sense to publish ip-mac metadata between to IP addresses.
		PublishUpdate pu = Requests.createPublishUpdate(ip, ip, mF.createIpMac());
		PublishRequest pr = Requests.createPublishReq(pu);
	
		// Create a SearchRequest. null means not specified. So this will search
		// the MAPS beginning from "192.168.0.1" with a max-depth of 10.
		SearchRequest sr = Requests.createSearchReq(null, 10, null, null, null, ip);

		try {
			
			// create a new session
			System.out.print("newSession ... ");
			ssrc.newSession();
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish the loop
			System.out.print("publish update ... ");
			ssrc.publish(pr);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();

			// search for the link
			System.out.print("search ... ");
			SearchResult sres = ssrc.search(sr);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// print out the result.
			IfmapJExamples.printSearchResult(sres);
			IfmapJExamples.sleepSomeTime();
			
			IfmapJExamples.doLastEndSession(ssrc);
			
		// catch errors that may occur:
		// - If the server replies with an ErrorResult, a IfmapErrorResult
		//	is thrown.
		// - Other failures, for example due to network outage result in
		//   an IfmapException.
		} catch (IfmapException e) {
			System.err.println(e.getDescription());
		} catch (IfmapErrorResult e) {
			System.err.println(e);
		}
	}

}
