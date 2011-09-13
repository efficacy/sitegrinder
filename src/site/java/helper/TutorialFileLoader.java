package helper;

import java.io.File;

import org.stringtree.finder.StringKeeper;
import org.stringtree.util.FileReadingUtils;
import org.stringtree.util.XMLEscaper;

public class TutorialFileLoader {
	File root;
	
	public TutorialFileLoader(String rootname) {
		this.root = new File(rootname);
	}
	
	public String load(StringKeeper context) {
		String name = context.get("this");
		String step = context.get("step");
		File file = new File(root, "step" + step + "/input/" + name);
		String ret = FileReadingUtils.readFile(file);
		
		XMLEscaper xml = new XMLEscaper();
		ret = xml.convert(ret);
		
		return ret;
	}
	
	@Override public String toString() {
		return "TutorialFileLoader[root=" + root.getAbsolutePath() + "]";
	}
}
