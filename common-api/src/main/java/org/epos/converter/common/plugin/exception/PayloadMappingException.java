package org.epos.converter.common.plugin.exception;

/**
 * Indicates an issue with message payload conversion.

 * It could be raised as a result of an outgoing payload failing validation.
 * This would indicate an unforeseen issue with the logic of the plug-in's transformation or an incorrect supplied schema.
 *
 */
public class PayloadMappingException extends Exception {

	private static final long serialVersionUID = 1L;

	public PayloadMappingException(String message, Exception cause) {
		super(message, cause);
	}

	public PayloadMappingException(String message) {
		super(message);
	}

}
