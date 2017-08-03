package com.afrAsia.service.impl;


import java.util.ArrayList;
import java.util.List;

import com.afrAsia.dao.jpa.ApplicationDetailsJpaDAO;
import com.afrAsia.entities.jpa.MsgHeader;
import com.afrAsia.entities.jpa.MsgHeader.Error;
import com.afrAsia.entities.request.ApplicantDetails;
import com.afrAsia.entities.request.ApplicationDetailsReq;
import com.afrAsia.entities.request.JointApplicants;
import com.afrAsia.entities.response.ApplicationDetailsResponse;
import com.afrAsia.entities.response.ApplicationDetailsResponse.Data;
import com.afrAsia.entities.response.ApplicationDetailsResponse.Data.AccountDetails;
import com.afrAsia.entities.transactions.MobAccountAdditionalDetail;
import com.afrAsia.entities.transactions.MobAccountDetail;
import com.afrAsia.entities.transactions.MobApplicantAdditionalDtl;
import com.afrAsia.entities.transactions.MobApplicantCommDetail;
import com.afrAsia.entities.transactions.MobApplicantEmploymentDtl;
import com.afrAsia.entities.transactions.MobApplicantPersonalDetail;
import com.afrAsia.entities.transactions.MobRmAppRefId;
import com.afrAsia.service.ApplicationDetailsService;


public class ApplicationDetailsServiceImpl implements ApplicationDetailsService {	
	ApplicationDetailsJpaDAO applicationDetailsDAO;

	public ApplicationDetailsJpaDAO getApplicationDetailsDAO() {
		return applicationDetailsDAO;
	}

	public void setapplicationDetailsDAO(ApplicationDetailsJpaDAO applicationDetailsDAO) {
		this.applicationDetailsDAO = applicationDetailsDAO;
	}

	public ApplicationDetailsResponse getApplicationDetails(ApplicationDetailsReq applicationDetailsReq){
		ApplicationDetailsResponse applicationDetailsResponse = new ApplicationDetailsResponse();
		Data data= new ApplicationDetailsResponse().new Data();
		MsgHeader msgHdr = new MsgHeader();
		AccountDetails accountDetails = new ApplicationDetailsResponse().new Data().new AccountDetails();
		ApplicantDetails primaryApplicantDetails;
		ApplicantDetails guardianDetails;
		List<JointApplicants> jointApplicants = new ArrayList<JointApplicants>();;

		//Application reference number from request
		Long appRefNo = applicationDetailsReq.getData().getRefNo();

		//1. Get data from MobRMAppRefId
		try{
			MobRmAppRefId mobRmAppRefId = applicationDetailsDAO.getApplicationDetails(appRefNo);
			if (mobRmAppRefId!=null) {
				System.out.println("Data received from Rm Application table");
				data.setRefNo(mobRmAppRefId.getId());
				data.setAppStatus(mobRmAppRefId.getAppStatus());
				data.setAppSubDate(mobRmAppRefId.getCreatedDate());
				data.setPendingRMSince(mobRmAppRefId.getModifiedDate());
				System.out.println(mobRmAppRefId.toString());
			}
			else{
				Error err = new MsgHeader().new Error();
				err.setRsn("The application number does not exist. Please check again");
				err.setCd("400");
				err.setCustomCode("ERROR 1");
				msgHdr.setError(err);
				applicationDetailsResponse.setMsgHeader(msgHdr);
				return applicationDetailsResponse;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("No data from mobRmAppRefId");
			Error err = new MsgHeader().new Error();
			err.setRsn("The application number does not exist. Please check again");
			err.setCd("404");
			err.setCustomCode("ERROR 1");
			msgHdr.setError(err);
			applicationDetailsResponse.setMsgHeader(msgHdr);
			return applicationDetailsResponse;
		}

		//2. Get Basic account details

		MobAccountDetail mobAccountDetail = applicationDetailsDAO.getMobAccountDetails(appRefNo);
		if(mobAccountDetail != null){
			System.out.println("Data received from Mob Account Table");
			accountDetails.setAccount(mobAccountDetail.getAccountCategory());
			accountDetails.setAccountType(mobAccountDetail.getAccountType());
			accountDetails.setMop(mobAccountDetail.getMop());
			System.out.println(mobAccountDetail.toString());
		}
		else{
			System.out.println("No data from mobAccountDetail");
		}



		//3. Get account additional details
		try{

			MobAccountAdditionalDetail mobAccountAddnDetail = applicationDetailsDAO.getMobAccountAdditionalDetails(appRefNo);
			if(mobAccountAddnDetail != null){
				System.out.println("Data received from Mob Account Additional Detail Table");
				accountDetails.setStmtDelivery(mobAccountAddnDetail.getStmtDelivery());
				accountDetails.setStmtAddr1(mobAccountAddnDetail.getStmtAddr1());
				accountDetails.setStmtAddr2(mobAccountAddnDetail.getStmtAddr2());
				accountDetails.setStmtAddr3(mobAccountAddnDetail.getStmtAddr3());
				accountDetails.setStmtCity(mobAccountAddnDetail.getStmtCity());
				accountDetails.setStmtCountry(mobAccountAddnDetail.getStmtCountry());
				accountDetails.setInternetBankingUn(mobAccountAddnDetail.getIbUsername());
				accountDetails.setPrefCommMode(mobAccountAddnDetail.getPrefCommMode());
				accountDetails.setwhrDidYouHearAbtAfrAsia(mobAccountAddnDetail.getHearAboutAfrasia());
				accountDetails.setAuthEmail1(mobAccountAddnDetail.getAuthEmail1());
				accountDetails.setAuthEmail2(mobAccountAddnDetail.getAuthEmail2());
				accountDetails.setAuthEmail3(mobAccountAddnDetail.getAuthEmail3());
				accountDetails.setOtpOverEmail(mobAccountAddnDetail.getOtpEmail());
				accountDetails.setNeedGlobalCustody(mobAccountAddnDetail.getGlobalCustody());
				accountDetails.setNeedForexBanking(mobAccountAddnDetail.getForexBanking());
				accountDetails.setNeedPrepaidCard(mobAccountAddnDetail.getPrepaidCards());
				accountDetails.setNeedInternetBanking(mobAccountAddnDetail.getInternetBanking());
				accountDetails.setOtpOverSMS(mobAccountAddnDetail.getOtpSms());
				accountDetails.setPinViaSMS(mobAccountAddnDetail.getPinViaSms());
				accountDetails.setPinViaPost(mobAccountAddnDetail.getPinViaPost());
				accountDetails.setAgreeCommEmail(mobAccountAddnDetail.getCommEmail());
				accountDetails.setAgreeCommSMS(mobAccountAddnDetail.getCommSms());
				accountDetails.setOptCallBackServices(mobAccountAddnDetail.getOptCallbkServices());
				accountDetails.setNeedCreditCard(mobAccountAddnDetail.getCreditCard());
				accountDetails.setOptTransactionsThruEmail(mobAccountAddnDetail.getOptTranEmail());
				System.out.println(mobAccountAddnDetail.toString());
			}
			else{
				System.out.println("No data from mobAccountAddnDetail");
			}


			//Primary Applicant Details
			if(mobAccountDetail.getIndvApplicantRefNo() != null){
				System.out.println("Primary Applicant");
				primaryApplicantDetails = getApplicantDetails(data, appRefNo, mobAccountDetail.getIndvApplicantRefNo(),"forPrincipalApplicant");
				data.setPrimaryApplicantDetails(primaryApplicantDetails);
			}
			else{
				System.out.println("Cannot get Principal Applicant Information");
				Error err = new MsgHeader().new Error();
				err.setRsn("Sorry, something went wrong. Cannot retrive applicant details. Please check again");
				err.setCd("404");
				err.setCustomCode("ERROR 1");
				msgHdr.setError(err);
				applicationDetailsResponse.setMsgHeader(msgHdr);
				return applicationDetailsResponse;
			}

			//Guardian Details
			if(mobAccountDetail.getIndvGuardianRefNo() != null){
				System.out.println("Guardian Detail");
				guardianDetails = getApplicantDetails(data, appRefNo,mobAccountDetail.getIndvGuardianRefNo(), "forPrincipalGuardian");
				data.setGuardianDetails(guardianDetails);
			}

			//Joint Applicant1 Details
			if(mobAccountDetail.getJoint1ApplicantRefNo() != null){
				JointApplicants jointApplicantsDetail = new JointApplicants();
				System.out.println("Joint 1");
				ApplicantDetails jointApplicant = getApplicantDetails(data, appRefNo, mobAccountDetail.getJoint1ApplicantRefNo(),"forJointApplicant1");
				jointApplicantsDetail.setJointApplicantDetail(jointApplicant);

				//Checking if joint applicant 1 has guardian
				if(mobAccountDetail.getJoint1GuardianRefNo() != null){
					System.out.println("Guardian Joint 1");
					ApplicantDetails jointGuardian = getApplicantDetails(data, appRefNo, mobAccountDetail.getJoint1GuardianRefNo(),"forJointGuardian1");
					jointApplicantsDetail.setGuardianDetail(jointGuardian);
				}
				jointApplicants.add(jointApplicantsDetail);
			}
			//Joint Applicant2 Details
			if(mobAccountDetail.getJoint2ApplicantRefNo() != null){
				JointApplicants jointApplicantsDetail = new JointApplicants();
				System.out.println("Joint 2");
				ApplicantDetails jointApplicant = getApplicantDetails(data, appRefNo, mobAccountDetail.getJoint2ApplicantRefNo(),"forJointApplicant2");
				jointApplicantsDetail.setJointApplicantDetail(jointApplicant);

				//Checking if joint applicant 1 has guardian
				if(mobAccountDetail.getJoint2GuardianRefNo() != null){
					System.out.println("Guardian Joint 2");
					ApplicantDetails jointGuardian = getApplicantDetails(data, appRefNo, mobAccountDetail.getJoint2GuardianRefNo(),"forJointGuardian2");
					jointApplicantsDetail.setGuardianDetail(jointGuardian);
				}
				jointApplicants.add(jointApplicantsDetail);
			}
			//Joint Applicant3 Details
			if(mobAccountDetail.getJoint3ApplicantRefNo() != null){
				JointApplicants jointApplicantsDetail = new JointApplicants();
				System.out.println("Joint 3");
				ApplicantDetails jointApplicant = getApplicantDetails(data, appRefNo, mobAccountDetail.getJoint3ApplicantRefNo(),"forJointApplicant3");
				jointApplicantsDetail.setJointApplicantDetail(jointApplicant);

				//Checking if joint applicant 1 has guardian
				if(mobAccountDetail.getJoint3GuardianRefNo() != null){
					System.out.println("Guardian Joint 3");
					ApplicantDetails jointGuardian = getApplicantDetails(data, appRefNo, mobAccountDetail.getJoint3GuardianRefNo(),"forJointGuardian3");
					jointApplicantsDetail.setGuardianDetail(jointGuardian);
				}
				jointApplicants.add(jointApplicantsDetail);
			}
			//Joint Applicant4 Details
			if(mobAccountDetail.getJoint4ApplicantRefNo() != null){
				JointApplicants jointApplicantsDetail = new JointApplicants();
				System.out.println("Joint 4");
				ApplicantDetails jointApplicant = getApplicantDetails(data, appRefNo, mobAccountDetail.getJoint4ApplicantRefNo(),"forJointApplicant4");
				jointApplicantsDetail.setJointApplicantDetail(jointApplicant);

				//Checking if joint applicant 1 has guardian
				if(mobAccountDetail.getJoint4GuardianRefNo() != null){
					System.out.println("Guardian Joint 4");
					ApplicantDetails jointGuardian = getApplicantDetails(data, appRefNo, mobAccountDetail.getJoint4GuardianRefNo(),"forJointGuardian4");
					jointApplicantsDetail.setGuardianDetail(jointGuardian);
				}
				jointApplicants.add(jointApplicantsDetail);
			}

			data.setAccountDetails(accountDetails);
			data.setJointApplicants(jointApplicants);
			System.out.println("Data displayed" + data.toString());
			applicationDetailsResponse.setData(data);
			return applicationDetailsResponse;
		}
		catch(Exception e){
			e.printStackTrace();
			Error err = new MsgHeader().new Error();
			err.setRsn("Sorry, something went wrong when trying to retrive data for this application. Please try again");
			err.setCd("404");
			err.setCustomCode("ERROR 1");
			msgHdr.setError(err);
			applicationDetailsResponse.setMsgHeader(msgHdr);
			return applicationDetailsResponse;
		}
	}


	public ApplicantDetails getApplicantDetails(Data data, Long appRefNo, Long primaryApplicantRefNo, String forWhom){
		System.out.println("Applicant " + forWhom);
		ApplicantDetails primaryApplicantDetails = new ApplicantDetails();

		//4. Get Primary Applicant Details
		MobApplicantPersonalDetail mobApplicantPersonalDetail = applicationDetailsDAO.getMobApplicantPersonalDetails(appRefNo,primaryApplicantRefNo);
		if(mobApplicantPersonalDetail != null){
			System.out.println("Personal Detail");
			primaryApplicantDetails.setResidencyStatus(mobApplicantPersonalDetail.getResidencyStatus());
			primaryApplicantDetails.setOtherBank1(mobApplicantPersonalDetail.getOtherBank1());
			primaryApplicantDetails.setOtherBank2(mobApplicantPersonalDetail.getOtherBank2());
			primaryApplicantDetails.setOtherBank3(mobApplicantPersonalDetail.getOtherBank3());
			primaryApplicantDetails.setIsEmployee(mobApplicantPersonalDetail.getIsEmployee());
			primaryApplicantDetails.setTitle(mobApplicantPersonalDetail.getTitle());
			primaryApplicantDetails.setFirstName(mobApplicantPersonalDetail.getFirstName());
			primaryApplicantDetails.setMaidenName(mobApplicantPersonalDetail.getMaidenName());
			primaryApplicantDetails.setLastName(mobApplicantPersonalDetail.getLastName());
			primaryApplicantDetails.setNic(mobApplicantPersonalDetail.getNic());
			primaryApplicantDetails.setPassportNo(mobApplicantPersonalDetail.getPassportNo());
			primaryApplicantDetails.setPassportExpDate(mobApplicantPersonalDetail.getPassportExpiryDate());
			primaryApplicantDetails.setNationality(mobApplicantPersonalDetail.getNationality());
			primaryApplicantDetails.setEmail(mobApplicantPersonalDetail.getEmail());
			primaryApplicantDetails.setDob(mobApplicantPersonalDetail.getDob());
			primaryApplicantDetails.setCountryBirth(mobApplicantPersonalDetail.getCountryBirth());
			primaryApplicantDetails.setIsExistingCustomer(mobApplicantPersonalDetail.getExistingCustomer());
			primaryApplicantDetails.setCustomerCIF(mobApplicantPersonalDetail.getCustCif());
			primaryApplicantDetails.setMaritialStatus(mobApplicantPersonalDetail.getMaritalStatus());
			System.out.println(mobApplicantPersonalDetail.toString());
		}
		else{
			System.out.println("No data from mobApplicantPersonalDetail " + forWhom);
		}

		//5. Get Primary Applicant Communication Details
		MobApplicantCommDetail mobApplicantCommDetail = applicationDetailsDAO.getMobApplicantCommDetails(appRefNo,primaryApplicantRefNo);
		if(mobApplicantPersonalDetail != null){
			System.out.println("Comm Detail");
			primaryApplicantDetails.setPermAddr1(mobApplicantCommDetail.getPermAddr1());
			primaryApplicantDetails.setPermAddr2(mobApplicantCommDetail.getPermAddr2());
			primaryApplicantDetails.setPermAddr3(mobApplicantCommDetail.getPermAddr3());
			primaryApplicantDetails.setPermCity(mobApplicantCommDetail.getPermCity());
			primaryApplicantDetails.setPermCountry(mobApplicantCommDetail.getPermCountry());
			primaryApplicantDetails.setMailAddr1(mobApplicantCommDetail.getMailAddr1());
			primaryApplicantDetails.setMailAddr2(mobApplicantCommDetail.getMailAddr2());
			primaryApplicantDetails.setMailAddr3(mobApplicantCommDetail.getMailAddr3());
			primaryApplicantDetails.setMailCity(mobApplicantCommDetail.getMailCity());
			primaryApplicantDetails.setMailCountry(mobApplicantCommDetail.getMailCountry());
			primaryApplicantDetails.setTelNoOff(mobApplicantCommDetail.getTelNoOff());
			primaryApplicantDetails.setTelOffCallingCode(mobApplicantCommDetail.getTelNoOffCc());
			primaryApplicantDetails.setTelNoHome(mobApplicantCommDetail.getTelNoHome());
			primaryApplicantDetails.setTelNoHomeCallingCode(mobApplicantCommDetail.getTelNoHomeCc());
			primaryApplicantDetails.setMobNo(mobApplicantCommDetail.getMobNo());
			primaryApplicantDetails.setMobNoCountryCode(mobApplicantCommDetail.getMobNoCc());
			primaryApplicantDetails.setFaxNo(mobApplicantCommDetail.getFaxNo());
			primaryApplicantDetails.setFaxNoCallingCode(mobApplicantCommDetail.getFaxNoCc());
			System.out.println(mobApplicantCommDetail.toString());
		}
		else{
			System.out.println("No data from mobApplicantPersonalDetail " + forWhom);
		}

		//6. Get Primary Applicant Employment Details
		MobApplicantEmploymentDtl mobApplicantEmploymentDtl = applicationDetailsDAO.getMobApplicantEmploymentDtl(appRefNo,primaryApplicantRefNo);
		if(mobApplicantEmploymentDtl != null){
			System.out.println("Employment Detail");
			primaryApplicantDetails.setEmploymentStatus(mobApplicantEmploymentDtl.getEmploymentSts());
			primaryApplicantDetails.setCurrentOccupation(mobApplicantEmploymentDtl.getCurrOccupation());
			primaryApplicantDetails.setEmployerName(mobApplicantEmploymentDtl.getEmployerName());
			primaryApplicantDetails.setEmployerAddr1(mobApplicantEmploymentDtl.getEmployerAddr1());
			primaryApplicantDetails.setEmployerAddr2(mobApplicantEmploymentDtl.getEmployerAddr2());
			primaryApplicantDetails.setEmployerAddr3(mobApplicantEmploymentDtl.getEmployerAddr3());
			primaryApplicantDetails.setEmployerCity(mobApplicantEmploymentDtl.getEmployerCity());
			primaryApplicantDetails.setEmployerCountry(mobApplicantEmploymentDtl.getEmployerCountry());
			primaryApplicantDetails.setNoYearsService(mobApplicantEmploymentDtl.getNoOfYearsService());
			primaryApplicantDetails.setBusinessSector(mobApplicantEmploymentDtl.getBusinessSector());
			primaryApplicantDetails.setDateStarted(mobApplicantEmploymentDtl.getDateStarted());
			primaryApplicantDetails.setNetMonthlyIncome(mobApplicantEmploymentDtl.getNetMonthlyIncome());
			primaryApplicantDetails.setAnnualDepositTurnover(mobApplicantEmploymentDtl.getAnnDepositTurnovr());
			primaryApplicantDetails.setAnnualCashDeposit(mobApplicantEmploymentDtl.getAnnCashDeposit());
			primaryApplicantDetails.setAnnualCashWithdrawl(mobApplicantEmploymentDtl.getAnnCashWithdrawl());
			primaryApplicantDetails.setOtherIncomeSource(mobApplicantEmploymentDtl.getOtherSourcesIncome());
			primaryApplicantDetails.setFundSources(mobApplicantEmploymentDtl.getFundSources());
			System.out.println(mobApplicantEmploymentDtl.toString());
		}
		else{
			System.out.println("No data from mobApplicantPersonalDetail " + forWhom);
		}

		//6. Get Primary Applicant Additional Details
		MobApplicantAdditionalDtl mobApplicantAdditionalDtl = applicationDetailsDAO.getMobApplicantAdditionalDtl(appRefNo,primaryApplicantRefNo);
		if(mobApplicantEmploymentDtl != null){
			System.out.println("Additional Details");
			primaryApplicantDetails.setiSUSCitizen(mobApplicantAdditionalDtl.getUsCitizen());
			primaryApplicantDetails.setIsIncomeTaxableinUSA(mobApplicantAdditionalDtl.getIncomeUsTaxable());
			primaryApplicantDetails.setUsaSsn(mobApplicantAdditionalDtl.getUsSsn());
			primaryApplicantDetails.setoAddr1(mobApplicantAdditionalDtl.getOseasAddr1());
			primaryApplicantDetails.setoAddr2(mobApplicantAdditionalDtl.getOseasAddr2());
			primaryApplicantDetails.setoAddr3(mobApplicantAdditionalDtl.getOseasAddr3());
			primaryApplicantDetails.setoCity(mobApplicantAdditionalDtl.getOseasCity());
			primaryApplicantDetails.setoCountry(mobApplicantAdditionalDtl.getOseasCountry());
			primaryApplicantDetails.setWorkPermitExpDate(mobApplicantAdditionalDtl.getWorkPermitExpDate());
			primaryApplicantDetails.setIncomeOtherCountryTaxable(mobApplicantAdditionalDtl.getIncomeOtherCountryTax());
			primaryApplicantDetails.setCrsCountryResidence1(mobApplicantAdditionalDtl.getCountry1());
			primaryApplicantDetails.setCrsCountryResidence2(mobApplicantAdditionalDtl.getCountry2());
			primaryApplicantDetails.setCrsCountryResidence3(mobApplicantAdditionalDtl.getCountry3());
			primaryApplicantDetails.setCrsTin1(mobApplicantAdditionalDtl.getTin1());
			primaryApplicantDetails.setCrsTin2(mobApplicantAdditionalDtl.getTin2());
			primaryApplicantDetails.setCrsTin3(mobApplicantAdditionalDtl.getTin3());
			System.out.println(mobApplicantAdditionalDtl.toString());
		}
		else{
			System.out.println("No data from mobApplicantPersonalDetail " + forWhom);
		}
		System.out.println("APPLICANT DETAILS" + primaryApplicantDetails.toString());
		return primaryApplicantDetails;
	}
}