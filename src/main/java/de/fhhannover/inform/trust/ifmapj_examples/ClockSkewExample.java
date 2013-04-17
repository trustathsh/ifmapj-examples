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
import de.fhhannover.inform.trust.ifmapj.identifier.Identifiers;
import de.fhhannover.inform.trust.ifmapj21.ClockSkewDetector;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This example shows how the clock skew detection can be used by clients
 * 
 * @author jk
 * @since 0.1.5
 */
public class ClockSkewExample {

	public void run(String args[]) throws InitializationException {
		System.out.println("====== RUNNING CLOCK SKEW EXAMPLE ======");
		SSRC ssrc;
		ClockSkewDetector csd;
		long clockSkew;
		
		/*
		 * Example time synchronization on newSession() and renewSession()
		 * when a device identifier was supplied on createSSRC() call
		 */
		System.out.println("--- AUTOMATIC CLOCK SYNC ON NEW SESSION ---");

		/*
		 * Use the new createSSRC() function and supply a device identifier
		 * for automated time synchronization on newSession() and
		 * renewSession() calls.
		 */
		ssrc = IfmapJ.createSSRC(
							Config.BASIC_AUTH_SERVER_URL,
							Config.BASIC_AUTH_USER,
							Config.BASIC_AUTH_PASSWORD,
							IfmapJHelper.getTrustManagers(getClass().getResourceAsStream(
											Config.TRUST_STORE_PATH),
											Config.TRUST_STORE_PASSWORD));
		
		try {
			// create new example session 
			System.out.print("Starting new session .. ");
			ssrc.newSession();
			System.out.println("OK");
			
			csd = IfmapJ.createClockSkewDetector(ssrc, Identifiers.createDev("device0"));
			
			// is the clock synchronized? probably not
			System.out.println("Time synchronized?  ... " + csd.getClockSynchronized());
		
			// Do the clock synchronization
			System.out.print("Doing clock synchronization  ... ");
			csd.performClockSynchronization();
			System.out.println("Done");

			System.out.println("Time synchronized?  ... " + csd.getClockSynchronized());
			
			// yes, so we have an updated clock skew value
			System.out.println("Detected clock skew ... " + csd.getClockSkewMilliseconds() +
						" ms ("+ csd.getClockSkewSeconds() + " sec.)");
			// and we have the server time too
			System.out.println("The server clock is ... " +
						formatDate(csd.getClockOfServer()));
			// so we can use it instead of local time from now on for
			// publishing metadata with timestamps
			System.out.println("Last synchronization .. " + 
						formatDate(csd.getClockLastSynchronization()));
			// wait a second as simple proof of working server time
			sleep(1000);
			
			System.out.println("The server clock one second later is ... " +
						formatDate(csd.getClockOfServer()));
			
			sleep(1000);
			// last synchronization does not update until calling renewSession()
			// or performClockSynchronization().
			System.out.print("Synchronizing time  ... ");
			// perform a clock skew detection with automatic synchronization
			csd.performClockSynchronization();
			System.out.println("OK");
			System.out.println("Last synchronization .. " + 
						formatDate(csd.getClockLastSynchronization()));
			
			sleep(1000);
			// closing session for good
			System.out.print("Ending the session  ... ");
			ssrc.endSession();
			System.out.println("OK");
			
			/*
			 * Manual time synchronization by application when no
			 * device identifer has been supplied on createSSRC() call
			 */
			System.out.println("--- MANUAL CLOCK SYNC BY APPLICATION ---");

			/*
			 * Use the createSSRC() function without a device identifier
			 * for manual time synchronization and backward compatibility.
			 */
			ssrc = IfmapJ.createSSRC(
								Config.BASIC_AUTH_SERVER_URL,
								Config.BASIC_AUTH_USER,
								Config.BASIC_AUTH_PASSWORD,
								IfmapJHelper.getTrustManagers(getClass().getResourceAsStream(
												Config.TRUST_STORE_PATH),
												Config.TRUST_STORE_PASSWORD));
		

			// create another new example session 
			System.out.print("Starting new session .. ");
			ssrc.newSession();
			System.out.println("OK");
			
			csd = IfmapJ.createClockSkewDetector(ssrc, Identifiers.createDev("device0"));
			
			
			// calling performClockSkewDetection() with Calendar object
			// which differs three seconds from the server time i.e.
			System.out.print("Adding three seconds .. ");
			Calendar wrongClientTime = Calendar.getInstance();
			wrongClientTime.add(Calendar.SECOND, 3);
			System.out.println("OK");
			System.out.println("The client clock is ... " +
						formatDate(wrongClientTime));
			
			// get the clock skew now with the three seconds adjusted client time
			clockSkew = csd.performClockSkewDetection(wrongClientTime);
			System.out.println("Detected clock skew ... " + clockSkew + " ms");
			
			// calculate the server time based on wrong client time and clock skew
			Calendar rightServerTime = (Calendar)wrongClientTime.clone();
			rightServerTime.add(Calendar.MILLISECOND, (int)clockSkew);
			System.out.println("The server clock is ... " +
						formatDate(rightServerTime));

			// closing session for good
			System.out.print("Ending the session  ... ");
			ssrc.endSession();
			System.out.println("OK");
			
		// catch errors that may occur:
		// - If the server replies with an ErrorResult, a IfmapErrorResult
		//	is thrown.
		// - Other failures, for example due to network outage result in
		//   an IfmapException.
		// - If the session gets aborted an EndSessionException is thrown
		} catch (IfmapException e) {
			System.err.println(e.getDescription());
		} catch (IfmapErrorResult e) {
			System.err.println(e);
		}
	}

	/**
	 * Format Calendar object as readable string for console output
	 * @param cal
	 * @return string with date and time
	 */
	private String formatDate(Calendar cal) {
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(cal.getTime());
	}

	/**
	 * Sleep for a defined timespan and tell about it
	 * @param duration sleep duration in milliseconds
	 */
	private void sleep(int duration) {
			System.out.print("Sleeping some time  ... "); 
			IfmapJExamples.sleepSomeTime(duration);
			System.out.println(Math.round(duration / 1000) + " sec.");
	}
}
