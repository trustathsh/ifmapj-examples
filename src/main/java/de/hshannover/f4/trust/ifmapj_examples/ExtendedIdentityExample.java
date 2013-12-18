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
 * This file is part of IfmapJ-examples, version 1.0.0, implemented by the
 * Trust@HsH research group at the Hochschule Hannover.
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
import de.hshannover.f4.trust.ifmapj.IfmapJHelper;
import de.hshannover.f4.trust.ifmapj.binding.IfmapStrings;
import de.hshannover.f4.trust.ifmapj.channel.SSRC;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.exception.InitializationException;
import de.hshannover.f4.trust.ifmapj.exception.MarshalException;
import de.hshannover.f4.trust.ifmapj.identifier.Device;
import de.hshannover.f4.trust.ifmapj.identifier.Identifier;
import de.hshannover.f4.trust.ifmapj.identifier.Identifiers;
import de.hshannover.f4.trust.ifmapj.messages.*;
import de.hshannover.f4.trust.ifmapj.metadata.StandardIfmapMetadataFactory;

/**
 * @since 0.1.5
 * @author jk
 */
public class ExtendedIdentityExample {
	
	public void run(String[] args) throws InitializationException {

		System.out.println("====== RUNNING EXTENDED IDENTITY TEST ======");

		Device dev = Identifiers.createDev("device01");

		// Use basic authentication.
		SSRC ssrc = IfmapJ.createSSRC(
						Config.BASIC_AUTH_SERVER_URL,
						Config.BASIC_AUTH_USER,
						Config.BASIC_AUTH_PASSWORD,
						IfmapJHelper.getTrustManagers(
									getClass().getResourceAsStream(
											Config.TRUST_STORE_PATH),
											Config.TRUST_STORE_PASSWORD));
		
		// To create simple requests, we need some factories.
		StandardIfmapMetadataFactory mF = IfmapJ.createStandardMetadataFactory();
		
		try {
			// create IF-MAP 2.1 compliant extended identifier from
			// any XML document (see spec. 3.2.3.2 for details)
			Identifier extId = Identifiers.createExtendedIdentity(
							getClass().getResourceAsStream(Config.EXTENDED_IDENTITY_XML));

			// Create a PublishUpdate object containing the
			// extended identifier followed by a PublishDelete
			PublishUpdate pu = Requests.createPublishUpdate(dev, extId, mF.createAuthAs());
			PublishDelete pd = Requests.createPublishDelete(extId, dev);
			pd.addNamespaceDeclaration("meta", IfmapStrings.STD_METADATA_NS_URI);
			pu.setLifeTime(MetadataLifetime.forever);
			PublishRequest pr1 = Requests.createPublishReq(pu);
			PublishRequest pr2 = Requests.createPublishReq(pd);

			// create a new session
			System.out.print("newSession ... ");
			ssrc.newSession();
			System.out.println("OK");
			
			// publish the loop
			System.out.print("publish update ... ");
			ssrc.publish(pr1);
			System.out.println("OK");
			ssrc.endSession();
			System.out.print("newSession ... ");
			ssrc.newSession();
			System.out.println("OK");
			System.out.print("delete ... ");
			ssrc.publish(pr2);
			System.out.println("OK");
			ssrc.endSession();
				
		// catch errors that may occur:
		// - If the server replies with an ErrorResult, a IfmapErrorResult
		//	is thrown.
		// - Other failures, for example due to network outage result in
		//   an IfmapException.
		} catch (MarshalException e) {
				System.err.println(e);
		} catch (IfmapException e) {
				System.err.println(e.getDescription());
		} catch (IfmapErrorResult e) {
				System.err.println(e);
		}
	}
}
