/*
* Copyright (c) 2006-2009 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Raj Kuttan
 * 		Lori Phillips
 */

package edu.harvard.i2b2.eclipse.plugins.fr.ws;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.DTOFactory;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.crc.loader.datavo.i2b2message.ApplicationType;
import edu.harvard.i2b2.crc.loader.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.crc.loader.datavo.i2b2message.FacilityType;
import edu.harvard.i2b2.crc.loader.datavo.i2b2message.MessageControlIdType;
import edu.harvard.i2b2.crc.loader.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.crc.loader.datavo.i2b2message.PasswordType;
import edu.harvard.i2b2.crc.loader.datavo.i2b2message.ProcessingIdType;
import edu.harvard.i2b2.crc.loader.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.crc.loader.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.crc.loader.datavo.i2b2message.SecurityType;

abstract public class CrcRequestData {

	public static final String THIS_CLASS_NAME = CrcRequestData.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);
	public CrcRequestData() {}

	/**
	 * Function to build i2b2 Request message header
	 * 
	 * @return RequestHeader object
	 */
	public RequestHeaderType getRequestHeader() { 
		RequestHeaderType reqHeader = new RequestHeaderType();
		reqHeader.setResultWaittimeMs(2000);
		return reqHeader;
	}
	
	/**
	 * Function to build i2b2 message header
	 * 
	 * @return MessageHeader object
	 */
	public MessageHeaderType getMessageHeader() {
		MessageHeaderType messageHeader = new MessageHeaderType();
		
		messageHeader.setI2B2VersionCompatible(new BigDecimal("1.1"));
		messageHeader.setHl7VersionCompatible(new BigDecimal("2.4"));
		
		ApplicationType appType = new ApplicationType();
		appType.setApplicationName("i2b2 File Repository");
		appType.setApplicationVersion("1.3"); 
		messageHeader.setSendingApplication(appType);
		
		FacilityType facility = new FacilityType();
		facility.setFacilityName("i2b2 Hive");
		messageHeader.setSendingFacility(facility);
		
		ApplicationType appType2 = new ApplicationType();
		appType2.setApplicationVersion("1.3");
		appType2.setApplicationName("File Repository Cell");		
		messageHeader.setReceivingApplication(appType2);
	
		FacilityType facility2 = new FacilityType();
		facility2.setFacilityName("i2b2 Hive");
		messageHeader.setReceivingFacility(facility2);
		
		SecurityType secType = new SecurityType();
		secType.setDomain(UserInfoBean.getInstance().getUserDomain());
		secType.setUsername(UserInfoBean.getInstance().getUserName());
		PasswordType ptype = new PasswordType();
		ptype.setIsToken(UserInfoBean.getInstance().getUserPasswordIsToken());
		ptype.setTokenMsTimeout(UserInfoBean.getInstance().getUserPasswordTimeout());
		ptype.setValue(UserInfoBean.getInstance().getUserPassword());

		secType.setPassword(ptype);
		messageHeader.setSecurity(secType);

		Date currentDate = new Date();
		DTOFactory factory = new DTOFactory();
		messageHeader.setDatetimeOfMessage(factory.getXMLGregorianCalendar(currentDate.getTime()));
		
		MessageControlIdType mcIdType = new MessageControlIdType();
		mcIdType.setInstanceNum(0);
		mcIdType.setMessageNum(generateMessageId());
		messageHeader.setMessageControlId(mcIdType);

		ProcessingIdType proc = new ProcessingIdType();
		proc.setProcessingId("P");
		proc.setProcessingMode("I");
		messageHeader.setProcessingId(proc);
		
		messageHeader.setAcceptAcknowledgementType("AL");
		messageHeader.setApplicationAcknowledgementType("AL");
		messageHeader.setCountryCode("US");
		
		messageHeader.setProjectId(UserInfoBean.getInstance().getProjectId());

		return messageHeader;
	}
	
	/**
	 * Function to generate i2b2 message header message number
	 * 
	 * @return String
	 */
	protected String generateMessageId() {
		StringWriter strWriter = new StringWriter();
		for(int i=0; i<20; i++) {
			int num = getValidAcsiiValue();
			strWriter.append((char)num);
		}
		return strWriter.toString();
	}
	
	/**
	 * Function to generate random number used in message number
	 * 
	 * @return int 
	 */
	private int getValidAcsiiValue() {
		int number = 48;
		while(true) {
			number = 48+(int) Math.round(Math.random() * 74);
			if((number > 47 && number < 58) || (number > 64 && number < 91) 
				|| (number > 96 && number < 123)) {
					break;
				}
		}
		return number;
	}
	/**
	 * Function to convert Ont Request message type to an XML string
	 * 
	 * @param reqMessageType   String containing Ont request message to be converted to string
	 * @return A String data type containing the Ont RequestMessage in XML format
	 */
	public String getXMLString(RequestMessageType reqMessageType) throws JAXBUtilException{ 
		StringWriter strWriter = null;
		try {
			strWriter = new StringWriter();
			edu.harvard.i2b2.crc.loader.datavo.i2b2message.ObjectFactory of = new edu.harvard.i2b2.crc.loader.datavo.i2b2message.ObjectFactory();
			//FRJAXBUtil.getJAXBUtil().marshaller(of.createPublishDataRequest(reqMessageType), strWriter);

			//PublishDataRequestType publishType = (PublishDataRequestType) reqMessageType.getMessageBody().getAny().get(0);
			FRJAXBUtil.getJAXBUtil().marshaller(of.createRequest(reqMessageType), strWriter);
		} catch (JAXBUtilException e) {
			log.error("Error marshalling request message");
			throw e;
		} 
		return strWriter.toString();
	}

	
	
	/**
	 * Function to build Request message type
	 * 
	 * @param messageHeader MessageHeader object  
	 * @param reqHeader     RequestHeader object
	 * @param bodyType      BodyType object 
	 * @return RequestMessageType object
	 */
	public RequestMessageType getRequestMessageType(MessageHeaderType messageHeader,
			RequestHeaderType reqHeader, BodyType bodyType) { 
		RequestMessageType reqMsgType = new RequestMessageType();
		reqMsgType.setMessageHeader(messageHeader);
		reqMsgType.setMessageBody(bodyType);
		reqMsgType.setRequestHeader(reqHeader);
		return reqMsgType;
	}
	
}