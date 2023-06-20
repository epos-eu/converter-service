package org.epos.converter.app.plugin.managment;

import static org.epos.converter.common.type.ContentFormat.JSON;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

import org.epos.converter.app.plugin.managment.model.PluginDescriptor;
import org.epos.converter.common.plugin.exception.PluginConfigurationException;
import org.epos.converter.common.test.TestResourcesAccessor;
import org.epos.converter.common.test.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class PluginDescriptorsReaderJsonTest {
	
	private static TestResourcesAccessor resourceAccessor;
	private static Path jsonSchemaPath;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		resourceAccessor = new TestResourcesAccessor(Paths.get("payloads", "plugin-metadata"), true);
		// TODO: write 'plugin-metadata-schema.json'
		jsonSchemaPath = Paths.get("schemas", "plugin-metadata-schema.json");
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

	static Stream<Path> createResourcePaths_JSON_OK() throws IOException {
		return resourceAccessor.getResourcePaths(Paths.get("valid"), JSON);
	}
	
	@Test
	void testInstance() {
		PluginDescriptorsReader reader = new PluginDescriptorsReaderJson(jsonSchemaPath.toString());
		assertNotNull(reader);
	}
	
	/**
	 * Limited test: verifies that at least one {@link PluginDescriptor} entity has been created from the
	 * deserialisation.
	 */
	@ParameterizedTest
	@MethodSource("createResourcePaths_JSON_OK")
	void testParse(Path testContentPath) throws IOException 
	{
		PluginDescriptorsReader reader = new PluginDescriptorsReaderJson(jsonSchemaPath.toString());
		String content = TestUtils.readContentToString(testContentPath);
		try {
			Set<PluginDescriptor> pluginsMetadata = reader.parse(content);
			assertTrue(pluginsMetadata.size() > 0);
		} catch (PluginConfigurationException e) {
			fail(e);
		}
	}

}
