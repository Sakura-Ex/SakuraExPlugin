package cn.sakuraex.sakuraexplug.command;

import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.event.events.FriendMessageEvent;

public abstract class SingleArgFriendCommand extends SingleArgCommand<Friend> {
	protected SingleArgFriendCommand(FriendMessageEvent event, String argName) {
		super(event.getMessage().contentToString(), event.getFriend(), event.getSender(), event.getSource(), argName);
	}
	
	protected SingleArgFriendCommand(FriendMessageEvent event, String argName, boolean argOmissible) {
		super(event.getMessage().contentToString(), event.getFriend(), event.getSender(), event.getSource(), argName, argOmissible);
	}
	
	protected SingleArgFriendCommand(FriendMessageEvent event) {
		this(event, "arg");
	}
	
	protected SingleArgFriendCommand(FriendMessageEvent event, boolean argOmissible) {
		this(event, "arg", argOmissible);
	}
	
	protected SingleArgFriendCommand(String argName) {
		super(argName);
	}
	
	protected SingleArgFriendCommand(String argName, boolean argOmissible) {
		super(argName, argOmissible);
	}
	
	@Override
	public void react() {
		getContact().sendMessage("This method might be realized one day.");
	}
	
	@Override
	public void detailedHelp() {
		getContact().sendMessage("This method hasn't had help info yet.");
	}
}
