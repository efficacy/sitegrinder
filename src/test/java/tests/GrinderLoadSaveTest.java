package tests;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.stringtree.Context;
import org.stringtree.Tract;
import org.stringtree.context.MapContext;
import org.stringtree.grinder.SiteGrinder;
import org.stringtree.solomon.Template;
import org.stringtree.solomon.token.StringToken;
import org.stringtree.util.tree.MutableTree;
import org.stringtree.util.tree.SimpleTree;
import org.stringtree.util.tree.Tree;

public class GrinderLoadSaveTest extends TestCase {
	SiteGrinder grinder;
	MutableTree<Template> pages;
	Context<String> context;
	
	public void setUp() {
		grinder = new SiteGrinder();
		pages = new SimpleTree<Template>();
		context = new MapContext<String>();
	}
	
	public void testInitialConditions() {
		assertTrue(pages.isEmpty());
	}
	
	public void testLoadEmpty() {
		grinder.load(new File("src/test/input/empty"), pages, context);
		assertFalse(pages.isEmpty());
		assertEquals(new StringToken("empty"), pages.getValue());
		assertTrue(pages.getChildren().isEmpty());
	}
	
	public void testSingleFlatTpl() {
		grinder.load(new File("src/test/input/test1"), pages, context);
		assertFalse(pages.isEmpty());
		assertEquals(new StringToken("test1"), pages.getValue());
		assertEquals(1, pages.getChildren().size());
	}
	
	public void testMultipleFlatTract() {
		grinder.load(new File("src/test/input/test2"), pages, context);
		assertFalse(pages.isEmpty());
		assertEquals(new StringToken("test2"), pages.getValue());
		assertEquals(2, pages.getChildren().size());
	}
	
	public void testMixedHierarchy() {
		grinder.load(new File("src/test/input/test3"), pages, context);
		assertFalse(pages.isEmpty());
		assertEquals(new StringToken("test3"), pages.getValue());

		Collection<Tree<Template>> children = pages.getChildren();
		assertEquals(2, children.size());

		Iterator<Tree<Template>> it = children.iterator();
		Tree<Template> c1 = it.next();
		Tree<Template> c2 = it.next();
		assertEquals(new StringToken("What is this?"), c1.getValue());
//		assertEquals("products", c2.getValue().get(SiteGrinder.NAME));

		Collection<Tree<Template>> grandchildren = c2.getChildren();
		assertEquals(2, grandchildren.size());
	}
}
