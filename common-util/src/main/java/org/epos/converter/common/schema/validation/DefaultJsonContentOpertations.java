package org.epos.converter.common.schema.validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;

import org.epos.converter.common.schema.validation.exception.ConfigurationException;
import org.epos.converter.common.schema.validation.exception.ContentValidationException;
import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.ContentOperationSet;
import org.everit.json.schema.SchemaException;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.gson.JsonParseException;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;

/**
 * Current default implementation for JSON. Uses...
 *    <b>JSON Path</b> for extracting substrings;
 *    <b>json-schema</b> for validation
 *    
 * TODO Use JSON Pointer instead of JSON Path as I think the simpler JSON Pointer would be sufficient here!
 *    {@link https://tools.ietf.org/html/rfc6901}
 */
public class DefaultJsonContentOpertations extends ContentOperationSet<org.everit.json.schema.Schema> {
	
	private final ParseContext JSONPATH_PARSE_CONTEXT = getJsonPathParseContext();
	
	/** 
	 * @return {@link ParseContext} for use with operations requiring JSON Path
	 * 
	 * @see <a href="https://stackoverflow.com/questions/10380835/is-it-ok-to-use-gson-instance-as-a-static-field-in-a-model-bean-reuse">GsonJsonProvider should be threadsafe</a>
	 */
	private static ParseContext getJsonPathParseContext() {
		Configuration conf = Configuration.builder()
				.jsonProvider(new GsonJsonProvider())
				.build();
		 return JsonPath.using(conf);
	}

	@Override
	public String getSubstrFromContent(String pointerExpr, String payload) {
		DocumentContext ctx = JSONPATH_PARSE_CONTEXT.parse(payload);				
		return ctx.read(pointerExpr).toString();	// JSONPath expression
	}

	@Override
	public Optional<Object> loadSchema(URL schemaPathUrl) throws ConfigurationException {

		// JSONTokener seems to have a problem loading schemas from jar via byte stream - hence getting up-front here as String
		try (BufferedReader br = new BufferedReader(new InputStreamReader(schemaPathUrl.openStream()))) {
			String schemaAsString = br.lines().collect(Collectors.joining(System.lineSeparator()));
			JSONObject rawSchema = new JSONObject(new JSONTokener(schemaAsString));
			
			SchemaLoader loader = SchemaLoader.builder()
				.schemaClient(SchemaClient.classPathAwareClient())
				.schemaJson(rawSchema)
				.resolutionScope(schemaPathUrl.toURI())
				.build();
			
			return Optional.ofNullable(loader.load().build());
			
		} catch (IOException | NullPointerException ex) {
			String errMsg = String.format(
				"PLUG-IN DEPLOYMENT ISSUE: Cannot locate the schema, '%s', specified [%s: %s]",
				schemaPathUrl.getPath(), ex.getClass(), ex.getMessage());	
			throw new ConfigurationException(errMsg);
		} catch (SchemaException | URISyntaxException ex) {
			String errMsg = String.format(
				"PLUG-IN DEPLOYMENT ISSUE: Failed to load the schema, '%s', specified [%s: %s]",
				schemaPathUrl.getPath(), ex.getClass(), ex.getMessage());	
			throw new ConfigurationException(errMsg);
		} catch (JSONException ex) {
			String errMsg = String.format(
				"PLUG-IN DEPLOYMENT ISSUE: Schema could not be parsed, '%s', specified [%s: %s]",
				schemaPathUrl.getPath(), ex.getClass(), ex.getMessage());	
			throw new ConfigurationException(errMsg);
		}
	}

	@Override
	protected void doValidateContent(org.everit.json.schema.Schema typedSchema, String content) throws ContentValidationException {	
		Object jsonContent = new JSONTokener(content).nextValue();	
		
		try {
			typedSchema.validate(jsonContent);			
		} catch (JsonParseException e) {
			String warnMsg = "Failed to parse payload ready for schema validation";
			throw new ContentValidationException(warnMsg, e);
		} catch (PathNotFoundException e) {
			String warnMsg = "Could not extract nested content from JSON";
			throw new ContentValidationException(warnMsg, e);
		}catch (JSONException e) {
			String warnMsg = String.format("Substring extracted from content was expected to be valid JSON%n "
					+ "However, the extracted JSON was...%n%s", content);
			throw new ContentValidationException(warnMsg, e);
		} catch (ValidationException e) {
			String failureMsgs = e.getAllMessages().stream()
				.map(msg -> String.format("%s[fail message] %s", System.lineSeparator(), msg))
				.collect(Collectors.joining());
						
			String warnMsg = String.format("Content failed to validate: %s%s", e.getMessage(), failureMsgs);
			throw new ContentValidationException(warnMsg, e);
		}
	}

	@Override
	protected Class<org.everit.json.schema.Schema> getSchemaClass() {
		return org.everit.json.schema.Schema.class;
	}

	@Override
	public ContentFormat getSupportedContentFormat() {
		return ContentFormat.JSON;
	}

	@Override
	public String getRootPointerExpr() {
		return "$";
	}

}
