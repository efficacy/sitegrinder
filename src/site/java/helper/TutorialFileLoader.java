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
	
	private String load(StringKeeper context, String zone) {
		String name = context.get("this");
		String step = context.get("step");
//System.err.println("load zone=[" + zone + "] name=[" + name + "] step=[" + step + "]");
		File file = new File(root, "step" + step + "/" + zone + "/" + name);
//System.err.println("load file=[" + file.getAbsolutePath() + "] exists=" + file.exists());
		String ret = FileReadingUtils.readFile(file);
		
		XMLEscaper xml = new XMLEscaper();
		ret = xml.convert(ret);
		
		return ret;
	}
	
	public String loadinput(StringKeeper context) {
		return load(context, "input");
	}
	
	public String loadoutput(StringKeeper context) {
		return load(context, "output");
	}
	
	@Override public String toString() {
		return "TutorialFileLoader[root=" + root.getAbsolutePath() + "]";
	}
}
