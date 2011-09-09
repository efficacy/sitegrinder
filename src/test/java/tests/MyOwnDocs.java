package tests;

import java.io.File;

import junit.framework.TestCase;

import org.stringtree.grinder.SiteGrinder;

public class MyOwnDocs extends TestCase {
	public void testGenerateDocs() {
		File output = new File("src/main/docs/output");
		for (File child : output.listFiles()) {
			child.delete();
		}
		SiteGrinder.main(new String[] {"src/main/docs/input","src/main/docs/output"});
	}

}
