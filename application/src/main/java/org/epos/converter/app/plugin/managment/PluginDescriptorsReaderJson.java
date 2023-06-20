package org.epos.converter.app.plugin.managment;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;

import org.epos.converter.app.configuration.properties.YamlPropertySourceFactory;
import org.epos.converter.app.plugin.managment.model.PluginDescriptor;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.schema.validation.ContentValidator;
import org.epos.converter.common.schema.validation.SchemaDescriptorTree;
import org.epos.converter.common.schema.validation.SchemaDescriptorTree.SchemaDescriptorTreeBuilder;
import org.epos.converter.common.schema.validation.exception.ConfigurationException;
import org.epos.converter.common.schema.validation.exception.ContentValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Parses a JSON Object representing metadata for one or more <b>Converter</b> plug-ins.
 * This service is thread-safe.
 * 
 * <p><b>Parsing entails...</b>
 * <dl> 
 * <dt>(1) JSON String validation</dt><dd>JSON validated against the internal JSON Schema for plug-in metadata</dd>
 * <dt>(2) Unmarshalling to DOs</dt><dd>JSON is unmarshalled to the DOs used to represent plug-in metatdata internally</dd>
 * </dl>
 * </p>
 * 
 */
@Component
@PropertySource(value = "classpath:pluginsmetadata-plugin_ICSC_V1.yml", factory = YamlPropertySourceFactory.class)
public class PluginDescriptorsReaderJson implements PluginDescriptorsReader {
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private final ContentValidator validator;
	
	public PluginDescriptorsReaderJson(@Value("${plugins.metadata.bootstrap.plugin.request-schema}")String jsonSchemaPath) {
		this.validator = createSimpleJsonValidator(Path.of(jsonSchemaPath));
	}
	
	private static ContentValidator createSimpleJsonValidator(Path jsonSchemaPath) 
	{
		SchemaDescriptorTree schemaDescriptorTree = SchemaDescriptorTreeBuilder
				.simpleJsonInstance(jsonSchemaPath)
				.build();
		try {
			return new ContentValidator(schemaDescriptorTree);
		} catch (ConfigurationException e) {
			String errMsg = String.format("Failed to create %s instance: Validator could not be loaded.", 
					PluginDescriptorsReaderJson.class.getName());
			throw new PluginManagementInitException(errMsg, e);
		}
	}


	@Override
	public Set<PluginDescriptor> parse(String content) throws PluginConfigurationException
	{
		try {
			validator.validateContent(content);
			return unmarshalConversions(content);
		} catch (ContentValidationException e) {
			throw new PluginConfigurationException("Failed to validate supplied plugins metadata against the supplied schema", e);
		}
	}

	private Set<PluginDescriptor> unmarshalConversions(String content) throws PluginConfigurationException 
	{
		try {
			return objectMapper.readValue(content, new TypeReference<Set<PluginDescriptor>>() {});
		} catch (JsonParseException e) {
			throw new PluginConfigurationException("Failed to parse supplied JSON formatted plugins metadata (Unexpected behaviour as JSON should have already been validated", e);
		} catch (JsonMappingException e) {
			throw new PluginConfigurationException("Failed to unmarshal supplied JSON formatted plugins metadata (Unexpected behaviour as JSON should have already been validated", e);
		} catch (IOException e) {
			throw new PluginConfigurationException("Failed to parse supplied JSON formatted plugins metadata due to IO issue", e);
		}
	}
	
}
