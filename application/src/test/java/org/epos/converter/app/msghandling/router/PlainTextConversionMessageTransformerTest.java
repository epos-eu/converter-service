package org.epos.converter.app.msghandling.router;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.epos.converter.app.msghandling.conversion.ConversionMessage;
import org.epos.converter.app.msghandling.exception.JsonMessageParseException;
import org.epos.converter.app.msghandling.exception.MessageProcessingException;
import org.epos.converter.app.plugin.managment.ConversionMetadata;
import org.epos.converter.common.type.ContentType;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

class PlainTextConversionMessageTransformerTest {
	
	private ConversionMessage createValidConversionMessage() {
		ConversionMetadata params = new ConversionMetadata.Builder("internalICS/pluginMetadata")
				.withConversionRequestContentType(ContentType.APPLICATION_JSON)
				.withConversionResponseContentType(ContentType.APPLICATION_JSON)
				.build();

		String content = 
			"{" +
				"\"leadActor\":\"Christopher Walken\"," +
				"\"views\":1010200," +
				"\"rottenRatRating\":8.5" +
			"}";
		return new ConversionMessage(content, params);
	}
	
	private String getValidConversionOutMessage() {
		return "{\"content\":"
				+ "{\"leadActor\":\"Christopher Walken\",\"views\":1010200,\"rottenRatRating\":8.5}" +
				"}";
	}
	
	private String getValidConversionInMessage() {
		return "{\"content\":" +
				"\"{\\\"leadActor\\\":\\\"Christopher Walken\\\",\\\"views\\\":1010200,\\\"rottenRatRating\\\":8.5}\"," +
				"\"parameters\":{"
				+ "\"operation\":\"internalICS/pluginMetadata\","
				+ "\"requestContentType\":\"application/json\","
				+ "\"responseContentType\":\"application/json\"}"
				+ "}";
	}

	@Test
	void testGetExternalRepr() throws JSONException 
	{		
		ConversionMessage validConversionMessageObj = createValidConversionMessage();
		String actual = PlainTextConversionMessageTransformer.transformFromInternalRepr(validConversionMessageObj);		
		JSONAssert.assertEquals(getValidConversionOutMessage(), actual, true);
	}

	@Test
	void testGetInternalRepr_valid() throws MessageProcessingException 
	{
		ConversionMessage validConversionMessage = createValidConversionMessage();
		ConversionMessage actual = PlainTextConversionMessageTransformer.transformFromExternalRepr(getValidConversionInMessage());	
		assertEquals(validConversionMessage, actual);
	}
	
	@Test
	void testGetInternalRepr_invalid_root_jsonElement() 
	{
		final String jsonArrayAsStr = "[\"Christopher Walken\", 1010200, 8.5]";
		MessageProcessingException exception = assertThrows(MessageProcessingException.class, () -> {
			PlainTextConversionMessageTransformer.transformFromExternalRepr(jsonArrayAsStr);
		});
		assertEquals(JsonMessageParseException.class, exception.getCause().getClass());
	}
	
	@Test
	void testGetInternalRepr_invalid_mandatory_fields_missing() throws MessageProcessingException 
	{
		// 'operation' parameter missing
		String missingParam = "{\"content\":" +
				"\"{\\\"leadActor\\\":\\\"Christopher Walken\\\",\\\"views\\\":1010200,\\\"rottenRatRating\\\":8.5}\"," +
				"\"parameters\":{"
				+ "\"requestContentType\":\"application/json\","
				+ "\"responseContentType\":\"application/json\"}"
				+ "}";
	
		MessageProcessingException exception = assertThrows(MessageProcessingException.class, () -> {
			PlainTextConversionMessageTransformer.transformFromExternalRepr(missingParam);
		});
		assertEquals(JsonMessageParseException.class, exception.getCause().getClass());
		
		// 'content' parameter missing
		final String missingContentParameter = "{\"parameters\":{"
				+ "\"operation\":\"internalICS/pluginMetadata\","
				+ "\"requestContentType\":\"application/json\","
				+ "\"responseContentType\":\"application/json\"}"
				+ "}";
	
		exception = assertThrows(MessageProcessingException.class, () -> {
			PlainTextConversionMessageTransformer.transformFromExternalRepr(missingContentParameter);
		});
		assertEquals(JsonMessageParseException.class, exception.getCause().getClass());
	}

}
