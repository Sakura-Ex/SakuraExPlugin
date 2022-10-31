package cn.sakuraex.sakuraexplug.config;

import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.console.data.java.JavaAutoSavePluginConfig;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public final class Config extends JavaAutoSavePluginConfig {
	public static final Config INSTANCE = new Config();
	
	private Config() {
		super("config");
	}
	
	public final Value<Map<String, Set<String>>> imageAPIs = typedValue("imageAPIs",
			createKType(Map.class, createKType(String.class), createKType(Set.class, createKType(String.class))),
			new TreeMap<String, Set<String>>() {{
				for (Default.DefaultImageKind kind : Default.DefaultImageKind.values()) {
					put(kind.getName(), new TreeSet<String>() {{
						add(kind.getURL());
					}});
				}
			}}
	);
	
	public final Value<Map<Long, Set<Long>>> whitelist = typedValue("whitelist",
			createKType(Map.class, createKType(Long.class), createKType(Set.class, createKType(Long.class))),
			new TreeMap<Long, Set<Long>>() {{
				put(1234567890L, new TreeSet<Long>() {{
					add(987654321L);
				}});
			}}
	);
	
	public final Value<Set<Long>> whiteQQList = typedValue("whiteQQList",
			createKType(Set.class, createKType(Long.class)),
			new TreeSet<Long>() {{
				add(123123123L);
			}});
	
	// 当网络连接出现故障时，重试的次数
	public final Value<Integer> imgRetryCount = value("imgRetryCount", Default.DEFAULT_RETRY_COUNT);
	// 冷却时间 ms
	public final Value<Integer> imgCD = value("imgCD", Default.DEFAULT_CD);
}
