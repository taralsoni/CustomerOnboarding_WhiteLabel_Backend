package com.afrAsia.service.impl;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.afrAsia.dao.jpa.ApplicationDetailsJpaDAO;
import com.afrAsia.entities.transactions.MobApplicantKycDocuments;
import com.afrAsia.service.KycDocDownloadService;

public class KycDocDownloadServiceImpl implements KycDocDownloadService {

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

	@Transactional(readOnly = false, rollbackFor = {Exception.class})
	public String getKycDocPath(Long appRefId, Long applicantId, String docId) throws NoResultException {
		try
		{
			MobApplicantKycDocuments mobApplicantKycDoc = applicationDetailsDAO.getKycDocumentDetails(appRefId,applicantId,docId);
			debugLog.debug("KYC Table entry::" + mobApplicantKycDoc + "doc url" + mobApplicantKycDoc.getDocUrl());
			return mobApplicantKycDoc.getDocUrl();

		}
		catch(NoResultException excpMessage){
			errorLog.error("Provided appRefId,applicantId or docId is not present, please pass proper value");
			throw excpMessage;
		}
	}
}