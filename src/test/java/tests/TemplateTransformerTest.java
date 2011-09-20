package tests;

import junit.framework.TestCase;

import org.stringtree.Context;
import org.stringtree.Tract;
import org.stringtree.context.MapContext;
import org.stringtree.grinder.SiteGrinder;
import org.stringtree.grinder.TemplateTreeTransformVisitor;
import org.stringtree.solomon.Session;
import org.stringtree.solomon.Solomon;
import org.stringtree.solomon.Template;
import org.stringtree.util.LiteralMap;
import org.stringtree.util.tree.MutableTree;
import org.stringtree.util.tree.SimpleTree;

public class TemplateTransformerTest extends TestCase {
	Solomon templater;
	Context<Template> templates;
	Context<String> context;
	Session session;
	TemplateTreeTransformVisitor tttv;
	
	MutableTree<Template> from;
	MutableTree<Tract> to;

	public void setUp() {
		templates = new MapContext<Template>();
		context = new MapContext<String>();
		session = new Session();
		tttv = new TemplateTreeTransformVisitor(templates, context, session);
		from = new SimpleTree<Template>();
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
		from.setValue(template(content, SiteGrinder.NAME, "example.tpl"));
		tttv.enter(from, to);
		assertEquals(content, to.getValue().getBodyAsString());
	}

	private Template template(String content, Object... attributes) {
		return new Template(content, new MapContext<String>(new LiteralMap<String,Object>(attributes)));
	}
	
	public void testSimpleSubstitution() {
		context.put("text", "Frank");
		from.setValue(template("example ${text}"));
		tttv.enter(from, to);
		assertEquals("example Frank", to.getValue().getBodyAsString());
	}
	
	public void testTractSubstitution() {
		from.setValue(template("example ${text}", "text", "Frank"));
		tttv.enter(from, to);
		assertEquals("example Frank", to.getValue().getBodyAsString());
	}
	
	public void testSubTemplate() {
		templates.put("text", new Template("Margaret"));
		from.setValue(template("example ${*text}"));
		tttv.enter(from, to);
		assertEquals("example Margaret", to.getValue().getBodyAsString());
	}
	
	public void testSubTract() {
		templates.put("text", template("${name}", "name", "Katherine"));
		context.put("name", "Elizabeth");
		
		from.setValue(template("${name} ${*text} ${name}"));
		tttv.enter(from, to);
		assertEquals("Elizabeth Katherine Elizabeth", to.getValue().getBodyAsString());
	}
	
	public void testHeaderFooter() {
		templates.put(TemplateTreeTransformVisitor.PAGE_PROLOGUE, new Template("Before["));
		templates.put(TemplateTreeTransformVisitor.PAGE_EPILOGUE, new Template("]After"));
		from.setValue(template("example text"));
		tttv.enter(from, to);
		assertEquals("Before[example text]After", to.getValue().getBodyAsString());
	}
	
	public void testHeaderFooterSubstitution() {
		templates.put(TemplateTreeTransformVisitor.PAGE_PROLOGUE, new Template("(title=${title})"));
		from.setValue(template("example text", "title", "Home"));
		tttv.enter(from, to);
		assertEquals("(title=Home)example text", to.getValue().getBodyAsString());
	}
}
