package cn.sakuraex.sakuraexplug.command;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.OnlineMessageSource;

public abstract class SingleArgCommand<T extends Contact> implements ICommand {
	
	private String[] rawMessage;
	private T contact;
	private User user;
	private OnlineMessageSource source;
	
	protected SingleArgCommand() {}
	
	protected SingleArgCommand(String rawMessage, T contact, User user, OnlineMessageSource source) {
		setRawMessage(rawMessage);
		this.contact = contact;
		this.user = user;
		this.source = source;
	}
	
	public String[] getRawMessage() {
		return rawMessage;
	}
	
	public T getContact() {
		return contact;
	}
	
	public User getUser() {
		return user;
	}
	
	public OnlineMessageSource getSource() {
		return source;
	}
	
	private void setRawMessage(String rawMessage) {
		this.rawMessage = rawMessage.split(" ");
	}
	
	public String getArg() {
		return rawMessage[1];
	}
}
