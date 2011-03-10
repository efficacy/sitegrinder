package tests;

import junit.framework.TestCase;

import org.stringtree.Tract;
import org.stringtree.fetcher.MapFetcher;
import org.stringtree.finder.FetcherTractKeeper;
import org.stringtree.finder.MapStringKeeper;
import org.stringtree.finder.StringKeeper;
import org.stringtree.finder.TractKeeper;
import org.stringtree.grinder.TemplateTreeTransformVisitor;
import org.stringtree.template.Templater;
import org.stringtree.tract.MapTract;
import org.stringtree.util.tree.MutableTree;
import org.stringtree.util.tree.SimpleTree;

public class TemplateTransformerTest extends TestCase {
	Templater templater;
	TractKeeper templates;
	StringKeeper context;
	TemplateTreeTransformVisitor tttv;
	
	MutableTree<Tract> from;
	MutableTree<Tract> to;

	public void setUp() {
		templates = new FetcherTractKeeper(new MapFetcher());
		context = new MapStringKeeper();
		tttv = new TemplateTreeTransformVisitor(templates, context);
		from = new SimpleTree<Tract>();
		to = new SimpleTree<Tract>();
	}
	
	public void testInitialConditions() {
		assertTrue(from.isEmpty());
		assertTrue(to.isEmpty());
	}
	
	public void testEmpty() {
		tttv.enter(from, to);
		testInitialConditions();
	}
	
	public void testLiteral() {
		String content = "example text";
		from.setValue(tract(content, "example.tpl"));
		tttv.enter(from, to);
		assertEquals(content, to.getValue().getContent());
	}

	private Tract tract(String content, String name) {
		Tract ret = new MapTract(content);
		ret.put(Tract.NAME, name);
		return ret;
	}
	
	public void testSimpleSubstitution() {
		context.put("text", "Frank");
		from.setValue(new MapTract("example ${text}"));
		tttv.enter(from, to);
		assertEquals("example Frank", to.getValue().getContent());
	}
	
	public void testTractSubstitution() {
		Tract page = new MapTract("example ${text}");
		page.put("text", "Frank");
		from.setValue(page);
		tttv.enter(from, to);
		assertEquals("example Frank", to.getValue().getContent());
	}
	
	public void testSubTemplate() {
		templates.put("text", new MapTract("Margaret"));
		from.setValue(new MapTract("example ${*text}"));
		tttv.enter(from, to);
		assertEquals("example Margaret", to.getValue().getContent());
	}
	
	public void testSubTract() {
		Tract tract = new MapTract("${name}");
		tract.put("name", "Katherine");
		templates.put("text", tract);
		context.put("name", "Elizabeth");
		
		from.setValue(new MapTract("${name} ${*text} ${name}"));
		tttv.enter(from, to);
		assertEquals("Elizabeth Katherine Elizabeth", to.getValue().getContent());
	}
	
	public void testHeaderFooter() {
		templates.put(TemplateTreeTransformVisitor.PAGE_PROLOGUE, new MapTract("Before["));
		templates.put(TemplateTreeTransformVisitor.PAGE_EPILOGUE, new MapTract("]After"));
		from.setValue(new MapTract("example text"));
		tttv.enter(from, to);
		assertEquals("Before[example text]After", to.getValue().getContent());
	}
	
	public void testHeaderFooterSubstitution() {
		templates.put(TemplateTreeTransformVisitor.PAGE_PROLOGUE, new MapTract("(title=${title})"));
		MapTract page = new MapTract("example text");
		page.put("title", "Home");
		from.setValue(page);
		tttv.enter(from, to);
		assertEquals("(title=Home)example text", to.getValue().getContent());
	}
}
