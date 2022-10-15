package cn.sakuraex.sakuraexplug.command.commands.friend;

import cn.sakuraex.sakuraexplug.command.SingleArgFriendCommand;
import net.mamoe.mirai.event.events.FriendMessageEvent;

public final class CheckCommand extends SingleArgFriendCommand {
	private static final String argName = "info";
	public static final CheckCommand INSTANCE = new CheckCommand();
	
	public CheckCommand(FriendMessageEvent event) {
		super(event, argName);
	}
	
	private CheckCommand() {
		super(argName);
	}
	
	@Override
	public String getName() {
		return "/check";
	}
	
	@Override
	public void react() {
	
	}
}
