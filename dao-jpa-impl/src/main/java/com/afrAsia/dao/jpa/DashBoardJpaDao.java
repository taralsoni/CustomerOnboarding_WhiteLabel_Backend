package com.afrAsia.dao.jpa;

import java.util.Collection;
import java.util.Date;

import com.afrAsia.dao.DashBoardDao;
import com.afrAsia.entities.jpa.ApplicationReference;

public interface DashBoardJpaDao extends DashBoardDao {
	
	//public List<String> getId(String id);
	
	public String getRmId(String rmUserId);

	public Collection<ApplicationReference> getMonthly(String rmId);

	public Collection<ApplicationReference> getQuarterly(String rmId);

	public Collection<ApplicationReference> getHalfYeary(String rmId);

	public Collection<ApplicationReference> getYearly(String rmId);

	public Collection<ApplicationReference> getLogged(String rmId);

	public Collection<ApplicationReference> getOpened(String rmId);

	public Collection<ApplicationReference> getUnderProcessing(String rmId);

	public Collection<ApplicationReference> getRejected(String rmId);

	// get the details of reference number
	public Collection<Long> getRefNo(String rmId);

	// get the Custermer Name
	public Collection<Object> getCustermerName(String rmId);

	// get pending since status
	public Collection<Date> getPendingSinceStatus(String rmId);
	
	/**
	 * @param appRef
	 */
	public void updateAppStatus(ApplicationReference appRef);
	
}
