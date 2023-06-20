package org.epos.converter.common.schema.validation;

import static org.epos.converter.common.type.LocationType.PATH;
import static org.epos.converter.common.type.LocationType.URL;
import static org.epos.converter.common.util.LambdaExceptionUtil.raiseIssueIf;
import static org.epos.converter.common.util.LambdaExceptionUtil.sneakyThrow;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.epos.converter.common.collections.TreeNode;
import org.epos.converter.common.schema.validation.exception.ConfigurationException;
import org.epos.converter.common.schema.validation.exception.ContentValidationException;
import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.ContentOperationSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * To replace PayloadValidator this version deals directly with a
 * {@link ConversionSchemaDesscriptor} instance, instead of maps of payload schemas
 *  (i.e. inPayloadSchemas; outPayloadSchemas).
 * 
 * It is intended to be a generic schema validator (not specifically tied to epos message payloads)
 *
 */
public final class ContentValidator {
	
	private static Logger logger = LoggerFactory.getLogger(ContentValidator.class);
	
	private final Map<ContentFormat, ContentOperationSet<?>> operationSets;
	private ClassLoader resourceClassloader;
	private final Pair<SchemaDescriptor, Object> schema; // TODO Implement as schema loading within Tree structure >
	
	public ContentValidator(SchemaDescriptorTree schemaDescriptorTree) throws ConfigurationException {
		this(schemaDescriptorTree, ContentValidator.class.getClassLoader());
	}
		
	public ContentValidator(SchemaDescriptorTree schemaDescriptorTree, ClassLoader resourceClassloader) throws ConfigurationException 
	{
		this.operationSets = schemaDescriptorTree.getContentOpSets();
		this.resourceClassloader = Objects.requireNonNull(resourceClassloader);		
		schema = loadSchema(schemaDescriptorTree.getRootNode());
	}
	
	public void validateContent(String content) throws ContentValidationException {
		
		raiseIssueIf(content, StringUtils::isBlank, ContentValidationException::new,
						String.format("Specified content is empty.", content));
		
		SchemaDescriptor descriptor = schema.getLeft();
		//Optional<String> pointerExpr = descriptor.getPointerExpression();

		ContentOperationSet<?> contentOpSet = operationSets.get(descriptor.getContentFormat());
		ContentOperationSet<?> pointerOpSet = operationSets.get(descriptor.getPointerContentFormat());
	
		String pointerExpr = "";
		try {
			pointerExpr = descriptor.getPointerExpression().isPresent() ? 
									descriptor.getPointerExpression().get() : 
									pointerOpSet.getRootPointerExpr();
			String cleanedContent = pointerOpSet.getAsCleanContent(content);			
			String contentSubstr = pointerOpSet.getSubstrFromContent(pointerExpr, cleanedContent);
			contentOpSet.validateContent(schema.getRight(), contentSubstr);
			
		} catch (ContentValidationException ex) {
			String warnMsg = String.format("%s%n Pointer expression used: >%s<%n Content: >%s<", 
					ex.getMessage(), pointerExpr, content);
			sneakyThrow(new ContentValidationException(warnMsg, ex.getCause()));
		} catch (Exception e) {
			String warnMsg = String.format("Failed to obtain a sub-string from content (due to a '%s') using pointer format for '%s' and pointer expression of '%s'"
					+ "%n Content: >%s<", 
					e.getClass().getName(), descriptor.getPointerContentFormat(), pointerExpr, content);
			sneakyThrow(new ContentValidationException(warnMsg, e.getCause()));
		}
	}
	
	/**
	 * TODO implement as schema loading within Tree structure and return that. 
	 *  The Tree structure should...
	 *  a) replicate that of the one passed in but capable of decorating it with a schema
	 *  b) be capable of extracting correctly from the passed in payload
	 *
	 */
	private Pair<SchemaDescriptor, Object> loadSchema(TreeNode<SchemaDescriptor> rootSchemaDescriptor) throws ConfigurationException {
		
		SchemaDescriptor descriptor = rootSchemaDescriptor.getData();		
		String schemaLocation = descriptor.getSchemaLocation();
		String schemaName = descriptor.getSchemaName();
		
		Optional<URL> schemaPathUrl = Optional.empty();
		
		switch (descriptor.getSchemaLocationType()) {
			case PATH:	
				Path resourcePath = Paths.get(schemaLocation).resolve(schemaName);
				String resourceName = resourcePath.toString().replaceAll("\\\\", "/");
				URL resourceUrl = resourceClassloader.getResource(resourceName);
				schemaPathUrl = Optional.ofNullable(resourceUrl);
				break;
			case URL:
				try {
					schemaPathUrl = Optional.ofNullable(new URL(new URL(schemaLocation), schemaName));
				} catch (MalformedURLException e) {
					String errMsg = String.format(
							"Failed to resolve schema URL. This is constructed from the schema's declared location (%s) and name (%s):%n %s", 
							schemaLocation, schemaName, e.getMessage());
					// TODO log errMsg @ERROR level
					throw new ConfigurationException(errMsg, e);
				}
				break;
			default:
				String errStr = String.format("The Schema Location type, %s, is not currently supported by the validator.%n Supported types are [%s]", 
						descriptor.getSchemaLocationType(), String.join("; ", PATH.getName(), URL.getName()));
				// TODO log errStr @ERROR level
				throw new ConfigurationException(new UnsupportedOperationException(errStr));
		}
		
		schemaPathUrl.orElseThrow(() -> new ConfigurationException(
				String.format("Failed to obtain URL for schema with specified location, >%s<", schemaLocation)));
		
		ContentOperationSet<?> contentOperationSet = operationSets.get(descriptor.getContentFormat());
		
		try {
			Object schema = contentOperationSet.loadSchema(schemaPathUrl.get()).orElseThrow();
			return new ImmutablePair<SchemaDescriptor, Object>(descriptor, schema);
		} catch (NoSuchElementException ex) {
			String fatalMsg = String.format("Content validator could not obtain schema for '%s'", schemaPathUrl.get().getPath());
			logger.error(fatalMsg);
			
			// log FATAL level fatalMsg
			throw new ConfigurationException(fatalMsg);
		}
	}
	
}
