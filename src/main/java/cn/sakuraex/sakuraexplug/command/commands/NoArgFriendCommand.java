package cn.sakuraex.sakuraexplug.command.commands;

import cn.sakuraex.sakuraexplug.command.NoArgCommand;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.event.events.FriendMessageEvent;

public abstract class NoArgFriendCommand extends NoArgCommand<Friend> {
	
	public NoArgFriendCommand() {}
	
	public NoArgFriendCommand(FriendMessageEvent event) {
		super(event.getMessage().contentToString(), event.getFriend(), event.getSender(), event.getSource());
	}
}
