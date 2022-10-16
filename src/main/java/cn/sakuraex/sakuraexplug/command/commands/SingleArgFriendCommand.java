package cn.sakuraex.sakuraexplug.command.commands;

import cn.sakuraex.sakuraexplug.command.SingleArgCommand;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.event.events.FriendMessageEvent;

public abstract class SingleArgFriendCommand extends SingleArgCommand<Friend> {
	
	protected SingleArgFriendCommand(FriendMessageEvent event, String argName) {
		super(event.getMessage().contentToString(), event.getFriend(), event.getSender(), event.getSource(), argName);
	}

	protected SingleArgFriendCommand(FriendMessageEvent event) {
		super(event.getMessage().contentToString(), event.getFriend(), event.getUser(), event.getSource());
	}

	protected SingleArgFriendCommand(String argName) {
		super(argName);
	}
}
