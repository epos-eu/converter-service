package org.epos.converter.common.plugin.descriptor;

import static org.epos.converter.common.util.LambdaExceptionUtil.rethrowIntFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.ConversionSchemasDescriptor;
import org.epos.converter.common.plugin.type.ExecutionDescriptor;
import org.epos.converter.common.plugin.type.HeaderDescriptor;
import org.epos.converter.common.plugin.type.MappingDescriptor;
import org.epos.converter.common.plugin.type.PluginDescriptor;
import org.epos.converter.common.schema.validation.ContentValidator;
import org.epos.converter.common.schema.validation.DefaultContentOperationSetFactory;
import org.epos.converter.common.schema.validation.SchemaDescriptor;
import org.epos.converter.common.schema.validation.SchemaDescriptorTree;
import org.epos.converter.common.schema.validation.SchemaDescriptorTree.SchemaDescriptorTreeBuilder;
import org.epos.converter.common.schema.validation.exception.ConfigurationException;
import org.epos.converter.common.schema.validation.exception.ContentValidationException;
import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.LocationType;
import org.epos.converter.common.util.LambdaExceptionUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Parses a plug-in descriptor.
 * <p>Parsing entails<ul>
 * <li> Reading Plug-in Descriptor from the specified path  
 * <li> Validating content of Plug-in Descriptor against plug-in descriptor schema
 * <li> Populating DOs, to be used elsewhere in the application, based on content
 * </ul</p>
 *
 */
public class PluginReader {
	
	private class PluginDescriptorFromJsonHelper extends PluginDescriptorHelper<JSONObject, JSONObject> {
		
		@Override
		public ConversionDescriptor createConversionDescriptor(JSONObject inType) throws PluginConfigurationException {
			
			// extraction for ExecutionDescriptor
			JSONObject execution = inType.getJSONObject("execution");
			ExecutionDescriptor executionDescriptor = createExecutionDescriptor(
					execution.getString("invoker-type"),
					execution.getString("plugin-install-location"),
					execution.getString("target-detail"));			
			
			// extraction for MappingDescriptor
			JSONObject request = inType.getJSONObject("request");
			String requestType = request.getString("type");
			String requestContentType = request.getString("content-type");
			
			JSONObject response = inType.getJSONObject("response");
			String responseContentType = response.getString("content-type");
			String[] attributes = null;
			
			if (!request.isNull("attributes")) {
				JSONArray attributesJson = request.getJSONArray("attributes");
				attributes = IntStream.range(0, attributesJson.length())
								.mapToObj(i -> attributesJson.get(i).toString())
								.toArray(size -> new String[size]);
			}
		
			MappingDescriptor mappingDescriptor = createMappingDescriptor(requestType, requestContentType, responseContentType, attributes);				
			
			// ConversionDescriptor
			return createConversionDescriptor(executionDescriptor, mappingDescriptor);
		}

		@Override
		protected HeaderDescriptor createHeader(JSONObject inType) 
		{
			// extraction for HeaderDescriptor
			JSONObject author = inType.getJSONObject("author");			
			return new HeaderDescriptor(
					inType.getString("name"), 
					inType.getString("id"), inType.getString("version"),
					author.getString("name"), author.getString("contact"));
		}
		
		@Override
		protected Map<ConversionDescriptor, ConversionSchemasDescriptor> createConversions(JSONObject inType) {
			
			// extraction for Conversions array
			JSONArray conversions = inType.getJSONArray("conversions");

			return IntStream.range(0, conversions.length())
				.mapToObj(rethrowIntFunction(i -> createConversionEntry(conversions.getJSONObject(i))))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
		
		private Map.Entry<ConversionDescriptor, ConversionSchemasDescriptor> createConversionEntry(JSONObject conversion) 
				throws PluginConfigurationException 
		{				
			ConversionDescriptor conversionDescriptor = createConversionDescriptor(conversion);
			
			// extraction for ConversionSchemaDescriptor
			ContentFormat requestPointerContentFormat = conversionDescriptor.getMappingDescriptor()
					.getRequestContentType().getContentFormat();
			SchemaDescriptorTree requestSchemaRoot = createSchemaRoot(
					conversion.getJSONObject("request"),
					requestPointerContentFormat);
			
			ContentFormat responsePointerContentFormat = conversionDescriptor.getMappingDescriptor()
					.getResponseContentType().getContentFormat();
			SchemaDescriptorTree responseSchemaRoot = createSchemaRoot(
					conversion.getJSONObject("response"),
					responsePointerContentFormat);
			
			ConversionSchemasDescriptor conversionSchemaDescriptor = new ConversionSchemasDescriptor(requestSchemaRoot, responseSchemaRoot);				
			return Map.entry(conversionDescriptor, conversionSchemaDescriptor);
			}

		private SchemaDescriptorTree createSchemaRoot(JSONObject requestOrResponse, ContentFormat pointerContentFormat) throws PluginConfigurationException {
			
			JSONObject requestOrResponseValidation = requestOrResponse.getJSONObject("validation");				
			String schemaName = requestOrResponseValidation.getString("schema-name");
			String schemaLocation = requestOrResponseValidation.getString("schema-location");		
			String schemaLocationType = requestOrResponseValidation.getString("schema-location-type");

			return createSchemaRoot(pointerContentFormat, schemaName, schemaLocation, schemaLocationType);		
		}

	}

	private static final String PLUGIN_DESCRIPTOR_SCHEMA_FILENAME = "plugin-descriptor-schema.json";
	private ContentValidator pluginValidator;
	private ClassLoader resourceClassloader;
	
	private final Path descriptorLocation;

	public PluginReader(Path descriptorLocation) throws PluginConfigurationException {
		this(descriptorLocation, PluginReader.class.getClassLoader());
	}
	
	public PluginReader(Path descriptorLocation, ClassLoader resourceClassloader) throws PluginConfigurationException {
		pluginValidator = createPluginValidator();
		this.descriptorLocation = descriptorLocation;
		this.resourceClassloader = resourceClassloader;
	}

	private static ContentValidator createPluginValidator() throws PluginConfigurationException 
	{	
		SchemaDescriptorTree schemaDescriptorTree = SchemaDescriptorTreeBuilder
				.instance(new SchemaDescriptor(ContentFormat.JSON, 
						PLUGIN_DESCRIPTOR_SCHEMA_FILENAME, 
						LocationType.PATH, Paths.get("schema").toString()))
				.build(new DefaultContentOperationSetFactory());
		
/*		TreeNode<SchemaDescriptor> schemaDescriptorRootNode = 
				SchemaDescriptorTreeNodeBuilder.getInstance(new SchemaDescriptor(
					ContentFormat.JSON, PLUGIN_DESCRIPTOR_SCHEMA_FILENAME, 
					LocationType.PATH, Paths.get("schema").toString()))
				.getRoot();*/
		
		try {
			return new ContentValidator(schemaDescriptorTree);			
		} catch (ConfigurationException e) {
			throw new PluginConfigurationException(
					String.format("Could not initialise plugin: Plugin Descriptor validator has not been loaded:%n >%s<",
									e.getMessage()), e);
		}
	}

	public PluginDescriptor parse() throws PluginConfigurationException 
	{	
		try {
			String content = readContentToString(descriptorLocation);
			pluginValidator.validateContent(content);
			
			JSONObject rootJson = new JSONObject(content);			
			return new PluginDescriptorFromJsonHelper().createPluginDescriptor(rootJson);
			
		} catch (IOException e) {				
			String errMsg = String.format("Failed to read plug-in descriptor from '%s'", descriptorLocation);
			throw new PluginConfigurationException(errMsg);
		} catch (ContentValidationException e) {
			String errMsg = String.format("Failed to validate plug-in descriptor from '%s' against '%s'", 
											descriptorLocation, PLUGIN_DESCRIPTOR_SCHEMA_FILENAME);
			throw new PluginConfigurationException(errMsg, e);
		} catch (ConfigurationException e) {
			String errMsg = String.format("Failed to parse plug-in descriptor from '%s': '%s'", 
					descriptorLocation, e.getMessage());
			throw new PluginConfigurationException(errMsg, e);
		}		
	}

	private String readContentToString(Path resourcePath) throws IOException 
	{		
		URL testResourceUrl = resourceClassloader.getResource(resourcePath.toString());
		LambdaExceptionUtil.raiseIssueIf(testResourceUrl, Objects::isNull,
				ConfigurationException::new,
				String.format("Location of plugin's descriptor could not be resolved (%s)", resourcePath.toString()));
				
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(testResourceUrl.openStream()))) {
			
			return reader.lines()
					.parallel()
					.collect(Collectors.joining(System.lineSeparator()));
		}
	}
	
}
