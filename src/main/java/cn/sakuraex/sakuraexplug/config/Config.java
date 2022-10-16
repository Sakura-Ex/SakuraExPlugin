package cn.sakuraex.sakuraexplug.config;

import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.console.data.java.JavaAutoSavePluginConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Config extends JavaAutoSavePluginConfig {
	public static final Config INSTANCE = new Config();
	
	private Config() {
		super("config");
	}
	
	public final Value<Map<String, List<String>>> imageAPIs = typedValue("imageAPIs",
			createKType(Map.class, createKType(String.class), createKType(List.class, createKType(String.class))),
			new HashMap<String, List<String>>() {{
				for (Default.DefaultImageKind kind : Default.DefaultImageKind.values()) {
					put(kind.getName(), new ArrayList<String>() {{
						add(kind.getURL());
					}});
				}
			}}
	);
	
	public final Value<Map<Long,List<Long>>> whitelist = typedValue("whitelist",
			createKType(Map.class, createKType(Long.class), createKType(List.class, createKType(Long.class))),
			new HashMap<Long, List<Long>>() {{
				put(1234567890L, new ArrayList<Long>() {{
					add(987654321L);
				}});
			}}
	);
	
	public final Value<List<Long>> whiteQQList = typedValue("whiteQQList",
			createKType(List.class, createKType(Long.class)),
			new ArrayList<Long>() {{
				add(123123123L);
			}});
	
	// 当网络连接出现故障时，重试的次数
	public final Value<Integer> imgRetryCount = value("imgRetryCount", Default.DEFAULT_RETRY_COUNT);
	// 冷却时间 ms
	public final Value<Integer> imgCD = value("imgCD", Default.DEFAULT_CD);
}
