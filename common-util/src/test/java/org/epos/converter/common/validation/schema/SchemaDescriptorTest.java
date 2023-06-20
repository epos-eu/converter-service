package org.epos.converter.common.validation.schema;

import java.util.Optional;

import org.epos.converter.common.schema.validation.DefaultXmlContentOpertations;
import org.epos.converter.common.schema.validation.SchemaDescriptor;
import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.ContentOperationSet;
import org.epos.converter.common.type.LocationType;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SchemaDescriptorTest {
	
	private static ContentOperationSet<?> xmlOps, jsonOps;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		xmlOps = new DefaultXmlContentOpertations();
		jsonOps = new DefaultXmlContentOpertations();
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
	void testSchemaDescriptor_BuildOK() {
		
		SchemaDescriptor simpleXmlRoot = new SchemaDescriptor(
				ContentFormat.XML, xmlOps.getRootPointerExpr(),
				ContentFormat.XML, "fsdn-station-restricted-1.0.xsd", 
				LocationType.PATH, "/schemas/request");
		Assert.assertNotNull(simpleXmlRoot);
		
		SchemaDescriptor simpleJsonRoot = new SchemaDescriptor(
				ContentFormat.JSON, jsonOps.getRootPointerExpr(),
				ContentFormat.JSON, "geojson.json", 
				LocationType.PATH, "/schemas/response");
		Assert.assertNotNull(simpleJsonRoot);
	}
	
	@Test
	void testSchemaDescriptor_BuildNOK() {
		
		// incorrect schema extension: .xss instead of .xsd
		try {
			new SchemaDescriptor(
					ContentFormat.XML, xmlOps.getRootPointerExpr(),
					ContentFormat.XML, "fsdn-station-restricted-1.0.xss", 
					LocationType.PATH, "/schemas/request");
			Assert.assertTrue(false);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
		
		// incorrect schema extension: .json instead of .jsop
		try {
			new SchemaDescriptor(
					ContentFormat.XML, xmlOps.getRootPointerExpr(),
					ContentFormat.XML, "fsdn-station-restricted-1.0.xss", 
					LocationType.PATH, "/schemas/request");
			Assert.assertTrue(false);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
		
		// missing args
		try {
			new SchemaDescriptor(
					ContentFormat.JSON, "fsdn-station-restricted-1.0.xss",
					null, "geojson.jsop", 
					LocationType.PATH, "/schemas/response");
			Assert.assertTrue(false);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}

	}

}
