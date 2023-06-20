package org.epos.converter.common.schema.validation.exception;

/**
 * Indicates validation of some content failed
 *
 */
public class ContentValidationException extends Exception {
	
	private static final long serialVersionUID = -3402375154302447142L;

	public ContentValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContentValidationException(String message) {
		super(message);
	}

	public ContentValidationException(Throwable cause) {
		super(cause);
	}

}
