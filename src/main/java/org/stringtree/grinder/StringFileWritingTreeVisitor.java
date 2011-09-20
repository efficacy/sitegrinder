package org.stringtree.grinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Stack;

import org.stringtree.Tract;
import org.stringtree.solomon.Template;
import org.stringtree.util.FileWritingUtils;
import org.stringtree.util.StringUtils;
import org.stringtree.util.tree.Tree;
import org.stringtree.util.tree.TreeVisitor;

public class StringFileWritingTreeVisitor implements TreeVisitor<Tract> {

	Stack<File> current;
	
	public StringFileWritingTreeVisitor(File destdir) {
		current = new Stack<File>();
		current.push(destdir);
	}

	public void enter(Tree<Tract> node) {
		Tract page = node.getValue();
		String type = page.get(SiteGrinder.TYPE);
		String name = page.get(SiteGrinder.NAME);
		if (SiteGrinder.TYPE_BINARY.equals(type)) {
			copy((File)page.getObject(SiteGrinder.FILE), new File(current.peek(), name));
		} else if (SiteGrinder.TYPE_FOLDER.equals(type)) {
			File file = new File(current.peek(), name);
			file.mkdirs();
			current.push(file);
		} else if (SiteGrinder.TYPE_PAGE.equals(type)) {
			try {
				if (StringUtils.isBlank(name)) {
					name = page.getBodyAsString();
					File file = new File(current.peek(), name);
					file.mkdir();
				} else {
					File file = new File(current.peek(), name);
					FileWritingUtils.writeFile(file, page.getBodyAsString());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void exit(Tree<Tract> node) {
		Tract page = node.getValue();
		String type = page.get(SiteGrinder.TYPE);
		if (SiteGrinder.TYPE_FOLDER.equals(type)) current.pop();
	}
	
    private void copy(File source, File dest) {
    	FileChannel in = null, out = null;
    	try {
			File parent = dest.getParentFile();
			if (!parent.isDirectory()) {
				parent.mkdirs();
			}
			in = new FileInputStream(source).getChannel();
			out = new FileOutputStream(dest).getChannel();

			long size = in.size();
			MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);

			out.write(buf);
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		if (in != null) try {in.close();} catch (IOException e) {}
    		if (out != null) try {out.close();} catch (IOException e) {}
    	}
   }
}
