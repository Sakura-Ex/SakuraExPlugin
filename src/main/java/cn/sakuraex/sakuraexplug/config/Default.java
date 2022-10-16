package cn.sakuraex.sakuraexplug.config;

public class Default {
	public static final int DEFAULT_RETRY_COUNT = 5;
	public static final int DEFAULT_CD = 10000;
	
	public static final String DEFAULT_ARG_NAME = "arg";
	
	public enum DefaultImageKind {
		COMIC("https://imgapi.cn/api.php?fl=dongman"),
		GIRL("https://imgapi.cn/api.php?fl=meizi"),
		LANDSCAPE("https://imgapi.cn/api.php?fl=fengjing");
		
		private final String name;
		private final String URL;
		
		DefaultImageKind(String name, String url) {
			this.name = name;
			this.URL = url;
		}
		
		DefaultImageKind(String url) {
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
}
