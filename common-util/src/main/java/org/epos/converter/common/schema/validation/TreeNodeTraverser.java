package org.epos.converter.common.schema.validation;

import java.util.List;

import org.epos.converter.common.collections.TreeNode;
import org.epos.converter.common.collections.Visitor;

public abstract class TreeNodeTraverser<U> implements Visitor<U, TreeNode<U>> {

	/**
	 * Depth-first traversal 
	 */
	public void traverse(TreeNode<U> node) {
		node.accept(this);
		
		if (node.hasChildren()) {
			List<TreeNode<U>> children = node.getChildren().get();
			children.forEach(child -> traverse(child));
		}
	}

}
