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



/**
 * Configuration for ifmapj examples. Default values assume that the keystore
 * is located in the created runnable jar file and that irond is running in its
 * default configuration on localhost.
 * 
 * @author ibente
 *
 */
public class Config {
	
	public static String KEY_STORE_PATH = "/ifmapj-examples.jks";
	public static String KEY_STORE_PASSWORD = "ifmapj-examples";
	public static String TRUST_STORE_PATH = "/ifmapj-examples.jks";
		public static String EXTENDED_IDENTITY_XML = "/example-ei.xml";
	public static String TRUST_STORE_PASSWORD = "ifmapj-examples";
	public static String BASIC_AUTH_SERVER_URL = "https://localhost:8443";
	public static String CERT_AUTH_SERVER_URL = "https://localhost:8444";
	public static String BASIC_AUTH_USER = "test";
	public static String BASIC_AUTH_PASSWORD = "test";
	
	private Config() { };
}
