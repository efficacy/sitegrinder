package org.stringtree.grinder;

import java.util.ArrayList;
import java.util.Collection;

import org.stringtree.Context;
import org.stringtree.Tract;
import org.stringtree.context.FallbackContext;
import org.stringtree.solomon.Collector;
import org.stringtree.solomon.Session;
import org.stringtree.solomon.Template;
import org.stringtree.solomon.collector.StringBuilderCollector;
import org.stringtree.solomon.tokenstream.TokenSource;
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
	
	private void addIfNotNull(Collection<TokenSource> collection, Template item) {
		if (null != item) collection.add(item);
	}
	
	private void addIfNotNull(Collection<TokenSource> collection, Collection<Template> items) {
		if (null != items && !items.isEmpty()) collection.addAll(items);
	}
	
	protected boolean visit(Tree<Template> from, MutableTree<Tract> to) {
		Template page = from.getValue();
		if (null == page) return false;
		
		String name = page.get(SiteGrinder.NAME);
		if (null == name) return false;
		
System.err.println("visit page=" + page);

		Collection<TokenSource> combined = new ArrayList<TokenSource>();
		Template prologue = templates.get(PAGE_PROLOGUE);
System.err.println("visit, prologue=" + prologue);
		addIfNotNull(combined, prologue);
		addIfNotNull(combined, templater.applyPrologueEpilogue(page.get(SiteGrinder.PARENT), page));
		Template epilogue = templates.get(PAGE_EPILOGUE);
		addIfNotNull(combined, epilogue);
System.err.println("visit, combined=" + combined);
		
		Collector collector = new StringBuilderCollector();
		Context<String> pageContext = new FallbackContext<String>(page, context);
		templater.expandAll((Collection<TokenSource>)combined, pageContext, templates, collector, session);
		Tract ret = new MapTract(collector.toString());
		
		String newname = name.replaceAll("\\.page$", ".html");
		ret.put(SiteGrinder.NAME, newname);
		ret.put(SiteGrinder.TYPE, page.get(SiteGrinder.TYPE));
		ret.put(SiteGrinder.FILE, page.get(SiteGrinder.FILE));
		ret.put(SiteGrinder.NAME, newname);
		to.setValue(ret);
		return true;
	}
}
