package org.epos.converter.common.schema.validation.exception;

public class ContentExtractionException extends Exception {

	private static final long serialVersionUID = 8831453508261668848L;

	public ContentExtractionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContentExtractionException(String message) {
		super(message);
	}

	public ContentExtractionException(Throwable cause) {
		super(cause);
	}
	
}
