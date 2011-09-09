package tests;

import java.io.File;

import junit.framework.TestCase;

import org.stringtree.grinder.SiteGrinder;

public class MyOwnDocs extends TestCase {
	public void testProjectWebsite() {
		grind("src/main/docs/");
	}

	public void testTutorialStep1() {
		grind("src/main/tutorial/step1/");
	}

	public void testTutorialStep2() {
		grind("src/main/tutorial/step2/");
	}

	public void grind(String base) {
		File output = new File(base + "output");
		for (File child : output.listFiles()) {
			child.delete();
		}
		SiteGrinder.main(new String[] {base + "input",base + "output"});
	}

}
