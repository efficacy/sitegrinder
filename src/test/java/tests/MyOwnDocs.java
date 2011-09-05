package tests;

import junit.framework.TestCase;

import org.stringtree.grinder.SiteGrinder;

public class MyOwnDocs extends TestCase {
	public void testGenerateDocs() {
		SiteGrinder.main(new String[] {"src/main/docs/input","src/main/docs/output"});
	}

}
