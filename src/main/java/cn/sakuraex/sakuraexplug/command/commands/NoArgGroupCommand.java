package cn.sakuraex.sakuraexplug.command.commands;

import cn.sakuraex.sakuraexplug.command.NoArgCommand;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public abstract class NoArgGroupCommand extends NoArgCommand<Group> {
	
	protected NoArgGroupCommand() {}
	
	protected NoArgGroupCommand(GroupMessageEvent event) {
		super(event.getMessage().contentToString(), event.getGroup(), event.getSender(), event.getSource());
	}
}
