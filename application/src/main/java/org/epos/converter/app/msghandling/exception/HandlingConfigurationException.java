package org.epos.converter.app.msghandling.exception;

public class HandlingConfigurationException extends Exception {

	private static final long serialVersionUID = 1L;

	public HandlingConfigurationException(String errMsg, Exception e) {
		super(errMsg, e);
	}

}
