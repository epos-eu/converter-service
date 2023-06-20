package org.epos.converter.common.validation.schema;

import static org.epos.converter.common.type.ContentFormat.JSON;
import static org.epos.converter.common.type.ContentFormat.XML;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.epos.converter.common.schema.validation.ContentValidator;
import org.epos.converter.common.schema.validation.DefaultContentOperationSetFactory;
import org.epos.converter.common.schema.validation.SchemaDescriptor;
import org.epos.converter.common.schema.validation.SchemaDescriptorTree;
import org.epos.converter.common.schema.validation.SchemaDescriptorTree.SchemaDescriptorTreeBuilder;
import org.epos.converter.common.schema.validation.exception.ConfigurationException;
import org.epos.converter.common.schema.validation.exception.ContentValidationException;
import org.epos.converter.common.test.TestResourcesAccessor;
import org.epos.converter.common.test.TestUtils;
import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.LocationType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ContentValidatorTest {
	
	private static TestResourcesAccessor resourceAccessor;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		resourceAccessor = new TestResourcesAccessor(Paths.get("content_for_schema_testing"), true);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}
		
	static Stream<Pair<ContentFormat, Path>> createResourcePaths_OK() throws IOException {
		return Stream.concat(
			resourceAccessor.getResourcePaths(Paths.get("valid"), JSON).map(p -> new ImmutablePair<>(JSON, p)),
			resourceAccessor.getResourcePaths(Paths.get("valid"), XML).map(p -> new ImmutablePair<>(XML, p)));
	}
	
	static Stream<Path> createResourcePaths_JSON_OK() throws IOException {
		return resourceAccessor.getResourcePaths(Paths.get("valid"), JSON);
	}

	static Stream<Path> createResourcePaths_JSON_NOK() throws IOException {
		return resourceAccessor.getResourcePaths(Paths.get("invalid"), JSON);
	}
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@ParameterizedTest
	@MethodSource("createResourcePaths_JSON_OK")
	void testValidateContent_SimpleJsonInstance_OK(Path testContentPath) throws ConfigurationException, IOException
	{
		Path pathOfSchema = Paths.get("schemas", "plugin-descriptor.json");
			
		SchemaDescriptorTree schemaDescriptorTree = SchemaDescriptorTreeBuilder
				.simpleJsonInstance(pathOfSchema)
				.build();
			
		ContentValidator validator = new ContentValidator(schemaDescriptorTree);
		String content = TestUtils.readContentToString(testContentPath);
		try {
			validator.validateContent(content);
			assertTrue(true);
		} catch (ContentValidationException e) {
			fail(e);
		}
	}
	
	
	@ParameterizedTest
	@MethodSource("createResourcePaths_JSON_NOK")
	void testValidateContent_SimpleJsonInstance_NOK(Path testContentPath) throws ConfigurationException, IOException
	{
		Path pathOfSchema = Paths.get("schemas", "plugin-descriptor.json");
		
		SchemaDescriptorTree schemaDescriptorTree = SchemaDescriptorTreeBuilder
				.simpleJsonInstance(pathOfSchema)
				.build();
		
		ContentValidator validator = new ContentValidator(schemaDescriptorTree);
		String content = TestUtils.readContentToString(testContentPath);
		try {
			validator.validateContent(content);
			fail();
		} catch (ContentValidationException e) {
			assertTrue(true);
		}
	}
	

	@ParameterizedTest
	@MethodSource("createResourcePaths_OK")
	void testValidateContent_OK(Pair<ContentFormat, Path> testResource) {
		
		ContentFormat contentFormat = testResource.getLeft();
		Path resourcePath = testResource.getRight();
		
		SchemaDescriptor rootSchemaDescriptor = null;

		try {
			switch (contentFormat) {
			case JSON : 
				rootSchemaDescriptor = new SchemaDescriptor(
					ContentFormat.JSON, "plugin-descriptor.json", 
					LocationType.PATH, Paths.get("schemas").toString());
				break;
				
			case XML :
				rootSchemaDescriptor = new SchemaDescriptor(
					ContentFormat.XML, "fsdn-station-1.0.xsd", 
					LocationType.PATH, Paths.get("schemas").toString());
				break;
				
			default:				
				fail();
				break;				
			}
			
			String descriptorAsStr = TestUtils.readContentToString(resourcePath);
			
			SchemaDescriptorTree schemaDescriptorTree = SchemaDescriptorTreeBuilder
					.instance(rootSchemaDescriptor)
					.build(new DefaultContentOperationSetFactory());
			
			ContentValidator validator = new ContentValidator(schemaDescriptorTree);
			validator.validateContent(descriptorAsStr);
			assertTrue(true);	
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail();
		} catch (ContentValidationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	@ParameterizedTest
	@MethodSource("createResourcePaths_JSON_NOK")
	void testValidateContent_JSON_NOK(Path testDescriptor) {
		
		// root with JSON specified
		SchemaDescriptor rootSchemaDescriptor = new SchemaDescriptor(
				ContentFormat.JSON, "plugin-descriptor.json", 
				LocationType.PATH, Paths.get("schemas").toString());
		
		SchemaDescriptorTree schemaDescriptorTree = SchemaDescriptorTreeBuilder
				.instance(rootSchemaDescriptor)
				.build(new DefaultContentOperationSetFactory());

		try {
			String descriptorAsStr = TestUtils.readContentToString(testDescriptor);
			
			ContentValidator validator = new ContentValidator(schemaDescriptorTree);
			validator.validateContent(descriptorAsStr);	
			
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (ContentValidationException e) {
			assertTrue(true);
		}
		
	}

}
