package helper;

import java.io.File;

import org.stringtree.util.FileReadingUtils;
import org.stringtree.xml.XMLEscaper;

public class TutorialFileLoader {
	File root;
	
	public TutorialFileLoader(String rootname) {
		this.root = new File(rootname);
	}
	
	private String load(String zone, String name, String step) {
		File file = new File(root, "step" + step + "/" + zone + "/" + name);
		String ret = FileReadingUtils.readFile(file);
		
		XMLEscaper xml = new XMLEscaper();
		ret = xml.convert(ret);
		
		return ret;
	}
	
	public String loadinput(String name, String step) {
		return load("input", name, step);
	}
	
	public String loadoutput(String name, String step) {
		return load("output", name, step);
	}
	
	@Override public String toString() {
		return "TutorialFileLoader[root=" + root.getAbsolutePath() + "]";
	}
}
