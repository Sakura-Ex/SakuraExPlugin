package cn.sakuraex.sakuraexplug.command;

import cn.sakuraex.sakuraexplug.util.MessageUtil;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.OnlineMessageSource;

public abstract class SingleArgGroupCommand extends SingleArgCommand<Group> {
	protected SingleArgGroupCommand(String rawMessage, Group contact, User user, OnlineMessageSource source) {
		super(rawMessage, contact, user, source);
	}
	
	SingleArgGroupCommand() {
		super();
	}
	
	@Override
	public void react() {
		MessageChainBuilder mcb = MessageUtil.groupQuoteAndAtMCB(getSource(), (Member) getUser());
		getContact().sendMessage(mcb.append("This method might be realized one day.").asMessageChain());
	}
	
	@Override
	public void help() {
		MessageChainBuilder mcb = MessageUtil.groupQuoteAndAtMCB(getSource(), (Member) getUser());
		getContact().sendMessage(mcb.append("This method hasn't had help info yet.").asMessageChain());
	}
}
