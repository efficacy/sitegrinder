package org.stringtree.grinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.stringtree.Tract;
import org.stringtree.fetcher.FallbackFetcher;
import org.stringtree.finder.FetcherStringFinder;
import org.stringtree.finder.StringFinder;
import org.stringtree.finder.StringKeeper;
import org.stringtree.finder.TractFinder;
import org.stringtree.template.ByteArrayStringCollector;
import org.stringtree.template.StringCollector;
import org.stringtree.template.TreeTemplater;
import org.stringtree.tract.MapTract;
import org.stringtree.util.tree.MutableTree;
import org.stringtree.util.tree.Tree;

public class TemplateTreeTransformVisitor extends SimpleTreeTransformVisitor<Tract, Tract> {
	public static final String PAGE_PROLOGUE = "prologue";
	public static final String PAGE_EPILOGUE = "epilogue";
	
	TreeTemplater templater;
	TractFinder templates;
	StringKeeper context;

	public TemplateTreeTransformVisitor(TractFinder templates, StringKeeper context) {
		this.templates = templates;
		this.context = context;
		this.templater = new TreeTemplater(templates);
	}
	
	private void addIfNotNull(Collection<Object> collection, Object item) {
		if (null != item) collection.add(item);
	}
	
	protected boolean visit(Tree<Tract> from, MutableTree<Tract> to) {
		Tract page = from.getValue();
		if (null == page) return false;

		List<Object> combined = new ArrayList<Object>();
		addIfNotNull(combined, templates.getObject(PAGE_PROLOGUE));
		addIfNotNull(combined, templater.applyPrologueEpilogue(page.get(SiteGrinder.PARENT), page));
		addIfNotNull(combined, templates.getObject(PAGE_EPILOGUE));
		
		StringCollector collector = new ByteArrayStringCollector();
		StringFinder pageContext = new FetcherStringFinder(new FallbackFetcher(page, context));
//Diagnostics.dumpFetcher(pageContext, "page context");
		templater.expandTemplate(pageContext, combined, collector);
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
