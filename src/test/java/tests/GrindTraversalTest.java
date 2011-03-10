package tests;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.stringtree.Tract;
import org.stringtree.fetcher.MapFetcher;
import org.stringtree.finder.FetcherTractKeeper;
import org.stringtree.finder.MapStringKeeper;
import org.stringtree.finder.StringKeeper;
import org.stringtree.finder.TractKeeper;
import org.stringtree.grinder.SiteGrinder;
import org.stringtree.tract.MapTract;
import org.stringtree.util.testing.Checklist;
import org.stringtree.util.tree.MutableTree;
import org.stringtree.util.tree.SimpleTree;
import org.stringtree.util.tree.Tree;

public class GrindTraversalTest extends TestCase {
	StringKeeper context;
	SiteGrinder grinder;
	MutableTree<Tract> pages;
	TractKeeper templates;
	MutableTree<Tract> site;
	
	public void setUp() {
		context = new MapStringKeeper();
		grinder = new SiteGrinder();
		pages = new SimpleTree<Tract>();
		templates = new FetcherTractKeeper(new MapFetcher());
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
	
	public void testSingleSimplePage() {
		pages.setValue(new MapTract("brasspyramid.com"));
		pages.addChild(new SimpleTree<Tract>(new MapTract("hello")));
		grinder.grind(pages, templates, site, context);
		assertFalse(site.isEmpty());
		assertEquals("brasspyramid.com", site.getValue().getContent());
		assertTrue(Checklist.compareCollection(new Tree[] { 
				new SimpleTree<Tract>(new MapTract("hello")) 
			}, site.getChildren()));
	}
	
	public void testMultipleSimplePage() {
		pages.setValue(new MapTract("brasspyramid.com"));
		pages.addChild(new SimpleTree<Tract>(new MapTract("hello")));
		pages.addChild(new SimpleTree<Tract>(new MapTract("goodbye")));
		grinder.grind(pages, templates, site, context);
		assertFalse(site.isEmpty());
		assertEquals("brasspyramid.com", site.getValue().getContent());
		assertTrue(Checklist.compareCollection(new Tree[] { 
				new SimpleTree<Tract>(new MapTract("hello")), 
				new SimpleTree<Tract>(new MapTract("goodbye")) 
			}, site.getChildren()));
	}
	
	public void testHierarchy() {
		pages.setValue(new MapTract("brasspyramid.com"));
		MutableTree<Tract> child = new SimpleTree<Tract>(new MapTract("hello"));
		pages.addChild(child);
		child.addChild(new SimpleTree<Tract>(new MapTract("goodbye")));
		grinder.grind(pages, templates, site, context);
		assertFalse(site.isEmpty());
		assertEquals("brasspyramid.com", site.getValue().getContent());
		assertTrue(Checklist.compareCollection(new Tree[] { 
				new SimpleTree<Tract>(new MapTract("hello"), children("goodbye")) 
			}, site.getChildren()));
	}

	private Collection<Tree<Tract>> children(String... strings) {
		Collection<Tree<Tract>> ret = new ArrayList<Tree<Tract>>();
		for (String string : strings) {
			ret.add(new SimpleTree<Tract>(new MapTract(string)));
		}
		return ret;
	}
}	
