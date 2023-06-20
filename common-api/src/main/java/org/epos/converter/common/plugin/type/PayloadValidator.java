package org.epos.converter.common.plugin.type;

import static java.util.stream.Collectors.toMap;
import static org.epos.converter.common.util.LambdaExceptionUtil.raiseIssueIf;
import static org.epos.converter.common.util.LambdaExceptionUtil.rethrowFunction;
import static org.epos.converter.common.util.LambdaExceptionUtil.sneakyThrow;

import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.epos.converter.common.schema.validation.exception.ConfigurationException;
import org.epos.converter.common.schema.validation.exception.ContentValidationException;
import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.ContentOperationSet;
import org.epos.converter.common.type.PayloadFormatDescriptor;
import org.epos.converter.common.type.PayloadSchemaDefinitions;
import org.epos.converter.common.util.LambdaExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated to remove - replaced by {@link org.epos.converter.common.payload.validation.PayloadValidator}
 *
 */
public final class PayloadValidator {
	
	private static Logger logger = LoggerFactory.getLogger(PayloadValidator.class);
	
	private final Map<ContentFormat, ContentOperationSet<?>> payloadOperationSets;
	
	private Map<PayloadFormatDescriptor, Object> inPayloadSchemas;
	private Map<PayloadFormatDescriptor, Object> outPayloadSchemas;
	private ClassLoader resourceClassloader;
	
	private PayloadValidator(String incomingSchemaLocation, String outgoingSchemaLocation, 
			PayloadSchemaDefinitions payloadSchemaDefinitions, 
			Map<ContentFormat, ContentOperationSet<?>> payloadProcessors,
			ClassLoader resourceClassloader) {
		
		super();
		this.payloadOperationSets = payloadProcessors;
		this.resourceClassloader = resourceClassloader;
		inPayloadSchemas = loadPayloadSchema(payloadSchemaDefinitions.getInPayloadSchemas(), incomingSchemaLocation);
		outPayloadSchemas = loadPayloadSchema(payloadSchemaDefinitions.getOutPayloadSchemas(), outgoingSchemaLocation);
	}
	
	public void validateIncomingPayload(String payload) throws ContentValidationException {
		validatePayload(inPayloadSchemas, payload);
	}
	
	public void validateOutgoingPayload(String payload) throws ContentValidationException {
		validatePayload(outPayloadSchemas, payload);
	}
	
	/**
	 * Extracts payload part for each of the declared schemas (based on the ContentFormat and its XPath/JSONPath)
	 *  and then validate these payload parts against their respective schemas
	 */
	private void validatePayload(Map<PayloadFormatDescriptor, Object> payloadSchemas, String payload) throws ContentValidationException {
		
		payloadSchemas.keySet().forEach(k -> {
		
			LambdaExceptionUtil.raiseIssueIf(payload, StringUtils::isBlank,
							ContentValidationException::new,
							String.format("Specified payload is empty.", payload));
			
			ContentFormat targetFormat = k.getTargetFormat();
			ContentFormat pointerFormat;
			String pointerExpr;
			
			// if format for pointer has not been defined then assume dealing with a root-level schema 
			pointerFormat = (k.getPointerFormat().isPresent()) ? k.getPointerFormat().get() : targetFormat;
			ContentOperationSet<?> payloadOperationSet = getPayloadOperationSet(pointerFormat);

			pointerExpr = (k.getPointerExpr().isPresent()) ? k.getPointerExpr().get() : payloadOperationSet.getRootPointerExpr();
			Object schema = payloadSchemas.get(k);
		
			try {
				String payloadSubstr = payloadOperationSet.getSubstrFromContent(pointerExpr, payload);
				
				/*			raiseIssueIf(payloadSubstr, Objects::isNull,
				PayloadValidationException_::new,
				String.format("Failed to obtain a sub-string from payload using pointer format of '%s' and pointer expression of '%s'",
						pointerFormat, pointerExpr));*/
				
				getPayloadOperationSet(targetFormat).validateContent(schema, payloadSubstr);
			} catch (ContentValidationException ex) {
				String warnMsg = String.format("%s%n Pointer expression used: >%s<%n Payload: >%s<", 
						ex.getMessage(), pointerExpr, payload);
				sneakyThrow(new ContentValidationException(warnMsg, ex.getCause()));
			} catch (Exception e) {
				String warnMsg = String.format("Failed to obtain a sub-string from payload using pointer format for '%s' and pointer expression of '%s'"
						+ "%n Payload: >%s<", 
						pointerFormat, pointerExpr, payload);
				sneakyThrow(new ContentValidationException(warnMsg, e.getCause()));
			}
		});		

	}

	private Map<PayloadFormatDescriptor, Object> loadPayloadSchema(
								Map<PayloadFormatDescriptor, String> payloadSchemas, 
								String schemaRelativeLocation) {
		
		  return payloadSchemas.entrySet()
			.stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				rethrowFunction(e -> {
					String schemaFilename = e.getValue();
					ContentFormat targetFormat = e.getKey().getTargetFormat();
					
					validatePayloadSchemaFile(targetFormat, schemaFilename);
					
					String schemaPath = schemaRelativeLocation + schemaFilename;
					URL schemaPathUrl = resourceClassloader.getResource(schemaPath);
					
					ContentOperationSet<?> payloadProcessor = getPayloadOperationSet(targetFormat);

					raiseIssueIf(schemaPathUrl, Objects::isNull,
							ConfigurationException::new,
							String.format("Failed to obtain URL for schema with path, >%s<", schemaPath));
				try {
					return payloadProcessor.loadSchema(schemaPathUrl).orElseThrow();
				} catch (NoSuchElementException ex) {
					String fatalMsg = String.format("Payload processing failed to obtain schema for '%s'", schemaPathUrl.getPath());
					logger.error(fatalMsg);
					// log FATAL level fatalMsg
					throw new ConfigurationException(fatalMsg);
				}
			})
		));
	}
	
	private ContentOperationSet<?> getPayloadOperationSet(ContentFormat payloadFormat) {
		return payloadOperationSets.get(payloadFormat);
	}


	/**
	 * @deprecated validation moved to SchemaDescriptor initialisation code
	 * Checks specified schema file has expected file extension
	 */
	private void validatePayloadSchemaFile(ContentFormat format, String payloadSchemaFileName) throws ConfigurationException {
		
		String schemaFileExtension = format.getSchemaFileExtension();
		if (!payloadSchemaFileName.endsWith(schemaFileExtension)) {
			String errMsg = String.format(
				"Inconsistency between specified target format '%s' and payload schema file '%s'"
					, format.name(), payloadSchemaFileName);
			throw new ConfigurationException(errMsg);
		}
	}

	// ---------------------------------------- <<<<< BUILDER >>>>> ----------------------------------------
	
	/**
	 * @deprecated to be removed!
	 */
	public static class PayloadValidatorBuilder {
		
		private final String incomingSchemaLocation;
		private final String outgoingSchemaLocation;
		private final PayloadSchemaDefinitions payloadSchemaDefinitions;
		private final List<ContentOperationSet<?>> payloadOperationSets = new ArrayList<>();
		private ClassLoader resourceClassloader;
				
		private PayloadValidatorBuilder(String incomingSchemaLocation, String outgoingSchemaLocation,
				PayloadSchemaDefinitions payloadSchemaDefinitions) {
			super();
			this.incomingSchemaLocation = incomingSchemaLocation;
			this.outgoingSchemaLocation = outgoingSchemaLocation;
			this.payloadSchemaDefinitions = payloadSchemaDefinitions;
		}

		public static PayloadValidatorBuilder getInstance(String incomingSchemaLocation, String outgoingSchemaLocation, PayloadSchemaDefinitions payloadDefinitions) {
			return new PayloadValidatorBuilder(incomingSchemaLocation, outgoingSchemaLocation, payloadDefinitions);
		}
		
		/**
		 * To support use with plug-ins (running in the same JVM) where it's non-shared classes are under the control of it's own 
		 * {@link ClassLoader}
		 */
		public PayloadValidatorBuilder addClassLoader(ClassLoader resourceClassloader) {
			this.resourceClassloader = resourceClassloader;
			return this;
		}
		
		public PayloadValidatorBuilder operationSet(ContentOperationSet<?> payloadOperationSet) {
			payloadOperationSets.add(payloadOperationSet);
			return this;
		}
		
		public PayloadValidator build() throws ConfigurationException {
			try {
				Map<ContentFormat, ContentOperationSet<?>> payloadOperationSetsAsMap = getPayloadOperationSetsAsMap();		
				validatePayloadOperationSetSupport(payloadOperationSetsAsMap.keySet());
				return new PayloadValidator(incomingSchemaLocation, outgoingSchemaLocation, payloadSchemaDefinitions, 
						payloadOperationSetsAsMap,
						(resourceClassloader == null) ? PayloadValidator.class.getClassLoader() : resourceClassloader);
			} catch (IllegalArgumentException e) {
				throw new ConfigurationException("Failed to instantiate validator for payload due to supplied configuration", e);
			}
		}

		private void validatePayloadOperationSetSupport(Set<ContentFormat> supportedPayloadformats) throws IllegalArgumentException {
			payloadSchemaDefinitions.getInPayloadSchemas().keySet().stream()
				.forEach(e -> {
					
					Optional<ContentFormat> pointerFormat = e.getPointerFormat();
					if (pointerFormat.isPresent() && !supportedPayloadformats.contains(e.getPointerFormat().get())) {
						throw new IllegalArgumentException("Payload Operation Set support is required for " + e.getPointerFormat() + " but none specified.");
					};
					
					if (!supportedPayloadformats.contains(e.getTargetFormat())) {
						throw new IllegalArgumentException("Payload Operation Set support is required for " + e.getTargetFormat() + " but none specified.");
					}
				});
		}

		private Map<ContentFormat, ContentOperationSet<?>> getPayloadOperationSetsAsMap() throws IllegalArgumentException {
			Map<ContentFormat, ContentOperationSet<?>> payloadOperationSetsAsMap = payloadOperationSets.stream()
				.peek(e -> { 
					if (e.getSupportedContentFormat() == null) {
						throw new IllegalArgumentException("No payload format has been associated with a specified ContentOperationSet");
					}})
				.collect(toMap(
					e -> e.getSupportedContentFormat(), 
					Function.identity(),
					(e1, e2) -> {
						if (e1.getClass() != e2.getClass()) {
							throw new IllegalArgumentException("Duplicate keys " + e1 + "and " + e2 + ".");					
						}						
						return e1;	// try to be forgiving
					},
					() -> new EnumMap<>(ContentFormat.class)
				));
			return payloadOperationSetsAsMap;
		}
	}
	
}
