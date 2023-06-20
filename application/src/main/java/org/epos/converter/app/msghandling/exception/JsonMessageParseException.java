package org.epos.converter.app.msghandling.exception;

/**
 * Indicates a message payload could not be parsed due to it not conforming to some expected JSON message structure 
 *
 */
public class JsonMessageParseException extends RuntimeException {

	public JsonMessageParseException(String format) {
		super(format);
	}

	private static final long serialVersionUID = 1L;

}
