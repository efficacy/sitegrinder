package tests;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.stringtree.grinder.SiteGrinder;
import org.stringtree.util.FileReadingUtils;

public class MainTest extends TestCase {
	ByteArrayOutputStream buf;
	File outroot;

	public void setUp() {
		buf = new ByteArrayOutputStream();
		SiteGrinder.setLogStream(new PrintStream(buf));
		outroot = new File("src/test/output/");
	}
	
	private File outfile(String name) {
		return new File(outroot, name);
	}

	private void clearfiles(String dir) {
		File base = outfile(dir);
		clearfiles(base);
	}

	private void clearfiles(File dir) {
		for (File file : dir.listFiles(new FileFilter() {
				public boolean accept(File file) {
					return !file.getName().startsWith(".");
				}})) {
			if (file.isDirectory()) clearfiles(file);
			file.delete();
		}
	}

	private void grind(String dir) throws IOException {
		SiteGrinder.main(new String[] {"src/test/input/" + dir,"src/test/output/" + dir});
	}
	
	public void testNoArgsGivesUsage() throws IOException {
		SiteGrinder.main(new String[] {});
		assertTrue(buf.toString().contains("usage: SiteGrinder <from> <to>"));
	}
	
	public void testSingleArgGivesUsage() throws IOException {
		SiteGrinder.main(new String[] {"thing"});
		assertTrue(buf.toString().contains("usage: SiteGrinder <from> <to>"));
	}
	
	public void testBadFromGivesError() throws IOException {
		SiteGrinder.main(new String[] {"thing","wossname"});
		assertTrue(buf.toString().contains("error: cannot read from folder"));
	}
	
	public void testBadToGivesError() throws IOException {
		SiteGrinder.main(new String[] {"src/test/input/test1","wossname"});
		assertTrue(buf.toString().contains("error: cannot write to folder"));
	}
	
	public void testSingleLiteralPage() throws IOException {
		clearfiles("test1");
		grind("test1");
		assertTrue(outfile("test1/home.html").exists());
	}

	public void testMultipleLiteralPage() throws IOException {
		clearfiles("test2");
		grind("test2");
		assertTrue(outfile("test2/index.html").exists());
		assertTrue(outfile("test2/about.html").exists());
	}

	public void testHierarchy() throws IOException {
		clearfiles("test3");
		grind("test3");
		assertTrue(outfile("test3/index.html").exists());
		assertTrue(outfile("test3/products/p1.html").exists());
		assertTrue(outfile("test3/products/p2.html").exists());
	}
	
	public void testTemplates() throws IOException {
		clearfiles("test4");
		grind("test4");
		assertEquals("start[this is the home page]end", FileReadingUtils.readFile(outfile("test4/index.html")));
	}
	
	public void testSiteSpec() throws IOException {
		clearfiles("test5");
		grind("test5");
		assertEquals("I see 3 things", FileReadingUtils.readFile(outfile("test5/index.html")));
	}
	
	public void testPluginClasses() throws IOException {
		clearfiles("test6");
		grind("test6");
		assertEquals("hello 4, hello 5", FileReadingUtils.readFile(outfile("test6/index.html")));
	}
	
	public void testCopyofOpaqueFiles() throws IOException {
		clearfiles("test7");
		grind("test7");
		assertTrue(outfile("test7/index.html").exists());
	}
}
