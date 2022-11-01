package cn.sakuraex.sakuraexplug.command.commands;

import cn.sakuraex.sakuraexplug.util.MessageUtil;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class ComputeCommand extends SingleArgGroupCommand {
	
	protected ComputeCommand(GroupMessageEvent event, String argName) {
		super(event, argName);
	}
	
	protected ComputeCommand(String argName) {
		super(argName);
	}
	
	protected String transformExp(String exp) {
		return MessageUtil.trimAllSpace(exp).replaceAll("(\\(|^)([+-])([\\d.]*)", "$1(0$2$3)");
	}
	
	protected List<String> resolveExpr(String experssion) {
		String operations = experssion.replaceAll("[\\d.]*", "");
		String exp = experssion.replaceAll("e", String.valueOf(Math.E)).replaceAll("pi", String.valueOf(Math.PI));
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
