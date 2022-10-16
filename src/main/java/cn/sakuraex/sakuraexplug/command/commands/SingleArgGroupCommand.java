package cn.sakuraex.sakuraexplug.command.commands;

import cn.sakuraex.sakuraexplug.command.SingleArgCommand;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public abstract class SingleArgGroupCommand extends SingleArgCommand<Group> {
	
	protected SingleArgGroupCommand(GroupMessageEvent event, String argName) {
		super(event.getMessage().contentToString(), event.getGroup(), event.getSender(), event.getSource(), argName);
	}
	
	protected SingleArgGroupCommand(GroupMessageEvent event) {
		super(event.getMessage().contentToString(), event.getGroup(), event.getSender(), event.getSource());
	}
	
	protected SingleArgGroupCommand() {
		super();
	}
	
	protected SingleArgGroupCommand(String argName) {
		super(argName);
	}
}
