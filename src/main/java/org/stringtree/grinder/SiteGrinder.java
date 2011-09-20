package org.stringtree.grinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;

import org.stringtree.Context;
import org.stringtree.SystemContext;
import org.stringtree.Tract;
import org.stringtree.context.ConvertingContext;
import org.stringtree.context.MapContext;
import org.stringtree.converter.TemplateFileConverter;
import org.stringtree.solomon.Session;
import org.stringtree.solomon.Template;
import org.stringtree.spec.SpecReader;
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
		
		Context<String> context = new MapContext<String>();
		MutableTree<Template> pages = new SimpleTree<Template>();
		
		File tpldir = new File(srcdir, "_templates");
		Context<Template> templates = tpldir.exists() 
			? new ConvertingContext<Template>(new TemplateFileConverter(tpldir))
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

	public void load(File srcdir, MutableTree<Template> pages, String parent, Context<String> context) {
//		pages.setValue(new MapTract(srcdir.getName()));
		Context<Template> src = new ConvertingContext<Template>(new TemplateFileConverter(srcdir));
		File[] files = srcdir.listFiles();
		for (File file : files) {
			String name = file.getName();
			if (name.startsWith(".") || name.startsWith("_")) {
				continue;
			}
			int dot = name.indexOf('.');
			String key = dot > 0 ? name.substring(0, dot) : name;
			if (!StringUtils.isBlank(parent)) key = parent + "/" + key; 

			MutableTree<Template> child = new SimpleTree<Template>();
			Template template;
			
			if (file.isDirectory()) {
				load(file, child, parent, context);
				template = new Template();
				template.put(TYPE, TYPE_FOLDER);
				template.put(NAME, name);
			} else if (name.endsWith(".tpl") || name.endsWith(".tract") || name.endsWith(".page")) {
				template = src.get(name);
				template.put(TYPE, TYPE_TEMPLATE);
				template.put(NAME, key + ".html");
			} else {
				template = new Template();
				template.put(TYPE, TYPE_BINARY);
				template.put(NAME, name);
			}

			template.put("page.key", key);
			template.put(PARENT, parent);
			template.put(FILE, file);

			child.setValue(template);
			pages.addChild(child);
		}
	}

	public void load(File srcdir, MutableTree<Template> pages, Context<String> context) {
		load(srcdir, pages, "", context);
	}
	
	public void save(File destdir, MutableTree<Tract> site) {
		TreeWalker<Tract> walker = new TreeWalker<Tract>(site);
		walker.walkChildren(new StringFileWritingTreeVisitor(destdir));
	}

	public void grind(final Tree<Template> pages, final Context<Template> templates, final MutableTree<Tract> site, 
			Context<String> context) {
		if (null == pages) throw new IllegalArgumentException("cannot grind from null page source");
		if (null == templates) throw new IllegalArgumentException("cannot grind from null template source");
		if (null == site) throw new IllegalArgumentException("cannot grind to null site tree");

		if (pages.isEmpty()) return;
		Session session = new Session();
		
		TreeTransformer<Template,Tract> tx = new TreeTransformer<Template,Tract>(pages, site);
		tx.transform(new TemplateTreeTransformVisitor(templates, context, session));
	}
}
