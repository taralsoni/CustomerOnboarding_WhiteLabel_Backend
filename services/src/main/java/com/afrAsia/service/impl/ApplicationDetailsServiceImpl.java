package com.afrAsia.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.afrAsia.dao.jpa.ApplicationDetailsJpaDAO;
import com.afrAsia.entities.jpa.MsgHeader;
import com.afrAsia.entities.jpa.MsgHeader.Error;
import com.afrAsia.entities.request.ApplicationDetailsReq;
import com.afrAsia.entities.request.KycInfo;
import com.afrAsia.entities.request.NomineeInfo;
import com.afrAsia.entities.response.ApplicantDetailsResponse;
import com.afrAsia.entities.response.ApplicationDetailsResponse;
import com.afrAsia.entities.response.ApplicationDetailsResponse.Data;
import com.afrAsia.entities.response.ApplicationDetailsResponse.Data.AccountDetails;
import com.afrAsia.entities.response.ApplicationDetailsResponse.Data.Comments;
import com.afrAsia.entities.response.JointApplicantsResponse;
import com.afrAsia.entities.transactions.MobAccountAdditionalDetail;
import com.afrAsia.entities.transactions.MobAccountDetail;
import com.afrAsia.entities.transactions.MobAppRefRecordId;
import com.afrAsia.entities.transactions.MobApplicantAdditionalDtl;
import com.afrAsia.entities.transactions.MobApplicantCommDetail;
import com.afrAsia.entities.transactions.MobApplicantEmploymentDtl;
import com.afrAsia.entities.transactions.MobApplicantKycDocuments;
import com.afrAsia.entities.transactions.MobApplicantPersonalDetail;
import com.afrAsia.entities.transactions.MobComments;
import com.afrAsia.entities.transactions.MobRmAppRefId;
import com.afrAsia.service.ApplicationDetailsService;

public class ApplicationDetailsServiceImpl implements ApplicationDetailsService {

	final static Logger debugLog = Logger.getLogger("debugLogger");
	final static Logger infoLog = Logger.getLogger("infoLogger");
	final static Logger errorLog = Logger.getLogger("errorLogger");
	
	private ApplicationDetailsJpaDAO applicationDetailsDAO;

	public ApplicationDetailsJpaDAO getApplicationDetailsDAO() {
		return applicationDetailsDAO;
	}

	public void setapplicationDetailsDAO(ApplicationDetailsJpaDAO applicationDetailsDAO) {
		this.applicationDetailsDAO = applicationDetailsDAO;
	}

	public ApplicationDetailsResponse getApplicationDetails(ApplicationDetailsReq applicationDetailsReq) {
		ApplicationDetailsResponse applicationDetailsResponse = new ApplicationDetailsResponse();
		Data data = new ApplicationDetailsResponse().new Data();
		MsgHeader msgHdr = new MsgHeader();
		AccountDetails accountDetails = new ApplicationDetailsResponse().new Data().new AccountDetails();
		ApplicantDetailsResponse primaryApplicantDetails;
		ApplicantDetailsResponse guardianDetails;
		List<JointApplicantsResponse> jointApplicants = new ArrayList<JointApplicantsResponse>();
		;

		// Application reference number from request
		Long appRefNo = applicationDetailsReq.getData().getRefNo();

		// 1. Get data from MobRMAppRefId
		try {
			MobRmAppRefId mobRmAppRefId = applicationDetailsDAO.getApplicationDetails(appRefNo);
			if (mobRmAppRefId != null) {
				// fetch record id from DB on the basis of the application reference number
				MobAppRefRecordId mobAppRefRecordId=applicationDetailsDAO.getRecordId(appRefNo);
				data.setRefNo(mobRmAppRefId.getId());
				data.setRecordId(mobAppRefRecordId.getRecordId());
				data.setAppStatus(mobRmAppRefId.getAppStatus());
				if(mobRmAppRefId.getCreatedDate()!=null)
				{
					data.setAppSubDate(mobRmAppRefId.getCreatedDate().getTime());
				}
				if(mobRmAppRefId.getModifiedDate()!=null)
				{
					data.setPendingRMSince(mobRmAppRefId.getModifiedDate().getTime());
				}
			} else {
				errorLog.error("The application number does not exist. Please check again");
				Error err = new MsgHeader().new Error();
				err.setRsn("The application number does not exist. Please check again");
				err.setCd("400");
				err.setCustomCode("ERROR 1");
				msgHdr.setError(err);
				applicationDetailsResponse.setMsgHeader(msgHdr);
				return applicationDetailsResponse;
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorLog.error("No data from mobRmAppRefId");
			Error err = new MsgHeader().new Error();
			err.setRsn("The application number does not exist. Please check again");
			err.setCd("404");
			err.setCustomCode("ERROR 1");
			msgHdr.setError(err);
			applicationDetailsResponse.setMsgHeader(msgHdr);
			return applicationDetailsResponse;
		}

		try {
			// get comments
			List<MobComments> mobComments = applicationDetailsDAO.getComments(appRefNo);
			List<Comments> comments = new ArrayList<Comments>();
			for (MobComments s : mobComments) {
				Comments comment = new ApplicationDetailsResponse().new Data().new Comments();
				comment.setComment(s.getComment());
				comment.setCommentAddedBy(s.getCommentedAddedBy());
				comment.setCommentDate(s.getCommentDate().getTime());
				comment.setUserCat(s.getUserCat());
				comments.add(comment);
			}
			data.setComments(comments);

			// 2. Get Basic account details
			MobAccountDetail mobAccountDetail = applicationDetailsDAO.getMobAccountDetails(appRefNo);
			if (mobAccountDetail != null) {
				accountDetails.setAccount(mobAccountDetail.getAccountType());
				accountDetails.setAccountType(mobAccountDetail.getAccountCategory());
				accountDetails.setMop(mobAccountDetail.getMop());
				accountDetails.setMinNoSignatures(mobAccountDetail.getMinNoSignatures());
				accountDetails.setOperatingInst(mobAccountDetail.getOperatingInst());
				infoLog.info("accountDetails in ApplicationDetailsServiceImpl" + accountDetails.toString());
				infoLog.info("mobAccountDetail in ApplicationDetailsServiceImpl" + mobAccountDetail.toString());
			} else {
				errorLog.error("No data from mobAccountDetail");
			}

			// 3. Get account additional details
			MobAccountAdditionalDetail mobAccountAddnDetail = applicationDetailsDAO
					.getMobAccountAdditionalDetails(appRefNo);
			if (mobAccountAddnDetail != null) {
				accountDetails.setStmtDeliveryPo(mobAccountAddnDetail.getStmtDeliveryPo());
				accountDetails.setStmtDeliveryEstmt(mobAccountAddnDetail.getStmtDeliveryEstmt());
				accountDetails.setStmtAddr1(mobAccountAddnDetail.getStmtAddr1());
				accountDetails.setStmtAddr2(mobAccountAddnDetail.getStmtAddr2());
				accountDetails.setStmtAddr3(mobAccountAddnDetail.getStmtAddr3());
				accountDetails.setStmtCity(mobAccountAddnDetail.getStmtCity());
				accountDetails.setStmtCountry(mobAccountAddnDetail.getStmtCountry());
				accountDetails.setInternetBankingUn(mobAccountAddnDetail.getIbUsername());
				accountDetails.setPrefCommMode(mobAccountAddnDetail.getPrefCommMode());
				accountDetails.setWhrDidYouHearAbtAfrAsia(mobAccountAddnDetail.getHearAboutAfrasia());
				accountDetails.setAfrasiaEventQues(mobAccountAddnDetail.getAfrasiaEventQues());
				accountDetails.setAfrasiaEventAns(mobAccountAddnDetail.getAfrasiaEventAns());
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

				List<NomineeInfo> nomineeInfo = new ArrayList<NomineeInfo>();
				NomineeInfo nomineeInfo1 = new NomineeInfo();

				nomineeInfo1.setNomineeCallbkNo(mobAccountAddnDetail.getNomineeCallbkNum1());
				nomineeInfo1.setNomineeEmail(mobAccountAddnDetail.getNomineeEmail1());
				nomineeInfo1.setNomineeId(mobAccountAddnDetail.getNomineeId1());
				nomineeInfo1.setNomineeName(mobAccountAddnDetail.getNomineeName1());
				nomineeInfo1.setNomineeCc(mobAccountAddnDetail.getNomineeCc1());
				nomineeInfo.add(nomineeInfo1);

				if (mobAccountAddnDetail.getNomineeName2() != null) {
					NomineeInfo nomineeInfo2 = new NomineeInfo();
					nomineeInfo2.setNomineeCallbkNo(mobAccountAddnDetail.getNomineeCallbkNum2());
					nomineeInfo2.setNomineeEmail(mobAccountAddnDetail.getNomineeEmail2());
					nomineeInfo2.setNomineeId(mobAccountAddnDetail.getNomineeId2());
					nomineeInfo2.setNomineeName(mobAccountAddnDetail.getNomineeName2());
					nomineeInfo2.setNomineeCc(mobAccountAddnDetail.getNomineeCc2());
					nomineeInfo.add(nomineeInfo2);
				}

				accountDetails.setNomineeInfo(nomineeInfo);
				infoLog.info("accountDetails in ApplicationDetailsServiceImpl" + accountDetails.toString());
				infoLog.info("mobAccountAddnDetail in ApplicationDetailsServiceImpl" + mobAccountAddnDetail.toString());
			} else {
				errorLog.error("No data from mobAccountAddnDetail");
			}

			// Primary Applicant Details
			if (mobAccountDetail.getIndvApplicantRefNo() != null) {
				primaryApplicantDetails = getApplicantDetails(data, appRefNo, mobAccountDetail.getIndvApplicantRefNo(),
						"forPrincipalApplicant");
				data.setPrimaryApplicantDetails(primaryApplicantDetails);
			} else {
				errorLog.error("Cannot get Principal Applicant Information");
				Error err = new MsgHeader().new Error();
				err.setRsn("Sorry, something went wrong. Cannot retrive applicant details. Please check again");
				err.setCd("404");
				err.setCustomCode("ERROR 1");
				msgHdr.setError(err);
				applicationDetailsResponse.setMsgHeader(msgHdr);
				return applicationDetailsResponse;
			}

			// Guardian Details
			if (mobAccountDetail.getIndvGuardianRefNo() != null) {
				guardianDetails = getApplicantDetails(data, appRefNo, mobAccountDetail.getIndvGuardianRefNo(),
						"forPrincipalGuardian");
				data.setGuardianDetails(guardianDetails);
			}

			// Joint Applicant1 Details
			if (mobAccountDetail.getJoint1ApplicantRefNo() != null) {
				JointApplicantsResponse jointApplicantsDetail = new JointApplicantsResponse();
				ApplicantDetailsResponse jointApplicant = getApplicantDetails(data, appRefNo,
						mobAccountDetail.getJoint1ApplicantRefNo(), "forJointApplicant1");
				jointApplicantsDetail.setJointApplicantDetail(jointApplicant);

				// Checking if joint applicant 1 has guardian
				if (mobAccountDetail.getJoint1GuardianRefNo() != null) {
					ApplicantDetailsResponse jointGuardian = getApplicantDetails(data, appRefNo,
							mobAccountDetail.getJoint1GuardianRefNo(), "forJointGuardian1");
					jointApplicantsDetail.setGuardianDetail(jointGuardian);
				}
				jointApplicants.add(jointApplicantsDetail);
			}
			// Joint Applicant2 Details
			if (mobAccountDetail.getJoint2ApplicantRefNo() != null) {
				JointApplicantsResponse jointApplicantsDetail = new JointApplicantsResponse();
				ApplicantDetailsResponse jointApplicant = getApplicantDetails(data, appRefNo,
						mobAccountDetail.getJoint2ApplicantRefNo(), "forJointApplicant2");
				jointApplicantsDetail.setJointApplicantDetail(jointApplicant);

				// Checking if joint applicant 1 has guardian
				if (mobAccountDetail.getJoint2GuardianRefNo() != null) {
					ApplicantDetailsResponse jointGuardian = getApplicantDetails(data, appRefNo,
							mobAccountDetail.getJoint2GuardianRefNo(), "forJointGuardian2");
					jointApplicantsDetail.setGuardianDetail(jointGuardian);
				}
				jointApplicants.add(jointApplicantsDetail);
			}
			// Joint Applicant3 Details
			if (mobAccountDetail.getJoint3ApplicantRefNo() != null) {
				JointApplicantsResponse jointApplicantsDetail = new JointApplicantsResponse();
				ApplicantDetailsResponse jointApplicant = getApplicantDetails(data, appRefNo,
						mobAccountDetail.getJoint3ApplicantRefNo(), "forJointApplicant3");
				jointApplicantsDetail.setJointApplicantDetail(jointApplicant);

				// Checking if joint applicant 1 has guardian
				if (mobAccountDetail.getJoint3GuardianRefNo() != null) {
					ApplicantDetailsResponse jointGuardian = getApplicantDetails(data, appRefNo,
							mobAccountDetail.getJoint3GuardianRefNo(), "forJointGuardian3");
					jointApplicantsDetail.setGuardianDetail(jointGuardian);
				}
				jointApplicants.add(jointApplicantsDetail);
			}
			// Joint Applicant4 Details
			if (mobAccountDetail.getJoint4ApplicantRefNo() != null) {
				JointApplicantsResponse jointApplicantsDetail = new JointApplicantsResponse();
				ApplicantDetailsResponse jointApplicant = getApplicantDetails(data, appRefNo,
						mobAccountDetail.getJoint4ApplicantRefNo(), "forJointApplicant4");
				jointApplicantsDetail.setJointApplicantDetail(jointApplicant);

				// Checking if joint applicant 1 has guardian
				if (mobAccountDetail.getJoint4GuardianRefNo() != null) {
					ApplicantDetailsResponse jointGuardian = getApplicantDetails(data, appRefNo,
							mobAccountDetail.getJoint4GuardianRefNo(), "forJointGuardian4");
					jointApplicantsDetail.setGuardianDetail(jointGuardian);
				}
				jointApplicants.add(jointApplicantsDetail);
			}
			// Joint Applicant5 Details
			if (mobAccountDetail.getJoint5ApplicantRefNo() != null) {
				JointApplicantsResponse jointApplicantsDetail = new JointApplicantsResponse();
				ApplicantDetailsResponse jointApplicant = getApplicantDetails(data, appRefNo,
						mobAccountDetail.getJoint5ApplicantRefNo(), "forJointApplicant5");
				jointApplicantsDetail.setJointApplicantDetail(jointApplicant);

				// Checking if joint applicant 1 has guardian
				if (mobAccountDetail.getJoint5GuardianRefNo() != null) {
					ApplicantDetailsResponse jointGuardian = getApplicantDetails(data, appRefNo,
							mobAccountDetail.getJoint5GuardianRefNo(), "forJointGuardian2");
					jointApplicantsDetail.setGuardianDetail(jointGuardian);
				}
				jointApplicants.add(jointApplicantsDetail);
			}

			data.setAccountDetails(accountDetails);
			data.setJointApplicants(jointApplicants);
			applicationDetailsResponse.setData(data);
			infoLog.info("applicationDetailsResponse in ApplicationDetailsServiceImpl is : "+applicationDetailsResponse);
			return applicationDetailsResponse;
		} catch (Exception e) {
			errorLog.error("Sorry, something went wrong when trying to retrive data for this application. Please try again"+ e.getMessage());
			e.printStackTrace();
			Error err = new MsgHeader().new Error();
			err.setRsn(
					"Sorry, something went wrong when trying to retrive data for this application. Please try again");
			err.setCd("404");
			err.setCustomCode("ERROR 1");
			msgHdr.setError(err);
			applicationDetailsResponse.setMsgHeader(msgHdr);
			return applicationDetailsResponse;
		}
	}

	public ApplicantDetailsResponse getApplicantDetails(Data data, Long appRefNo, Long primaryApplicantRefNo, String forWhom) {
		ApplicantDetailsResponse primaryApplicantDetails = new ApplicantDetailsResponse();

		// 4. Get Primary Applicant Details
		MobApplicantPersonalDetail mobApplicantPersonalDetail = applicationDetailsDAO
				.getMobApplicantPersonalDetails(appRefNo, primaryApplicantRefNo);
		if (mobApplicantPersonalDetail != null) {
			primaryApplicantDetails.setApplicantId(mobApplicantPersonalDetail.getId().getApplicantId());
			primaryApplicantDetails.setResidencyStatus(mobApplicantPersonalDetail.getResidencyStatus());
			primaryApplicantDetails.setOtherBank1(mobApplicantPersonalDetail.getOtherBank1());
			primaryApplicantDetails.setOtherBank2(mobApplicantPersonalDetail.getOtherBank2());
			primaryApplicantDetails.setOtherBank3(mobApplicantPersonalDetail.getOtherBank3());
			primaryApplicantDetails.setIsEmployee(mobApplicantPersonalDetail.isEmployee());
			primaryApplicantDetails.setTitle(mobApplicantPersonalDetail.getTitle());
			primaryApplicantDetails.setFirstName(mobApplicantPersonalDetail.getFirstName());
			primaryApplicantDetails.setMaidenName(mobApplicantPersonalDetail.getMaidenName());
			primaryApplicantDetails.setLastName(mobApplicantPersonalDetail.getLastName());
			primaryApplicantDetails.setNic(mobApplicantPersonalDetail.getNic());
			primaryApplicantDetails.setPassportNo(mobApplicantPersonalDetail.getPassportNo());
			if(mobApplicantPersonalDetail.getPassportExpiryDate()!=null)
			{
				primaryApplicantDetails.setPassportExpDate(mobApplicantPersonalDetail.getPassportExpiryDate().getTime());
			}
			primaryApplicantDetails.setNationality(mobApplicantPersonalDetail.getNationality());
			primaryApplicantDetails.setEmail(mobApplicantPersonalDetail.getEmail());
			if(mobApplicantPersonalDetail.getDob()!=null)
			{
				primaryApplicantDetails.setDob(mobApplicantPersonalDetail.getDob().getTime());
			}
			primaryApplicantDetails.setCountryBirth(mobApplicantPersonalDetail.getCountryBirth());
			primaryApplicantDetails.setIsExistingCustomer(mobApplicantPersonalDetail.isExistingCustomer());
			primaryApplicantDetails.setCustomerCIF(mobApplicantPersonalDetail.getCustCif());
			primaryApplicantDetails.setMaritialStatus(mobApplicantPersonalDetail.getMaritalStatus());
			primaryApplicantDetails.setIsMinor(mobApplicantPersonalDetail.getIsMinor());
			primaryApplicantDetails.setSex(mobApplicantPersonalDetail.getSex());
			primaryApplicantDetails.setSignatoryType(mobApplicantPersonalDetail.getSignatoryType());
			primaryApplicantDetails.setIsHnwi(mobApplicantPersonalDetail.getIsHnwi());
			infoLog.info("mobApplicantPersonalDetail in ApplicationDetailsServiceImpl" + mobApplicantPersonalDetail.toString());
		} else {
			errorLog.error("No data from mobApplicantPersonalDetail "+ forWhom);
		}

		// 5. Get Primary Applicant Communication Details
		MobApplicantCommDetail mobApplicantCommDetail = applicationDetailsDAO.getMobApplicantCommDetails(appRefNo,
				primaryApplicantRefNo);
		if (mobApplicantPersonalDetail != null) {
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
			infoLog.info("mobApplicantCommDetail in ApplicationDetailsServiceImpl" + mobApplicantCommDetail.toString());
		} else {
			errorLog.error("No data from mobApplicantPersonalDetail "+ forWhom);
		}

		// 6. Get Primary Applicant Employment Details
		MobApplicantEmploymentDtl mobApplicantEmploymentDtl = applicationDetailsDAO
				.getMobApplicantEmploymentDtl(appRefNo, primaryApplicantRefNo);
		if (mobApplicantEmploymentDtl != null) {
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
			if(mobApplicantEmploymentDtl.getDateStarted()!=null)
			{
				primaryApplicantDetails.setDateStarted(mobApplicantEmploymentDtl.getDateStarted().getTime());
			}
			primaryApplicantDetails.setNetMonthlyIncome(mobApplicantEmploymentDtl.getNetMonthlyIncome());
			primaryApplicantDetails.setAnnualDepositTurnover(mobApplicantEmploymentDtl.getAnnDepositTurnovr());
			primaryApplicantDetails.setAnnualCashDeposit(mobApplicantEmploymentDtl.getAnnCashDeposit());
			primaryApplicantDetails.setAnnualCashWithdrawl(mobApplicantEmploymentDtl.getAnnCashWithdrawl());
			primaryApplicantDetails.setOtherIncomeSource(mobApplicantEmploymentDtl.getOtherSourcesIncome());
			primaryApplicantDetails.setFundSources(mobApplicantEmploymentDtl.getFundSources());
			infoLog.info("mobApplicantAdditionalDtl in ApplicationDetailsServiceImpl" + mobApplicantEmploymentDtl.toString());
		} else {
			errorLog.error("No data from mobApplicantPersonalDetail "+ forWhom);
		}

		// 6. Get Primary Applicant Additional Details
		MobApplicantAdditionalDtl mobApplicantAdditionalDtl = applicationDetailsDAO
				.getMobApplicantAdditionalDtl(appRefNo, primaryApplicantRefNo);
		if (mobApplicantEmploymentDtl != null) {
			primaryApplicantDetails.setiSUSCitizen(mobApplicantAdditionalDtl.getUsCitizen());
			primaryApplicantDetails.setIsIncomeTaxableinUSA(mobApplicantAdditionalDtl.getIncomeUsTaxable());
			primaryApplicantDetails.setUsaSsn(mobApplicantAdditionalDtl.getUsSsn());
			primaryApplicantDetails.setoAddr1(mobApplicantAdditionalDtl.getOseasAddr1());
			primaryApplicantDetails.setoAddr2(mobApplicantAdditionalDtl.getOseasAddr2());
			primaryApplicantDetails.setoAddr3(mobApplicantAdditionalDtl.getOseasAddr3());
			primaryApplicantDetails.setoCity(mobApplicantAdditionalDtl.getOseasCity());
			primaryApplicantDetails.setoCountry(mobApplicantAdditionalDtl.getOseasCountry());
			if(mobApplicantAdditionalDtl.getWorkPermitExpDate()!=null)
			{
				primaryApplicantDetails.setWorkPermitExpDate(mobApplicantAdditionalDtl.getWorkPermitExpDate().getTime());
			}
			primaryApplicantDetails.setIncomeOtherCountryTaxable(mobApplicantAdditionalDtl.getIncomeOtherCountryTax());
			primaryApplicantDetails.setCrsCountryResidence1(mobApplicantAdditionalDtl.getCountry1());
			primaryApplicantDetails.setCrsCountryResidence2(mobApplicantAdditionalDtl.getCountry2());
			primaryApplicantDetails.setCrsCountryResidence3(mobApplicantAdditionalDtl.getCountry3());
			primaryApplicantDetails.setCrsTin1(mobApplicantAdditionalDtl.getTin1());
			primaryApplicantDetails.setCrsTin2(mobApplicantAdditionalDtl.getTin2());
			primaryApplicantDetails.setCrsTin3(mobApplicantAdditionalDtl.getTin3());
			infoLog.info("mobApplicantAdditionalDtl in ApplicationDetailsServiceImpl" + mobApplicantAdditionalDtl.toString());
		} else {
			errorLog.error("No data from mobApplicantPersonalDetail " + forWhom);
		}

		// get KYC
		List<MobApplicantKycDocuments> mobApplicantKycDocs = applicationDetailsDAO.getMobApplicantKyc(appRefNo,
				primaryApplicantRefNo);

		if (mobApplicantKycDocs.size() > 0) {
			List<KycInfo> kycList = new ArrayList<KycInfo>();
			for (MobApplicantKycDocuments kyc : mobApplicantKycDocs) {
				KycInfo kycData = new KycInfo();
				kycData.setDocId(kyc.getId().getDocId());
				kycData.setDocUrl(kyc.getDocUrl());

				kycList.add(kycData);
			}
			primaryApplicantDetails.setKycInfo(kycList);
		} else {
			primaryApplicantDetails.setKycInfo(null);
		}

		infoLog.info("APPLICANT DETAILS" + primaryApplicantDetails.toString());
		return primaryApplicantDetails;
	}
}
