package org.epos.converter.common.validation.schema;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.epos.converter.common.collections.TreeNode;
import org.epos.converter.common.schema.validation.DefaultContentOperationSetFactory;
import org.epos.converter.common.schema.validation.DefaultJsonContentOpertations;
import org.epos.converter.common.schema.validation.DefaultXmlContentOpertations;
import org.epos.converter.common.schema.validation.SchemaDescriptor;
import org.epos.converter.common.schema.validation.SchemaDescriptorTree;
import org.epos.converter.common.schema.validation.SchemaDescriptorTree.SchemaDescriptorTreeBuilder;
import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.ContentOperationSet;
import org.epos.converter.common.type.LocationType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * @author patk
 *
 */
class SchemaDescriptorTreeTest {
	
	private static ContentOperationSet<?> xmlOps, jsonOps;
	private static SchemaDescriptor simpleXmlRoot, simpleJsonRoot;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		xmlOps = new DefaultXmlContentOpertations();
		jsonOps = new DefaultJsonContentOpertations();
		
		simpleXmlRoot = new SchemaDescriptor(
				ContentFormat.XML, xmlOps.getRootPointerExpr(),
				ContentFormat.XML, "fsdn-station-restricted-1.0.xsd", 
				LocationType.PATH, "/schemas/request");
	
		simpleJsonRoot = new SchemaDescriptor(
			ContentFormat.JSON, jsonOps.getRootPointerExpr(),
			ContentFormat.JSON, "geojson.json", 
			LocationType.PATH, "/schemas/response");
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
	void testSchemaDescriptorNode_SimpleJsonInstance_buildOK() {
		
		// --- SIMPLE NON-NESTED SCHEMA STRUCTURE ---
		Path schamaPath = Paths.get("schemas", "plugin-descriptor.json");
		SchemaDescriptorTree schemaDescriptorTree = SchemaDescriptorTreeBuilder.simpleJsonInstance(schamaPath).build();
		
		// test
		TreeNode<SchemaDescriptor> schemaDescriptorRoot = schemaDescriptorTree.getRootNode();		
		assertTrue(!schemaDescriptorRoot.hasChildren());
		assertTrue(schemaDescriptorRoot.isRoot());
		
		
	}

	@Test
	void testSchemaDescriptorNode_buildOK() {

		// --- SIMPLE NON-NESTED SCHEMA STRUCTURES ---
		List<SchemaDescriptor> simpleRoots = List.of(simpleJsonRoot, simpleXmlRoot);
		
		simpleRoots.stream().forEach(schemaDescriptor -> {
			SchemaDescriptorTree schemaDescriptorTree = SchemaDescriptorTreeBuilder
					.instance(schemaDescriptor)
					.build(new DefaultContentOperationSetFactory());
			TreeNode<SchemaDescriptor> schemaDescriptorRoot = schemaDescriptorTree.getRootNode();
			
			assertTrue(!schemaDescriptorRoot.hasChildren());
			assertTrue(schemaDescriptorRoot.isRoot());
		});

		// --- 2-LEVEL OF SCHEMA NESTING / MULTI-CHILD ---
		SchemaDescriptor child_L1_No1 = new SchemaDescriptor(
				ContentFormat.XML, "/my-element/some-element-with-json-in",
				ContentFormat.JSON, "my-nested-json.json",
				LocationType.PATH, "/schemas/request");

		SchemaDescriptor child_L1_No2 = new SchemaDescriptor(
				ContentFormat.XML, "/my-element/some-element-with-more-xml-in",
				ContentFormat.XML, "my-nested-xml.xsd",
				LocationType.PATH, "/schemas/request");

		SchemaDescriptor child_L2_No1 = new SchemaDescriptor(
				ContentFormat.JSON, "$.some-xml-here",
				ContentFormat.XML, "my-nested-nested-xml.xsd",
				LocationType.PATH, "/schemas/request");

		
		SchemaDescriptorTree schemaDescriptorTree = SchemaDescriptorTreeBuilder
				.instance(simpleXmlRoot)
				.addChild(child_L1_No1)
				.decend().addChild(child_L2_No1).ascend()
				.addChild(child_L1_No2)
				.build(new DefaultContentOperationSetFactory());
		
		assertTrue(schemaDescriptorTree.getRootNode().hasChildren(), "Expect a simple non-nested schema declaration");
		assertTrue(schemaDescriptorTree.getRootNode().getChildren().get().size() == 2, "Wrong number of L1 children");
		TreeNode<SchemaDescriptor> l1_first_child = schemaDescriptorTree.getRootNode().getChildren().get().get(0);		
		assertTrue(l1_first_child.hasChildren() && l1_first_child.getChildren().get().size() == 1, "Wrong number of L2 children");		
		assertTrue(schemaDescriptorTree.getRootNode().isRoot());

	}
	
	@Test
	void testSchemaDescriptorNode_buildNOK() {

		List<SchemaDescriptor> simpleRoots = List.of(
				// Simple XML root BUT mismatching pointer content format 
				new SchemaDescriptor(
					ContentFormat.JSON, xmlOps.getRootPointerExpr(),
					ContentFormat.XML, "fsdn-station-restricted-1.0.xsd", 
					LocationType.PATH, "/schemas/request"),
				// Simple JSON root BUT mismatching pointer content format 
				new SchemaDescriptor(
					ContentFormat.XML, jsonOps.getRootPointerExpr(),
					ContentFormat.JSON, "geojson.json", 
					LocationType.PATH, "/schemas/response")
			);
		
		simpleRoots.stream().forEach(schemaDescriptor -> {
			try {
				SchemaDescriptorTreeBuilder.instance(schemaDescriptor);			
				fail();
			} catch (Exception e) {
				assertTrue(e instanceof IllegalArgumentException);
			}
		});
		
		// --- 1-LEVEL OF SCHEMA NESTING ---
		
		// pointer type format does not match with parent's content type format
		SchemaDescriptor child_L1_No1 = new SchemaDescriptor(
				ContentFormat.JSON, "$.idonotexist",
				ContentFormat.JSON, "my-nested-json.json", 
				LocationType.PATH, "/schemas/request");
		
		try {
			SchemaDescriptorTreeBuilder.instance(simpleXmlRoot)
				.addChild(child_L1_No1);
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		
	}

}
