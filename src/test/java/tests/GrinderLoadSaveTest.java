package tests;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.stringtree.Context;
import org.stringtree.context.MapContext;
import org.stringtree.grinder.SiteGrinder;
import org.stringtree.solomon.Template;
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
	
	public void testLoadEmpty() throws IOException {
		grinder.load(new File("src/test/input/empty"), pages, context);
		assertFalse(pages.isEmpty());
		assertEquals("empty", pages.getValue().getBodyAsString());
		assertTrue(pages.getChildren().isEmpty());
	}
	
	public void testSingleFlatTpl() throws IOException {
		grinder.load(new File("src/test/input/test1"), pages, context);
		assertFalse(pages.isEmpty());
		assertEquals("test1", pages.getValue().getBodyAsString());
		assertEquals(1, pages.getChildren().size());
	}
	
	public void testMultipleFlatTract() throws IOException {
		grinder.load(new File("src/test/input/test2"), pages, context);
		assertFalse(pages.isEmpty());
		assertEquals("test2", pages.getValue().getBodyAsString());
		assertEquals(2, pages.getChildren().size());
	}
	
	public void testMixedHierarchy() throws IOException {
		grinder.load(new File("src/test/input/test3"), pages, context);
		assertFalse(pages.isEmpty());
		assertEquals("test3", pages.getValue().getBodyAsString());

		Collection<Tree<Template>> children = pages.getChildren();
		assertEquals(2, children.size());

		Iterator<Tree<Template>> it = children.iterator();
		Tree<Template> c1 = it.next();
		Tree<Template> c2 = it.next();
		assertEquals("products", c2.getValue().get(SiteGrinder.NAME));
		assertEquals("What is this?", c1.getValue().getBodyAsString());

		Collection<Tree<Template>> grandchildren = c2.getChildren();
		assertEquals(2, grandchildren.size());
	}
}
