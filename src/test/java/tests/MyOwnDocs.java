package tests;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.stringtree.grinder.SiteGrinder;

public class MyOwnDocs extends TestCase {

	public void testTutorialStep1() throws IOException {
		grind("src/main/tutorial/step1/");
	}

	public void testTutorialStep2() throws IOException {
		grind("src/main/tutorial/step2/");
	}

	public void testTutorialStep3() throws IOException {
		grind("src/main/tutorial/step3/");
	}

	// do this last, as it uses output from the steps
	public void testProjectWebsite() throws IOException {
		grind("src/main/docs/");
	}

	public void grind(String base) throws IOException {
		File output = new File(base + "output");
		if (output.exists()) for (File child : output.listFiles()) {
			child.delete();
		} else {
			output.mkdirs();
		}
		SiteGrinder.main(new String[] {base + "input",base + "output"});
	}

}
