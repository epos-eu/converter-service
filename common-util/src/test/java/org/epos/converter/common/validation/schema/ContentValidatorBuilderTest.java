package org.epos.converter.common.validation.schema;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.epos.converter.common.schema.validation.ContentOperationSetFactory;
import org.epos.converter.common.schema.validation.ContentValidator;
import org.epos.converter.common.schema.validation.DefaultContentOperationSetFactory;
import org.epos.converter.common.schema.validation.SchemaDescriptor;
import org.epos.converter.common.schema.validation.SchemaDescriptorTree;
import org.epos.converter.common.schema.validation.SchemaDescriptorTree.SchemaDescriptorTreeBuilder;
import org.epos.converter.common.schema.validation.exception.ConfigurationException;
import org.epos.converter.common.schema.validation.exception.ContentValidationException;
import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.ContentOperationSet;
import org.epos.converter.common.type.LocationType;
import org.everit.json.schema.Schema;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class ContentValidatorBuilderTest {

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
	void testBuild_OK() {
		
		List<SchemaDescriptorTree> nonNestedSchemaStructures = List.of(
			
			SchemaDescriptorTreeBuilder
				.instance(new SchemaDescriptor(ContentFormat.XML, ContentFormat.XML, 
					"fsdn-station-1.0.xsd", LocationType.PATH,
					Paths.get("schemas").toString()))
				.build(new DefaultContentOperationSetFactory()),
				
			SchemaDescriptorTreeBuilder
				.instance(new SchemaDescriptor(ContentFormat.JSON, ContentFormat.JSON,
					"wp10-station.json", LocationType.PATH,
					Paths.get("schemas").toString()))
				.build(new DefaultContentOperationSetFactory())//,

		/* --- REQUIRES INTERNET CONNECTION ---
		 * 
		 * SchemaDescriptorTreeBuilder .instance(new SchemaDescriptor(ContentFormat.XML,
		 * ContentFormat.XML, "fdsn-station-1.0.xsd", LocationType.URL,
		 * "http://www.fdsn.org/xml/station/")) .build(new
		 * DefaultContentOperationSetFactory())
		 */
				
		);
		
		nonNestedSchemaStructures.stream().forEach(s -> {
			try {
				ContentValidator validator = new ContentValidator(s);
				assertTrue(!Objects.isNull(validator));
			} catch (ConfigurationException e) {
				fail(e.getMessage());
			}
		});		
	}
	
	@Test
	void testBuild_NOK() {
		
		List<SchemaDescriptorTree> nonNestedSchemaStructures = List.of(
			
			// invalid protocol on schema URL
			SchemaDescriptorTreeBuilder
				.instance(new SchemaDescriptor(ContentFormat.XML, ContentFormat.XML,
					"fdsn-station-1.0.xsd", LocationType.URL, 
					"shttp://www.fdsn.org/xml/station/"))
				.build(new DefaultContentOperationSetFactory())		
		);
		
		nonNestedSchemaStructures.stream().forEach(s -> {
			try {
				new ContentValidator(s);
				fail();
			} catch (ConfigurationException e) {
				assertTrue(e.getCause() instanceof MalformedURLException);
			}
		});		
	}
	
	@Test
	void testBuild_NOK_missing_OperationSets() {
		
		ContentOperationSetFactory mockOperationSetFactory = new ContentOperationSetFactory() {

			@Override
			public ContentOperationSet<?> getInstance(ContentFormat contentFormat) throws UnsupportedOperationException {			
				return new ContentOperationSet<org.everit.json.schema.Schema>() {	
					
					@Override
					public String getSubstrFromContent(String pointerExpr, String payload) {
						return null;
					}
					@Override
					public ContentFormat getSupportedContentFormat() {
						// deliberately missing for purpose of test
						return null;
					}						
					@Override
					protected void doValidateContent(Schema typedSchema, String payloadSubstr)
							throws ContentValidationException {
					}
					@Override
					protected Class<Schema> getSchemaClass() {
						return org.everit.json.schema.Schema.class;
					
					}
					@Override
					public String getRootPointerExpr() {
						return "$";
					}
					@Override
					public Optional<Object> loadSchema(URL schemaPathUrl) throws ConfigurationException {
						return Optional.empty();
					}
					
				};
			}
			
		};
		
		List<SchemaDescriptorTree> nonNestedSchemaStructures = List.of(
				
			SchemaDescriptorTreeBuilder
				.instance(new SchemaDescriptor(ContentFormat.XML, ContentFormat.XML,
					"fsdn-station-1.0.xsd", LocationType.PATH,
					Paths.get("schemas").toString()))
				.build(mockOperationSetFactory),
				
			SchemaDescriptorTreeBuilder
				.instance(new SchemaDescriptor(ContentFormat.JSON, ContentFormat.JSON,
					"wp10-station.json", LocationType.PATH,
					Paths.get("schemas").toString()))
				.build(mockOperationSetFactory)
		);
		
		nonNestedSchemaStructures.stream().forEach(s -> {
			try {
				new ContentValidator(s);
				fail();
			} catch (ConfigurationException e) {
				assertTrue(true);
			}
		});
	}

/*	@Test
	void testBuild_NOK_different_OperationSets_added_for_same_content_format() {
		
		List<TreeNode<SchemaDescriptor>> nonNestedSchemaStructures = List.of(
				
			// root with JSON Schema specified
			SchemaDescriptorTreeNodeBuilder.getInstance(new SchemaDescriptor(
					ContentFormat.JSON, ContentFormat.JSON, 
					"wp10-station.json", LocationType.PATH, 
					Paths.get("schemas").toString()))
			.getRoot()		
		);
		
		nonNestedSchemaStructures.stream().forEach(s -> {
			try {
				ContentValidatorBuilder.getInstance(s)
					.addOperationSet(new DefaultXmlContentOpertations())
					.addOperationSet(new DefaultJsonContentOpertations())
					.addOperationSet(new ContentOperationSet<org.everit.json.schema.Schema>() {	
						
						@Override
						public String getSubstrFromContent(String pointerExpr, String payload) {
							return null;
						}
						@Override
						public ContentFormat getSupportedContentFormat() {
							return ContentFormat.JSON;
						}						
						@Override
						protected void doValidateContent(Schema typedSchema, String payloadSubstr)
								throws ContentValidationException {
						}
						@Override
						protected Class<Schema> getSchemaClass() {
							return org.everit.json.schema.Schema.class;
						
						}
						@Override
						public String getRootPointerExpr() {
							return "$";
						}
						@Override
						public Optional<Object> loadSchema(URL schemaPathUrl) throws ConfigurationException {
							return null;
						}
						
					})
					.build();
				fail();
			} catch (ConfigurationException e) {
				assertTrue(true);
			}
		});
	}*/

	
}
