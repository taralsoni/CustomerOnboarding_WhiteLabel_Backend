package com.ofss.fcubs.gw.ws;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;

import com.afrAsia.entities.masters.RMDetails;
import com.afrAsia.entities.request.MobCreateCustomerSOAPRequest;
import com.afrAsia.entities.transactions.MainTableCompositePK;
import com.afrAsia.entities.transactions.MobApplicantAdditionalDtl;
import com.google.gson.Gson;
import com.ofss.fcubs.gw.ws.types.FCUBSCustomerService;
import com.ofss.fcubs.gw.ws.types.FCUBSCustomerServiceSEI;
import com.ofss.fcubs.service.fcubsaccservice.CREATECUSTACCFSFSREQ;
import com.ofss.fcubs.service.fcubscustomerservice.CREATECUSTOMERFSFSREQ;
import com.ofss.fcubs.service.fcubscustomerservice.CREATECUSTOMERFSFSRES;
import com.ofss.fcubs.service.fcubscustomerservice.CustjointFullType;
import com.ofss.fcubs.service.fcubscustomerservice.CustjointFullType.Custjoint;
import com.ofss.fcubs.service.fcubscustomerservice.CustmisFullType;
import com.ofss.fcubs.service.fcubscustomerservice.CustmisFullType.Compositemis;
import com.ofss.fcubs.service.fcubscustomerservice.CustmisFullType.Customermis;
import com.ofss.fcubs.service.fcubscustomerservice.CustomerFullType;
import com.ofss.fcubs.service.fcubscustomerservice.CusttextFullType;
import com.ofss.fcubs.service.fcubscustomerservice.ERRORType;
import com.ofss.fcubs.service.fcubscustomerservice.FCUBSHEADERType;
import com.ofss.fcubs.service.fcubscustomerservice.LinkedEntityFullType;
import com.ofss.fcubs.service.fcubscustomerservice.LinkedEntityFullType.RelationshipLinkage;
import com.ofss.fcubs.service.fcubscustomerservice.MsgStatType;
import com.ofss.fcubs.service.fcubscustomerservice.UBSCOMPType;
import com.ofss.fcubs.service.fcubscustomerservice.UDFDETAILSType2;
import com.ofss.fcubs.service.fcubscustomerservice.WARNINGType;

 public class CreateCustomerSOAP implements CreateCustomerSOAPConstants {
	 final static Logger debugLog = Logger.getLogger("debugLogger");
		final static Logger infoLog = Logger.getLogger("infoLogger");
		final static Logger errorLog = Logger.getLogger("errorLogger");
    static SecureRandom rnd = new SecureRandom();
	
    public Map<String,Object> createAfrAsiaCustomer(String userId,MobCreateCustomerSOAPRequest mobCreateCustomerSOAPRequest,MobCreateCustomerSOAPRequest mobApplicantGuardianDetails, RMDetails rmDetails, String compflexID) throws Exception	{
		infoLog.info("===> createAfrAsiaCustomer start ");
		
		String firstName = getFirstName(mobCreateCustomerSOAPRequest);
		String middleName = getMaidenName(mobCreateCustomerSOAPRequest);
		String lastName = getLastName(mobCreateCustomerSOAPRequest);
		
		CREATECUSTOMERFSFSREQ requestMsg = new CREATECUSTOMERFSFSREQ();
				
		// set the header value
		//-------------- header start-------------------
		FCUBSHEADERType headerValue = getSOAPRequestHeader(compflexID);
		
		//-------------- header end-------------------
		// set the 9 request to header value for create customer 
		requestMsg.setFCUBSHEADER(headerValue);
		
		//============================set the request body=====================================//
		// set the request body
		//--------------start-------------------
		CustomerFullType customerFT = new CustomerFullType();
		
		//  set custno String value  <fcub:CUSTNO>?</fcub:CUSTNO> | blank
		customerFT.setCUSTNO(BLANK);
		
		// set the ctype String value  <fcub:CTYPE>?</fcub:CTYPE> | I
		customerFT.setCTYPE(CTYPE);
		
		// set the name String value  <fcub:NAME>?</fcub:NAME> | FIRST_NAME MAIDEN_NAME LAST_NAME
		//Limited to 105 characters. Truncate rest | MOB_APPLICANT_PERSONAL_DETAILS | 105
		String fullName = getFullName(mobCreateCustomerSOAPRequest);
		customerFT.setNAME(fullName);
		
		String mailAddr1 = null;
		String mailAddr2 = null;
		String mailAddr3 = null;
		String mailCity = null;
		String mailCountry = null;
		
		if( null != mobApplicantGuardianDetails){
			 mailAddr1 = mobApplicantGuardianDetails.getMobApplicantCommDetail().getMailAddr1();
			 mailAddr2 = mobApplicantGuardianDetails.getMobApplicantCommDetail().getMailAddr2();
			 mailAddr3 = mobApplicantGuardianDetails.getMobApplicantCommDetail().getMailAddr3();
			 mailCity = mobApplicantGuardianDetails.getMobApplicantCommDetail().getMailCity();
			 mailCountry = mobApplicantGuardianDetails.getMobApplicantCommDetail().getMailCountry();
		}
		else{
			 mailAddr1 = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getMailAddr1();
			 mailAddr2 = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getMailAddr2();
			 mailAddr3 = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getMailAddr3();
			 mailCity = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getMailCity();
			 mailCountry = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getMailCountry();
		}
		if(MUR.equals(mailCountry))
		{
			mailCity=mailCity+","+MAURITIUS;
		}
		mailAddr1=substractChar(MAX_NAME_CHAR,mailAddr1);
		mailAddr2=substractChar(MAX_NAME_CHAR,mailAddr2);
		mailAddr3=substractChar(MAX_NAME_CHAR,mailAddr3);
		mailCity=substractChar(MAX_NAME_CHAR,mailCity);
		mailCountry=substractChar(3,mailCountry);
       // set the addrln1 String value <fcub:ADDRLN1>?</fcub:ADDRLN1> | PERM_ADDR_1 | 
	   // MOB_APPLICANT_COMM_DETAILS | 105
		String permAddr1 = null;
		if(null != mobApplicantGuardianDetails){
			permAddr1 = mobApplicantGuardianDetails.getMobApplicantCommDetail().getPermAddr1();
		}
		else{
			permAddr1 = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getPermAddr1();
		}
		
		permAddr1 = substractChar(MAX_NAME_CHAR, permAddr1);
		customerFT.setADDRLN1(mailAddr1);
		
	    // set the addrln2 String value <fcub:ADDRLN2>?</fcub:ADDRLN2> | PERM_ADDR_2 | 
		// MOB_APPLICANT_COMM_DETAILS | 105
	    String permAddr2 = null;
	    if(null != mobApplicantGuardianDetails){
	    	permAddr2 = mobApplicantGuardianDetails.getMobApplicantCommDetail().getPermAddr2();
		}
		else{
			permAddr2 = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getPermAddr2();
		}
		permAddr2 = substractChar(MAX_NAME_CHAR, permAddr2);
		customerFT.setADDRLN2(mailAddr2);
		
		// set the addrln3 String value <fcub:ADDRLN3>?</fcub:ADDRLN3> | PERM_ADDR_3 | 
		// MOB_APPLICANT_COMM_DETAILS | 105
		String permAddr3 = null;
		if(null != mobApplicantGuardianDetails){
			permAddr3 = mobApplicantGuardianDetails.getMobApplicantCommDetail().getPermAddr3();
		}
		else{
			permAddr3 = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getPermAddr3();
		}
		permAddr3 = substractChar(MAX_NAME_CHAR, permAddr3);
		
		if(null==customerFT.getADDRLN2() || BLANK.equals(customerFT.getADDRLN2()))
		{
			customerFT.setADDRLN2(mailAddr3);
		}
		else
		{
			customerFT.setADDRLN3(mailAddr3);
		}
		// set the addrln4 String value <fcub:ADDRLN4>?</fcub:ADDRLN4> | PERM_CITY | 
		// MOB_APPLICANT_COMM_DETAILS | 105
		String permCity = null;
		 if(null != mobApplicantGuardianDetails){
			 permCity = mobApplicantGuardianDetails.getMobApplicantCommDetail().getPermCity();
			}
			else{
			 permCity = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getPermCity();
			}
		 
		 String permCountry = null;
		 if(null != mobApplicantGuardianDetails){
			 
			 permCountry = mobApplicantGuardianDetails.getMobApplicantCommDetail().getPermCountry();
			}
			else{
				permCountry = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getPermCountry();
			}
		if(MUR.equals(permCountry))
		{
			permCity = permCity+","+MAURITIUS;
		}
		permCity = substractChar(MAX_NAME_CHAR, permCity);
		if(null==customerFT.getADDRLN2()|| BLANK.equals(customerFT.getADDRLN2()))
		{
			customerFT.setADDRLN2(mailCity);
		}
		else if(null==customerFT.getADDRLN3() || BLANK.equals(customerFT.getADDRLN3()))
		{
			customerFT.setADDRLN3(mailCity);
		}
		else
		{
			customerFT.setADDRLN4(mailCity);
		}
			
		// set the country String value  <fcub:COUNTRY>?</fcub:COUNTRY> | PERM_COUNTRY | 
		// MOB_APPLICANT_COMM_DETAILS | 3
			
		permCountry = substractChar(3, permCountry);
		customerFT.setCOUNTRY(mailCountry);
				
		// set the sname String value <fcub:SNAME>?</fcub:SNAME> | [FN LN] limited to 17 characters
		// If duplicate SN service will return error | 17
		
		if(null != mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getShortName()){
			customerFT.setSNAME( mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getShortName());
		}
		else{
			
			String fName = firstName != null ? firstName : BLANK ;
			String lName = lastName != null ? lastName : BLANK;
			String sName = lName + fName;
			sName = substractChar(17, sName);
			debugLog.debug("sName:"+sName);
			customerFT.setSNAME(sName);
		}
		
		// set the nlty String value <fcub:NLTY>?</fcub:NLTY> | NATIONALITY | 
		// MOB_APPLICANT_PERSONAL_DETAILS | 3
		String nationality = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getNationality();
		nationality = substractChar(3, nationality);		
		customerFT.setNLTY(nationality);
		
		// set the nlty String value  <fcub:LBRN>?</fcub:LBRN>  | Default - 001 | 
		 customerFT.setLBRN(DEFAULT_LBRN); 
		
		// set the ccateg String value <fcub:CCATEG>?</fcub:CCATEG>  | Indicates customer category | ? | 10
		/*String residencyStatusVal = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getResidencyStatus();
		
		if( null != residencyStatusVal && !residencyStatusVal.isEmpty()){
			customerFT.setCCATEG(residencyStatusVal.toUpperCase());
		}
		else{
			customerFT.setCCATEG(BLANK);
		}*/
		customerFT.setCCATEG(CCATEG_RESIDENT);
		// set the fullname String value <fcub:FULLNAME>?</fcub:FULLNAME> | FIRST_NAME<space>LAST_NAME | 
		// MOB_APPLICANT_PERSONAL_DETAILS | 105
		//String personFullName = firstName != null ? firstName : BLANK + " " + lastName != null ? lastName : BLANK;
		String personFullName = getFullName(mobCreateCustomerSOAPRequest);
		personFullName = substractChar(105,personFullName);
		customerFT.setFULLNAME(personFullName);	
		
		// set the expcntry String value  <fcub:EXPCNTRY>?</fcub:EXPCNTRY> 
		// -  foreign resident country code (ignore in case of Mauritain resident) - ? | OSEAS_COUNTRY | 
		// MOB_APPLICANT_ADDITIONAL_DTLS | 3
		String oseasCountry = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getOseasCountry();
		oseasCountry = oseasCountry != null ? oseasCountry : BLANK;
		customerFT.setEXPCNTRY(oseasCountry);	
		
		// set the uidname String value  <fcub:UIDNAME>?</fcub:UIDNAME> - master table for the name
		// (if you select passport, passport should be sent in this key) 
		// set the uidval String value <fcub:UIDVAL>?</fcub:UIDVAL> - value
		String passportNoVal = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getPassportNo();
		if( null != passportNoVal && !passportNoVal.isEmpty()){
			customerFT.setUIDNAME(UIDNAME_PASSPORT);
			customerFT.setUIDVAL(passportNoVal);
		}
		else{
			customerFT.setUIDNAME(UIDNAME_IDENTITY_CARD);
			String nicVal = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getNic();
			customerFT.setUIDVAL(nicVal != null ? nicVal : BLANK);
		}
		
		// set the media String value <fcub:MEDIA>?</fcub:MEDIA> - Default to "MAIL" (case sensitive) | MAIL
		//customerFT.setMAILRSREQD("MAIL");
		customerFT.setMEDIA(DEFAULT_MEDIA);
		
		// set the ssn String value <fcub:SSN>?</fcub:SSN> - SSN for FATCA compliance  else ignore | 
		// Only if US_CITIZEN = true or INCOME_US_TAXABLE = true US_SSN | MOB_APPLICANT_ADDITIONAL_DTLS | 11
		Boolean usCitizen = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getUsCitizen();
		Boolean incomeUsTaxable = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getIncomeUsTaxable();
		if(usCitizen || incomeUsTaxable ){
			String usSsn = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getUsSsn();
			customerFT.setSSN(usSsn);
		}
				
		// set the loc String value  <fcub:LOC>?</fcub:LOC> | OTH
		customerFT.setLOC(DEFAULT_LOC);
	   // set the xref String value <fcub:XREF>?</fcub:XREF> - Application Reference Number | ID | MOB_APPLICANT_PERSONAL_DETAILS
		MainTableCompositePK id = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getId();
		customerFT.setXREF(Long.toString(id.getApplicantId()));
		
		// set the createacc String value  <fcub:CREATEACC>?</fcub:CREATEACC>  | Y if account to be created else N
		customerFT.setCREATEACC(N);//?
		
		// set the tracklimits String value  <fcub:TRACK_LIMITS>?</fcub:TRACK_LIMITS> - Default it to Y | Y
		customerFT.setTRACKLIMITS(Y);	   
		
		// set the lmccy String value  <fcub:LMCCY>?</fcub:LMCCY> Default it to MUR | MUR
		customerFT.setLMCCY(MUR);	 
		
		// set the String value  <fcub:RM_ID>?</fcub:RM_ID> - RM user id of Flexcube 
		// which is different from AD ID. At backend, we need to maintain the master of RM AD ID
		// to RM Flexcube ID and pass RM Flexcube ID here | Flex ID of RM | RM_ID | MOB_RM_APP_REF_ID
		/*String rmUsedId = mobCreateCustomerSOAPRequest.getMobRmAppRefId().getRmUsedId();
		rmUsedId = rmUsedId != null ? rmUsedId : BLANK ;
		customerFT.setRMID("SEW");*/
		customerFT.setRMID(rmDetails.getFlex_Id());
		
		//=======CustomerFullType.Custpersonal ===================//
		CustomerFullType.Custpersonal custpersonal = new CustomerFullType.Custpersonal();
		//CustomerFullType.Custpersonal custpersonal = customerFT.getCustpersonal();
		//   <fcub:FSTNAME>?</fcub:FSTNAME> | FIRST_NAME | MOB_APPLICANT_PERSONAL_DETAILS
		custpersonal.setFSTNAME(firstName);
	//   <fcub:MIDNAME>?</fcub:MIDNAME> | MAIDEN_NAME | MOB_APPLICANT_PERSONAL_DETAILS 
		custpersonal.setMIDNAME(middleName);
	//   <fcub:LSTNAME>?</fcub:LSTNAME> | LAST_NAME | MOB_APPLICANT_PERSONAL_DETAILS
		custpersonal.setLSTNAME(lastName);	
		
	//   <fcub:DOB>?</fcub:DOB> YYYY-MM-DD | DOB | MOB_APPLICANT_PERSONAL_DETAILS
		Date applicantBob = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getDob();
		GregorianCalendar gcal = new GregorianCalendar();
		if( null != applicantBob){
		gcal.setTime(applicantBob);
		XMLGregorianCalendar appDob = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
		/*appDob.setYear(gcal.get(GregorianCalendar.YEAR));
		appDob.setMonth(gcal.get(GregorianCalendar.MONTH));
		appDob.setDay(gcal.get(GregorianCalendar.DAY_OF_MONTH));*/
		appDob.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
		appDob.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		custpersonal.setDOB(appDob);
		}
		// <fcub:SEX>?</fcub:SEX> M/F |?
		String sex = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getSex();
		custpersonal.setSEX(sex);
	// <fcub:NATIONID>?</fcub:NATIONID> NIC for mauritial resident, 
	// Passport for foreign resident or mauritial resident not having NIC | NIC/ PASSPORT_NO | 
		//MOB_APPLICANT_PERSONAL_DETAILS | 105
		String passportNo = passportNoVal;
		String nic = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getNic();
		if(nationality.equalsIgnoreCase(MUR) && nic != null && !nic.isEmpty()){
			custpersonal.setNATIONID(nic);
		}/*else{
			custpersonal.setNATIONID(passportNo);
		}*/
	// <fcub:PPTNO>?</fcub:PPTNO> Passport Number for foreign resident (mandatory)
	// - Non-mandatory for mauritial resident with NIC | PASSPORT_NO | MOB_APPLICANT_PERSONAL_DETAILS
		
		if(nationality.equalsIgnoreCase(MUR) && nic != null && !nic.isEmpty()){
			// passport not require
		}else{
			custpersonal.setPPTNO(passportNo);
		}
		
		// <fcub:PPTISSDT>?</fcub:PPTISSDT> Issue date (same as above)  yyyy-mm-dd	|MOB_APPLICANT_PERSONAL_DETAILS	Date in yyyy-mm-dd format						
		// newly added
		if( null != passportNo && !passportNo.isEmpty()){	
			custpersonal.setPPTNO(passportNo);
			/*SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date passportIssueDate = null ;
			try {
				passportIssueDate = sdf.parse("01/04/2017");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			Date passportIssueDate = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getPassportIssueDate();
			if( null != passportIssueDate){
				gcal.setTime(passportIssueDate);
				XMLGregorianCalendar passIssueDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
				/*passIssueDate.setYear(gcal.get(GregorianCalendar.YEAR));
				passIssueDate.setMonth(gcal.get(GregorianCalendar.MONTH));
				passIssueDate.setDay(gcal.get(GregorianCalendar.DAY_OF_MONTH));*/
				passIssueDate.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
				passIssueDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
				custpersonal.setPPTISSDT(passIssueDate);
			}
		}
		
		/*custpersonal.setPPTISSDT(value);*/
	 //=  <fcub:PPTEXPDT>?</fcub:PPTEXPDT> expiry date (same as above) yyyy-mm-dd | PASSPORT_EXPIRY_DATE | 
		// MOB_APPLICANT_PERSONAL_DETAILS	| Date in yyyy-mm-dd format
		
		if( null != passportNo && !passportNo.isEmpty()){		
			Date passportExpiryDate = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getPassportExpiryDate();
			if( null != passportExpiryDate){
				gcal.setTime(passportExpiryDate);
				XMLGregorianCalendar passExDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
				/*passExDate.setYear(gcal.get(GregorianCalendar.YEAR));
				passExDate.setMonth(gcal.get(GregorianCalendar.MONTH));
				passExDate.setDay(gcal.get(GregorianCalendar.DAY_OF_MONTH));*/
				passExDate.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
				passExDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
				custpersonal.setPPTEXPDT(passExDate);
			}
		}
		//<fcub:SAME_PERM_ADDR>?</fcub:SAME_PERM_ADDR> Y - Next DADD1 will be same as Per Address	
		// IS_MAIL_PERM_SAME	MOB_APPLICANT_COMM_DETAILS	Boolean Y/N
		Boolean isMailPermSame = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getIsMailPermSame();
		String mailPermSame = N;
		if( null != isMailPermSame && isMailPermSame){
			mailPermSame= Y;
		}
		custpersonal.setSAMEPERMADDR(mailPermSame);
		
		
		
		Long telNoHomeCc = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getTelNoHomeCc();
		Long mobNoCc = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getMobNoCc();
		Long faxNoCc = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getFaxNoCc();
		Long telNoHome = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getTelNoHome();
		Long faxNo = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getFaxNo();
		Long mobNo = mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getMobNo();
		
		//custpersonal.setDADD1(mailAddr1);
		custpersonal.setDADD1(permAddr1);
		custpersonal.setDADD2(permAddr2);
		if(null==custpersonal.getDADD2()|| BLANK.equals(custpersonal.getDADD2()))
		{
			custpersonal.setDADD2(permAddr3);
		}
		else
		{
			custpersonal.setDADD3(permAddr3);
		}
		
		if(null==custpersonal.getDADD2()|| BLANK.equals(custpersonal.getDADD2()))
		{
			custpersonal.setDADD2(permCity);
		}
		else if(null==custpersonal.getDADD3()|| BLANK.equals(custpersonal.getDADD3()))
		{
			custpersonal.setDADD3(permCity);
		}
		else
		{
			custpersonal.setDADD4(permCity);
		}
		custpersonal.setDCNTRY(permCountry);
		
		
		// <fcub:TELISDNOPERSONAL>?</fcub:TELISDNOPERSONAL> | TEL_NO_HOME_CC |MOB_APPLICANT_COMM_DETAILS
		//custpersonal.setTELISDNOPERSONAL(new BigDecimal(telNoHomeCc != null && telNoHomeCc !=0 ? telNoHomeCc : 230));
		//Changed by Avisha
		if(telNoHomeCc != null && telNoHomeCc !=0)
		{
			custpersonal.setTELISDNOPERSONAL(new BigDecimal(telNoHomeCc));
		}
		//<fcub:MOBISDNO>?</fcub:MOBISDNO> | MOB_NO_CC | MOB_APPLICANT_COMM_DETAILS							
		//custpersonal.setMOBISDNO(new BigDecimal(mobNoCc != null && mobNoCc !=0 ? mobNoCc : 230));
		if(mobNoCc != null && mobNoCc !=0)
		{
			custpersonal.setMOBISDNO(new BigDecimal(mobNoCc));
		}
		// <fcub:FAXISDNO>?</fcub:FAXISDNO> | FAX_NO_CC	| MOB_APPLICANT_COMM_DETAILS	
		// Default 230 
		// edited
		//custpersonal.setFAXISDNO(new BigDecimal(faxNoCc != null && faxNoCc !=0 ? faxNoCc : 230));
		if(faxNoCc != null && faxNoCc !=0)
		{
			custpersonal.setFAXISDNO(new BigDecimal(faxNoCc));
		}
		// <fcub:TELEPHNO>?</fcub:TELEPHNO> | TEL_NO_HOME | MOB_APPLICANT_COMM_DETAILS | 105
		if( null != telNoHome && telNoHome.intValue() != 0){
			custpersonal.setTELEPHNO(telNoHome.toString());
		}
		/*else{
			custpersonal.setTELEPHNO(BLANK);
		}*/
		//<fcub:FAXNUMBER>?</fcub:FAXNUMBER> - Fax NUmber (Non mandatory) | 
		// FAX_NO	MOB_APPLICANT_COMM_DETAILS	105	
		if( null != faxNo && faxNo.intValue() != 0){
			custpersonal.setFAXNUMBER(faxNo.toString());
		}
      //  <fcub:MOBNUM>?</fcub:MOBNUM> - Mobile number |MOB_NO|MOB_APPLICANT_COMM_DETAILS|105	
		if( null != mobNo && mobNo.intValue() != 0){
			custpersonal.setMOBNUM(mobNo.toString());
		}
		else{
			custpersonal.setMOBNUM(BLANK);
		}
		
	 // <fcub:EMAILID>?</fcub:EMAILID> - Email ID 	|EMAIL|MOB_APPLICANT_PERSONAL_DETAILS|255						
		String email = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getEmail();
		custpersonal.setEMAILID(substractChar(255,email));
	//  <fcub:SAME_CORR_ADDR>?</fcub:SAME_CORR_ADDR> 	| Y								
		custpersonal.setSAMECORRADDR(mailPermSame);
		
		/*mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getPermAddr1();
		mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getPermAddr2();
		mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getPermAddr3();
		mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getPermCity();
		mobCreateCustomerSOAPRequest.getMobApplicantCommDetail().getPermCountry();
		*/
		
		//<fcub:PADD1>?</fcub:PADD1>	PERM_ADDR_1	MOB_APPLICANT_COMM_DETAILS					
		custpersonal.setPADD1(permAddr1);
       // <fcub:PADD3>?</fcub:PADD3>	PERM_ADDR_3	MOB_APPLICANT_COMM_DETAILS							
		//custpersonal.setPADD3(permAddr3);
      //  <fcub:PADD2>?</fcub:PADD2>	PERM_ADDR_2	MOB_APPLICANT_COMM_DETAILS							
		custpersonal.setPADD2(permAddr2);
      //  <fcub:PADD4>?</fcub:PADD4>	PERM_CITY	MOB_APPLICANT_COMM_DETAILS							
		//custpersonal.setPADD4(permCity);
     //   <fcub:PCNTRY>?</fcub:PCNTRY>	PERM_COUNTRY	MOB_APPLICANT_COMM_DETAILS
		
		if(null==custpersonal.getPADD2()|| BLANK.equals(custpersonal.getPADD2()))
		{
			custpersonal.setPADD2(permAddr3);
		}
		else
		{
			custpersonal.setPADD3(permAddr3);
		}
		
		if(null==custpersonal.getPADD2()|| BLANK.equals(custpersonal.getPADD2()))
		{
			custpersonal.setPADD2(permCity);
		}
		else if(null==custpersonal.getPADD3()|| BLANK.equals(custpersonal.getPADD3()))
		{
			custpersonal.setPADD3(permCity);
		}
		else
		{
			custpersonal.setPADD4(permCity);
		}
		
		custpersonal.setPCNTRY(permCountry);
		
			
        
	//  <fcub:CUST_COMM_MODE>?</fcub:CUST_COMM_MODE> "PREF_COMM_MODE | If email, value = E 
		//| If SMS value = S"	MOB_ACCOUNT_ADDITIONAL_DETAILS	M or E 						
		String prefCommMode = mobCreateCustomerSOAPRequest.getMobAccountAdditionalDetail().getPrefCommMode();
		if( null != prefCommMode && SMS.equalsIgnoreCase(prefCommMode)){
			prefCommMode = SMS_KEY;
		}
		else if( null != prefCommMode && EMAIL.equalsIgnoreCase(prefCommMode)){
			prefCommMode = EMAIL_KEY;
		}
		
		custpersonal.setCUSTCOMMMODE(prefCommMode);
		
	// <fcub:RESSTATUS>?</fcub:RESSTATUS> - R for Resident 	"R- Resident / N - Nonresident  | RESIDENCY_STATUS"
	//	MOB_APPLICANT_PERSONAL_DETAILS	"NON_RES and Resident is stored in table.R = Resident N = Non resident"						
		/*String residencyStatus = residencyStatusVal;
		if( null != residencyStatus && residencyStatus.equalsIgnoreCase(RESIDENCYSTATUSVAL)){
			residencyStatus = N;
		}else if( null != residencyStatus && residencyStatus.equalsIgnoreCase(CCATEG_RESIDENT)){
			residencyStatus = DEFAULT_RESISTAT;
		}
		custpersonal.setRESSTATUS(residencyStatus);*/
		custpersonal.setRESSTATUS(DEFAULT_RESISTAT);
		
	// <fcub:LANG>?</fcub:LANG> - Default to ENG |Default to "ENG"								
		custpersonal.setLANG("ENG");
		
	// <fcub:MINOR>?</fcub:MINOR> 	|IS_MINOR	|MOB_APPLICANT_PERSONAL_DETAILS	|Y/N	
		 Boolean isMinor = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getIsMinor();
			
		
	//<fcub:GUARDIAN>?</fcub:GUARDIAN> FIRST_NAME<space> LAST_NAME	"MOB_APPLICANT_PERSONAL_DETAILS
	//	Get guardian reference no from MOB_ACCOUNt_DETAILS table"|	105		
	 if(isMinor){
			String grdFirstName = mobApplicantGuardianDetails.getMobApplicantPersonalDetail().getFirstName();
			grdFirstName = grdFirstName != null ? grdFirstName : BLANK;
			String grdLastName = mobApplicantGuardianDetails.getMobApplicantPersonalDetail().getLastName();
			grdLastName = grdLastName != null ? grdLastName : BLANK;
			String grdFullName = grdFirstName + " " +grdLastName;
			custpersonal.setGUARDIAN(substractChar(MAX_NAME_CHAR,grdFullName));
		 }
		
	// <fcub:SBMTAGEPROOF>?</fcub:SBMTAGEPROOF> 	Default to "N"							
		custpersonal.setSBMTAGEPROOF(N);
		
	// <fcub:MINOR>?</fcub:MINOR> 	|IS_MINOR	|MOB_APPLICANT_PERSONAL_DETAILS	|Y/N	
	   custpersonal.setMINOR(isMinor != true ? N : Y);
					
		/*custpersonal.setKYCSTAT(value);*/
    // <fcub:SSTAFF>?</fcub:SSTAFF> |IS_EMPLOYEE | MOB_APPLICANT_PERSONAL_DETAILS |Y/N		
		 boolean isEmployee = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().isEmployee();
		 custpersonal.setSSTAFF(isEmployee != true ? N : Y);
		/*custpersonal.setKYCREFNO(value);*/
	// <fcub:TITLE>?</fcub:TITLE> 	|TITLE	|MOB_APPLICANT_PERSONAL_DETAILS		
		 String title = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getTitle();
		 custpersonal.setTITLE(title != null ? title.trim().toUpperCase() : BLANK);
		
		/*custpersonal.setPLACEOFBIRTH(value);*/
	// <fcub:BIRTHCOUNTRY>?</fcub:BIRTHCOUNTRY> - Country code 	|COUNTRY_BIRTH	|MOB_APPLICANT_PERSONAL_DETAILS|3						
		 String countryBirth = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getCountryBirth();
		 custpersonal.setBIRTHCOUNTRY(countryBirth != null ? substractChar(3,countryBirth) : BLANK);
		
		//================CustomerFullType.Custpersonal.Custdomestic=============
		CustomerFullType.Custpersonal.Custdomestic custdomestic = new CustomerFullType.Custpersonal.Custdomestic();
	//  <fcub:CUSTNO>?</fcub:CUSTNO> - Default it to Blank	Blank								
		custdomestic.setCUSTNO(BLANK);
		
	//  <fcub:MARITALSTAT>?</fcub:MARITALSTAT> - Marital status code as per master	"Indicates the Marital Status.
	//	S-Single,M-Married,D-Divorced,R-Remarried,P-Separated,E-Spouse Expired. |MARITAL_STATUS"	
	//  MOB_APPLICANT_PERSONAL_DETAILS	enumeration^S,M,D,R,P,E			
		String maritalStatus = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getMaritalStatus();
		//Map<String, String> maritalStatusMap = getMaritalStatus();
		if(maritalStatus != null){
			//maritalStatus = maritalStatusMap.get(maritalStatus.toUpperCase());
			custdomestic.setMARITALSTAT(maritalStatus.trim().toUpperCase());
		}
		//================
		custpersonal.setCustdomestic(custdomestic);
	//=============== CustomerFullType.Custpersonal.Custprof ==============
		CustomerFullType.Custpersonal.Custprof custprof = new CustomerFullType.Custpersonal.Custprof();
   //     <fcub:CUSTNO>?</fcub:CUSTNO> - Deafult to blank	Blank		
		custprof.setCUSTNO(BLANK);
	//  <fcub:EMPSTAT>?</fcub:EMPSTAT> - Employment master code 	
	//	"Indicates the Employment Status. F-Full-time permanent,T-Full-time temporary,U-Unemployed,
	//	S-Self Employed,P-Part-time,R-Retired-pensioned,N-Retired non-pensioned,O-Other
	//	EMPLOYMENT_STS"	MOB_APPLICANT_EMPLOYMENT_DTLS	enumeration^F,T,U,S,P,R,N,O						
		String employmentSts = mobCreateCustomerSOAPRequest.getMobApplicantEmploymentDtl().getEmploymentSts();
		//Map<String, String> employmentStatusMap = getEmploymentStatus();
		if( null != employmentSts){
			//employmentSts = employmentStatusMap.get(employmentSts.toUpperCase());
			custprof.setEMPSTAT(employmentSts.trim().toUpperCase());
		}
    //  <fcub:EMPTENURE>?</fcub:EMPTENURE> - No of years - 2 digits - employment 
	//	tenure	|NO_OF_YEARS_SERVICE	|MOB_APPLICANT_EMPLOYMENT_DTLS	|Should be less than 99						
		Long noOfYearsService = mobCreateCustomerSOAPRequest.getMobApplicantEmploymentDtl().getNoOfYearsService();
		noOfYearsService = noOfYearsService != null ?  noOfYearsService : 0;
		custprof.setEMPTENURE(new BigDecimal(noOfYearsService));
	// <fcub:CURRDESIG>?</fcub:CURRDESIG> - 	CURR_OCCUPATION	|MOB_APPLICANT_EMPLOYMENT_DTLS	
		String currOccupation = mobCreateCustomerSOAPRequest.getMobApplicantEmploymentDtl().getCurrOccupation();
		custprof.setCURRDESIG(currOccupation);
		
	// <fcub:CURREMP>?</fcub:CURREMP> - Ignore	EMPLOYER_NAME, if EMPSTAT - F(Full time)
	//	MOB_APPLICANT_EMPLOYMENT_DTLS							
		custprof.setCURREMP(BLANK);
	
	// <fcub:ADD1>?</fcub:ADD1> - Office address line 1	EMPLOYER_ADDR_1	MOB_APPLICANT_EMPLOYMENT_DTLS	
		String employerAddr1 = mobCreateCustomerSOAPRequest.getMobApplicantEmploymentDtl().getEmployerAddr1();
		custprof.setADD1(employerAddr1);
		
	//  <fcub:ADD2>?</fcub:ADD2>	EMPLOYER_ADDR_2	MOB_APPLICANT_EMPLOYMENT_DTLS	
		String employerAddr2 = mobCreateCustomerSOAPRequest.getMobApplicantEmploymentDtl().getEmployerAddr2();
		custprof.setADD2(employerAddr2);
		
	//  <fcub:ADD3>?</fcub:ADD3>	EMPLOYER_CITY	MOB_APPLICANT_EMPLOYMENT_DTLS	
		String employerCity = mobCreateCustomerSOAPRequest.getMobApplicantEmploymentDtl().getEmployerCity();
		if(MUR.equals(mobCreateCustomerSOAPRequest.getMobApplicantEmploymentDtl().getEmployerCountry()))
		{
			employerCity=employerCity+","+MAURITIUS;
		}
		if(null==custprof.getADD2()|| BLANK.equals(custprof.getADD2()))
		{
			custprof.setADD2(employerCity);
		}
		else{
			custprof.setADD3(employerCity);
		}
    //  <fcub:AMTCCY1>?</fcub:AMTCCY1> - Currency code for monthly net income - default to MUR	MUR		
		custprof.setAMTCCY1(MUR);
		
	//  <fcub:SALARY>?</fcub:SALARY> - Monthly net income (up to 22 digits)	NET_MONTHLY_INCOME	
    //	MOB_APPLICANT_EMPLOYMENT_DTLS		
		Long netMonthlyIncome = mobCreateCustomerSOAPRequest.getMobApplicantEmploymentDtl().getNetMonthlyIncome();
		
		if( null != netMonthlyIncome && netMonthlyIncome.longValue() != 0){
			custprof.setSALARY(new BigDecimal(netMonthlyIncome));
		}
	//  <fcub:CNTY>?</fcub:CNTY> - Country code employer |EMPLOYER_COUNTRY	|MOB_APPLICANT_EMPLOYMENT_DTLS|3	
		 String employerCountry = mobCreateCustomerSOAPRequest.getMobApplicantEmploymentDtl().getEmployerCountry();
		custprof.setCNTY(substractChar(3,employerCountry));
		
	//	<!--  Add employer name here from EMPLOYER_NAME in MOB_APPLICANT_EMPLOYMENT_DTLS, if EMPSTAT - F(Full time)-->
     //   <fcub:EMPLOYER_DESC></fcub:EMPLOYER_DESC>
		
		if(null != custprof.getEMPSTAT() && custprof.getEMPSTAT().equalsIgnoreCase("F")){
			String employerName = mobCreateCustomerSOAPRequest.getMobApplicantEmploymentDtl().getEmployerName();
			custprof.setEMPLOYERDESC(employerName != null ? employerName : BLANK);
		}
		
		//==---------
		custpersonal.setCustprof(custprof);
		
		// set custpersonal to customerFT
		customerFT.setCustpersonal(custpersonal);
		//============== CustomerFullType.CustLiab ===================//
		CustomerFullType.CustLiab custLiab = getCustomerLiab(firstName, lastName);
		customerFT.setCustLiab(custLiab);
		
		//============= CustmisFullType ========================//
		CustmisFullType custmisFT = getCustomerMis(mobCreateCustomerSOAPRequest, rmDetails.getRmCode());
		customerFT.setCustmis(custmisFT);
		
		//====================== CustjointFullType ==========================//
		/*if(isMinor){
			CustjointFullType jointcustomer = getJointcustomer(mobApplicantGuardianDetails, gcal);
			customerFT.setJointcustomer(jointcustomer);
			 			
			// added new
			LinkedEntityFullType custaccdet = getCustomerRelationshipLinkage(mobApplicantGuardianDetails);
			customerFT.setCustaccdet(custaccdet);
		}*/
	
	//---------------end----------------------
	  //===================== CusttextFullType ======================= //
	  CusttextFullType custacdetail = getCustomerACDetails(mobCreateCustomerSOAPRequest);
	  customerFT.setCustacdetail(custacdetail);
	  //==============================================
	  //=====UDFDETAILSType2==
	  //loop
	  List<UDFDETAILSType2> udfdetailsList = customerFT.getUDFDETAILS();
	  Map<String, String> udfDetailsMap = getUdfDetailsMap(mobCreateCustomerSOAPRequest, mobApplicantGuardianDetails);
	  for(String key : udfDetailsMap.keySet()){
		  UDFDETAILSType2 udfdetails = new UDFDETAILSType2();
		  udfdetails.setFLDNAM(key);
		  udfdetails.setFLDVAL(udfDetailsMap.get(key));
		  udfdetailsList.add(udfdetails);
	   }
	  
	  /*
		Field name: LEISURE_PREFERENCE 
		Values: hobby_code_1, hobby_code_2, hobby_code_3 from MOB_APPLICANT_ADDITIONAL_DETAILS table.
		This field will be repeated 0-3 times, depending on if hobby_code_1, hobby_code_2, hobby_code_3 have value 
		*/
	  	MobApplicantAdditionalDtl mobApplicantAdditionalDtl = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl();
		String hobbyCode1 = mobApplicantAdditionalDtl.getHobbyCode1();
		String hobbyCode2 = mobApplicantAdditionalDtl.getHobbyCode2();
		String hobbyCode3 = mobApplicantAdditionalDtl.getHobbyCode3();
		if(null != hobbyCode1){
			UDFDETAILSType2 udfdetails = new UDFDETAILSType2();
			  udfdetails.setFLDNAM(LEISURE_PREFERENCE);
			  udfdetails.setFLDVAL(hobbyCode1);
			  udfdetailsList.add(udfdetails);
		}
		if(null != hobbyCode2){
			UDFDETAILSType2 udfdetails = new UDFDETAILSType2();
			  udfdetails.setFLDNAM(LEISURE_PREFERENCE);
			  udfdetails.setFLDVAL(hobbyCode2);
			  udfdetailsList.add(udfdetails);
		}
		if(null != hobbyCode3){
			  UDFDETAILSType2 udfdetails = new UDFDETAILSType2();
			  udfdetails.setFLDNAM(LEISURE_PREFERENCE);
			  udfdetails.setFLDVAL(hobbyCode3);
			  udfdetailsList.add(udfdetails);
		}
		
	  //=================================	  
	  // setting the body
	  CREATECUSTOMERFSFSREQ.FCUBSBODY bodyValue = new CREATECUSTOMERFSFSREQ.FCUBSBODY();
	  bodyValue.setCustomerFull(customerFT);
	  requestMsg.setFCUBSBODY(bodyValue);
	  debugLog.debug("===> createAfrAsiaCustomer bodyValue setting done " + bodyValue);
	  //===============================================
	  Gson gson = new Gson();  
		//  requestMsg
		  String requestMsgJson = gson.toJson(requestMsg);
		  debugLog.debug("requestMsgJson :" + requestMsgJson);
		  try
		  {
			  JAXBContext context = JAXBContext.newInstance(CREATECUSTOMERFSFSREQ.class);
			  Marshaller marshaller = context.createMarshaller();
			  StringWriter sw = new StringWriter();
			  marshaller.marshal(requestMsg, sw);
			  
		
			  String xmlString = sw.toString();
			  debugLog.debug(xmlString);
		  }
		  catch (Exception e)
		  {
			  e.printStackTrace();
		  }
	//===================================================  
	  Map<String,Object> result= null;	  
	  try{
		  // Service call
		  CREATECUSTOMERFSFSRES responseData = callCreateCustomerSOAP(requestMsg);
		  
			if( null != responseData){
				
				 result = new HashMap<String, Object>();
				 MsgStatType msgstat = responseData.getFCUBSHEADER().getMSGSTAT();
				
				// success
				if(null != msgstat  && msgstat.value().equalsIgnoreCase(SUCCESS)){
					
					result.put(MSGSTAT,  msgstat.value());
					result.put(CUSTNO,  responseData.getFCUBSBODY().getCustomerFull().getCUSTNO());
				}
				// failure
				else{
					
					result.put(MSGSTAT,  msgstat.value());
					result.put(CUSTNO,  null);
				}
				
				 List<WARNINGType> fcubswarningresp = responseData.getFCUBSBODY().getFCUBSWARNINGRESP();
				 List<ERRORType> fcubserrorresp = responseData.getFCUBSBODY().getFCUBSERRORRESP();
				 result.put(WARNING, (fcubswarningresp));
				 result.put(ERROR,  (fcubserrorresp));
				// result.put("WARNING_JSON",  gson.toJson(fcubswarningresp));
				// result.put("ERROR_JSON",  gson.toJson(fcubserrorresp));
			}
			else{
				
				infoLog.info("No response from Service call . Got Exception!");
			}
			
			String json2 = gson.toJson(responseData);
			
			infoLog.info("END");
			debugLog.debug("Got response from CreateCustomer: "+json2);
			
			debugLog.debug("Map MSGSTAT : "+result);
			//System.out.println("Map ERROR_JSON : "+result.get("ERROR_JSON"));
			//System.out.println("Map WARNING_JSON : "+result.get("WARNING_JSON"));
		  
	  } catch (Exception ex){
		  errorLog.error("callCreateCustomerSOAP :", ex);
		throw ex;
	  }
	  
	  infoLog.info("===> createAfrAsiaCustomer end ");
	  return result;
  }

	private CREATECUSTOMERFSFSRES callCreateCustomerSOAP(CREATECUSTOMERFSFSREQ requestMsg) {
		 FCUBSCustomerService fCUBSCustomerService = new FCUBSCustomerService();
		 FCUBSCustomerServiceSEI fCUBSCustomerServiceSEI = fCUBSCustomerService.getFCUBSCustomerServiceSEI();
		  //===============================================================================
		//Set timeout until a connection is established
		  /* ((BindingProvider)fCUBSCustomerServiceSEI).getRequestContext()
		   .put("javax.xml.ws.client.connectionTimeout", "120000");
		  */
		 //Set timeout until the response is received
		  /*((BindingProvider) fCUBSCustomerServiceSEI).getRequestContext()
		   .put("javax.xml.ws.client.receiveTimeout", "120000");*/
		  //===============================================================================
		  
		  CREATECUSTOMERFSFSRES cREATECUSTOMERFSFSRES= fCUBSCustomerServiceSEI.createCustomerFS(requestMsg);
		//  ((BindingProvider)fCUBSCustomerServiceSEI).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", 10000);
		  
		  if( null != cREATECUSTOMERFSFSRES){
			  debugLog.debug(" cREATECUSTOMERFSFSRES : "+ cREATECUSTOMERFSFSRES);
			  
			if(null !=  cREATECUSTOMERFSFSRES.getFCUBSHEADER()){
				debugLog.debug(" cREATECUSTOMERFSFSRES.getFCUBSHEADER : "+ cREATECUSTOMERFSFSRES.getFCUBSHEADER().getMSGID());
				debugLog.debug(" cREATECUSTOMERFSFSRES.getFCUBSHEADER : "+ cREATECUSTOMERFSFSRES.getFCUBSHEADER().getMSGSTAT());
			}
			
			if(null !=  cREATECUSTOMERFSFSRES.getFCUBSBODY()){
				debugLog.debug("getFCUBSBODY"+cREATECUSTOMERFSFSRES.getFCUBSBODY().toString());
				 if( null != cREATECUSTOMERFSFSRES.getFCUBSBODY().getFCUBSERRORRESP() &&
						 cREATECUSTOMERFSFSRES.getFCUBSBODY().getFCUBSERRORRESP().size() != 0 ){
					 
					 debugLog.debug("cREATECUSTOMERFSFSRES.getFCUBSBODY().getFCUBSERRORRESP"+ 
							 cREATECUSTOMERFSFSRES.getFCUBSBODY().getFCUBSERRORRESP().get(0).getERROR().get(0).getECODE());
					 debugLog.debug("cREATECUSTOMERFSFSRES.getFCUBSBODY().getFCUBSERRORRESP"+ 
							 cREATECUSTOMERFSFSRES.getFCUBSBODY().getFCUBSERRORRESP().get(0).getERROR().get(0).getEDESC());
				 }
				if( null != cREATECUSTOMERFSFSRES.getFCUBSBODY().getCustomerFull()){
					debugLog.debug("cREATECUSTOMERFSFSRES.getFCUBSBODY().getCustomerFull() :" + cREATECUSTOMERFSFSRES.getFCUBSBODY().getCustomerFull());
					debugLog.debug(" cREATECUSTOMERFSFSRES.getFCUBSBODY=> CUSTNO : "+ cREATECUSTOMERFSFSRES.getFCUBSBODY().getCustomerFull().getCUSTNO());
				}
			}
		  }
		return cREATECUSTOMERFSFSRES;
	}

	private CusttextFullType getCustomerACDetails(MobCreateCustomerSOAPRequest mobCreateCustomerSOAPRequest) {
		CusttextFullType custacdetail = new CusttextFullType();
		  String custCif = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getCustCif();
		  custacdetail.setCUSTNO(custCif != null ? custCif : BLANK);
		  List<CusttextFullType.CUSTTEXT> custtextList = custacdetail.getCUSTTEXT();
		  
		  String authEmail1 = mobCreateCustomerSOAPRequest.getMobAccountAdditionalDetail().getAuthEmail1();
		  String authEmail2 = mobCreateCustomerSOAPRequest.getMobAccountAdditionalDetail().getAuthEmail2();
		  String authEmail3 = mobCreateCustomerSOAPRequest.getMobAccountAdditionalDetail().getAuthEmail3();
		  
		  String nomineeId1 = mobCreateCustomerSOAPRequest.getMobAccountAdditionalDetail().getNomineeId1();
		  String nomineeName1 = mobCreateCustomerSOAPRequest.getMobAccountAdditionalDetail().getNomineeName1();
		  Long nomineeNo1 = mobCreateCustomerSOAPRequest.getMobAccountAdditionalDetail().getNomineeCallbkNum1();
		  String nomineeEmail1 = mobCreateCustomerSOAPRequest.getMobAccountAdditionalDetail().getNomineeEmail1();
		  
		  String nomineeId2 = mobCreateCustomerSOAPRequest.getMobAccountAdditionalDetail().getNomineeId2();
		  String nomineeName2 = mobCreateCustomerSOAPRequest.getMobAccountAdditionalDetail().getNomineeName2();
		  Long nomineeNo2 = mobCreateCustomerSOAPRequest.getMobAccountAdditionalDetail().getNomineeCallbkNum2();
		  String nomineeEmail2 = mobCreateCustomerSOAPRequest.getMobAccountAdditionalDetail().getNomineeEmail2();
		  
		  String hobby1 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getHobbyCode1();
		  String hobby2 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getHobbyCode2();
		  String hobby3 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getHobbyCode3();
		  
		  String rmComment = mobCreateCustomerSOAPRequest.getMobAccountAdditionalDetail().getRmComment();
		  
		  String  authEmail = null;
		  String  nomineeOne = null;
		  String  nomineeTwo = null;
		  String  hobbies = null;
		  String authEmailAvail = "NO";
		  String nomineeInfo = null;
		  
		  if( null != authEmail1 || null != authEmail2 || null != authEmail3)
		  {
			  authEmailAvail = "YES";
			  authEmail = (authEmail1 != null ? ('\n'+"AuthEmail1: "+authEmail1+'\n') :BLANK)
				   + (authEmail2 != null ? ("AuthEmail2: "+authEmail2+'\n') :BLANK)
				   + (authEmail3 != null ? ("AuthEmail3: "+authEmail3+'\n') :BLANK);
		  }
		  
		  if( null != nomineeId1 && !BLANK.equals(nomineeId1))
		  {
			  nomineeOne = (nomineeId1 != null ? ("NomineeId1: "+nomineeId1+'\n') :BLANK)
					   + (nomineeName1 != null ? ("NomineeName1: "+nomineeName1+'\n') :BLANK)
					   + (nomineeNo1 != null ? ("NomineeNo1: "+nomineeNo1.toString()+'\n') : BLANK)
					   + (nomineeEmail1 != null ? ("NomineeEmail1: "+nomineeEmail1.toString()+'\n') : BLANK);
		  }
		  
		  if( null != nomineeId2 && !BLANK.equals(nomineeId2))
		  {
			  nomineeTwo = (nomineeId2 != null ? ("NomineeId2: "+nomineeId2+'\n') :BLANK)
					   + (nomineeName2 != null ? ("NomineeName2: "+nomineeName2+'\n') :BLANK)
					   + (nomineeNo2 != null ? ("NomineeNo2: "+nomineeNo2.toString()+'\n') : BLANK)
					   + (nomineeEmail2 != null ? ("NomineeEmail2: "+nomineeEmail2.toString()+'\n') : BLANK);
		  }
		  
		  if(null!= nomineeOne || null!=nomineeTwo)
		  {
			  nomineeInfo='\n'+"CALL BACK MANDATE - "+'\n'+"AS PER CALL BACK AUTHORISATION MANDATE, THE FOLLOWING PERSONS ARE NOMINATED TO BE "
			  		+ "CALLED BACK TO CONFIRM FACSIMILE/ORIGINAL TRANSFER/EMAIL INSTRUCTIONS SENT TO THE BANK:"+'\n'+
			  		"1) FIRST NOMINEE"+'\n';
			  if(null!= nomineeOne && null!=nomineeTwo)
			  {
				  nomineeInfo = nomineeInfo+nomineeOne+"2) SECOND NOMINEE"+'\n'+nomineeTwo+'\n';
			  }	
			  else 
			  {
				  nomineeInfo = nomineeInfo+ (null!=nomineeOne?nomineeOne:nomineeTwo)+'\n';
			  }
			  		
		  }
		  
		  if( null != hobby1 || null != hobby2 || null != hobby3)
		  {
			  hobbies = '\n'+"HOBBIES- "+(hobby1 != null ? hobby1 :BLANK) 
				   + (hobby2 != null ? (", "+hobby2) :BLANK)
				   + (hobby3 != null ? (", "+hobby3) :BLANK);
		  }
		  
		  String incomeSource = mobCreateCustomerSOAPRequest.getMobApplicantEmploymentDtl().getFundSources();
		  String sources = null;
		  if(incomeSource!=null && !BLANK.equals(incomeSource))
		  {
			  sources = "SOURCE OF FUNDS- "+incomeSource;
		  }
		  
		  String tDesc = "FAX AND EMAIL INDEMNITY HELD: "+authEmailAvail+(authEmail != null ? authEmail :BLANK)+'\n'+(nomineeInfo != null ? nomineeInfo :BLANK)+'\n'+
				  (hobbies != null ? hobbies :BLANK)+'\n'+'\n'+
				  (rmComment != null ? ("ADDITIONAL COMMENTS- "+rmComment) :BLANK)+'\n'+'\n'+
				  (sources != null ? (sources) :BLANK);
		  
		  CusttextFullType.CUSTTEXT custText = new CusttextFullType.CUSTTEXT();
		  custText.setCUSTNO(custCif);
		  custText.setSEQNO(new BigDecimal(1));
		  custText.setTDESC(substractChar(4000,tDesc));
		  custtextList.add(custText);
		  
		//Sequence one
		  /*if( null != authEmail1 || null != authEmail2 || null != authEmail3){
			  String  authEmail = (authEmail1 != null ? authEmail1 :BLANK) + " "
				   + (authEmail2 != null ? authEmail2 :BLANK) + " "
				   + (authEmail3 != null ? authEmail3 :BLANK);
			  
			  CusttextFullType.CUSTTEXT custTextForEmail = new CusttextFullType.CUSTTEXT();
			  custTextForEmail.setCUSTNO(custCif);
			  custTextForEmail.setSEQNO(new BigDecimal(1));
			  custTextForEmail.setTDESC(substractChar(4000,authEmail));
			  custtextList.add(custTextForEmail);
		  }
		  
		  //Sequence two => for nominee one
		  
			 
		  String  nomineeOne = (nomineeId1 != null ? nomineeId1 :BLANK) + " "
				   + (nomineeName1 != null ? nomineeName1 :BLANK) + " "
				   + (nomineeNo1 != null ? nomineeNo1.toString() : BLANK) + " "
				   + (nomineeEmail1 != null ? nomineeEmail1.toString() : BLANK);
			  
			  CusttextFullType.CUSTTEXT custTextForNomineeOne = new CusttextFullType.CUSTTEXT();
			  custTextForNomineeOne.setCUSTNO(custCif);
			  custTextForNomineeOne.setSEQNO(new BigDecimal(2));
			  custTextForNomineeOne.setTDESC(substractChar(4000,nomineeOne));
			  custtextList.add(custTextForNomineeOne);
			  
			//Sequence three => for nominee two
			  
				 
			  String  nomineeTwo = (nomineeId2 != null ? nomineeId2 :BLANK) + " "
					   + (nomineeName2 != null ? nomineeName2 :BLANK) + " "
					   + (nomineeNo2 != null ? nomineeNo2.toString() : BLANK) + " "
					   + (nomineeEmail2 != null ? nomineeEmail2.toString() : BLANK);
				  
				  CusttextFullType.CUSTTEXT custTextForNomineeTwo = new CusttextFullType.CUSTTEXT();
				  custTextForNomineeTwo.setCUSTNO(custCif);
				  custTextForNomineeTwo.setSEQNO(new BigDecimal(3));
				  custTextForNomineeTwo.setTDESC(substractChar(4000,nomineeTwo));
			custtextList.add(custTextForNomineeTwo);*/
		return custacdetail;
	}

	private CustmisFullType getCustomerMis(MobCreateCustomerSOAPRequest mobCreateCustomerSOAPRequest, String rmCode) {
		CustmisFullType custmisFT = new CustmisFullType();
		
	//  <fcub:CUST>?</fcub:CUST> 	Blank
		custmisFT.setCUST(BLANK);
		
	//  <fcub:BRNCD>?</fcub:BRNCD> 	Default is "001"
		custmisFT.setBRNCD(DEFAULT_BRANCH);
		
			// loop 
			List<CustmisFullType.Customermis> customermisList = custmisFT.getCustomermis();
			Map<String, String> customermisMap = getCustomermisMap(mobCreateCustomerSOAPRequest, rmCode);
			for(String key:customermisMap.keySet()){
				CustmisFullType.Customermis customermis = new Customermis();
				customermis.setMISCLS(key);
				customermis.setMISCD(customermisMap.get(key));
				customermisList.add(customermis);
			}
			
		// loop 
			List<CustmisFullType.Compositemis> compositemisList = custmisFT.getCompositemis();
			Map<String, String> compositemisMap = getCompositemisMap(mobCreateCustomerSOAPRequest);
			for(String key:compositemisMap.keySet()){
				CustmisFullType.Compositemis compositemis = new Compositemis();
				compositemis.setMISCLS(key);
				compositemis.setMISCD(compositemisMap.get(key));
				compositemisList.add(compositemis);
			}
		return custmisFT;
	}


	private CustomerFullType.CustLiab getCustomerLiab(String firstName, String lastName) {
		
		CustomerFullType.CustLiab custLiab = new CustomerFullType.CustLiab();
		
	//  <fcub:LIABILITY_NUMBER>?</fcub:LIABILITY_NUMBER> - Send Blank	Blank
		custLiab.setLIABILITYNUMBER(BLANK);
		
   //   <fcub:LIABILTY_NAME>?</fcub:LIABILTY_NAME> - Customer Full name	FIRST_NAME<space> LAST_NAME	|
   //		MOB_APPLICANT_PERSONAL_DETAILS
		custLiab.setLIABILTYNAME(firstName + " " + lastName);
		
   //   <fcub:LIAB_BRANCH>?</fcub:LIAB_BRANCH> - Default 001	Default is "001"
		custLiab.setLIABBRANCH(DEFAULT_BRANCH);
		
   //   <fcub:LIAB_CCY>?</fcub:LIAB_CCY> - Default to MUR	MUR
		custLiab.setLIABCCY(MUR);
		
		return custLiab;
	}
	
	private LinkedEntityFullType getCustomerRelationshipLinkage(MobCreateCustomerSOAPRequest mobApplicantGuardianDetails) {
	/*	<fcub:Custaccdet>
        <!--Optional:-->
        <fcub:CUSTNO></fcub:CUSTNO>
        <!--Optional:-->
        <fcub:BRNCD>001</fcub:BRNCD> 
        <!--Zero or more repetitions:-->
        <fcub:Relationship-Linkage>
           <fcub:CUSTOMER>048141</fcub:CUSTOMER> 
           <fcub:RELATIONSHIP>PRIMARY</fcub:RELATIONSHIP>
           <fcub:INHERIT>Y</fcub:INHERIT>
           <fcub:DESCP>AVISHA JINDAL</fcub:DESCP>
        </fcub:Relationship-Linkage>
     </fcub:Custaccdet>
*/
		LinkedEntityFullType custaccdet = new LinkedEntityFullType();
		//  <fcub:CUSTNO></fcub:CUSTNO>
		custaccdet.setCUSTNO(BLANK);
		
		//  <fcub:BRNCD>001</fcub:BRNCD> 
		custaccdet.setBRNCD(DEFAULT_BRANCH);
		// <fcub:Relationship-Linkage>
		List<LinkedEntityFullType.RelationshipLinkage> relationshipLinkage = custaccdet.getRelationshipLinkage();
		
		RelationshipLinkage relationshipLnk = new RelationshipLinkage();
		relationshipLnk.setCUSTOMER(mobApplicantGuardianDetails.getMobApplicantPersonalDetail().getCustCif());
		relationshipLnk.setRELATIONSHIP(PRIMARY);
		relationshipLnk.setINHERIT(Y);
		relationshipLnk.setDESCP(getFullName(mobApplicantGuardianDetails));
		relationshipLinkage.add(relationshipLnk);
		
	  return custaccdet;
	}

	private CustjointFullType getJointcustomer(MobCreateCustomerSOAPRequest mobApplicantGuardianDetails,
			GregorianCalendar gcal) throws DatatypeConfigurationException {
		
		CustjointFullType jointcustomer = new CustjointFullType();
		//Take Guardian detail reference no  
		//  <fcub:CUSTNO>?</fcub:CUSTNO>	CIF of guardian
		jointcustomer.setCUSTNO(mobApplicantGuardianDetails.getMobApplicantPersonalDetail().getCustCif());
		
		List<CustjointFullType.Custjoint> custjointList = jointcustomer.getCustjoint();
		Custjoint custjoint1 = new com.ofss.fcubs.service.fcubscustomerservice.CustjointFullType.Custjoint();
		
		// <fcub:CUSTPREFIX>?</fcub:CUSTPREFIX>	TITLE	MOB_APPLICANT_PERSONAL_DETAILS	
		custjoint1.setCUSTPREFIX(mobApplicantGuardianDetails.getMobApplicantPersonalDetail().getTitle());
		
		//  <fcub:FRSTNAME>?</fcub:FRSTNAME>	FIRST_NAME	MOB_APPLICANT_PERSONAL_DETAILS							
		custjoint1.setFRSTNAME(mobApplicantGuardianDetails.getMobApplicantPersonalDetail().getFirstName());
		
       //   <fcub:MIDNAME>?</fcub:MIDNAME>	MAIDEN_NAME								
		custjoint1.setMIDNAME(mobApplicantGuardianDetails.getMobApplicantPersonalDetail().getMaidenName());
		
	 // <fcub:LASTNAME>?</fcub:LASTNAME>	LASTNAME	MOB_APPLICANT_PERSONAL_DETAILS							
		custjoint1.setLASTNAME(mobApplicantGuardianDetails.getMobApplicantPersonalDetail().getLastName());
		
	 // <fcub:DOB>?</fcub:DOB>	DOB	MOB_APPLICANT_PERSONAL_DETAILS	
		Date grdDob = mobApplicantGuardianDetails.getMobApplicantPersonalDetail().getDob();
		if( null != grdDob ){
			gcal.setTime(grdDob);
			XMLGregorianCalendar xmlGrdDob = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
			/*xmlGrdDob.setYear(gcal.get(GregorianCalendar.YEAR));
			xmlGrdDob.setMonth(gcal.get(GregorianCalendar.MONTH));
			xmlGrdDob.setDay(gcal.get(GregorianCalendar.DAY_OF_MONTH));*/
			xmlGrdDob.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
			xmlGrdDob.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			custjoint1.setDOB(xmlGrdDob);
		}
	//  <fcub:SEX>?</fcub:SEX>	SEX	MOB_APPLICANT_PERSONAL_DETAILS	
		String grdSex = mobApplicantGuardianDetails.getMobApplicantPersonalDetail().getSex();
		custjoint1.setSEX(grdSex);
		
    //  <fcub:PPNO>?</fcub:PPNO>	PASSPORT_NO	MOB_APPLICANT_PERSONAL_DETAILS							
		custjoint1.setPPNO(mobApplicantGuardianDetails.getMobApplicantPersonalDetail().getPassportNo());
		
	//  <fcub:PPTEXPDT>?</fcub:PPTEXPDT>	PASSPORT_EXPIRY_DATE	MOB_APPLICANT_PERSONAL_DETAILS	
		Date grdPassportExpiryDate = mobApplicantGuardianDetails.getMobApplicantPersonalDetail().getPassportExpiryDate();
		if( null != grdPassportExpiryDate){
		gcal.setTime(grdPassportExpiryDate);
		XMLGregorianCalendar xmlGrdPassportExpiryDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
		/*xmlGrdPassportExpiryDate.setYear(gcal.get(GregorianCalendar.YEAR));
		xmlGrdPassportExpiryDate.setMonth(gcal.get(GregorianCalendar.MONTH));
		xmlGrdPassportExpiryDate.setDay(gcal.get(GregorianCalendar.DAY_OF_MONTH));*/
		xmlGrdPassportExpiryDate.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
		xmlGrdPassportExpiryDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		custjoint1.setPPTEXPDT(xmlGrdPassportExpiryDate);
		
		}
	// <fcub:ADD1>?</fcub:ADD1>	PERM_ADDR_1	MOB_APPLICANT_COMM_DETAILS							
		custjoint1.setADD1(mobApplicantGuardianDetails.getMobApplicantCommDetail().getPermAddr1());
		
	// <fcub:ADD2>?</fcub:ADD2>	PERM_ADDR_2	MOB_APPLICANT_COMM_DETAILS							
		custjoint1.setADD2(mobApplicantGuardianDetails.getMobApplicantCommDetail().getPermAddr2());
		
	// <fcub:ADD3>?</fcub:ADD3>	PERM_ADDR_3	MOB_APPLICANT_COMM_DETAILS							
		custjoint1.setADD3(mobApplicantGuardianDetails.getMobApplicantCommDetail().getPermAddr3());
		
	// <fcub:TEL>?</fcub:TEL>	TEL_NO_HOME	MOB_APPLICANT_COMM_DETAILS	
		Long grdTelNoHome = mobApplicantGuardianDetails.getMobApplicantCommDetail().getTelNoHome();
		if( null != grdTelNoHome){
			custjoint1.setTEL(grdTelNoHome.toString());
		}
		
	// <fcub:EMAIL>?</fcub:EMAIL>	EMAIL	MOB_APPLICANT_PERSONAL_DETAILS							
		custjoint1.setEMAIL(mobApplicantGuardianDetails.getMobApplicantPersonalDetail().getEmail());
		
	// <fcub:SSN>?</fcub:SSN>	US_SSN	MOB_APPLICANT_ADDITIONAL_DTLS							
		custjoint1.setSSN(mobApplicantGuardianDetails.getMobApplicantAdditionalDtl().getUsSsn());
		
		/*custjoint1.setDECEASED(value);*/
  //   <fcub:RESISTAT>?</fcub:RESISTAT> - Default to R	R								
		custjoint1.setRESISTAT(DEFAULT_RESISTAT);
		
	  custjointList.add(custjoint1);
	  return jointcustomer;
	}
	private FCUBSHEADERType getSOAPRequestHeader(String flexID) {
		
		FCUBSHEADERType headerValue = new FCUBSHEADERType();
		// set source String value {  <fcub:SOURCE>?</fcub:SOURCE> => constant value, to be created for Pwc } 
		headerValue.setSOURCE(SOURCE);
		
		// set UBSCOMP enum {String} value { <fcub:UBSCOMP> FCUBS </fcub:UBSCOMP> } 
		headerValue.setUBSCOMP(UBSCOMPType.FCUBS);
		
		// set msgid String value <fcub:MSGID>?</fcub:MSGID>  - 20 char long, alphanumeric unique no Eg: EMP1a20s1f2116
		String uniqueNo = randomString(20);
		uniqueNo = uniqueNo != null ? uniqueNo : DEFAULT_MSGID ;
		headerValue.setMSGID(uniqueNo);
		
		// set correlid String value <fcub:CORRELID>?</fcub:CORRELID> - can be same as message Id | Same as MSGID
		headerValue.setCORRELID(uniqueNo);
		
		// set correlid String value  <fcub:USERID>?</fcub:USERID> - user id of compliance member who initiated the request
		//  Flex id of compliance  who created the request | Login user id | Max: 12
		//headerValue.setUSERID(DEFAULT_USERID); 
		headerValue.setUSERID(flexID);
		
		// set branch String value  <fcub:BRANCH>?</fcub:BRANCH> - 001 | Default value - 001
		headerValue.setBRANCH(DEFAULT_BRANCH);
		
		// set moduleid String value <fcub:MODULEID>?</fcub:MODULEID>  |  AC
		headerValue.setMODULEID(DEFAULT_MODULEID);
		
		// set service String value <fcub:SERVICE>?</fcub:SERVICE> - FCUBSCustomerService | FCUBSCustomerService
		headerValue.setSERVICE(SERVICE);
		
		// set operation String value <fcub:OPERATION>?</fcub:OPERATION> - CreateCustomer | CreateCustomer
		headerValue.setOPERATION(OPERATION_CUSTOMER);
		
		return headerValue;
	}

	private String substractChar(int MAX_NAME_CHAR, String inputChar) {
		
		if(inputChar == null){
			return null;
		}
		inputChar = inputChar.trim().length() > MAX_NAME_CHAR ? inputChar.trim().substring(0, MAX_NAME_CHAR) : inputChar.trim();
		return inputChar;
	}
	
	private Map<String,String> getCustomermisMap(MobCreateCustomerSOAPRequest mobCreateCustomerSOAPRequest, String rmCode){
		/*CUST_SEGM - (Values can be R_PRIV, R_HNWI, R_STAFF)
		In MOB_APPLICANT_PERSONAL_DETAILS, if IS_EMPLOYEE = true value should be R_STAFF.
		In MOB_APPLICANT_PERSONAL_DETAILS, if IS_HNWI = true value should be R_HNWI else it should be R_PRIV*/
		Map<String,String> misMap = new HashMap<String,String>();
		boolean isEmployee = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().isEmployee();
		boolean isHnwi = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getIsHnwi();
		if(isEmployee)
		{
			misMap.put("CUST_SEGM", CUST_SEGM_EMP); //misMap.put("CUST_SEGM", "R_STAFF");
		}
		else if (isHnwi){
			misMap.put("CUST_SEGM", CUST_SEGM_HNWI); // misMap.put("CUST_SEGM", "R_HNWI");
		}
		else{
			misMap.put("CUST_SEGM", CUST_SEGM_OTHER);     // misMap.put("CUST_SEGM", "R_PRIV");
		}
		
		/*IND_SEGM: By default � HHLD 
		SUB_SECT: By default - PERS_ALL 
		REL_MANAG: RM_ID
		This will be RM_ID that can be obtained from MOB_RM_APP_REF_ID table, against appref (id) column*/
		
		misMap.put("IND_SEGM", IND_SEGM);  // misMap.put("IND_SEGM", "HHLD");
		misMap.put("SUB_SECT", SUB_SECT); // misMap.put("SUB_SECT", "PERS_ALL");
		//String rmUsedId = mobCreateCustomerSOAPRequest.getMobRmAppRefId().getRmUsedId();
		misMap.put("REL_MANAG", rmCode); 
		
		/*CUST_TYPE: By Default � INDIVIDUA 
		OFFSHORE: To be selected by RM 
		[On hold - Adilah to confirm] // ?
		TDS_TYPE: By default � INDIVIDUA 
		SUBSECT: By default - S95  */
		misMap.put("CUST_TYPE", CUST_TYPE);  //misMap.put("CUST_TYPE", "INDIVIDUA");
		//misMap.put("OFFSHORE", OFFSHORE); // misMap.put("OFFSHORE", BLANK);
		misMap.put("TDS_TYPE", TDS_TYPE); // misMap.put("TDS_TYPE", "INDIVIDUA");
		//misMap.put("SUBSECT", SUBSECT); // misMap.put("SUBSECT", "S95");
		
		return misMap;
	}

	private Map<String,String> getCompositemisMap(MobCreateCustomerSOAPRequest mobCreateCustomerSOAPRequest){
		
		/*LOAN_STRU 
		INTL_RISK  
		IFRS_PART   
		XBRLGROUP
		SECURED_B 
		SUBSECT      -  S95
		SCH_TYPE   
		HLD_FAC      */
		Map<String,String> misMap = new LinkedHashMap<String,String>();
		misMap.put("INTL_RISK", BLANK);
		misMap.put("LOAN_STRU", BLANK);
		misMap.put("IFRS_PART", BLANK);
		misMap.put("SUBSECT", "S95");
		misMap.put("XBRLGROUP", BLANK);
		misMap.put("SECURED_B", BLANK);
		misMap.put("HLD_FAC", BLANK);
		misMap.put("SCH_TYPE", BLANK);
		
		return misMap;
		
	}
	
	private Map<String,String> getUdfDetailsMap(MobCreateCustomerSOAPRequest mobCreateCustomerSOAPRequest, MobCreateCustomerSOAPRequest mobApplicantGuardianDetails){
		
		Map<String,String> udfDetailsMap = new HashMap<String,String>();
		
		/*STMT_DELIVERY
		If value for STMT_DELIVERY is Estatement then send following values: 
		E_STATEMENT_FLG : Y
		E_STATEMENT_FREQ;M
		E_ADVICE_FLG: Y
		Table : MOB_ACCOUNT_ADDITIONAL_DETAILS
		*/
		
		/*Table Field name:
			STMT_DELIVERY_ESTMT
			If value for STMT_DELIVERY_ESTMT is Estatement then send following values: 
			E_STATEMENT_FLG : Y
			E_STATEMENT_FREQ;M
			E_ADVICE_FLG: Y
			E_MAIL: Email of cutomer from MOB_APPLICANT_PERSONAL_DETAILS table.
		 */
		
		Boolean stmtDelivery = mobCreateCustomerSOAPRequest.getMobAccountAdditionalDetail().getStmtDeliveryEstmt();
		String email ="";
		if(mobApplicantGuardianDetails!=null && mobApplicantGuardianDetails.getMobApplicantPersonalDetail()!=null)
		{
			email = mobApplicantGuardianDetails.getMobApplicantPersonalDetail().getEmail();
		}
		else
		{
			email=mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getEmail();
		}
		if(null != stmtDelivery && stmtDelivery == true ){
			
			udfDetailsMap.put("E_STATEMENT_FLG", "Y");
			udfDetailsMap.put("E_STATEMENT_FREQ", "M");
			udfDetailsMap.put("E_ADVICE_FLG", "Y");
			udfDetailsMap.put("E_MAIL", email);
		 }
			/*CRS_COUNTRY_OF_TAX_RESIDENCE_1
			CRS_COUNTRY_OF_TAX_RESIDENCE_2
			CRS_COUNTRY_OF_TAX_RESIDENCE_3
			Value:  3 digit country code
			Table: MOB_APPLICANT_ADDITIONAL_DTLS
			If INCOME_OTHER_COUNTRY_TAX then create this else no.
			COUNTRY_1 |	COUNTRY_2 |	COUNTRY_3*/
		Boolean incomeOtherCountryTax = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getIncomeOtherCountryTax();
		if(incomeOtherCountryTax){
			String country1 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getCountry1();
			String country2 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getCountry2();
			String country3 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getCountry3();
			
			
			country1 = country1 != null ? country1 :BLANK;
			country2 = country2 != null ? country2 :BLANK;
			country3 = country3 != null ? country3 :BLANK;
			
			udfDetailsMap.put("CRS_COUNTRY_OF_TAX_RESIDENCE_1", country1);
			udfDetailsMap.put("CRS_COUNTRY_OF_TAX_RESIDENCE_2", country2);
			udfDetailsMap.put("CRS_COUNTRY_OF_TAX_RESIDENCE_3", country3);
		}
		
		/* CRS_TIN_1 |	CRS_TIN_2 | CRS_TIN_3
		Value:  3 digit country code
		Table: MOB_APPLICANT_ADDITIONAL_DTLS
		If INCOME_OTHER_COUNTRY_TAX then create this else no.
		TIN_1 | TIN_2 | TIN_3
		
		If TIN_AVAILABLE_1, If TIN_AVAILABLE_2, If TIN_AVAILABLE_3, 
		then populate with TIN_1, TIN_2 and TIN_3 
		else populate with NO_TIN_OPTION_1, NO_TIN_OPTION_2, NO_TIN_OPTION_3

		IF NO_TIN_OPTION_1, IF NO_TIN_OPTION_2, IF NO_TIN_OPTION_3 is == B, 
		then concatenate this with NO_TIN_REASON_1, NO_TIN_REASON_2, NO_TIN_REASON_3
		*/
		
		if(incomeOtherCountryTax){
			
			Boolean tinAvailable1 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getTinAvailable1();
			Boolean tinAvailable2 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getTinAvailable2();
			Boolean tinAvailable3 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getTinAvailable3();
			
			
			// tin 1
			if(null != tinAvailable1 && tinAvailable1){
				String tin1 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getTin1();
				tin1 = tin1 != null ? tin1 :BLANK;
				udfDetailsMap.put(CRS_TIN_1, tin1);
			}
			else{
				 String noTinOption1 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getNoTinOption1();
				 // if noTinOption1 == B concatenate with no  NO_TIN_REASON 
				 if( null != noTinOption1 && noTinOption1.equalsIgnoreCase(NO_TIN_OPTION_B)){
					String noTinReason1 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getNoTinReason1();
					noTinReason1 = noTinReason1 != null ? noTinReason1 : BLANK;
					
					udfDetailsMap.put(CRS_TIN_1, noTinOption1+","+noTinReason1);
				 }
				 else{
					 
					 udfDetailsMap.put(CRS_TIN_1, noTinOption1 ); 
				 }
			}
			
			//tin 2
			if(null != tinAvailable2 && tinAvailable2){
				String tin2 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getTin2();
				tin2 = tin2 != null ? tin2 :BLANK;
				
				udfDetailsMap.put(CRS_TIN_2, tin2);
			}
			else{
				 String noTinOption2 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getNoTinOption2();
				 // if noTinOption2 == B concatenate with no  NO_TIN_REASON 
				 if( null != noTinOption2 && noTinOption2.equalsIgnoreCase(NO_TIN_OPTION_B)){
					String noTinReason2 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getNoTinReason2();
					noTinReason2 = noTinReason2 != null ? noTinReason2 : BLANK;
					
					udfDetailsMap.put(CRS_TIN_2, noTinOption2+","+noTinReason2);
				 }
				 else{
					 
					 udfDetailsMap.put(CRS_TIN_2, noTinOption2 ); 
				 }
				
			}
			
			//tin 3
			if(null != tinAvailable3 && tinAvailable3){
				String tin3 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getTin3();
				tin3 = tin3 != null ? tin3 :BLANK;
				
				udfDetailsMap.put(CRS_TIN_3, tin3);
			}
			else{
				 String noTinOption3 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getNoTinOption3();
				 // if noTinOption3 == B concatenate with no  NO_TIN_REASON 
				 if( null != noTinOption3 && noTinOption3.equalsIgnoreCase(NO_TIN_OPTION_B)){
					String noTinReason3 = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl().getNoTinReason3();
					noTinReason3 = noTinReason3 != null ? noTinReason3 : BLANK;
					
					udfDetailsMap.put(CRS_TIN_3, noTinOption3+","+noTinReason3);
				 }
				 else{
					 
					 udfDetailsMap.put(CRS_TIN_3, noTinOption3 ); 
				 }
			}
		}
		
		/*Field Name: LINE_OF_BUSINESS
		Value :  CURRENT_OCCUPATION in MOB_APPLICANT_EMPLOYMENT_DTLS table | MOB_appLICANT_emp_dt)
		If Retired, send retired. 
		If blank or null, ignore*/
		
		String currOccupation = mobCreateCustomerSOAPRequest.getMobApplicantEmploymentDtl().getCurrOccupation();
		if( null != currOccupation && !currOccupation.isEmpty()){
			if(currOccupation.equalsIgnoreCase("Retired")){
				udfDetailsMap.put("LINE_OF_BUSINESS","retired");
			}
			else{
				
				udfDetailsMap.put("LINE_OF_BUSINESS",currOccupation);
			}
		}
		
	/*	Field name:	LCY_YEARLY_TURNOVER
		Value: ANN_DEPOSIT_TURNOVR in MOB_APPLICANT_EMPLOYMENT_DTLS table
	*/
		Long annDepositTurnovr = 0L;
		if(mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getIsMinor())
		{
			annDepositTurnovr = mobApplicantGuardianDetails.getMobApplicantEmploymentDtl().getAnnDepositTurnovr();
		}
		else{
			annDepositTurnovr = mobCreateCustomerSOAPRequest.getMobApplicantEmploymentDtl().getAnnDepositTurnovr();
		}
		udfDetailsMap.put("LCY_YEARLY_TURNOVER", annDepositTurnovr.toString());
		
		/*Field name: LCY_CASH_YEARLY_TURNOVER
		Value: ANN_CASH_DEPOSIT in MOB_APPLICANT_EMPLOYMENT_DTLS table
		*/
		Long annCashDeposit = 0L;
		if(mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getIsMinor())
		{
			annCashDeposit = mobApplicantGuardianDetails.getMobApplicantEmploymentDtl().getAnnCashDeposit();
		}
		else{
			annCashDeposit = mobCreateCustomerSOAPRequest.getMobApplicantEmploymentDtl().getAnnCashDeposit();
		}
		
		udfDetailsMap.put("LCY_CASH_YEARLY_TURNOVER",annCashDeposit.toString());
		
		
		/*Field name: FIRST_HEAR_ABOUT_AFRASIA
		Values: GOLF_EVENT OTHER_EVENTS  FRIEND_REFERRAL BILLBOARD_MAGZNE MAGZNE_NEWSPAPER	WEBSITE_AFRASIA	SOCIAL_MEDIA
		OTH_BANNER_VIDEO BRANCH_VISIT MOBILE_APP TV_RADIO MANAGEMNT_COMP RMS_CSAS OTHER
		Table Field name HEAR_ABOUT_AFRASIA | MOB_APPLICANT_ADDITONAL_DTLS */
		
		MobApplicantAdditionalDtl mobApplicantAdditionalDtl = mobCreateCustomerSOAPRequest.getMobApplicantAdditionalDtl();
		udfDetailsMap.put(FIRST_HEAR_ABOUT_AFRASIA, mobApplicantAdditionalDtl.getHearAboutAfrasia());
		
		return udfDetailsMap;
		
	}
	// S-Single,M-Married,D-Divorced,R-Remarried,P-Separated,E-Spouse Expired. 
	public Map<String,String> getMaritalStatus() {
		Map<String,String> maritalStatus = new HashMap<String,String>();
		maritalStatus.put(MARITAL_STATUS_KEY_S, MARITAL_STATUS_VAL_S);
		maritalStatus.put(MARITAL_STATUS_KEY_M, MARITAL_STATUS_VAL_M);
		maritalStatus.put(MARITAL_STATUS_KEY_D, MARITAL_STATUS_VAL_D);
		maritalStatus.put(MARITAL_STATUS_KEY_R, MARITAL_STATUS_VAL_R);
		maritalStatus.put(MARITAL_STATUS_KEY_P, MARITAL_STATUS_VAL_P);
		maritalStatus.put(MARITAL_STATUS_KEY_E, MARITAL_STATUS_VAL_E);
		
		/*maritalStatus.put("S", "Single");
		maritalStatus.put("M", "Married");
		maritalStatus.put("D", "Divorced");
		maritalStatus.put("R", "Remarried");
		maritalStatus.put("P", "Separated");
		maritalStatus.put("E", "Spouse Expired");
		*/
		
	return 	maritalStatus;
	}
	// F-Full-time permanent,T-Full-time temporary,U-Unemployed,
	//	S-Self Employed,P-Part-time,R-Retired-pensioned,N-Retired non-pensioned,O-Other 
	public Map<String,String> getEmploymentStatus() {
		Map<String,String> employmentStatus = new HashMap<String,String>();
		
		employmentStatus.put(EMPLOYMENT_STATUS_KEY_F, EMPLOYMENT_STATUS_VAL_F);
		employmentStatus.put(EMPLOYMENT_STATUS_KEY_T, EMPLOYMENT_STATUS_VAL_T);
		employmentStatus.put(EMPLOYMENT_STATUS_KEY_U, EMPLOYMENT_STATUS_VAL_U);
		employmentStatus.put(EMPLOYMENT_STATUS_KEY_S, EMPLOYMENT_STATUS_VAL_S);
		employmentStatus.put(EMPLOYMENT_STATUS_KEY_P, EMPLOYMENT_STATUS_VAL_P);
		employmentStatus.put(EMPLOYMENT_STATUS_KEY_R, EMPLOYMENT_STATUS_VAL_R);
		employmentStatus.put(EMPLOYMENT_STATUS_KEY_N, EMPLOYMENT_STATUS_VAL_N);
		employmentStatus.put(EMPLOYMENT_STATUS_KEY_O, EMPLOYMENT_STATUS_VAL_O);
		
		/*employmentStatus.put("F", "Full-time permanent");
		employmentStatus.put("T", "Full-time temporary");
		employmentStatus.put("U", "Unemployed");
		employmentStatus.put("S", "Self Employed");
		employmentStatus.put("P", "Part-time");
		employmentStatus.put("R", "Retired-pensioned");
		employmentStatus.put("N", "Retired non-pensioned");
		employmentStatus.put("O", "Other");*/
	return 	employmentStatus;
	}
	
	/*private String randomString(final int length) {
	    Random r = new Random(); 
	    StringBuilder sb = new StringBuilder();
	    for(int i = 0; i < length; i++) {
	        char c = (char)(r.nextInt((int)(Character.MAX_VALUE)));
	        sb.append(c);
	    }
	    return sb.toString();
	}*/
	
	private	String randomString( int len ){
		   StringBuilder sb = new StringBuilder( len );
		   for( int i = 0; i < len; i++ ) 
		      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
		   return sb.toString();
		}

	private String getFirstName( MobCreateCustomerSOAPRequest mobCreateCustomerSOAPRequest){
		String firstName = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getFirstName();
		firstName = firstName != null ? firstName : BLANK ;
						 
		firstName = substractChar(MAX_NAME_CHAR, firstName);
		return firstName;
	}
	
	private String getMiddleName( MobCreateCustomerSOAPRequest mobCreateCustomerSOAPRequest){
		String middleName = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getMiddleName();
		middleName = middleName != null ? middleName : BLANK ;
						 
		middleName = substractChar(MAX_NAME_CHAR, middleName);
		return middleName;
	}
	
	private String getMaidenName( MobCreateCustomerSOAPRequest mobCreateCustomerSOAPRequest){
		String maidenName = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getMaidenName();
		maidenName = maidenName != null ? maidenName : BLANK ;
						 
		maidenName = substractChar(MAX_NAME_CHAR, maidenName);
		return maidenName;
	}
	
	private String getLastName( MobCreateCustomerSOAPRequest mobCreateCustomerSOAPRequest){
		String lastName = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getLastName();
		lastName = lastName != null ? lastName : BLANK ;
						 
		lastName = substractChar(MAX_NAME_CHAR, lastName);
		return lastName;
	}
	
	private String getFullName( MobCreateCustomerSOAPRequest mobCreateCustomerSOAPRequest){
		String firstName = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getFirstName();
		String middleName = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getMiddleName();
		String lastName = mobCreateCustomerSOAPRequest.getMobApplicantPersonalDetail().getLastName();
		debugLog.debug("firstName"+firstName);
		debugLog.debug("lastName"+lastName);
		debugLog.debug("middleName"+middleName);
		String fullName = "";
				String 	frName= firstName != null ? firstName : BLANK;
				String 	midName =	  middleName != null ? " " +middleName : BLANK ;
				String 	lstName	=  lastName != null ? " "+lastName : BLANK ;
				fullName = 	frName + midName + lstName;
		debugLog.debug("fullName"+fullName);
		fullName = substractChar(MAX_NAME_CHAR, fullName);
		debugLog.debug("fullName"+fullName);
		return fullName;
	}

}
