package cn.sakuraex.sakuraexplug.image;

public enum ImageKind {
	COMIC("https://imgapi.cn/api.php?fl=dongman"),
	GIRL("https://imgapi.cn/api.php?fl=meizi"),
	LANDSCAPE("https://imgapi.cn/api.php?fl=fengjing");
	
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
