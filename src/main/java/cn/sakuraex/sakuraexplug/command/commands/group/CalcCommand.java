package cn.sakuraex.sakuraexplug.command.commands.group;

import cn.sakuraex.sakuraexplug.command.commands.SingleArgGroupCommand;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public final class CalcCommand extends SingleArgGroupCommand {
	private static final String argName = "expression";
	public static final CalcCommand INSTANCE = new CalcCommand(argName);
	
	@Override
	public String getName() {
		return "/calc";
	}
	
	private CalcCommand(String argName) {
		super(argName);
	}
	
	public CalcCommand(GroupMessageEvent event) {
		super(event, argName);
	}
}
