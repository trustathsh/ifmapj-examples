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
import de.hshannover.f4.trust.ifmapj.binding.IfmapStrings;
import de.hshannover.f4.trust.ifmapj.channel.SSRC;
import de.hshannover.f4.trust.ifmapj.config.BasicAuthConfig;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.exception.InitializationException;
import de.hshannover.f4.trust.ifmapj.identifier.Identifiers;
import de.hshannover.f4.trust.ifmapj.identifier.IpAddress;
import de.hshannover.f4.trust.ifmapj.identifier.MacAddress;
import de.hshannover.f4.trust.ifmapj.messages.MetadataLifetime;
import de.hshannover.f4.trust.ifmapj.messages.PublishDelete;
import de.hshannover.f4.trust.ifmapj.messages.PublishRequest;
import de.hshannover.f4.trust.ifmapj.messages.Requests;
import de.hshannover.f4.trust.ifmapj.metadata.StandardIfmapMetadataFactory;

/**
 * This example shows how the creation of publish requests can be condensed
 * to a few lines. If no lifetime is given for the creation of a PublishUpdate
 * object, it's by default {@link MetadataLifetime#session}.
 * 
 * It is also shown how "lifetime forever" metadata can be published.
 */
public class PublishExample2 {
	
	
	private StandardIfmapMetadataFactory mf = IfmapJ.createStandardMetadataFactory();
	private IpAddress ip = Identifiers.createIp4("192.168.0.1");
	private MacAddress mac = Identifiers.createMac("aa:bb:cc:dd:ee:ff");

	public void run(String args[]) throws InitializationException {
		

		System.out.println("====== RUNNING PUBLISH EXAMPLE 2 ======");
		
		SSRC ssrc = IfmapJ.createSsrc(new BasicAuthConfig(
				Config.BASIC_AUTH_SERVER_URL,
				Config.BASIC_AUTH_USER,
				Config.BASIC_AUTH_PASSWORD,
				Config.TRUST_STORE_PATH,
				Config.TRUST_STORE_PASSWORD));
		
		try {
		
			System.out.print("newSession ... ");
			ssrc.newSession();
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// Publish ip-mac metadata between 192.168.0.1 and aa:bb:cc:dd:ee:ff
			System.out.print("publish update (session) ... ");
			ssrc.publish(Requests.createPublishReq(Requests.createPublishUpdate(
					ip, mac, mf.createIpMac())));
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
								
			// Delete it again, see preparePublishDelete()...
			System.out.print("publish delete ... ");
			ssrc.publish(preparePublishDelete());
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
	
			// Publish the same metadata, this time using explicit lifetime forever
			System.out.print("publish update (forever) ... ");
			ssrc.publish(Requests.createPublishReq(Requests.createPublishUpdate(
					ip, mac, mf.createIpMac(), MetadataLifetime.forever)));
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// Publish the same metadata, this time using explicit lifetime session
			System.out.print("publish update (session) ... ");
			ssrc.publish(Requests.createPublishReq(Requests.createPublishUpdate(
					ip, mac, mf.createIpMac(), MetadataLifetime.session)));
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
		
			// Ending the session removes all metadata with lifetime=session.
			// Note: If we directly call newSession(), the same behavior is expected,
			// but it wouldn't be as clear.
			System.out.print("endSession ... ");
			ssrc.endSession();
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// Create a new session.
			System.out.print("newSession ... ");
			ssrc.newSession();
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// Delete the forever metadata. "Forever metadata" stays across multiple
			// sessions.
			System.out.print("publish delete ... ");
			ssrc.publish(preparePublishDelete());
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// Now, end the session, hoping there's no metadata left.
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

	private PublishRequest preparePublishDelete() {
		PublishDelete del = Requests.createPublishDelete(ip, mac, "meta:ip-mac");
		
		// As the meta: prefix in the filter string is used, the declaration
		// of this namespace needs to be made. If this isn't done, a MAPS will
		// either complain about it, or won't delete the appropriate metadata.
		del.addNamespaceDeclaration(IfmapStrings.STD_METADATA_PREFIX,
				IfmapStrings.STD_METADATA_NS_URI);
		return Requests.createPublishReq(del);
	}
}
