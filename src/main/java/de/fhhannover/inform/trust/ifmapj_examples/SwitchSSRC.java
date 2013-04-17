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

public class SwitchSSRC {
	
	public void run(String[] args) throws InitializationException {
		
		System.out.println("====== SWITCH SSRC EXAMPLE ======");
		
		try {
			SSRC ssrc = IfmapJ.createSSRC(
					Config.BASIC_AUTH_SERVER_URL,
					Config.BASIC_AUTH_USER,
					Config.BASIC_AUTH_PASSWORD,
					IfmapJHelper.getTrustManagers(
							getClass().getResourceAsStream(Config.TRUST_STORE_PATH),
							Config.TRUST_STORE_PASSWORD));
			
			SSRC ssrc2 = IfmapJ.createSSRC(
					Config.BASIC_AUTH_SERVER_URL,
					Config.BASIC_AUTH_USER,
					Config.BASIC_AUTH_PASSWORD,
					IfmapJHelper.getTrustManagers(
							getClass().getResourceAsStream(Config.TRUST_STORE_PATH),
							Config.TRUST_STORE_PASSWORD));
		
			ssrc.newSession();
			System.out.println("newSession successful");
			System.out.println("session-id: " + ssrc.getSessionId());
			System.out.println("publisher-id: " + ssrc.getPublisherId());
			
			ssrc2.setSessionId(ssrc.getSessionId());
			ssrc2.setPublisherId(ssrc.getPublisherId());
			ssrc2.endSession();
			ssrc.closeTcpConnection();
			ssrc2.closeTcpConnection();
			System.out.println("endSession successful");
		} catch (IfmapErrorResult e) {
			System.out.println(e.toString());
		} catch (IfmapException e) {
			System.out.println(e.getDescription());
			System.out.println(e.getMessage());
		}
	}
}
