package org.stringtree.grinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.stringtree.Context;
import org.stringtree.Tract;
import org.stringtree.context.FallbackContext;
import org.stringtree.solomon.Collector;
import org.stringtree.solomon.Session;
import org.stringtree.solomon.Template;
import org.stringtree.solomon.collector.StringBuilderCollector;
import org.stringtree.solomon.tree.TreeTemplater;
import org.stringtree.tract.MapTract;
import org.stringtree.util.tree.MutableTree;
import org.stringtree.util.tree.Tree;

public class TemplateTreeTransformVisitor extends SimpleTreeTransformVisitor<Template, Tract> {
	public static final String PAGE_PROLOGUE = "prologue";
	public static final String PAGE_EPILOGUE = "epilogue";
	
    static final String CHARSET = "charset";
	
	TreeTemplater templater;
	Context<Template> templates;
	Context<String> context;
	Session session;

	public TemplateTreeTransformVisitor(Context<Template> templates, Context<String> context, Session session) {
		this.templates = templates;
		this.context = context;
		this.templater = new TreeTemplater(templates);
		this.session = session;
	}
	
	private void addIfNotNull(Collection<Object> collection, Object item) {
		if (null != item) collection.add(item);
	}
	
	protected boolean visit(Tree<Template> from, MutableTree<Tract> to) {
		Template page = from.getValue();
		if (null == page) return false;

		List<Object> combined = new ArrayList<Object>();
		addIfNotNull(combined, templates.get(PAGE_PROLOGUE));
		addIfNotNull(combined, templater.applyPrologueEpilogue(page.get(SiteGrinder.PARENT), page));
		addIfNotNull(combined, templates.get(PAGE_EPILOGUE));
		
		Collector collector = new StringBuilderCollector();
		Context<String> pageContext = new FallbackContext<String>(page, context);
//Diagnostics.dumpFetcher(pageContext, "page context");
		templater.expand(page, pageContext, templates, collector, session);
		Tract ret = new MapTract(collector.toString());
		
		String oldname = page.get(SiteGrinder.NAME);
		String newname = oldname.replaceAll("\\.(tract|tpl)$", ".html");
		ret.put(SiteGrinder.NAME, newname);
		ret.put(SiteGrinder.TYPE, page.get(SiteGrinder.TYPE));
		ret.put(SiteGrinder.FILE, page.get(SiteGrinder.FILE));
		ret.put(SiteGrinder.NAME, newname);
		to.setValue(ret);
		return true;
	}
}
