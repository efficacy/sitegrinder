package tests;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.stringtree.Tract;
import org.stringtree.finder.MapStringKeeper;
import org.stringtree.finder.StringKeeper;
import org.stringtree.grinder.SiteGrinder;
import org.stringtree.util.tree.MutableTree;
import org.stringtree.util.tree.SimpleTree;
import org.stringtree.util.tree.Tree;

import junit.framework.TestCase;

public class GrinderLoadSaveTest extends TestCase {
	SiteGrinder grinder;
	MutableTree<Tract> pages;
	StringKeeper context;
	
	public void setUp() {
		grinder = new SiteGrinder();
		pages = new SimpleTree<Tract>();
		context = new MapStringKeeper();
	}
	
	public void testInitialConditions() {
		assertTrue(pages.isEmpty());
	}
	
	public void testLoadEmpty() {
		grinder.load(new File("src/test/input/empty"), pages, context);
		assertFalse(pages.isEmpty());
		assertEquals("empty", pages.getValue().getContent());
		assertTrue(pages.getChildren().isEmpty());
	}
	
	public void testSingleFlatTpl() {
		grinder.load(new File("src/test/input/test1"), pages, context);
		assertFalse(pages.isEmpty());
		assertEquals("test1", pages.getValue().getContent());
		assertEquals(1, pages.getChildren().size());
	}
	
	public void testMultipleFlatTract() {
		grinder.load(new File("src/test/input/test2"), pages, context);
		assertFalse(pages.isEmpty());
		assertEquals("test2", pages.getValue().getContent());
		assertEquals(2, pages.getChildren().size());
	}
	
	public void testMixedHierarchy() {
		grinder.load(new File("src/test/input/test3"), pages, context);
		assertFalse(pages.isEmpty());
		assertEquals("test3", pages.getValue().getContent());

		Collection<Tree<Tract>> children = pages.getChildren();
		assertEquals(2, children.size());

		Iterator<Tree<Tract>> it = children.iterator();
		Tree<Tract> c1 = it.next();
		Tree<Tract> c2 = it.next();
		assertEquals("What is this?", c1.getValue().getContent());
		assertEquals("products", c2.getValue().get(Tract.NAME));

		Collection<Tree<Tract>> grandchildren = c2.getChildren();
		assertEquals(2, grandchildren.size());
	}
}
