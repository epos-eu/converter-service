package org.epos.converter.common.java;

import static org.epos.converter.common.type.ContentType.APPLICATION_JSON;
import static org.epos.converter.common.type.ContentType.APPLICATION_XML;
import static org.epos.converter.common.type.ContentType.EPOS_COVERAGE_JSON;
import static org.epos.converter.common.type.ContentType.EPOS_GEO_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.epos.converter.common.exception.PayloadProcessingException;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.MappingDescriptor;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CallableJavaPluginTest {
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		System.clearProperty("EPOS-PLUGIN-DESCRIPTOR-LOCATION");
	}

	@AfterEach
	void tearDown() throws Exception {
		System.clearProperty("EPOS-PLUGIN-DESCRIPTOR-LOCATION");
	}
	
	@Test
	void testDoInvoke_SupportedMappingsNOK() {
		
		Path pluginDescriptorPath = Paths.get("sample_mapping_descriptors", "valid");		
		System.setProperty("EPOS-PLUGIN-DESCRIPTOR-LOCATION", pluginDescriptorPath.toString());
		
		List<MappingDescriptor> unsupportedMappings = List.of(
			new MappingDescriptor("tcs/station/fdsn", APPLICATION_XML, EPOS_COVERAGE_JSON),
			new MappingDescriptor("tcs/station/wp10", APPLICATION_XML, EPOS_GEO_JSON),
			new MappingDescriptor("tcs/station/fdsnb", APPLICATION_XML, EPOS_GEO_JSON),
			new MappingDescriptor("tcs/station/wp10b", APPLICATION_JSON, EPOS_GEO_JSON)
		);

		unsupportedMappings.stream().forEach(mappingDescriptor -> {			
			try {
				new CallableJavaPlugin(mappingDescriptor) {					
					@Override
					protected Optional<String> doInvoke(String payload) throws PayloadProcessingException {
						return null;
					}
				};				
				Assert.fail();
			} catch (Throwable e) {
				Assert.assertTrue(e instanceof PluginConfigurationException);
			}			
		});
	}

	@Test
	void testDoInvoke_SupportedMappingsOK() {
		
		// test #1
		testSupportedMappingsOK("conversions-descriptor_#01.json", List.of(
				new MappingDescriptor("tcs/station/fdsn", APPLICATION_XML, EPOS_GEO_JSON),
				new MappingDescriptor("tcs/station/wp10", APPLICATION_JSON, EPOS_GEO_JSON)
			));
		
		// test #2
		testSupportedMappingsOK("conversions-descriptor_#02.json", List.of(
				new MappingDescriptor("QuakeML", APPLICATION_XML, EPOS_GEO_JSON)
			));
		
// ----------------------------------------------------------------------------------------------------------------------------------
/*
 		ConversionDescriptor transformationDescriptor = new ConversionDescriptor(VISUALISE_STATION_TCSJSON.getId());
		
		try {
			CallableJavaPlugin plugin = new CallableJavaPlugin(transformationDescriptor) {

				@Override
				protected Optional<String> doInvoke(String payload) throws PayloadProcessingException {
					return Optional.of("{\"MAPRESULT\": {\"type\": \"FeatureCollection\"}}");
				}

				@Override
				protected Map<ConversionDescriptor, PayloadSchemaDefinitions> getSupportedTransformations() {

					try {
						return Map.of(
							
							// 'visualise/station/wp10-json'
							new ConversionDescriptor(VISUALISE_STATION_TCSJSON.getId()), 
							PayloadSchemaDefinitionsBuilder.getInstance()
								.incomingSchema(new PayloadFormatDescriptor(JSON), "root_payload_schema_in.json")
								.incomingSchema(new PayloadFormatDescriptor(JSON, JSON, "$.TCSRESULT"), "nested_payload_schema_in.json")
								.outgoingSchema(new PayloadFormatDescriptor(JSON), "root_payload_schema_out.json")
								.outgoingSchema(new PayloadFormatDescriptor(JSON, JSON, "$.MAPRESULT"), "nested_payload_schema_out.json")
								.build(),
							
							// 'visualise/station/fsdn-xml'
							new ConversionDescriptor(VISUALISE_STATION_TCSXML.getId()), 						
							PayloadSchemaDefinitionsBuilder.getInstance()
								.incomingSchema(new PayloadFormatDescriptor(JSON), "root_payload_schema_in.json")
								.incomingSchema(new PayloadFormatDescriptor(JSON, XML, "$.TCSRESULT"), "nested_payload_schema_in.xml")
								.outgoingSchema(new PayloadFormatDescriptor(JSON), "root_payload_schema_out.json")											
								.build()
						);
					} catch (ConfigurationException e) {
						// TODO later all will be read from a config file
						return null;
					}
				}

				@Override
				protected String getOutgoingPayloadSchemaLocation() {
					return "schema_validation_testing/schema_set_SKELETON/schemas/payload_outgoing/";
				}

				@Override
				protected String getIncomingPayloadSchemaLocation() {
					return "schema_validation_testing/schema_set_SKELETON/schemas/payload_incoming/";
				}
			
			};
			
			String incomingPayload = "{\"TCSRESULT\":{\"result\":[]}}";			
			String actual = plugin.invoke(incomingPayload);
			assertEquals("{\"MAPRESULT\": {\"type\": \"FeatureCollection\"}}", actual);	
			
		} catch (PluginConfigurationException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (PayloadMappingException e) {
			e.printStackTrace();
			Assert.fail();
		}
*/
	}

	private void testSupportedMappingsOK(String descriptorName, List<MappingDescriptor> mappingsToTest) {
		
		Path pluginDescriptorsPath = Paths.get("sample_mapping_descriptors", "valid");
		
		Path pluginDescriptorPath = pluginDescriptorsPath.resolve(descriptorName);
		System.setProperty("EPOS-PLUGIN-DESCRIPTOR-LOCATION", pluginDescriptorPath.toString());
		
		mappingsToTest.stream().forEach(mappingDescriptor -> {
			
			try {
				CallableJavaPlugin plugin = new CallableJavaPlugin(mappingDescriptor) {
					
					@Override
					protected Optional<String> doInvoke(String payload) throws PayloadProcessingException {
						return Optional.of("{\"MAPRESULT\": {\"type\": \"FeatureCollection\"}}");
					}
				};
				
				String incomingPayload = "{\"TCSRESULT\":{\"result\":[]}}";			
				String actual = plugin.invoke(incomingPayload);
				assertEquals("{\"MAPRESULT\": {\"type\": \"FeatureCollection\"}}", actual);
			} catch (PluginConfigurationException e) {
				e.printStackTrace();
				Assert.fail();
			} catch (PayloadProcessingException e) {
				e.printStackTrace();
				Assert.fail();
			}
			
		});
	}
	
	
/*	@Test
	void testCallableJavaPlugin_specifiedSchemaFileNotPresent() {

		ConversionDescriptor transformationDescriptor = new ConversionDescriptor(VISUALISE_STATION_TCSXML.getId());

		try {
			new CallableJavaPlugin(transformationDescriptor) {

				@Override
				protected Optional<String> doInvoke(String payload) throws PayloadProcessingException {
					return null;
				}

				@Override
				protected Map<ConversionDescriptor, PayloadSchemaDefinitions> getSupportedTransformations() throws PluginConfigurationException {
					try {
						return Map.of(
								
							// 'visualise/station/wp10-json'
							new ConversionDescriptor(VISUALISE_STATION_TCSJSON.getId()), 
							PayloadSchemaDefinitionsBuilder.getInstance()
								.incomingSchema(new PayloadFormatDescriptor(JSON), "root_payload_schema_in.json")
								.incomingSchema(new PayloadFormatDescriptor(JSON, JSON, "$.TCSRESULT"), "nested_payload_schema_in.json")
								.outgoingSchema(new PayloadFormatDescriptor(JSON), "root_payload_schema_out.json")
								.outgoingSchema(new PayloadFormatDescriptor(JSON, JSON, "$.MAPRESULT"), "nested_payload_schema_out.json")
								.build(),
							
							// 'visualise/station/fsdn-xml'
							new ConversionDescriptor(VISUALISE_STATION_TCSXML.getId()), 						
							PayloadSchemaDefinitionsBuilder.getInstance()
								.incomingSchema(new PayloadFormatDescriptor(JSON), "root_payload_schema_in.json")
								.incomingSchema(new PayloadFormatDescriptor(JSON, XML, "$.TCSRESULT"), "nested_payload_schema_in.xml")
								.outgoingSchema(new PayloadFormatDescriptor(JSON), "root_payload_schema_out.json")
								// PROBLEM!! Specified schema file not present
								.outgoingSchema(new PayloadFormatDescriptor(JSON, JSON, "$.MAPRESULT"), "nested_payload_schema_out_GONE_FISHING.json")											
								.build()
						);
					} catch (ConfigurationException e) {
						// TODO later all will be read from a config file
						return null;
					}
					
				}
				
				@Override
				protected String getOutgoingPayloadSchemaLocation() {
					return "schema_validation_testing/schema_set_#3/schemas/payload_outgoing/";
				}

				@Override
				protected String getIncomingPayloadSchemaLocation() {
					return "schema_validation_testing/schema_set_#3/schemas/payload_incoming/";
				}
				
			};			
		} catch (PluginConfigurationException e) {			
			Assert.assertTrue(true);
		}

	}*/
	
}
