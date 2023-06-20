package org.epos.converter.common.schema.validation;

import java.util.HashSet;
import java.util.Set;

import org.epos.converter.common.collections.TreeNode;
import org.epos.converter.common.type.ContentFormat;

public final class ContentFormatsVisitor extends TreeNodeTraverser<SchemaDescriptor> {
	
	private Set<ContentFormat> contentFormats = new HashSet<>();
//	private Map<String, Set<ContentFormat>> contentFormats = new HashMap<>();

/*	public static String CONTENT_FORMATS_KEY = "CONTENT-FORMAT";
	public static String POINTER_CONTENT_FORMATS_KEY = "POINTER_CONTENT-FORMAT";*/
	
//	public static Map<String, Set<ContentFormat>> getContentFormats(TreeNode<SchemaDescriptor> rootNode) {
	public static Set<ContentFormat> getContentFormats(TreeNode<SchemaDescriptor> rootNode) {
		ContentFormatsVisitor instance = new ContentFormatsVisitor();
		instance.traverse(rootNode);
		return instance.contentFormats;
	}
	
	private ContentFormatsVisitor() {
		super();
	}

	@Override
	public void visit(TreeNode<SchemaDescriptor> node) {
		SchemaDescriptor schemaDescriptor = node.getData();
		
/*		Set<ContentFormat> content = contentFormats.computeIfAbsent(CONTENT_FORMATS_KEY, k -> new HashSet<>());
		content.add(schemaDescriptor.getContentFormat());
		Set<ContentFormat> pointer = contentFormats.computeIfAbsent(POINTER_CONTENT_FORMATS_KEY, k -> new HashSet<>());
		pointer.add(schemaDescriptor.getPointerContentFormat());*/
		
		contentFormats.add(schemaDescriptor.getContentFormat());
		contentFormats.add(schemaDescriptor.getPointerContentFormat());
	}
		
}
