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



import de.hshannover.f4.trust.ifmapj.channel.ARC;
import de.hshannover.f4.trust.ifmapj.channel.SSRC;
import de.hshannover.f4.trust.ifmapj.exception.EndSessionException;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.exception.InitializationException;
import de.hshannover.f4.trust.ifmapj.messages.ResultItem;
import de.hshannover.f4.trust.ifmapj.messages.SearchResult;
import java.util.Collection;

/**
 * Hello world!
 *
 */
public class IfmapJExamples 
{
	// some examples want to do some sleeping
	private static final int SLEEP_TIME = 50; // ms
	
	public static void main( String[] args ) {
		System.out.println( "=== IFMAPJ EXAMPLES ===" );
		
		try {
				new IdentityTesting().run(args);
			new PollExample().run(args);
			new SwitchSSRC().run(args);
			new SubscriptionSearchResults().run(args);
			new PollWithSupgraphAddingRemovingExample().run(args);
			new PollForEvents().run(args);
			new SessionExample().run(args);
			new PublishExample().run(args);
			new LoopLink().run(args);
				new PublishExample2().run(args);
				new ClockSkewExample().run(args);
				new ExtendedIdentityExample().run(args);
	} catch (InitializationException e) {
		System.err.println(e.getDescription());
		System.err.println(e.getMessage());
	}
	}
	
	static void sleepSomeTime() {
		sleepSomeTime(SLEEP_TIME);
	}

	static void sleepSomeTime(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// ignore
		}
	};
	
	/**
	 * The last poll on an {@link ARC} channel might lead to an ErrorResult or
	 * EndSessionResult at or while the poll call is done.
	 * Unify this as the polling examples are using this.
	 * 
	 * @param arc
	 * @throws IfmapException 
	 */
	static void doLastPoll(ARC arc) throws IfmapException {
		String finalMsg = null;
		try {
			arc.poll();
		} catch (EndSessionException e) {
			finalMsg = "EndSessionResult";
		} catch (IfmapErrorResult e) {
			finalMsg = e.toString();
		} finally {
			System.out.println("[poll-thread] Last poll ... Resulted in " + finalMsg);
			arc.closeTcpConnection();
		}
	}
	
	static void doLastEndSession(SSRC ssrc) throws IfmapException {
		doLastEndSession(null, ssrc);
	}

	public static void doLastEndSession(String prefix, SSRC ssrc) throws IfmapException {
		String finalMsg = "OK";
		prefix = (prefix != null) ? prefix + " " : "";
		try {
			ssrc.endSession();
		} catch (IfmapErrorResult err) {
			finalMsg = err.toString();
		} finally {
			System.out.println(prefix + "endSession ... " + finalMsg);
			ssrc.closeTcpConnection();
		}
	}
	

	/**
	 * Helper to print out ResultItems...
	 */
	static void printSearchResult(SearchResult sres) {
		printSearchResult("searchResult", sres);
	}

	public static void printSearchResult(String resultName, SearchResult sres) {
		System.out.println("-------- print " + resultName + " name=" + sres.getName());
		Collection<ResultItem> ris = sres.getResultItems();
		int i = 0;
		for (ResultItem ri : ris) {
			System.out.println("Item " + i + ") " + ri);
			i++;
		}
		System.out.println("-------------------------------------------------");
	}
}
