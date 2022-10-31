package cn.sakuraex.sakuraexplug.command;

import cn.sakuraex.sakuraexplug.util.MessageUtil;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.OnlineMessageSource;

public abstract class AbstractCommand<T extends Contact> implements ICommand {
	
	private String[] rawMessage;
	private T contact;
	private User user;
	private OnlineMessageSource source;
	
	protected AbstractCommand() {}
	
	protected AbstractCommand(String rawMessage, T contact, User user, OnlineMessageSource source) {
		this.rawMessage = rawMessage.split(" ");
		this.contact = contact;
		this.user = user;
		this.source = source;
	}
	
	protected String[] getRawMessage() {
		return rawMessage;
	}
	
	protected T getContact() {
		return contact;
	}
	
	protected User getUser() {
		return user;
	}
	
	protected OnlineMessageSource getSource() {
		return source;
	}
	
	public static void react(ICommand command) {
		command.react();
	}
	
	@Override
	public void react() {
		String str = "This method might be realized one day.";
		if (contact instanceof Friend) {
			getContact().sendMessage(str);
		} else if (contact instanceof Group) {
			MessageChainBuilder mcb = MessageUtil.groupQuoteAndAtMCB(getSource(), (Member) getUser());
			getContact().sendMessage(mcb.append(str).asMessageChain());
		}
	}
	
	@Override
	public void detailedHelp() {
		String str = "This method hasn't had help info yet.";
		if (contact instanceof Friend) {
			getContact().sendMessage(str);
		} else if (contact instanceof Group) {
			MessageChainBuilder mcb = MessageUtil.groupQuoteAndAtMCB(getSource(), (Member) getUser());
			getContact().sendMessage(mcb.append(str).asMessageChain());
		}
	}
}
