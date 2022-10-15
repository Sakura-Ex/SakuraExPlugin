package cn.sakuraex.sakuraexplug.command.commands;

import cn.sakuraex.sakuraexplug.command.SingleArgCommand;
import cn.sakuraex.sakuraexplug.util.MessageUtil;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

public abstract class SingleArgGroupCommand extends SingleArgCommand<Group> {
	
	protected SingleArgGroupCommand(GroupMessageEvent event, String argName) {
		super(event.getMessage().contentToString(), event.getGroup(), event.getSender(), event.getSource(), argName);
	}
	
	protected SingleArgGroupCommand(GroupMessageEvent event, String argName, boolean argOmissible) {
		super(event.getMessage().contentToString(), event.getGroup(), event.getSender(), event.getSource(), argName, argOmissible);
	}
	
	protected SingleArgGroupCommand(GroupMessageEvent event) {
		this(event, "arg");
	}
	
	protected SingleArgGroupCommand(GroupMessageEvent event, boolean argOmissible) {
		this(event, "arg", argOmissible);
	}
	
	protected SingleArgGroupCommand(String argName) {
		super(argName);
	}
	
	protected SingleArgGroupCommand(String argName, boolean argOmissible) {
		super(argName, argOmissible);
	}
	
	@Override
	public void react() {
		MessageChainBuilder mcb = MessageUtil.groupQuoteAndAtMCB(getSource(), (Member) getUser());
		getContact().sendMessage(mcb.append("This method might be realized one day.").asMessageChain());
	}
	
	@Override
	public void detailedHelp() {
		MessageChainBuilder mcb = MessageUtil.groupQuoteAndAtMCB(getSource(), (Member) getUser());
		getContact().sendMessage(mcb.append("This method hasn't had help info yet.").asMessageChain());
	}
}
