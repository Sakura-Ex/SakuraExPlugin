package cn.sakuraex.sakuraexplug.command.commands;

import cn.sakuraex.sakuraexplug.command.ComplexCommand;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public abstract class ComplexGroupCommand extends ComplexCommand<Group> {
	protected ComplexGroupCommand(int argLength) {
		super(argLength);
	}
	
	protected ComplexGroupCommand(String... argNames) {
		super(argNames);
	}
	
	protected ComplexGroupCommand(GroupMessageEvent event, String... argNames) {
		super(event.getMessage().contentToString(), event.getGroup(), event.getSender(), event.getSource(), argNames);
	}
	
	protected ComplexGroupCommand(GroupMessageEvent event) {
		super(event.getMessage().contentToString(), event.getGroup(), event.getSender(), event.getSource());
	}
}
