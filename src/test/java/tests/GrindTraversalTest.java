package tests;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.rack4java.Context;
import org.rack4java.context.MapContext;
import org.stringtree.Tract;
import org.stringtree.grinder.SiteGrinder;
import org.stringtree.solomon.Template;
import org.stringtree.util.tree.MutableTree;
import org.stringtree.util.tree.SimpleTree;
import org.stringtree.util.tree.Tree;

public class GrindTraversalTest extends TestCase {
	Context<String> context;
	SiteGrinder grinder;
	MutableTree<Template> pages;
	Context<Template> templates;
	MutableTree<Tract> site;
	
	public void setUp() {
		context = new MapContext<String>();
		grinder = new SiteGrinder();
		pages = new SimpleTree<Template>();
		pages.setValue(Helper.folder("brasspyramid.com"));
		templates = new MapContext<Template>();
		site = new SimpleTree<Tract>();
	}
	
	public void testNullPages() {
		try {
			grinder.grind(null, templates, site, context);
			fail("grind from null pages should throw");
		} catch(IllegalArgumentException e) {
			// expected.
		}
	}
	
	public void testNullTemplates() {
		try {
			grinder.grind(pages, null, site, context);
			fail("grind from null templates should throw");
		} catch(IllegalArgumentException e) {
			// expected.
		}
	}
	
	public void testNullSite() {
		try {
			grinder.grind(pages, templates, null, context);
			fail("grind to null site should throw");
		} catch(IllegalArgumentException e) {
			// expected.
		}
	}
	
	public void testEmpty() {
		pages.clear();
		grinder.grind(pages, templates, site, context);
		assertTrue(site.isEmpty());
	}
	
	public void testSingleSimplePage() {
		pages.addChild(new SimpleTree<Template>(Helper.page("hello")));
		grinder.grind(pages, templates, site, context);
		assertFalse(site.isEmpty());
		assertEquals("brasspyramid.com", site.getValue().getBodyAsString());
		
		Collection<Tree<Tract>> children = site.getChildren();
		assertEquals(1, children.size());
		Tree<Tract> kid1 = children.iterator().next();
		assertEquals("hello", kid1.getValue().getBodyAsString());
	}
	
	public void testMultipleSimplePage() {
		pages.addChild(new SimpleTree<Template>(Helper.page("hello")));
		pages.addChild(new SimpleTree<Template>(Helper.page("goodbye")));
		grinder.grind(pages, templates, site, context);
		assertFalse(site.isEmpty());
		assertEquals("brasspyramid.com", site.getValue().getBodyAsString());

		Collection<Tree<Tract>> children = site.getChildren();
		assertEquals(2, children.size());
		Iterator<Tree<Tract>> iterator = children.iterator();
		Tree<Tract> kid1 = iterator.next();
		assertEquals("hello", kid1.getValue().getBodyAsString());
		Tree<Tract> kid2 = iterator.next();
		assertEquals("goodbye", kid2.getValue().getBodyAsString());
	}
	
	public void testHierarchy() {
		MutableTree<Template> child = new SimpleTree<Template>(Helper.folder("hello"));
		pages.addChild(child);
		Template page = Helper.page("goodbye", "hello");
		child.addChild(new SimpleTree<Template>(page));
		grinder.grind(pages, templates, site, context);
		
		assertFalse(site.isEmpty());
		assertEquals("brasspyramid.com", site.getValue().getBodyAsString());
		
		Collection<Tree<Tract>> children = site.getChildren();
		assertEquals(1, children.size());
		Tree<Tract> kid1 = children.iterator().next();
		assertEquals("hello", kid1.getValue().getBodyAsString());
		
		Collection<Tree<Tract>> grandchildren = kid1.getChildren();
		assertEquals(1, grandchildren.size());
		Tree<Tract> kid2 = grandchildren.iterator().next();
		assertEquals("goodbye", kid2.getValue().getBodyAsString());
	}
}	
