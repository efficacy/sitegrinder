package tests;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.stringtree.Context;
import org.stringtree.Tract;
import org.stringtree.context.MapContext;
import org.stringtree.grinder.SiteGrinder;
import org.stringtree.solomon.Template;
import org.stringtree.tract.MapTract;
import org.stringtree.util.testing.Checklist;
import org.stringtree.util.tree.MutableTree;
import org.stringtree.util.tree.SimpleTree;
import org.stringtree.util.tree.Tree;

public class GrindTraversalTest extends TestCase {
	Context<Object> context;
	SiteGrinder grinder;
	MutableTree<Template> pages;
	Context<Template> templates;
	MutableTree<Tract> site;
	
	public void setUp() {
		context = new MapContext<Object>();
		grinder = new SiteGrinder();
		pages = new SimpleTree<Template>();
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
		grinder.grind(pages, templates, site, context);
		assertTrue(site.isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	public void testSingleSimplePage() {
//		pages.setValue(new MapTract("brasspyramid.com"));
		pages.addChild(new SimpleTree<Template>(new Template("hello")));
		grinder.grind(pages, templates, site, context);
		assertFalse(site.isEmpty());
		assertEquals("brasspyramid.com", site.getValue().getBodyAsString());
		assertTrue(new Checklist<Tree<Tract>>(
				new SimpleTree<Tract>(new MapTract("hello")) 
			).check(site.getChildren()));
	}
	
	@SuppressWarnings("unchecked")
	public void testMultipleSimplePage() {
//		pages.setValue(new MapTract("brasspyramid.com"));
		pages.addChild(new SimpleTree<Template>(new Template("hello")));
		pages.addChild(new SimpleTree<Template>(new Template("goodbye")));
		grinder.grind(pages, templates, site, context);
		assertFalse(site.isEmpty());
		assertEquals("brasspyramid.com", site.getValue().getBodyAsString());
		assertTrue(new Checklist<Tree<Tract>>(
				new SimpleTree<Tract>(new MapTract("hello")), 
				new SimpleTree<Tract>(new MapTract("goodbye")) 
			).check(site.getChildren()));
	}
	
	@SuppressWarnings("unchecked")
	public void testHierarchy() {
//		pages.setValue(new MapTract("brasspyramid.com"));
		MutableTree<Template> child = new SimpleTree<Template>(new Template("hello"));
		pages.addChild(child);
		child.addChild(new SimpleTree<Template>(new Template("goodbye")));
		grinder.grind(pages, templates, site, context);
		assertFalse(site.isEmpty());
		assertEquals("brasspyramid.com", site.getValue().getBodyAsString());
		assertTrue(new Checklist<Tree<Tract>>(
				new SimpleTree<Tract>(new MapTract("hello"), children("goodbye")) 
		).check(site.getChildren()));
	}

	private Collection<Tree<Tract>> children(String... strings) {
		Collection<Tree<Tract>> ret = new ArrayList<Tree<Tract>>();
		for (String string : strings) {
			ret.add(new SimpleTree<Tract>(new MapTract(string)));
		}
		return ret;
	}
}	
