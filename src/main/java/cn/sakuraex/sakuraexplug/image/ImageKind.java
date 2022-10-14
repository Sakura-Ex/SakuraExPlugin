package cn.sakuraex.sakuraexplug.image;

import java.util.HashMap;

public enum ImageKind {
	COMIC("https://imgapi.cn/api.php?fl=dongman"),
	HUMAN("https://imgapi.cn/cos.php?return=img"),
	LANDSCAPE("https://imgapi.cn/api.php?fl=fengjing");
	
	public static final HashMap<String, String> kindMap = new HashMap<String, String>() {{
		for (ImageKind value : ImageKind.values()) {
			put(value.name, value.URL);
		}
	}};
	private final String name;
	private final String URL;
	
	ImageKind(String name, String url) {
		this.name = name;
		this.URL = url;
	}
	
	ImageKind(String url) {
		this.name = this.name().toLowerCase();
		this.URL = url;
	}
	
	public String getName() {
		return name;
	}
	
	public String getURL() {
		return URL;
	}
	
}
