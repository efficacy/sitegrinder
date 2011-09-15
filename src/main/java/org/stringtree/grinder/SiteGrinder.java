package org.stringtree.grinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.stringtree.Context;
import org.stringtree.Tract;
import org.stringtree.context.DirectoryContext;
import org.stringtree.context.MapContext;
import org.stringtree.solomon.Template;
import org.stringtree.tract.MapTract;
import org.stringtree.util.SmartPathClassLoader;
import org.stringtree.util.StringUtils;
import org.stringtree.util.tree.MutableTree;
import org.stringtree.util.tree.SimpleTree;
import org.stringtree.util.tree.Tree;
import org.stringtree.util.tree.TreeTransformer;
import org.stringtree.util.tree.TreeWalker;

public class SiteGrinder {
	public static final String NAME = "~name";
	public static final String DATE = "~date";
	public static final String TYPE = "~type";
	public static final String FILE = "~file";
	public static final String PARENT = "~parent";

	public static final String TYPE_TEMPLATE = "template";
	public static final String TYPE_FOLDER = "folder";
	public static final String TYPE_BINARY = "binary";
	
	private static final TractRecognizer TRACT_RECOGNISER = new TractRecognizer() {
	    public boolean isTract(File file) {
	        String name = file.getName();
			return file != null && (name.endsWith(".tract") || name.endsWith(".page"));
	    }
	};
	private static PrintStream log = System.out; 
	
	public static void main(String[] args) {
		if (args.length < 2) {
			log.println("usage: SiteGrinder <from> <to>");
			return;
		}
		String src = args[0];
		String dest = args[1];
		SiteGrinder grinder = new SiteGrinder();
		grinder.grind(new File(src), new File(dest));
	}

	public static void setLogStream(PrintStream log) {
		SiteGrinder.log = log;
	}

	public void grind(File srcdir, File destdir) {
		if (!srcdir.isDirectory() || !srcdir.canRead()) {
			log.println("error: cannot read from folder " + srcdir.getAbsolutePath());
			return;
		}
		if (!destdir.isDirectory() || !destdir.canWrite()) {
			log.println("error: cannot write to folder " + destdir.getAbsolutePath());
			return;
		}
		
		Context<Object> context = new MapContext<Object>();
		MutableTree<Template> pages = new SimpleTree<Template>();
		
		File tpldir = new File(srcdir, "_templates");
		Context<Template> templates = tpldir.exists() 
			? new DirectoryContext<Template>(tpldir, new SuffixListFilter(".tract", ".tpl"), false, context)
		    : new MapContext<Template>();
		
		File classdir = new File(srcdir, "_classes");
		if (classdir.isDirectory()) {
			context.put(SystemContext.SYSTEM_CLASSLOADER, new SmartPathClassLoader(classdir.getPath(), getClass().getClassLoader()));
		}
		
		File spec = new File(srcdir, "_site.spec");
		if (spec.canRead())
			try {
				SpecReader.load(context, new FileReader(spec));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		MutableTree<Tract> site = new SimpleTree<Tract>();
		load(srcdir, pages, "", context);
		grind(pages, templates, site, context);
		save(destdir, site);
	}

	public void load(File srcdir, MutableTree<Template> pages, String parent, Context<Object> context) {
		pages.setValue(new MapTract(srcdir.getName()));
		File[] files = srcdir.listFiles();
		for (File file : files) {
			String name = file.getName();
			if (name.startsWith(".") || name.startsWith("_")) {
				continue;
			}
			int dot = name.indexOf('.');
			String key = dot > 0 ? name.substring(0, dot) : name;
			if (!StringUtils.isBlank(parent)) key = parent + "/" + key; 

			MutableTree<Tract> child = new SimpleTree<Tract>();
			Tract tract = new MapTract();
			tract.put("page.key", key);
			tract.put(PARENT, parent);
			tract.put(FILE, file);
			
			
			if (file.isDirectory()) {
				load(file, child, parent, context);
				tract.put(TYPE, TYPE_FOLDER);
				tract.put(NAME, name);
			} else if (name.endsWith(".tpl") || name.endsWith(".tract") || name.endsWith(".page")) {
				try {
					FileTractReader.load(tract, file, TRACT_RECOGNISER, context);
				} catch (IOException e) {
					e.printStackTrace();
				}
				tract.put(TYPE, TYPE_TEMPLATE);
				tract.put(NAME, key + ".html");
			} else {
				tract.put(TYPE, TYPE_BINARY);
				tract.put(NAME, name);
			}

			child.setValue(tract);
			pages.addChild(child);
		}
	}

	public void load(File srcdir, MutableTree<Template> pages, Context<Object> context) {
		load(srcdir, pages, "", context);
	}
	
	public void save(File destdir, MutableTree<Tract> site) {
		TreeWalker<Tract> walker = new TreeWalker<Tract>(site);
		walker.walkChildren(new StringFileWritingTreeVisitor(destdir));
	}

	public void grind(final Tree<Template> pages, final Context<Template> templates, final MutableTree<Tract> site, 
			Context<Object> context) {
		if (null == pages) throw new IllegalArgumentException("cannot grind from null page source");
		if (null == templates) throw new IllegalArgumentException("cannot grind from null template source");
		if (null == site) throw new IllegalArgumentException("cannot grind to null site tree");

		if (pages.isEmpty()) return;
		
		TreeTransformer<Tract,Tract> tx = new TreeTransformer<Tract,Tract>(pages, site);
		tx.transform(new TemplateTreeTransformVisitor(templates, context));
	}
}
