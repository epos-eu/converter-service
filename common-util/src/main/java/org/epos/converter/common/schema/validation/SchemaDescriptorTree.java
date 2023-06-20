package org.epos.converter.common.schema.validation;

import static java.util.stream.Collectors.toMap;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.epos.converter.common.collections.TreeNode;
import org.epos.converter.common.collections.TreeNode.TreeNodeBuilder;
import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.ContentOperationSet;
import org.epos.converter.common.type.LocationType;

public final class SchemaDescriptorTree {
	
	private TreeNode<SchemaDescriptor> rootNode;
	private Map<ContentFormat, ContentOperationSet<?>> contentOpSets;
	
	private SchemaDescriptorTree(TreeNode<SchemaDescriptor> rootNode,
			Map<ContentFormat, ContentOperationSet<?>> contentOpSets) {
		this.rootNode = rootNode;
		this.contentOpSets = contentOpSets;
	}
	
	public Map<ContentFormat, ContentOperationSet<?>> getContentOpSets() {
		return contentOpSets;
	}
	
	public TreeNode<SchemaDescriptor> getRootNode() {
		return rootNode;
	}
	
	// ---------------------------------------- <BUILDER> ----------------------------------------	
	public static class SchemaDescriptorTreeBuilder {

		private TreeNodeBuilder<SchemaDescriptor> nodeBuilder = null;
		
		public static SchemaDescriptorTreeBuilder simpleJsonInstance(Path schema) 
		{
			Objects.requireNonNull(schema);
			
			SchemaDescriptor rootSchemaDescriptor = new SchemaDescriptor(
					ContentFormat.JSON, 
					schema.getFileName().toString(), 
					LocationType.PATH, 
					schema.getParent().toString());
					
			return instance(rootSchemaDescriptor);
		}
				
		public static SchemaDescriptorTreeBuilder instance(SchemaDescriptor rootSchemaDescriptor) {
			
			return new SchemaDescriptorTreeBuilder(
					rootSchemaDescriptor,					
					new TreeNodeBuilder<SchemaDescriptor>() {

						@Override
						protected void validateRoot(SchemaDescriptor schemaDescriptor) throws IllegalArgumentException {
							
							// Validate content format matches pointer format
							if (!schemaDescriptor.getPointerContentFormat().equals(schemaDescriptor.getContentFormat())) {
								throw new IllegalArgumentException("A root schema descriptor is expected to have the same 'pointer content format' and 'target schema content format'");
							}
						}
		
						@Override
						protected void validateChild(SchemaDescriptor schemaDescriptor) throws IllegalArgumentException {
							
							// Validate parent-child continuity
							if (!currentNode.getData().getContentFormat().equals(schemaDescriptor.getPointerContentFormat())) {
								String errMsg = String.format(
										"Incongruous parent-child schema descriptor relation: Child's pointer format type '%s' "
												+ "does not match the parent's content format type, '%s'", 
												schemaDescriptor.getPointerContentFormat().getName(),
										currentNode.getData().getContentFormat().getName());
								// TODO log errMsg @ERROR level;
								throw new IllegalArgumentException(errMsg);
							}
						}
						
					}
				);
		}
		
		private SchemaDescriptorTreeBuilder(SchemaDescriptor rootSchemaDescriptor, TreeNodeBuilder<SchemaDescriptor> nodeBuilder) {
			this.nodeBuilder = nodeBuilder;
			nodeBuilder.addRoot(rootSchemaDescriptor);
		}
		
		public SchemaDescriptorTree build() throws UnsupportedOperationException {
			return build(new DefaultContentOperationSetFactory());
		}
			
		public SchemaDescriptorTree build(ContentOperationSetFactory contentOperationSetFactory) throws UnsupportedOperationException {
			
			if (contentOperationSetFactory == null) {
				throw new IllegalArgumentException("Content Operation Set factory must be supplied for a SchemaDescriptorTree to be built");
			}
			
			TreeNode<SchemaDescriptor> rootNode = nodeBuilder.getRoot();
			
			// determine Operations Sets required for content specified in the SchemaDescriptor tree 
			Set<ContentFormat> contentFormats = ContentFormatsVisitor.getContentFormats(rootNode);
			Map<ContentFormat, ContentOperationSet<?>> contentOpSets = contentFormats.stream()
					.collect(toMap(
							Function.identity(), 
							f -> contentOperationSetFactory.getInstance(f)
						));
			
			return new SchemaDescriptorTree(rootNode, contentOpSets);
		}

		// < Generated delegation methods >
		
		public SchemaDescriptorTreeBuilder addChild(SchemaDescriptor schemaDescriptor) throws IllegalArgumentException {
			nodeBuilder.addChild(schemaDescriptor);
			return this;
		}
		
		public SchemaDescriptorTreeBuilder ascend() {
			nodeBuilder.ascend();
			return this;
		}

		public SchemaDescriptorTreeBuilder decend() {
			nodeBuilder.decend();
			return this;
		}
		
		// </ Generated delegation methods >
	}
	// ---------------------------------------- </BUILDER> ----------------------------------------
}
