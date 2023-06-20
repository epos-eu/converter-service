package org.epos.converter.common.exception;

public class PayloadProcessingException extends Exception {

	private static final long serialVersionUID = 8814757647654776323L;

	public PayloadProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public PayloadProcessingException(String message) {
		super(message);
	}

}
