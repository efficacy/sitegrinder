package org.stringtree.grinder;

import org.stringtree.util.tree.MutableTree;
import org.stringtree.util.tree.Tree;
import org.stringtree.util.tree.TreeTransformVisitor;

public abstract class SimpleTreeTransformVisitor<T, U> implements TreeTransformVisitor<T, U> {

	public boolean enter(Tree<T> from, MutableTree<U> to) {
		return visit(from, to);
	}

	protected abstract boolean visit(Tree<T> from, MutableTree<U> to);

	public boolean exit(Tree<T> from, MutableTree<U> to) {
		return false;
	}

}
