package cn.hssnow.dler.articlecore.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.io.File;
import java.util.Set;

public class EpubBuilder {
	
	private static final String[] SKIP_IMGS = { "static/image/", "bababian.com", "/js/", ".gif", "editor/images/face" };
	private String path;
	private String filename;
	private String title;
	private Set<String> imgs = new HashSet<>();

	private File sourceFile;
	
	public EpubBuilder(String path, String filename, String title, File sourceFile, List<String> imgs) {
		this.path = path;
		this.filename = filename;
		this.title = title;
		this.sourceFile = sourceFile;
		
		for (String img : imgs) {
			boolean skip = false;
			for (String skipImg : SKIP_IMGS) {
				if (img.contains(skipImg)) {
					skip = true;
					break;
				}
			}
			if (!skip) {
				imgs.add(img);
			}
		}
	}
	
	public 
	
}
