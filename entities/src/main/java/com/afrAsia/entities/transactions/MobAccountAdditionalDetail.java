package com.afrAsia.entities.transactions;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the MOB_ACCOUNT_ADDITIONAL_DETAILS database table.
 * 
 */
@Entity
@Table(name="MOB_ACCOUNT_ADDITIONAL_DETAILS")

public class MobAccountAdditionalDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID")
	private Long id;

	@Column(name="AUTH_EMAIL_1")
	private String authEmail1;

	@Column(name="AUTH_EMAIL_2")
	private String authEmail2;

	@Column(name="AUTH_EMAIL_3")
	private String authEmail3;

	@Column(name="COMM_EMAIL")
	private Boolean commEmail;

	@Column(name="COMM_SMS")
	private Boolean commSms;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Temporal(TemporalType.DATE)
	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="CREDIT_CARD")
	private Boolean creditCard;

	@Column(name="FOREX_BANKING")
	private Boolean forexBanking;

	@Column(name="GLOBAL_CUSTODY")
	private Boolean globalCustody;

	@Column(name="HEAR_ABOUT_AFRASIA")
	private String hearAboutAfrasia;

	@Column(name="IB_USERNAME")
	private String ibUsername;

	@Column(name="INTERNET_BANKING")
	private Boolean internetBanking;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Temporal(TemporalType.DATE)
	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	@Column(name="NOMINEE_CALLBK_NUM")
	private Long nomineeCallbkNum;

	@Column(name="NOMINEE_CALLBK_NUM1")
	private Long nomineeCallbkNum1;

	@Column(name="NOMINEE_CALLBK_NUM2")
	private Long nomineeCallbkNum2;

	@Column(name="NOMINEE_ID")
	private String nomineeId;

	@Column(name="NOMINEE_ID1")
	private String nomineeId1;

	@Column(name="NOMINEE_ID2")
	private String nomineeId2;

	@Column(name="NOMINEE_NAME")
	private String nomineeName;

	@Column(name="NOMINEE_NAME1")
	private String nomineeName1;

	@Column(name="NOMINEE_NAME2")
	private String nomineeName2;

	@Column(name="OPT_CALLBK_SERVICES")
	private Boolean optCallbkServices;

	@Column(name="OPT_TRAN_EMAIL")
	private Boolean optTranEmail;

	@Column(name="OTP_EMAIL")
	private Boolean otpEmail;

	@Column(name="OTP_SMS")
	private Boolean otpSms;

	@Column(name="PIN_VIA_POST")
	private Boolean pinViaPost;

	@Column(name="PIN_VIA_SMS")
	private Boolean pinViaSms;

	@Column(name="PREF_COMM_MODE")
	private String prefCommMode;

	@Column(name="PREPAID_CARDS")
	private Boolean prepaidCards;

	@Column(name="RECORD_ID")
	private Long recordId;

	@Column(name="STMT_ADDR_1")
	private String stmtAddr1;

	@Column(name="STMT_ADDR_2")
	private String stmtAddr2;

	@Column(name="STMT_ADDR_3")
	private String stmtAddr3;

	@Column(name="STMT_CITY")
	private String stmtCity;

	@Column(name="STMT_COUNTRY")
	private String stmtCountry;

	@Column(name="STMT_DELIVERY")
	private String stmtDelivery;

	public MobAccountAdditionalDetail() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAuthEmail1() {
		return authEmail1;
	}

	public void setAuthEmail1(String authEmail1) {
		this.authEmail1 = authEmail1;
	}

	public String getAuthEmail2() {
		return authEmail2;
	}

	public void setAuthEmail2(String authEmail2) {
		this.authEmail2 = authEmail2;
	}

	public String getAuthEmail3() {
		return authEmail3;
	}

	public void setAuthEmail3(String authEmail3) {
		this.authEmail3 = authEmail3;
	}

	public Boolean getCommEmail() {
		return commEmail;
	}

	public void setCommEmail(Boolean commEmail) {
		this.commEmail = commEmail;
	}

	public Boolean getCommSms() {
		return commSms;
	}

	public void setCommSms(Boolean commSms) {
		this.commSms = commSms;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Boolean getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(Boolean creditCard) {
		this.creditCard = creditCard;
	}

	public Boolean getForexBanking() {
		return forexBanking;
	}

	public void setForexBanking(Boolean forexBanking) {
		this.forexBanking = forexBanking;
	}

	public Boolean getGlobalCustody() {
		return globalCustody;
	}

	public void setGlobalCustody(Boolean globalCustody) {
		this.globalCustody = globalCustody;
	}

	public String getHearAboutAfrasia() {
		return hearAboutAfrasia;
	}

	public void setHearAboutAfrasia(String hearAboutAfrasia) {
		this.hearAboutAfrasia = hearAboutAfrasia;
	}

	public String getIbUsername() {
		return ibUsername;
	}

	public void setIbUsername(String ibUsername) {
		this.ibUsername = ibUsername;
	}

	public Boolean getInternetBanking() {
		return internetBanking;
	}

	public void setInternetBanking(Boolean internetBanking) {
		this.internetBanking = internetBanking;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Long getNomineeCallbkNum() {
		return nomineeCallbkNum;
	}

	public void setNomineeCallbkNum(Long nomineeCallbkNum) {
		this.nomineeCallbkNum = nomineeCallbkNum;
	}

	public Long getNomineeCallbkNum1() {
		return nomineeCallbkNum1;
	}

	public void setNomineeCallbkNum1(Long nomineeCallbkNum1) {
		this.nomineeCallbkNum1 = nomineeCallbkNum1;
	}

	public Long getNomineeCallbkNum2() {
		return nomineeCallbkNum2;
	}

	public void setNomineeCallbkNum2(Long nomineeCallbkNum2) {
		this.nomineeCallbkNum2 = nomineeCallbkNum2;
	}

	public String getNomineeId() {
		return nomineeId;
	}

	public void setNomineeId(String nomineeId) {
		this.nomineeId = nomineeId;
	}

	public String getNomineeId1() {
		return nomineeId1;
	}

	public void setNomineeId1(String nomineeId1) {
		this.nomineeId1 = nomineeId1;
	}

	public String getNomineeId2() {
		return nomineeId2;
	}

	public void setNomineeId2(String nomineeId2) {
		this.nomineeId2 = nomineeId2;
	}

	public String getNomineeName() {
		return nomineeName;
	}

	public void setNomineeName(String nomineeName) {
		this.nomineeName = nomineeName;
	}

	public String getNomineeName1() {
		return nomineeName1;
	}

	public void setNomineeName1(String nomineeName1) {
		this.nomineeName1 = nomineeName1;
	}

	public String getNomineeName2() {
		return nomineeName2;
	}

	public void setNomineeName2(String nomineeName2) {
		this.nomineeName2 = nomineeName2;
	}

	public Boolean getOptCallbkServices() {
		return optCallbkServices;
	}

	public void setOptCallbkServices(Boolean optCallbkServices) {
		this.optCallbkServices = optCallbkServices;
	}

	public boolean getOptTranEmail() {
		return optTranEmail;
	}

	public void setOptTranEmail(boolean optTranEmail) {
		this.optTranEmail = optTranEmail;
	}

	public Boolean getOtpEmail() {
		return otpEmail;
	}

	public void setOtpEmail(Boolean otpEmail) {
		this.otpEmail = otpEmail;
	}

	public Boolean getOtpSms() {
		return otpSms;
	}

	public void setOtpSms(Boolean otpSms) {
		this.otpSms = otpSms;
	}

	public Boolean getPinViaPost() {
		return pinViaPost;
	}

	public void setPinViaPost(Boolean pinViaPost) {
		this.pinViaPost = pinViaPost;
	}

	public Boolean getPinViaSms() {
		return pinViaSms;
	}

	public void setPinViaSms(Boolean pinViaSms) {
		this.pinViaSms = pinViaSms;
	}

	public String getPrefCommMode() {
		return prefCommMode;
	}

	public void setPrefCommMode(String prefCommMode) {
		this.prefCommMode = prefCommMode;
	}

	public Boolean getPrepaidCards() {
		return prepaidCards;
	}

	public void setPrepaidCards(Boolean prepaidCards) {
		this.prepaidCards = prepaidCards;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public String getStmtAddr1() {
		return stmtAddr1;
	}

	public void setStmtAddr1(String stmtAddr1) {
		this.stmtAddr1 = stmtAddr1;
	}

	public String getStmtAddr2() {
		return stmtAddr2;
	}

	public void setStmtAddr2(String stmtAddr2) {
		this.stmtAddr2 = stmtAddr2;
	}

	public String getStmtAddr3() {
		return stmtAddr3;
	}

	public void setStmtAddr3(String stmtAddr3) {
		this.stmtAddr3 = stmtAddr3;
	}

	public String getStmtCity() {
		return stmtCity;
	}

	public void setStmtCity(String stmtCity) {
		this.stmtCity = stmtCity;
	}

	public String getStmtCountry() {
		return stmtCountry;
	}

	public void setStmtCountry(String stmtCountry) {
		this.stmtCountry = stmtCountry;
	}

	public String getStmtDelivery() {
		return stmtDelivery;
	}

	public void setStmtDelivery(String stmtDelivery) {
		this.stmtDelivery = stmtDelivery;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "MobAccountAdditionalDetail [id=" + id + ", authEmail1=" + authEmail1 + ", authEmail2=" + authEmail2
				+ ", authEmail3=" + authEmail3 + ", commEmail=" + commEmail + ", commSms=" + commSms + ", createdBy="
				+ createdBy + ", createdDate=" + createdDate + ", creditCard=" + creditCard + ", forexBanking="
				+ forexBanking + ", globalCustody=" + globalCustody + ", hearAboutAfrasia=" + hearAboutAfrasia
				+ ", ibUsername=" + ibUsername + ", internetBanking=" + internetBanking + ", modifiedBy=" + modifiedBy
				+ ", modifiedDate=" + modifiedDate + ", nomineeCallbkNum=" + nomineeCallbkNum + ", nomineeCallbkNum1="
				+ nomineeCallbkNum1 + ", nomineeCallbkNum2=" + nomineeCallbkNum2 + ", nomineeId=" + nomineeId
				+ ", nomineeId1=" + nomineeId1 + ", nomineeId2=" + nomineeId2 + ", nomineeName=" + nomineeName
				+ ", nomineeName1=" + nomineeName1 + ", nomineeName2=" + nomineeName2 + ", optCallbkServices="
				+ optCallbkServices + ", optTranEmail=" + optTranEmail + ", otpEmail=" + otpEmail + ", otpSms=" + otpSms
				+ ", pinViaPost=" + pinViaPost + ", pinViaSms=" + pinViaSms + ", prefCommMode=" + prefCommMode
				+ ", prepaidCards=" + prepaidCards + ", recordId=" + recordId + ", stmtAddr1=" + stmtAddr1
				+ ", stmtAddr2=" + stmtAddr2 + ", stmtAddr3=" + stmtAddr3 + ", stmtCity=" + stmtCity + ", stmtCountry="
				+ stmtCountry + ", stmtDelivery=" + stmtDelivery + "]";
	}
}