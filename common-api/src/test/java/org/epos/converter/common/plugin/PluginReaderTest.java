package org.epos.converter.common.plugin;

import static org.epos.converter.common.type.ContentFormat.JSON;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.epos.converter.common.plugin.descriptor.PluginReader;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.plugin.type.PluginDescriptor;
import org.epos.converter.common.schema.validation.exception.ConfigurationException;
import org.epos.converter.common.schema.validation.exception.ContentValidationException;
import org.epos.converter.common.test.TestResourcesAccessor;
import org.epos.converter.common.test.TestUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


class PluginReaderTest {
	
	private static TestResourcesAccessor testResourcesAccessor;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		testResourcesAccessor = new TestResourcesAccessor(Paths.get("sample_plugin_descriptors"), true);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}
	
	/**
	 * for the SUT need to pass in a relative path - hence the relativising of an absolute path
	 */
	static Stream<Path> createResourcePaths_ParseOK() throws IOException {
		return testResourcesAccessor.getResourcePaths(Paths.get("valid"), JSON)
				.map(p -> TestUtils.MODULE_ROOT_TEST_RESOURCES_PATH.relativize(p));
	}
	
	/**
	 * for the SUT need to pass in a relative path - hence the relativising of an absolute path
	 */
	static Stream<Path> createResourcePaths_ValidationNOK() throws IOException {
		return testResourcesAccessor.getResourcePaths(Paths.get("invalid", "validation"), JSON)
				.map(p -> TestUtils.MODULE_ROOT_TEST_RESOURCES_PATH.relativize(p));
	}
	
	/**
	 * for the SUT need to pass in a relative path - hence the relativising of an absolute path
	 */
	static Stream<Path> createResourcePaths_ParseNOK() throws IOException {
		return testResourcesAccessor.getResourcePaths(Paths.get("invalid", "parse"), JSON)
				.map(p -> TestUtils.MODULE_ROOT_TEST_RESOURCES_PATH.relativize(p));
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@ParameterizedTest
	@MethodSource("createResourcePaths_ParseOK")
	void testParse_OK(Path testResource)
	{		
		try {
			PluginReader reader = new PluginReader(testResource);
			PluginDescriptor pluginDescriptor = reader.parse();
			Assert.assertNotNull(pluginDescriptor.getHeader());
		} catch (PluginConfigurationException e1) {
			e1.printStackTrace();
			Assert.fail();
		}
	}
	
	
	@ParameterizedTest
	@MethodSource("createResourcePaths_ParseNOK")
	void testParse_parseNOK(Path testResource) throws PluginConfigurationException
	{
		PluginReader reader = new PluginReader(testResource);
		
		try {
			reader.parse();
			Assert.fail();
		} catch (PluginConfigurationException e) {
			// passed
		}
	}
	
	@ParameterizedTest
	@MethodSource("createResourcePaths_ValidationNOK")
	void testParse_validationNOK(Path testResource) throws PluginConfigurationException 
	{
		PluginReader reader = new PluginReader(testResource);
		
		try {			
			reader.parse();
			Assert.fail();
		} catch (PluginConfigurationException e) {			
			Assert.assertTrue(e.getCause() instanceof ContentValidationException);
		}
	}

}
