package cn.sakuraex.sakuraexplug.command.commands;

import cn.sakuraex.sakuraexplug.util.MessageUtil;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class ComputeCommand extends SingleArgGroupCommand {
	
	private static final String PRECISE_E = "2.7182818284590452353602874713526624977572470936999595749669676277"; // 64位小数
	private static final String PRECISE_PI = "3.1415926535897932384626433832795028841971693993751058209749445923";
	
	protected ComputeCommand(GroupMessageEvent event, String argName) {
		super(event, argName);
	}
	
	protected ComputeCommand(String argName) {
		super(argName);
	}
	
	protected String transformExp(String exp) {
		return MessageUtil.trimAllSpace(exp).replaceAll("(?<=\\(|^)-(?=[\\d.]*)", "(0-1)*")
				.replaceAll("(?<=\\(|^)+(?=[\\d.]*)", "");
	}
	
	protected List<String> resolveExpr(String expression) {
		String operations = expression.replaceAll("([\\w.]*)", "");
		String exp = expression.replaceAll("(?<!\\w)e(?!\\w)", PRECISE_E).replaceAll("(?<!\\w)pi(?!\\w)", PRECISE_PI);
		List<String> list = new ArrayList<>();
		int k;
		for (int i = 0; i < operations.length(); i++) {
			String p = operations.substring(i, i + 1);
			k = exp.indexOf(p);
			if (exp.substring(0, k).trim().length() != 0) {
				list.add(exp.substring(0, k));
			}
			list.add(exp.substring(k, k + 1));
			exp = exp.substring(k + 1);
		}
		if (exp.length() > 0) {
			list.add(exp);
		}
		return list;
	}
}
