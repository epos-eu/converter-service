package org.epos.converter.common.payload.validation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.epos.converter.common.exception.UnsupportedPayloadConversionException;
import org.epos.converter.common.plugin.descriptor.PluginReader;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.ConversionDescriptor;
import org.epos.converter.common.plugin.type.ExecutionDescriptor;
import org.epos.converter.common.plugin.type.MappingDescriptor;
import org.epos.converter.common.plugin.type.PluginDescriptor;
import org.epos.converter.common.plugin.type.PluginProxyType;
import org.epos.converter.common.schema.validation.DefaultContentOperationSetFactory;
import org.epos.converter.common.type.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PayloadValidatorTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testPayloadValidator_InstantiationOK() 
	{	
		ExecutionDescriptor executionDescriptor = new ExecutionDescriptor(
				PluginProxyType.JAVA_REFLECTION, 
				"/plugins/java",
				"artefact-relative-location",
				"plugin-station-default-0.0.1.jar",
				"org.epos.converter.plugin.visualisestation.VisualiseStationXMLCallablePlugin");		
		MappingDescriptor mappingDescriptor = new MappingDescriptor("tcs/station/fdsn", ContentType.APPLICATION_XML, ContentType.EPOS_GEO_JSON);
		
		ConversionDescriptor conversionDescriptor = new ConversionDescriptor(executionDescriptor, mappingDescriptor);
		
		try {
			Path pluginDescriptorPath = Paths.get("sample_plugin_descriptors", "valid", "plugin-descriptor.json");
			PluginDescriptor pluginDescriptor = new PluginReader(pluginDescriptorPath).parse();
			PayloadValidator payloadValidator = new PayloadValidator(
					conversionDescriptor, pluginDescriptor, 
					new DefaultContentOperationSetFactory());
			assertNotNull(payloadValidator);
		} catch (UnsupportedPayloadConversionException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (PluginConfigurationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
