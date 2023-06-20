package org.epos.converter.app.msghandling.exception;

/**
 * Indicates an issue with being able to process a message originating from an external source.
 * (For example, it could be due to the message failing a schema validation or that the message type is not supported.)
 */
public class MessageProcessingException extends Exception {

	private static final long serialVersionUID = 1L;

	public MessageProcessingException(String warnMsg) {
		super(warnMsg);
	}
	
	public MessageProcessingException(String warnMsg, Exception e) {
		super(warnMsg, e);
	}

}
