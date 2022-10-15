package cn.sakuraex.sakuraexplug.command;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
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
}
