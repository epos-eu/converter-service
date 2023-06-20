package org.epos.converter.common.collections;

public interface Visitor<U, T extends TreeNode<U>> {
	
	void visit(T node);

}
