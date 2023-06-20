package org.epos.converter.common.validation;

import static org.epos.converter.common.type.ContentFormat.JSON;
import static org.epos.converter.common.type.ContentFormat.XML;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.epos.converter.common.plugin.type.PayloadValidator;
import org.epos.converter.common.plugin.type.PayloadValidator.PayloadValidatorBuilder;
import org.epos.converter.common.schema.validation.DefaultJsonContentOpertations;
import org.epos.converter.common.schema.validation.DefaultXmlContentOpertations;
import org.epos.converter.common.schema.validation.exception.ConfigurationException;
import org.epos.converter.common.schema.validation.exception.ContentValidationException;
import org.epos.converter.common.test.TestResourcesAccessor;
import org.epos.converter.common.test.TestUtils;
import org.epos.converter.common.type.PayloadFormatDescriptor;
import org.epos.converter.common.type.PayloadSchemaDefinitions;
import org.epos.converter.common.type.PayloadSchemaDefinitions.PayloadSchemaDefinitionsBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


/**
 * @deprecated At some point may reproduce the nested schema validation functionality using SchemaDescriptionTree structure to reimplement
 */
class PayloadValidator_PayloadValidationTest {
	
	private static final String PAYLOAD_SCHEMA_INCOMING_1 = "schema_validation_testing/schema_set_#1/schemas/payload_incoming/";
	private static final String PAYLOAD_SCHEMA_OUTGOING_1 = "schema_validation_testing/schema_set_#1/schemas/payload_outgoing/";
	
	private static final String PAYLOAD_SCHEMA_INCOMING_2 = "schema_validation_testing/schema_set_#2/schemas/payload_incoming/";
	private static final String PAYLOAD_SCHEMA_OUTGOING_2 = "schema_validation_testing/schema_set_#2/schemas/payload_outgoing/";

	private static final String PAYLOAD_SCHEMA_INCOMING_3 = "schema_validation_testing/schema_set_#3/schemas/payload_incoming/";
	private static final String PAYLOAD_SCHEMA_OUTGOING_3 = "schema_validation_testing/schema_set_#3/schemas/payload_outgoing/";
	
	// Configuration that would have been obtained from the plug-in descriptor / plug-in registry
	private static PayloadSchemaDefinitions payloadDefinitions_1;
	private static PayloadSchemaDefinitions payloadDefinitions_2;
	private static PayloadSchemaDefinitions payloadDefinitions_3;
	
	private static TestResourcesAccessor resourceAccessor;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
		resourceAccessor = new TestResourcesAccessor(Paths.get("schema_validation_testing"), true);
		
		try {
			payloadDefinitions_1 = PayloadSchemaDefinitionsBuilder.getInstance()
				.incomingSchema(new PayloadFormatDescriptor(JSON), "root_payload_schema_in.json")
				.incomingSchema(new PayloadFormatDescriptor(JSON, JSON, "$.TCSRESULT"), "nested_payload_schema_in.json")
				.outgoingSchema(new PayloadFormatDescriptor(JSON), "root_payload_schema_out.json")
				.outgoingSchema(new PayloadFormatDescriptor(JSON, JSON, "$.MAPRESULT"), "nested_payload_schema_out.json")
				.build();
			
			payloadDefinitions_2 = PayloadSchemaDefinitionsBuilder.getInstance()
				.incomingSchema(new PayloadFormatDescriptor(JSON), "root_payload_schema_in.json")
				.incomingSchema(new PayloadFormatDescriptor(JSON, JSON, "$.TCSRESULT"), "nested_payload_schema_in.json")
				.outgoingSchema(new PayloadFormatDescriptor(JSON), "root_payload_schema_out.json")
				.outgoingSchema(new PayloadFormatDescriptor(JSON, JSON, "$.MAPRESULT"), "nested_payload_schema_out.json")
				.build();
			
			payloadDefinitions_3 = PayloadSchemaDefinitionsBuilder.getInstance()
				.incomingSchema(new PayloadFormatDescriptor(JSON), "root_payload_schema_in.json")
				.incomingSchema(new PayloadFormatDescriptor(XML, JSON, "$.TCSRESULT"), "nested_payload_schema_in.xsd")
				.outgoingSchema(new PayloadFormatDescriptor(JSON), "root_payload_schema_out.json")
//				.outgoingSchema(new PayloadFormatDescriptor(JSON, JSON, "$.MAPRESULT"), "nested_payload_schema_out.json")
				.build();
			
		} catch (ConfigurationException e) {
			payloadDefinitions_1 = null;
			payloadDefinitions_2 = null;
			payloadDefinitions_3 = null;
		}
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

	// ------------------------------ SCHEMA SET #1 (snapshot of visualise/radon/tcsjson)  ------------------------------
	
	static Stream<Path> createResourcePaths_forValidIncomingPayloads_1() throws IOException {
		return resourceAccessor.getResourcePaths(Paths.get("schema_set_#1", "payloads", "incoming", "valid"), JSON);
	}

	@ParameterizedTest
	@MethodSource("createResourcePaths_forValidIncomingPayloads_1")
	void testValidateIncomingPayload_1_VALID(Path testResource) {
		
		try {
			PayloadValidator payloadValidator = PayloadValidatorBuilder.getInstance(
						PAYLOAD_SCHEMA_INCOMING_1, PAYLOAD_SCHEMA_OUTGOING_1, payloadDefinitions_1)
					.operationSet(new DefaultJsonContentOpertations())
					.build();
			
			String incomingPayload = TestUtils.readContentToString(testResource);
			payloadValidator.validateIncomingPayload(incomingPayload);
			Assert.assertTrue(true);
			
		} catch (ConfigurationException | ContentValidationException | IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	
	static Stream<Path> createResourcePaths_forValidOutgoingPayloads_1() throws IOException {
		return resourceAccessor.getResourcePaths(Paths.get("schema_set_#1", "payloads", "outgoing", "valid"), JSON);
	}
	
	@ParameterizedTest
	@MethodSource("createResourcePaths_forValidOutgoingPayloads_1")
	void testValidateOutgoingPayload_1_VALID(Path testResource) {
		
		try {
			PayloadValidator payloadValidator = PayloadValidatorBuilder.getInstance(
						PAYLOAD_SCHEMA_INCOMING_1, PAYLOAD_SCHEMA_OUTGOING_1, payloadDefinitions_1)
					.operationSet(new DefaultJsonContentOpertations())
					.build();
			
			String incomingPayload = TestUtils.readContentToString(testResource);
			payloadValidator.validateOutgoingPayload(incomingPayload);
			Assert.assertTrue(true);
			
		} catch (ConfigurationException | ContentValidationException | IOException e) {
			e.printStackTrace();
			Assert.fail();
		}	
	}
		
	/**
	 * The JSON within these test resource files are expected to be minified
	 * {@link https://codebeautify.org/jsonminifier}
	 */
	static Stream<Path> createResourcePaths_forInvalidIncomingPayloads_1() throws IOException {
		return resourceAccessor.getResourcePaths(Paths.get("schema_set_#1", "payloads", "incoming", "invalid"), JSON);
	}
	
	@ParameterizedTest
	@MethodSource("createResourcePaths_forInvalidIncomingPayloads_1")
	void testValidateIncomingPayload_1_INVALID(Path testResource) {
		
		try {
			PayloadValidator payloadValidator = PayloadValidatorBuilder.getInstance(
						PAYLOAD_SCHEMA_INCOMING_1, PAYLOAD_SCHEMA_OUTGOING_1, payloadDefinitions_1)
					.operationSet(new DefaultJsonContentOpertations())
					.build();
			
			String incomingPayload = TestUtils.readContentToString(testResource);
			payloadValidator.validateIncomingPayload(incomingPayload);
			Assert.fail();

		} catch (ConfigurationException | ContentValidationException | IOException e) {
			Assert.assertTrue(String.format("Exception '%s' was not expected.", e.getClass().getName()), e instanceof ContentValidationException);
		}
	}
	
	// ------------------------------ SCHEMA SET #2 (snapshot of visualise/station/wp10-json)  ------------------------------
	
	static Stream<Path> createResourcePaths_forValidIncomingPayloads_2() throws IOException {
		return resourceAccessor.getResourcePaths(Paths.get("schema_set_#2", "payloads", "incoming", "valid"), JSON);
	
	}

	@ParameterizedTest
	@MethodSource("createResourcePaths_forValidIncomingPayloads_2")
	void testValidateIncomingPayload_2_VALID(Path testResource) {
		
		try {
			PayloadValidator payloadValidator = PayloadValidatorBuilder.getInstance(
						PAYLOAD_SCHEMA_INCOMING_2, PAYLOAD_SCHEMA_OUTGOING_2, payloadDefinitions_2)
					.operationSet(new DefaultJsonContentOpertations())
					.build();
			
			String incomingPayload = TestUtils.readContentToString(testResource);
			payloadValidator.validateIncomingPayload(incomingPayload);
			Assert.assertTrue(true);
			
		} catch (ConfigurationException | ContentValidationException | IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	static Stream<Path> createResourcePaths_forValidOutgoingPayloads_2() throws IOException {
		return resourceAccessor.getResourcePaths(Paths.get("schema_set_#2", "payloads", "outgoing", "valid"), JSON);
	}
	
	@ParameterizedTest
	@MethodSource("createResourcePaths_forValidOutgoingPayloads_2")
	void testValidateOutgoingPayload_2_VALID(Path testResource) {
		
		try {
			PayloadValidator payloadValidator = PayloadValidatorBuilder.getInstance(
						PAYLOAD_SCHEMA_INCOMING_2, PAYLOAD_SCHEMA_OUTGOING_2, payloadDefinitions_2)
					.operationSet(new DefaultJsonContentOpertations())
					.build();
			
			String incomingPayload = TestUtils.readContentToString(testResource);
			payloadValidator.validateOutgoingPayload(incomingPayload);
			Assert.assertTrue(true);
			
		} catch (ConfigurationException | ContentValidationException | IOException e) {
			e.printStackTrace();
			Assert.fail();
		}	
	}
	
	// ------------------------------ SCHEMA SET #3 (snapshot of visualise/station/fsdn-xml)  ------------------------------
	
	static Stream<Path> createResourcePaths_forValidIncomingPayloads_3() throws IOException {
		return resourceAccessor.getResourcePaths(Paths.get("schema_set_#3", "payloads", "incoming", "valid"), JSON);
	}

	@ParameterizedTest
	@MethodSource("createResourcePaths_forValidIncomingPayloads_3")
	void testValidateIncomingPayload_3_VALID(Path testResource) {
		
		try {
			PayloadValidator payloadValidator = PayloadValidatorBuilder.getInstance(
						PAYLOAD_SCHEMA_INCOMING_3, PAYLOAD_SCHEMA_OUTGOING_3, payloadDefinitions_3)
					.operationSet(new DefaultJsonContentOpertations())
					.operationSet(new DefaultXmlContentOpertations())
					.build();
			
			String incomingPayload = TestUtils.readContentToString(testResource);
			payloadValidator.validateIncomingPayload(incomingPayload);
			Assert.assertTrue(true);
			
		} catch (ConfigurationException | ContentValidationException | IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
