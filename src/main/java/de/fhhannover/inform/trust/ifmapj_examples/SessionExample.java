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

import de.fhhannover.inform.trust.ifmapj.IfmapJ;
import de.fhhannover.inform.trust.ifmapj.IfmapJHelper;
import de.fhhannover.inform.trust.ifmapj.channel.SSRC;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapErrorResult;
import de.fhhannover.inform.trust.ifmapj.exception.IfmapException;
import de.fhhannover.inform.trust.ifmapj.exception.InitializationException;

public class SessionExample {
	
	public void run(String[] args) throws InitializationException {
		
		System.out.println("====== RUNNING SESSION EXAMPLE ======");
		
		try {
			// To start a new IF-MAP session, we first need to establish a
			// synchronous send and receive channel (SSRC). At this moment,
			// you have to choose whether you want to do basic or certificate
			// based authentication.
			SSRC ssrc = IfmapJ.createSSRC(
					Config.BASIC_AUTH_SERVER_URL,
					Config.BASIC_AUTH_USER,
					Config.BASIC_AUTH_PASSWORD,
					IfmapJHelper.getTrustManagers(
							getClass().getResourceAsStream(Config.TRUST_STORE_PATH),
							Config.TRUST_STORE_PASSWORD));
			System.out.println("Creating SSRC with basic authentication successful");
			
			// alternatively, use certificate based authentication
			// make sure to add the ifmapj-examples public key to the appropriate
			// configuration file of your MAP server
//			SSRC ssrc = IfmapJ.createSSRC(
//					Config.CERT_AUTH_SERVER_URL,
//					IfmapJHelper.getKeyManagers(getClass().getResourceAsStream(Config.KEY_STORE_PATH), Config.KEY_STORE_PASSWORD),
//					IfmapJHelper.getTrustManagers(getClass().getResourceAsStream(Config.TRUST_STORE_PATH), Config.TRUST_STORE_PASSWORD));
//			System.out.println("Creating SSRC with certificate based authentication successful");
						
			// now we can call for a new session
			System.out.println("Doing newSession");
			ssrc.newSession();
			System.out.println("newSession successful");
			// you can easily access the parameters of the session
			System.out.println("session-id=" + ssrc.getSessionId());
			System.out.println("ifmap-publisher-id=" + ssrc.getPublisherId());
			System.out.println("max-poll-result-size=" + ssrc.getMaxPollResSize());
			
			// now would be a good time to issue some further IF-MAP operations
			// publish update | delete | notify | search | subscribe 
			IfmapJExamples.sleepSomeTime();
			
			// it is also allowed to obtain a new session even when there is
			// already an existing session. In this case, the new session will
			// be used, the old one will be discarded by the MAP server (hopefully ...)
			System.out.println("Doing newSession, again");
			// this time we specify max-poll-result-size
			ssrc.newSession(new Integer("1234"));
			System.out.println("newSession successful");
			System.out.println("session-id=" + ssrc.getSessionId());
			System.out.println("ifmap-publisher-id=" + ssrc.getPublisherId());
			System.out.println("max-poll-result-size=" + ssrc.getMaxPollResSize());
			IfmapJExamples.sleepSomeTime();
			
			// if you have done all necessary IF-MAP operations, you can end
			// the session. Note that this call implicitly deletes all IF-MAP
			// metadata objects whose lifetime is session.
			System.out.println("Doing endSession");
			ssrc.endSession();
			ssrc.closeTcpConnection();
			System.out.println("endSession successful");
		} catch (IfmapErrorResult e) {
			System.out.println(e.toString());
		} catch (IfmapException e) {
			System.out.println(e.getDescription());
			System.out.println(e.getMessage());
		}
	}
}
