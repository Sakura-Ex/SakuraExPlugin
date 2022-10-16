package cn.sakuraex.sakuraexplug.command.commands;

import cn.sakuraex.sakuraexplug.command.ComplexCommand;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.event.events.FriendMessageEvent;

public abstract class ComplexFriendCommand extends ComplexCommand<Friend> {
	
	protected ComplexFriendCommand(int argLength) {
		super(argLength);
	}
	
	protected ComplexFriendCommand(String... argNames) {
		super(argNames);
	}
	
	protected ComplexFriendCommand(FriendMessageEvent event, String... argNames) {
		super(event.getMessage().contentToString(), event.getFriend(), event.getUser(), event.getSource(), argNames);
	}
	
	protected ComplexFriendCommand(FriendMessageEvent event) {
		super(event.getMessage().contentToString(), event.getFriend(), event.getUser(), event.getSource());
	}
}
