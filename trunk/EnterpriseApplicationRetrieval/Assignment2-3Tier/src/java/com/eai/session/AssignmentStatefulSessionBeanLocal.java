package com.eai.session;

import javax.ejb.Local;

/**
 *
 * @author Max
 */
@Local
public interface AssignmentStatefulSessionBeanLocal {

	public void setCityName(String cityName);
	public String getCityName();

}
