package org.stringtree.grinder;

import org.stringtree.Tract;
import org.stringtree.fetcher.FallbackFetcher;
import org.stringtree.finder.FetcherStringFinder;
import org.stringtree.finder.StringFinder;
import org.stringtree.finder.StringKeeper;
import org.stringtree.finder.TractFinder;
import org.stringtree.template.ByteArrayStringCollector;
import org.stringtree.template.EasyTemplater;
import org.stringtree.template.StringCollector;
import org.stringtree.template.Templater;
import org.stringtree.tract.MapTract;
import org.stringtree.util.tree.MutableTree;
import org.stringtree.util.tree.Tree;

public class TemplateTreeTransformVisitor extends SimpleTreeTransformVisitor<Tract, Tract> {
	public static final String PAGE_PROLOGUE = "page_prologue";
	public static final String PAGE_EPILOGUE = "page_epilogue";
	
	Templater templater;
	TractFinder templates;
	StringKeeper context;

	public TemplateTreeTransformVisitor(TractFinder templates, StringKeeper context) {
		this.templates = templates;
		this.context = context;
		this.templater = new EasyTemplater(templates);
	}
	
	protected boolean visit(Tree<Tract> from, MutableTree<Tract> to) {
		Tract page = from.getValue();
		if (null == page) return false;

		StringCollector collector = new ByteArrayStringCollector();
		StringFinder pageContext = new FetcherStringFinder(new FallbackFetcher(page, context));
		Tract prologue = (Tract) templates.getObject(PAGE_PROLOGUE);
		if (null != prologue) {
			templater.expandTemplate(pageContext, prologue, collector);
		}
		templater.expandTemplate(context, page, collector);
		Tract epilogue = (Tract) templates.getObject(PAGE_EPILOGUE);
		if (null != epilogue) {
			templater.expand(pageContext, PAGE_EPILOGUE, collector);
		}
		Tract ret = new MapTract(collector.toString());
		
		String oldname = page.get(Tract.NAME);
		String newname = oldname.replaceAll("\\.(tract|tpl)$", ".html");
		ret.put(Tract.NAME, newname);
		ret.put(SiteGrinder.TYPE, page.getObject(SiteGrinder.TYPE));
		ret.put(SiteGrinder.FILE, page.getObject(SiteGrinder.FILE));
		ret.put(Tract.NAME, newname);
		to.setValue(ret);
		return true;
	}
}
