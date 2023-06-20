package org.epos.converter.app.msghandling.router;

import java.lang.reflect.Type;

import org.epos.converter.app.msghandling.conversion.ConversionMessage;
import org.epos.converter.app.msghandling.exception.JsonMessageParseException;
import org.epos.converter.app.msghandling.exception.MessageProcessingException;
import org.epos.converter.app.plugin.managment.ConversionMetadata;
import org.epos.converter.app.plugin.managment.ConversionMetadata.Builder;
import org.epos.converter.common.type.ContentType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

/**
 * 2-way transformer capable of transforming an incoming JSONString representation to a {@link ConversionMessage} instance 
 * and transforming an outgoing {@link ConversionMessage} instance to an outgoing JSONString representation.
 *
 */
public class PlainTextConversionMessageTransformer {
	
	private static final String PARAMETERS_PROP_KEY ="parameters";
	private static final String CONVERSION_REQUEST_TYPE_KEY = "operation";
	private static final String CONVERSION_REQUEST_CONTENT_TYPE_KEY = "requestContentType";
	private static final String CONVERSION_RESPONSE_CONTENT_TYPE_KEY = "responseContentType";
	
	public static final String CONTENT_PROP_KEY = "content";
	
	private static class PlainTextPayloadJsonDeserialiser implements JsonDeserializer<ConversionMessage> {

		@Override
		public ConversionMessage deserialize(JsonElement jsonElem, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException 
		{
			JsonObject rootObject = getManadatoryObject(jsonElem);

			// --- Message parameters ---
			JsonObject msgParams = getMandatoryObjectProp(rootObject, PARAMETERS_PROP_KEY);

			String conversionRequestType = getMandatoryPrimitiveProp(msgParams, CONVERSION_REQUEST_TYPE_KEY, String.class);
			Builder conversionMetadataBuilder = new ConversionMetadata.Builder(conversionRequestType);

			ContentType conversionRequestContentType = getPrimitiveProp(msgParams, CONVERSION_REQUEST_CONTENT_TYPE_KEY, ContentType.class);
			conversionMetadataBuilder.withConversionRequestContentType(conversionRequestContentType);

			ContentType conversionResponseContentType = getPrimitiveProp(msgParams, CONVERSION_RESPONSE_CONTENT_TYPE_KEY, ContentType.class);
			conversionMetadataBuilder.withConversionResponseContentType(conversionResponseContentType);
		
			// --- Message Content ---
			String msgContent = getMandatoryPrimitiveProp(rootObject, CONTENT_PROP_KEY, String.class);
			
			// --- Construct internal representation of message ---
			return new ConversionMessage(msgContent, conversionMetadataBuilder.build());
		}
		
		private JsonObject getManadatoryObject(JsonElement jsonElem) 
		{
			if (!jsonElem.isJsonObject()) {
				String errMsg = String.format("Expected JSON object but the element is of type %s", jsonElem.getClass().getName());
				throw new JsonMessageParseException(errMsg);
			}
			return jsonElem.getAsJsonObject();
		}

		private JsonObject getMandatoryObjectProp(JsonObject propBag, String propKey) 
		{
			JsonObject objectProp = propBag.getAsJsonObject(propKey);
			if (objectProp == null) {
				String errMsg = String.format("Expected JSON object property %s but could not be found", propKey);
				throw new JsonMessageParseException(errMsg);
			}
			return objectProp;
		}
		
		private <T> T getMandatoryPrimitiveProp(JsonObject propBag, String propKey, Class<T> clazz) 
		{
			T primitiveProp = getPrimitiveProp(propBag, propKey, clazz);
			if (primitiveProp == null) {
				String errMsg = String.format("Expected JSON primitive property %s but could not be found", propKey);
				throw new JsonMessageParseException(errMsg);
			}
			return primitiveProp;
		}

		private <T> T getPrimitiveProp(JsonObject propBag, String propKey, Class<T> clazz) 
		{
			JsonElement prop = propBag.get(propKey);
			
			if (prop == null || !prop.isJsonPrimitive()) {
				return null;
			}
			
			JsonPrimitive propVal = propBag.getAsJsonPrimitive(propKey);
			Object val = null;
				
			if (propVal.isString()) {
				String propValStr = propVal.getAsString();
				if (clazz == ContentType.class) {
					val = ContentType.fromValue(propValStr).orElse(null);
				} else {
					val = propValStr;
				}
			} else if (propVal.isNumber()) {
				val = propVal.getAsNumber();				
			}
			
			return clazz.cast(val);
		}
		
	}
	
	private static class PlainTextPayloadJsonSerialiser implements JsonSerializer<ConversionMessage> {

		@Override
		public JsonElement serialize(ConversionMessage msgObj, Type typeOfSrc, JsonSerializationContext context) throws JsonSyntaxException 
		{
			JsonObject jRoot = new JsonObject();
			jRoot.add(CONTENT_PROP_KEY, JsonParser.parseString(msgObj.getContent()));
			return jRoot;
		}
	}
	
	private static final Gson GSON_TO_INTERNAL = new GsonBuilder()
			.registerTypeAdapter(ConversionMessage.class, new PlainTextPayloadJsonDeserialiser())
			.registerTypeAdapter(ConversionMessage.class, new PlainTextPayloadJsonSerialiser())
			.create();
	
	private PlainTextConversionMessageTransformer() {
		throw new IllegalStateException("Static transformer class");
	}
	  
	static ConversionMessage transformFromExternalRepr(String msgPlainText) throws MessageProcessingException {
		try {
			return GSON_TO_INTERNAL.fromJson(msgPlainText, ConversionMessage.class);
		} catch (JsonMessageParseException | JsonSyntaxException ex) {
			String errMsg = String.format("CONVERSION MESSAGE WRAPPER VALIDATION FAILURE: "
					+ "Failed to parse externally-supplied JSON message conversion string: [%s]", ex.getMessage());
			throw new MessageProcessingException(errMsg, ex);
		}
	}
	
	static String transformFromInternalRepr(ConversionMessage msgObj) {
		return GSON_TO_INTERNAL.toJson(msgObj);
	}

}
