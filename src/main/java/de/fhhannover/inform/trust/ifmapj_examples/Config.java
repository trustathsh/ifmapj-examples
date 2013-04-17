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
