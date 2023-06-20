package org.epos.converter.common.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TreeNode<U> {
	
	protected U data;
	protected Optional<TreeNode<U>> parent;
	protected Optional<List<TreeNode<U>>> children;
	
	protected TreeNode(U data, Optional<TreeNode<U>> parent) {
		this.data = data;
		this.parent = parent;
		this.children = Optional.empty();
	}
	
	public void accept(Visitor<U, TreeNode<U>> visitor) {
		visitor.visit(this);
	}
		
	public U getData() {
		return data;
	}
	
	public Optional<TreeNode<U>> getParent() {
		return parent;
	}

	public Optional<List<TreeNode<U>>> getChildren() {
		return children;
	}

	public boolean isRoot() {
		return !parent.isPresent();
	}

	public boolean hasChildren() {
		if (children.isPresent()) {
			return !children.get().isEmpty();
		}
		return false;
	}
	
	// ---------------------------------------- <<<<< BUILDER >>>>> ----------------------------------------
	
	public static abstract class TreeNodeBuilder<T> {
		
		protected TreeNode<T> currentNode;
		protected Optional<TreeNode<T>> activeChildNode;
		
/*		protected TreeNodeBuilder(T data) {
//			currentNode = createNode(data, Optional.empty());
			currentNode = new TreeNode<T>(data, Optional.empty());
		}*/

//		protected abstract TreeNode<T> createNode(T data, Optional<TreeNode<T>> parent);
		
//		public abstract TreeNodeBuilder<T> addChild(T data);
		
		public TreeNodeBuilder<T> addRoot(T data) throws IllegalArgumentException {			
			validateRoot(data);
			currentNode = new TreeNode<T>(data, Optional.empty());
			return this;
		}
		
		protected void validateRoot(T data) throws IllegalArgumentException {
			// do nothing stub
		}
		
		protected void validateChild(T data) throws IllegalArgumentException {
			// do nothing stub
		}
		
		public TreeNodeBuilder<T> addChild(T data) throws IllegalArgumentException {
			
			validateChild(data);
	
			if (!currentNode.children.isPresent()) {
				currentNode.children = Optional.of(new ArrayList<>());
			}
			
			TreeNode<T> childNode = new TreeNode<T>(data, Optional.of(currentNode));
			
			currentNode.children.get().add(childNode);
			activeChildNode = Optional.of(childNode);
			
			return this;
		}

		public TreeNodeBuilder<T> ascend() {
			currentNode = currentNode.parent.orElseThrow(UnsupportedOperationException::new);
			activeChildNode = Optional.empty();
			return this;
		}
		
		public TreeNodeBuilder<T> decend() {
			currentNode = activeChildNode.orElseThrow(UnsupportedOperationException::new);
			activeChildNode = Optional.empty();
			return this;
		}
		
		public TreeNode<T> getRoot() {
			while (!currentNode.isRoot()) {
				currentNode = currentNode.parent.get();
			}
			return currentNode;
		}
		
	}

}
