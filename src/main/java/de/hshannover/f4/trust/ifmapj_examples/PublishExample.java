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



import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import de.hshannover.f4.trust.ifmapj.IfmapJ;
import de.hshannover.f4.trust.ifmapj.IfmapJHelper;
import de.hshannover.f4.trust.ifmapj.channel.SSRC;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.exception.InitializationException;
import de.hshannover.f4.trust.ifmapj.identifier.AccessRequest;
import de.hshannover.f4.trust.ifmapj.identifier.Device;
import de.hshannover.f4.trust.ifmapj.identifier.Identifiers;
import de.hshannover.f4.trust.ifmapj.identifier.Identity;
import de.hshannover.f4.trust.ifmapj.identifier.IdentityType;
import de.hshannover.f4.trust.ifmapj.identifier.IpAddress;
import de.hshannover.f4.trust.ifmapj.identifier.MacAddress;
import de.hshannover.f4.trust.ifmapj.messages.PublishNotify;
import de.hshannover.f4.trust.ifmapj.messages.PublishRequest;
import de.hshannover.f4.trust.ifmapj.messages.PublishUpdate;
import de.hshannover.f4.trust.ifmapj.messages.Requests;
import de.hshannover.f4.trust.ifmapj.metadata.EnforcementAction;
import de.hshannover.f4.trust.ifmapj.metadata.EventType;
import de.hshannover.f4.trust.ifmapj.metadata.LocationInformation;
import de.hshannover.f4.trust.ifmapj.metadata.Significance;
import de.hshannover.f4.trust.ifmapj.metadata.StandardIfmapMetadataFactory;
import de.hshannover.f4.trust.ifmapj.metadata.WlanSecurityEnum;
import de.hshannover.f4.trust.ifmapj.metadata.WlanSecurityType;

/**
 * A simple exmaple that deomonstrates how to publish metadata to a MAP server
 * by using ifmapj.
 * 
 * @author aw
 * @author ib
 * 
 */
public class PublishExample {
	
	// in order to create the necessary objects, make use of the appropriate
	// factory classes
	private static StandardIfmapMetadataFactory mf = IfmapJ
			.createStandardMetadataFactory();
	
	public void run(String args[]) throws InitializationException {

		System.out.println("====== RUNNING PUBLISH EXAMPLE ======");
		
		// create SSRC using basic authentication
		SSRC ssrc = IfmapJ.createSSRC(
				Config.BASIC_AUTH_SERVER_URL,
				Config.BASIC_AUTH_USER,
				Config.BASIC_AUTH_PASSWORD,
				IfmapJHelper.getTrustManagers(getClass().getResourceAsStream(Config.TRUST_STORE_PATH), Config.TRUST_STORE_PASSWORD));
		System.out.println("Creating SSRC with basic authentication successful");

		try {
			// obtain new session
			System.out.print("newSession ... ");
			ssrc.newSession();
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
	
			// publish access-request-device metadata by using publish update
			System.out.print("publish update access-request-device ... ");
			PublishRequest arDevUpdate = preparePublishAcessRequestDevice();
			ssrc.publish(arDevUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish access-request-ip metadata by using publish update
			System.out.print("publish update access-request-ip ... ");
			PublishRequest arIpUpdate = preparePublishAcessRequestIp();
			ssrc.publish(arIpUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish access-request-mac metadata by using publish update
			System.out.print("publish update access-request-mac ... ");
			PublishRequest arMacUpdate = preparePublishAcessRequestMac();
			ssrc.publish(arMacUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish authenticated-as metadata by using publish update
			System.out.print("publish update authenticated-as ... ");
			PublishRequest authAsUpdate = preparePublishAuthenticatedAs();
			ssrc.publish(authAsUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish authenticated-by metadata by using publish update
			System.out.print("publish update authenticated-by ... ");
			PublishRequest authByUpdate = preparePublishAuthenticatedBy();
			ssrc.publish(authByUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish capability by using publish update
			System.out.print("publish update capability ... ");
			PublishRequest capUpdate = preparePublishCapability();
			ssrc.publish(capUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish device-attribute by using publish update
			System.out.print("publish update device-attribute ... ");
			PublishRequest devAttrUpdate = preparePublishDeviceAttribute();
			ssrc.publish(devAttrUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish device-characteristic by using publish update
			System.out.print("publish update device-characteristic ... ");
			PublishRequest devCharUpdate = preparePublishDeviceCharacteristic();
			ssrc.publish(devCharUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish device-ip by using publish update
			System.out.print("publish update device-ip ... ");
			PublishRequest devIpUpdate = preparePublishDeviceIp();
			ssrc.publish(devIpUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish discovered-by by using publish update
			System.out.print("publish update discovered-by ... ");
			PublishRequest discByUpdate = preparePublishDiscoveredBy();
			ssrc.publish(discByUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish enforcement-report by using publish update
			System.out.print("publish update enforcement-report ... ");
			PublishRequest enfReport = preparePublishEnforcementReport();
			ssrc.publish(enfReport);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish event by using publish notify
			System.out.print("publish notify event ... ");
			PublishRequest eventNotify = preparePublishEvent();
			ssrc.publish(eventNotify);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish ip-mac metadata by using publish update
			System.out.print("publish update ip-mac ... ");
			PublishRequest ipMacUpdate = preparePublishIpMac();
			ssrc.publish(ipMacUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish layer2-information by using publish update
			System.out.print("publish update layer2-information ... ");
			PublishRequest layer2InfUpdate = preparePublishLayer2Information();
			ssrc.publish(layer2InfUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish location by using publish update
			System.out.print("publish update location ... ");
			PublishRequest locationUpdate = preparePublishLocation();
			ssrc.publish(locationUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish request-for-investigation by using publish update
			System.out.print("publish update request-for-investigation ... ");
			PublishRequest rfiUpdate = preparePublishRequestForInvestigation();
			ssrc.publish(rfiUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish role by using publish update
			System.out.print("publish update role ... ");
			PublishRequest roleUpdate = preparePublishRole();
			ssrc.publish(roleUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish wlan-information by using publish update
			System.out.print("publish wlan-information ... ");
			PublishRequest wlanUpdate = preparePublishWlanInformation();
			ssrc.publish(wlanUpdate);
			System.out.println("OK");
			IfmapJExamples.sleepSomeTime();
			
			// publish unexpected-behavior by using publish update
			System.out.print("publish unexpected-behavior ... ");
			PublishRequest ubUpdate = preparePublishUnexpectedBehavior();
			ssrc.publish(ubUpdate);
			System.out.println("OK");
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

	/**
	 * Create a publish update request for access-request-device metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishAcessRequestDevice() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set access-request identifier
		AccessRequest ar = Identifiers.createAr("ar012345678", "de.hshannover.f4.trust");
		update.setIdentifier1(ar);
		// create and set device identifier
		Device dev = Identifiers.createDev("device01");
		update.setIdentifier2(dev);
		// create and set access-request-device metadata
		Document arDev = mf.createArDev();
		update.addMetadata(arDev);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish update request for access-request-ip metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishAcessRequestIp() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set access-request identifier
		AccessRequest ar = Identifiers.createAr("ar012345678", "de.hshannover.f4.trust");
		update.setIdentifier1(ar);
		// create and set ip identifier
		IpAddress ip = Identifiers.createIp4("192.168.0.1");
		update.setIdentifier2(ip);
		// create and set access-request-ip metadata
		Document arIp = mf.createArIp();
		update.addMetadata(arIp);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish update request for access-request-mac metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishAcessRequestMac() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set access-request identifier
		AccessRequest ar = Identifiers.createAr("ar012345678", "de.hshannover.f4.trust");
		update.setIdentifier1(ar);
		// create and set mac identifier
		MacAddress mac = Identifiers.createMac("aa:bb:cc:11:22:33");
		update.setIdentifier2(mac);
		// create and set access-request-mac metadata
		Document arMac = mf.createArMac();
		update.addMetadata(arMac);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish update request for authenticated-as metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishAuthenticatedAs() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set access-request identifier
		AccessRequest ar = Identifiers.createAr("ar012345678", "de.hshannover.f4.trust");
		update.setIdentifier1(ar);
		// create and set identity identifier
		Identity id = Identifiers.createIdentity(IdentityType.userName, "bob", "de.hshannover.f4.trust");
		update.setIdentifier2(id);
		// create and set authenticated-as metadata
		Document authAs = mf.createAuthAs();
		update.addMetadata(authAs);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish update request for authenticated-by metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishAuthenticatedBy() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set access-request identifier
		AccessRequest ar = Identifiers.createAr("ar012345678", "de.hshannover.f4.trust");
		update.setIdentifier1(ar);
		// create and set device identifier
		Device dev = Identifiers.createDev("pdp");
		update.setIdentifier2(dev);
		// create and set authenticated-by metadata
		Document authBy = mf.createAuthBy();
		update.addMetadata(authBy);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish update request for capability metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishCapability() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set access-request identifier
		AccessRequest ar = Identifiers.createAr("ar012345678", "de.hshannover.f4.trust");
		update.setIdentifier1(ar);
		// create and set capability metadata
		Document cap = mf.createCapability("invincible", "de.hshannover.f4.trust");
		update.addMetadata(cap);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish update request for device-attribute metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishDeviceAttribute() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set access-request identifier
		AccessRequest ar = Identifiers.createAr("ar012345678", "de.hshannover.f4.trust");
		update.setIdentifier1(ar);
		// create and set device identifier
		Device dev = Identifiers.createDev("device01");
		update.setIdentifier2(dev);
		// create and set metadata
		Document cap = mf.createDevAttr("a meaningful device attribute");
		update.addMetadata(cap);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish update request for device-characteristic metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishDeviceCharacteristic() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set access-request identifier
		AccessRequest ar = Identifiers.createAr("ar012345678", "de.hshannover.f4.trust");
		update.setIdentifier1(ar);
		// create and set device identifier
		Device dev = Identifiers.createDev("device01");
		update.setIdentifier2(dev);
		// create and set metadata
		Document devChar = mf.createDevChar("Lenovo", "Thinkpad X201", "Ubuntu Linux", "11.04", "laptop", "2011-01-26T19:32:52Z", "my id", "active probing");
		update.addMetadata(devChar);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish update request for device-ip metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishDeviceIp() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set device identifier
		Device dev = Identifiers.createDev("pdp");
		update.setIdentifier1(dev);
		// create and set ip identifier
		IpAddress ip = Identifiers.createIp4("192.168.0.254");
		update.setIdentifier2(ip);
		// create and set metadata
		Document devChar = mf.createDevIp();
		update.addMetadata(devChar);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish update request for discoverey-by metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishDiscoveredBy() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set device identifier
		Device dev = Identifiers.createDev("pdp");
		update.setIdentifier1(dev);
		// create and set ip identifier
		IpAddress ip = Identifiers.createIp4("192.168.0.1");
		update.setIdentifier2(ip);
		// create and set metadata
		Document discoveredBy = mf.createDiscoveredBy();
		update.addMetadata(discoveredBy);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish update request for enforcement-report metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishEnforcementReport() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set device identifier
		Device dev = Identifiers.createDev("pep");
		update.setIdentifier1(dev);
		// create and set ip identifier
		IpAddress ip = Identifiers.createIp4("192.168.0.1");
		update.setIdentifier2(ip);
		// create and set metadata
		Document enfReprt = mf.createEnforcementReport(EnforcementAction.block, null, "suspicious traffic");
		update.addMetadata(enfReprt);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish notify request for event metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishEvent() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish notify element
		PublishNotify notify = Requests.createPublishNotify();
		// create and set ip identifier
		IpAddress ip = Identifiers.createIp4("192.168.0.1");
		notify.setIdentifier1(ip);
		// create and set metadata
		Document event = mf.createEvent("test event", "2011-01-26T19:32:52Z", "my id", 50, 50, Significance.critical, EventType.cve, null, "a test event for cve vulnerability", "http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2011-1866");
		notify.addMetadata(event);
		// add all to the publish request
		ret.addPublishElement(notify);
		return ret;
	}
	
	/**
	 * Create a publish update request for ip-mac metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishIpMac() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set ip identifier
		IpAddress ip = Identifiers.createIp4("192.168.0.1");
		update.setIdentifier1(ip);
		// create and set mac identifier
		MacAddress mac = Identifiers.createMac("aa:bb:cc:11:22:33");
		update.setIdentifier2(mac);
		// create and set ip-mac metadata
		Document ipMac = mf.createIpMac("2011-01-26T19:32:52Z", "2011-01-27T19:32:52Z", "trust-dhcp-server");
		update.addMetadata(ipMac);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish update request for layer2-information metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishLayer2Information() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set access-request identifier
		AccessRequest ar = Identifiers.createAr("ar012345678", "de.hshannover.f4.trust");
		update.setIdentifier1(ar);
		// create and set device identifier
		Device dev = Identifiers.createDev("pep");
		update.setIdentifier2(dev);
		// create and set metadata
		Document layer2 = mf.createLayer2Information(96, "trusted", 12, "de.hshannover.f4.trust");
		update.addMetadata(layer2);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish update request for location metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishLocation() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set identity identifier
		Identity id = Identifiers.createIdentity(IdentityType.userName, "bob", "de.hshannover.f4.trust");
		update.setIdentifier1(id);
		// create and set metadata
		List<LocationInformation> l = new ArrayList<LocationInformation>();
		LocationInformation li1 = new LocationInformation("GPS", "N 52.517526 E 13.44");
		LocationInformation li2 = new LocationInformation("ROOM", "326");
		l.add(li1);
		l.add(li2);		
		Document layer2 = mf.createLocation(l, "2011-01-27T19:32:52Z", "my location sensor");
		update.addMetadata(layer2);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish update request for request-for-investigation metadata.
	 * Note that you SHOULD use notify instead of update.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishRequestForInvestigation() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set device identifier
		Device dev = Identifiers.createDev("pdp");
		update.setIdentifier1(dev);
		// create and set ip identifier
		IpAddress ip = Identifiers.createIp4("192.168.0.1");
		update.setIdentifier2(ip);
		Document rfi = mf.createRequestForInvestigation("a cool qualifier value");
		update.addMetadata(rfi);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	private PublishRequest preparePublishRole() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set access-request identifier
		AccessRequest ar = Identifiers.createAr("ar012345678", "de.hshannover.f4.trust");
		update.setIdentifier1(ar);
		// create and set identity identifier
		Identity id = Identifiers.createIdentity(IdentityType.userName, "bob", "de.hshannover.f4.trust");
		update.setIdentifier2(id);
		// create and set authenticated-as metadata
		Document role = mf.createRole("employee", "de.hshannover.f4.trust");
		update.addMetadata(role);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish update request for wlan-information metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishWlanInformation() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish update element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set access-request identifier
		AccessRequest ar = Identifiers.createAr("ar012345678", "de.hshannover.f4.trust");
		update.setIdentifier1(ar);
		// create and set device identifier
		Device dev = Identifiers.createDev("pep");
		update.setIdentifier2(dev);
		// create and set wlan-information metadata
		WlanSecurityType wlan1 = new WlanSecurityType(WlanSecurityEnum.ccmp, null);
		WlanSecurityType wlan2 = new WlanSecurityType(WlanSecurityEnum.other, "my own wlan security type");
		WlanSecurityType wlan3 = new WlanSecurityType(WlanSecurityEnum.tkip, null);
		List<WlanSecurityType> wlans = new ArrayList<WlanSecurityType>();
		wlans.add(wlan1);
		wlans.add(wlan2);
		wlans.add(wlan3);
		Document wlanInformation = mf.createWlanInformation("eduroam", wlans, wlan2, wlans);
		update.addMetadata(wlanInformation);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
	
	/**
	 * Create a publish update request for unexpected-behavior metadata.
	 * 
	 * @return
	 */
	private static PublishRequest preparePublishUnexpectedBehavior() {
		// create a container for the publish elements (which can be update,
		// delete or notify)
		PublishRequest ret = Requests.createPublishReq();
		// create a publish notify element
		PublishUpdate update = Requests.createPublishUpdate();
		// create and set ip identifier
		IpAddress ip = Identifiers.createIp4("192.168.0.1");
		update.setIdentifier1(ip);
		// create and set metadata
		Document ub = mf.createUnexpectedBehavior("2011-01-26T19:32:52Z", "my id", 50, 50, Significance.critical, "a domain specific type");
		update.addMetadata(ub);
		// add all to the publish request
		ret.addPublishElement(update);
		return ret;
	}
}
