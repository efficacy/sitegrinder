package helper;

import java.io.File;

import org.stringtree.Context;
import org.stringtree.util.FileReadingUtils;
import org.stringtree.xml.XMLEscaper;

public class TutorialFileLoader {
	File root;
	
	public TutorialFileLoader(String rootname) {
		this.root = new File(rootname);
	}
	
	private String load(Context<Object> context, String zone) {
		String name = (String)context.get("this");
		String step = (String)context.get("step");
//System.err.println("load zone=[" + zone + "] name=[" + name + "] step=[" + step + "]");
		File file = new File(root, "step" + step + "/" + zone + "/" + name);
//System.err.println("load file=[" + file.getAbsolutePath() + "] exists=" + file.exists());
		String ret = FileReadingUtils.readFile(file);
		
		XMLEscaper xml = new XMLEscaper();
		ret = xml.convert(ret);
		
		return ret;
	}
	
	public String loadinput(Context<Object> context) {
		return load(context, "input");
	}
	
	public String loadoutput(Context<Object> context) {
		return load(context, "output");
	}
	
	@Override public String toString() {
		return "TutorialFileLoader[root=" + root.getAbsolutePath() + "]";
	}
}
