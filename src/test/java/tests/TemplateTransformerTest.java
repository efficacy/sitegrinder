package tests;

import junit.framework.TestCase;

import org.rack4java.Context;
import org.rack4java.context.MapContext;
import org.stringtree.Tract;
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
		context.with("text", "Frank");
		Template template = Helper.page("example ${text}");
		from.setValue(template);
		tttv.enter(from, to);
		assertEquals("example Frank", to.getValue().getBodyAsString());
	}
	
	public void testTractSubstitution() {
		Template template = Helper.page("example ${text}");
		template.with("text", "Frank");
		from.setValue(template);
		tttv.enter(from, to);
		assertEquals("example Frank", to.getValue().getBodyAsString());
	}
	
	public void testSubTemplate() {
		templates.with("text", new Template("Margaret"));
		from.setValue(Helper.page("example ${*text}"));
		tttv.enter(from, to);
		assertEquals("example Margaret", to.getValue().getBodyAsString());
	}
	
	public void testSubTract() {
		Template template = template("${name}", "name", "Katherine");
		templates.with("text", template);
		context.with("name", "Elizabeth");
		
		from.setValue(Helper.page("${name} ${*text} ${name}"));
		tttv.enter(from, to);
		assertEquals("Elizabeth Katherine Elizabeth", to.getValue().getBodyAsString());
	}
	
	public void testHeaderFooter() {
		templates.with(TemplateTreeTransformVisitor.PAGE_PROLOGUE, new Template("Before["));
		templates.with(TemplateTreeTransformVisitor.PAGE_EPILOGUE, new Template("]After"));
		from.setValue(Helper.page("example text"));
		tttv.enter(from, to);
		assertEquals("Before[example text]After", to.getValue().getBodyAsString());
	}
	
	public void testHeaderFooterSubstitution() {
		templates.with(TemplateTreeTransformVisitor.PAGE_PROLOGUE, new Template("(title=${title})"));
		Template template = Helper.page("example text");
		template.with("title", "Home");
		from.setValue(template);
		tttv.enter(from, to);
		assertEquals("(title=Home)example text", to.getValue().getBodyAsString());
	}
}
