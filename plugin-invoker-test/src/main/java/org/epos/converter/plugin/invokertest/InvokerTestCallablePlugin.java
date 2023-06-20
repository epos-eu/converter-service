package org.epos.converter.plugin.invokertest;

import java.util.Optional;

import org.epos.converter.common.exception.PayloadProcessingException;
import org.epos.converter.common.java.CallableJavaPlugin;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.MappingDescriptor;
import org.epos.converter.common.util.LambdaExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;


public class InvokerTestCallablePlugin extends CallableJavaPlugin {
	
	private static Logger logger = LoggerFactory.getLogger(InvokerTestCallablePlugin.class);

	public InvokerTestCallablePlugin(MappingDescriptor mapping)
			throws PluginConfigurationException {
		super(mapping);
	}

	@Override
	protected Optional<String> doInvoke(String payload) throws PayloadProcessingException {
		
		LambdaExceptionUtil lambdaExceptionUtil = new LambdaExceptionUtil();
		String classLoaderClazz = lambdaExceptionUtil.getClass().getClassLoader().getClass().getName();

		String outputFromLocalClassInstance = lambdaExceptionUtil.toString();
		logger.info("Callee: {} | {}", outputFromLocalClassInstance, classLoaderClazz);
		
		String transformedPayload = String.format(
				"{\"in-payload\":\"%s\",\"mapping\":\"%s\",\"local-class\":\"%s\"}",
				payload.replaceAll("[{}\"]", ""),
				mapping.getStatementOfUniqueness(),
				outputFromLocalClassInstance);

		return Optional.of(transformedPayload);
	}
/*
	@Override
	protected Map<ConversionDescriptor, ConversionSchemasDescriptor> getSupportedTransformations() {
		
		final ContentType requestJsonMediaType = ContentType.APPLICATION_JSON;
		final ContentType responseJsonMediaType = ContentType.APPLICATION_JSON;
		
		final ContentType requestXmlMediaType = ContentType.APPLICATION_JSON;
		final ContentType responseXmlMediaType = ContentType.APPLICATION_JSON;
		
		return Map.of(
				
			// Ok
			new ConversionDescriptor("OTHER_TRANSFORMATION_TYPE-1", requestJsonMediaType, responseJsonMediaType),
			new ConversionSchemasDescriptor(
				// Request schema
				SchemaDescriptorTreeNodeBuilder.getInstance(							
					new SchemaDescriptor(requestJsonMediaType.getContentFormat(), 
										 getPayloadOperationSet(requestJsonMediaType.getContentFormat()).getRootPointerExpr(),
										 requestJsonMediaType.getContentFormat(), 
										 "my_nice_schema_in.json", 
										 LocationType.PATH, "schemas/payload_incoming/")
					).getRoot(),					
				// Response schema
				SchemaDescriptorTreeNodeBuilder.getInstance(							
					new SchemaDescriptor(responseJsonMediaType.getContentFormat(), 
										 getPayloadOperationSet(responseJsonMediaType.getContentFormat()).getRootPointerExpr(),
										 responseJsonMediaType.getContentFormat(), 
										 "my_nice_schema_out.json", 
										 LocationType.PATH, "schemas/payload_outgoing/")
					).getRoot()	
			),
				PayloadSchemaDefinitionsBuilder.getInstance()
					.incomingSchema(new PayloadFormatDescriptor(JSON), "my_nice_schema_in.json")
					.outgoingSchema(new PayloadFormatDescriptor(JSON), "my_nice_schema_out.json")					
					.build(),	
			
			// Inconsistent configuration: in format equals XML but looks like JSON schema validation file!
			new ConversionDescriptor("OTHER_TRANSFORMATION_TYPE-2", requestXmlMediaType, responseJsonMediaType),
			new ConversionSchemasDescriptor(
				// Request schema
				SchemaDescriptorTreeNodeBuilder.getInstance(							
					new SchemaDescriptor(requestXmlMediaType.getContentFormat(), 
										 getPayloadOperationSet(requestXmlMediaType.getContentFormat()).getRootPointerExpr(),
										 requestXmlMediaType.getContentFormat(), 
										 "my_nice_schema_in.json", 
										 LocationType.PATH, "schemas/payload_incoming/")
					).getRoot(),					
				// Response schema
				SchemaDescriptorTreeNodeBuilder.getInstance(							
					new SchemaDescriptor(responseJsonMediaType.getContentFormat(), 
										 getPayloadOperationSet(responseJsonMediaType.getContentFormat()).getRootPointerExpr(),
										 responseJsonMediaType.getContentFormat(), 
										 "my_nice_schema_out.json", 
										 LocationType.PATH, "schemas/payload_outgoing/")
					).getRoot()	
				),
				PayloadSchemaDefinitionsBuilder.getInstance()
					.incomingSchema(new PayloadFormatDescriptor(XML), "my_nice_schema_in.json")
					.outgoingSchema(new PayloadFormatDescriptor(JSON), "my_nice_schema_out.json")
					.build(),
				
			// Inconsistent configuration: out schema file is not present!
			new ConversionDescriptor("OTHER_TRANSFORMATION_TYPE-3", requestJsonMediaType, responseXmlMediaType),
			new ConversionSchemasDescriptor(
				// Request schema
				SchemaDescriptorTreeNodeBuilder.getInstance(							
					new SchemaDescriptor(requestJsonMediaType.getContentFormat(), 
										 getPayloadOperationSet(requestJsonMediaType.getContentFormat()).getRootPointerExpr(),
										 requestJsonMediaType.getContentFormat(), 
										 "my_nice_schema_in.json", 
										 LocationType.PATH, "schemas/payload_incoming/")
					).getRoot(),					
				// Response schema
				SchemaDescriptorTreeNodeBuilder.getInstance(							
					new SchemaDescriptor(responseXmlMediaType.getContentFormat(), 
										 getPayloadOperationSet(responseXmlMediaType.getContentFormat()).getRootPointerExpr(),
										 responseXmlMediaType.getContentFormat(), 
										 "my_missing_schema_out.xsd", 
										 LocationType.PATH, "schemas/payload_outgoing/")
					).getRoot()
				)
			
				PayloadSchemaDefinitionsBuilder.getInstance()
					.incomingSchema(new PayloadFormatDescriptor(JSON), "my_nice_schema_in.json")
					.outgoingSchema(new PayloadFormatDescriptor(XML), "my_missing_schema_out.xsd")
					.build(),			
			
			
			// WOULD NEED TO CONSTRUCT SOME VALID PAYLOADS FOR SCHEMA IF TO INCLUDE SUCH TESTS!
				
				// Inconsistent configuration: in schema for root needs to be defined as the first!
				new ConversionDescriptor("OTHER_TRANSFORMATION_TYPE-4", requestJsonMediaType, responseXmlMediaType),
				PayloadSchemaDefinitionsBuilder.getInstance()
					.incomingSchema(new PayloadFormatDescriptor(JSON, JSON, "$.blah"), "my_nice_schema_in.json")
					.incomingSchema(new PayloadFormatDescriptor(JSON), "my_nice_schema_in.json")
					.outgoingSchema(new PayloadFormatDescriptor(XML), "my_missing_schema_out.xsd")
					.build(),

				// Inconsistent configuration: cannot have multiple root schemas defined!
				new ConversionDescriptor("OTHER_TRANSFORMATION_TYPE-5"),
				PayloadSchemaDefinitionsBuilder.getInstance()					
					.incomingSchema(new PayloadFormatDescriptor(JSON), "my_nice_schema_in.json")
					.incomingSchema(new PayloadFormatDescriptor(JSON, JSON, "$.blah"), "my_nice_schema_in.json")
					.incomingSchema(new PayloadFormatDescriptor(JSON), "my_nice_schema_in.json")
					.outgoingSchema(new PayloadFormatDescriptor(XML), "my_missing_schema_out.xsd")
					.build()

		);
	}
	*/

}
