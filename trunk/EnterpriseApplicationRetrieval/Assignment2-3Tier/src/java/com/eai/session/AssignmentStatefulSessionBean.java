package com.eai.session;

import javax.ejb.Stateful;

/**
 *
 * @author Max
 */
@Stateful
public class AssignmentStatefulSessionBean implements AssignmentStatefulSessionBeanLocal {

	private String cityName;

	@Override
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	@Override
	public String getCityName() {
		return this.cityName;
	}

}
