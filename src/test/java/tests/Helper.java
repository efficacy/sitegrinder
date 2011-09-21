package tests;

import org.stringtree.grinder.SiteGrinder;
import org.stringtree.solomon.Template;

public class Helper {

	public static Template folder(String name) {
		return SiteGrinder.template("", SiteGrinder.TYPE_FOLDER, name, name);
	}

	public static Template page(String body) {
		return SiteGrinder.template("", SiteGrinder.TYPE_PAGE, "example.page", body);
	}

	public static Template page(String body, String parent) {
		return SiteGrinder.template(parent, SiteGrinder.TYPE_PAGE, body + ".page", body);
	}

}
