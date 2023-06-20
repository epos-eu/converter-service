package org.epos.converter.common.plugin.descriptor;

import static java.util.stream.Collectors.toList;
import static org.epos.converter.common.util.LambdaExceptionUtil.rethrowIntFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.MappingDescriptor;
import org.epos.converter.common.schema.validation.ContentValidator;
import org.epos.converter.common.schema.validation.DefaultContentOperationSetFactory;
import org.epos.converter.common.schema.validation.SchemaDescriptor;
import org.epos.converter.common.schema.validation.SchemaDescriptorTree;
import org.epos.converter.common.schema.validation.SchemaDescriptorTree.SchemaDescriptorTreeBuilder;
import org.epos.converter.common.schema.validation.exception.ConfigurationException;
import org.epos.converter.common.schema.validation.exception.ContentValidationException;
import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.ContentType;
import org.epos.converter.common.type.LocationType;
import org.epos.converter.common.util.LambdaExceptionUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses a conversions descriptor.
 * <p>Parsing entails<ul>
 * <li> Reading Mappings Descriptor from the specified path  
 * <li> Validating content of Mappings Descriptor against mappings descriptor schema
 * <li> Populating DOs, to be used elsewhere in the application, based on content
 * </ul</p>
 *
 */
public class ConversionsDescriptorReader {

	private static Logger LOG = LoggerFactory.getLogger(ConversionsDescriptorReader.class);
	
	private static final String CONVERSIONS_DESCRIPTOR_SCHEMA_FILENAME = "conversions-descriptor-schema.json";
	
	private final Path descriptorLocation;
	private final ClassLoader resourceClassloader;
	private final ContentValidator mappingsValidator;
	
	public ConversionsDescriptorReader(Path descriptorLocation) throws PluginConfigurationException {
		this(descriptorLocation, ConversionsDescriptorReader.class.getClassLoader());
	}
	
	public ConversionsDescriptorReader(Path descriptorLocation, ClassLoader resourceClassloader) throws PluginConfigurationException 
	{
		mappingsValidator = createValidator();
		this.descriptorLocation = descriptorLocation;
		this.resourceClassloader = resourceClassloader;
	}

	private static ContentValidator createValidator() throws PluginConfigurationException {
		
		SchemaDescriptor rootSchemaDescriptor = new SchemaDescriptor(
														ContentFormat.JSON, 
														CONVERSIONS_DESCRIPTOR_SCHEMA_FILENAME, 
														LocationType.PATH, 
														Paths.get("schema").toString());
		
		SchemaDescriptorTree schemaDescriptorTree = SchemaDescriptorTreeBuilder
											.instance(rootSchemaDescriptor)
											.build(new DefaultContentOperationSetFactory());
				
		try {
			return new ContentValidator(schemaDescriptorTree);			
		} catch (ConfigurationException e) {
			String errMsg = String.format(
					"Mappings Descriptor validator could not be loaded:%n >%s<",
					e.getMessage());
			LOG.error(errMsg);
			throw new PluginConfigurationException(errMsg);
		}
	}
	
	public Map<String, List<MappingDescriptor>> parse() throws PluginConfigurationException {
		
		try {
			String content = readContentToString(descriptorLocation);
			mappingsValidator.validateContent(content);
			return unmarshalConversions(content);
			
		} catch (IOException e) {				
			String errMsg = String.format("Failed to read mapping descriptor from '%s'", descriptorLocation);
			throw new PluginConfigurationException(errMsg);
		} catch (ContentValidationException e) {
			String errMsg = String.format("Failed to validate mapping descriptor from '%s' against '%s'", 
											descriptorLocation, CONVERSIONS_DESCRIPTOR_SCHEMA_FILENAME);
			throw new PluginConfigurationException(errMsg, e);
		} catch (PluginConfigurationException e) {
			String errMsg = String.format("Failed to parse mapping descriptor from '%s': '%s'", 
					descriptorLocation, e.getMessage());
			throw new PluginConfigurationException(errMsg, e);
		}
		
	}
	
	private Map<String, List<MappingDescriptor>> unmarshalConversions(String content) throws PluginConfigurationException
	{	
		JSONObject rootJson = new JSONObject(content);
		JSONArray conversions = rootJson.getJSONArray("conversions");
		
		return IntStream.range(0, conversions.length())
				.mapToObj(rethrowIntFunction(i -> unmarshalConversion(conversions.getJSONObject(i))))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	private Map.Entry<String, List<MappingDescriptor>> unmarshalConversion(JSONObject jsonObj) throws PluginConfigurationException 
	{
		String targetClassname = jsonObj.getString("target-classname");
		JSONArray mappings = jsonObj.getJSONArray("mappings");

		List<MappingDescriptor> _mappings = IntStream.range(0, mappings.length())
				.mapToObj(rethrowIntFunction(i -> unmarshalMapping(mappings.getJSONObject(i))))
				.collect(toList());

		return new AbstractMap.SimpleEntry<String, List<MappingDescriptor>>(targetClassname, _mappings);
	}

	/**
	 * Unmarshal to primitives representation
	 */
	private MappingDescriptor unmarshalMapping(JSONObject jsonObj) throws PluginConfigurationException {
		
		JSONObject request = jsonObj.getJSONObject("request");
		JSONObject response = jsonObj.getJSONObject("response");
		
		String requestType = request.getString("type");
		String requestContentType = request.getString("content-type");
		String responseContentType = response.getString("content-type");
		String[] attributes = null;
		
		if (!request.isNull("attributes")) {
			JSONArray attributesJson = request.getJSONArray("attributes");
			attributes = IntStream.range(0, attributesJson.length())
							.mapToObj(i -> attributesJson.get(i).toString())
							.toArray(size -> new String[size]);
		}
		
		return unmarshalMapping(requestType, requestContentType, responseContentType, attributes);	
	}
	
	/**
	 * Unmarshal to Domain Object
	 */
	protected MappingDescriptor unmarshalMapping(String requestType, String requestContentType, String responseContentType, String... attributes) throws PluginConfigurationException {

		ContentType _requestContentType = ContentType.fromValue(requestContentType).orElseThrow(() -> {
			String errStr = String.format("Content Type value, '%s', is not supported.%n "
				+ "Available IDs: %s", requestContentType, ContentType.prettyPrintSupportedValues());
			return new PluginConfigurationException(errStr);
		});

		ContentType _responseContentType = ContentType.fromValue(responseContentType).orElseThrow(() -> {
			String errStr = String.format("Content Type value, '%s', is not supported.%n "
				+ "Available IDs: %s", requestContentType, ContentType.prettyPrintSupportedValues());
			return new PluginConfigurationException(errStr);
		});

		Optional<String[]> _attributes = (attributes == null || attributes.length == 0) ? 
											Optional.empty() : Optional.of(attributes);

		return new MappingDescriptor(requestType, _requestContentType, _responseContentType, _attributes);
	}
	

	private String readContentToString(Path resourcePath) throws IOException {
		
		URL testResourceUrl = resourceClassloader.getResource(resourcePath.toString());
		LambdaExceptionUtil.raiseIssueIf(testResourceUrl, Objects::isNull,
				PluginConfigurationException::new,
				String.format("Location of plugin's descriptor could not be resolved (%s)", resourcePath.toString()));
				
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(testResourceUrl.openStream()))) {
			
			return reader.lines()
					.parallel()
					.collect(Collectors.joining(System.lineSeparator()));
		}
	}

}
