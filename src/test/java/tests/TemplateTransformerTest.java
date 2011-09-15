package tests;

import junit.framework.TestCase;

import org.stringtree.Context;
import org.stringtree.Tract;
import org.stringtree.context.MapContext;
import org.stringtree.grinder.SiteGrinder;
import org.stringtree.grinder.TemplateTreeTransformVisitor;
import org.stringtree.solomon.Solomon;
import org.stringtree.solomon.Template;
import org.stringtree.tract.MapTract;
import org.stringtree.util.tree.MutableTree;
import org.stringtree.util.tree.SimpleTree;

public class TemplateTransformerTest extends TestCase {
	Solomon templater;
	Context<Template> templates;
	Context<Object> context;
	TemplateTreeTransformVisitor tttv;
	
	MutableTree<Tract> from;
	MutableTree<Tract> to;

	public void setUp() {
		templates = new MapContext<Template>();
		context = new MapContext<Object>();
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
		assertEquals(content, to.getValue().getBodyAsString());
	}

	private Tract tract(String content, String name) {
		Tract ret = new MapTract(content);
		ret.put(SiteGrinder.NAME, name);
		return ret;
	}
	
	public void testSimpleSubstitution() {
		context.put("text", "Frank");
		from.setValue(new MapTract("example ${text}"));
		tttv.enter(from, to);
		assertEquals("example Frank", to.getValue().getBodyAsString());
	}
	
	public void testTractSubstitution() {
		Tract page = new MapTract("example ${text}");
		page.put("text", "Frank");
		from.setValue(page);
		tttv.enter(from, to);
		assertEquals("example Frank", to.getValue().getBodyAsString());
	}
	
	public void testSubTemplate() {
		templates.put("text", new MapTract("Margaret"));
		from.setValue(new MapTract("example ${*text}"));
		tttv.enter(from, to);
		assertEquals("example Margaret", to.getValue().getBodyAsString());
	}
	
	public void testSubTract() {
		Tract tract = new MapTract("${name}");
		tract.put("name", "Katherine");
		templates.put("text", tract);
		context.put("name", "Elizabeth");
		
		from.setValue(new MapTract("${name} ${*text} ${name}"));
		tttv.enter(from, to);
		assertEquals("Elizabeth Katherine Elizabeth", to.getValue().getBodyAsString());
	}
	
	public void testHeaderFooter() {
		templates.put(TemplateTreeTransformVisitor.PAGE_PROLOGUE, new MapTract("Before["));
		templates.put(TemplateTreeTransformVisitor.PAGE_EPILOGUE, new MapTract("]After"));
		from.setValue(new MapTract("example text"));
		tttv.enter(from, to);
		assertEquals("Before[example text]After", to.getValue().getBodyAsString());
	}
	
	public void testHeaderFooterSubstitution() {
		templates.put(TemplateTreeTransformVisitor.PAGE_PROLOGUE, new MapTract("(title=${title})"));
		MapTract page = new MapTract("example text");
		page.put("title", "Home");
		from.setValue(page);
		tttv.enter(from, to);
		assertEquals("(title=Home)example text", to.getValue().getBodyAsString());
	}
}
