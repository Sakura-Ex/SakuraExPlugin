package cn.sakuraex.sakuraexplug.command;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.OnlineMessageSource;

public abstract class NoArgCommand<T extends Contact> extends AbstractCommand<T> {
	protected NoArgCommand() {
	}
	
	protected NoArgCommand(String rawMessage, T contact, User user, OnlineMessageSource source) {
		super(rawMessage, contact, user, source);
	}
	
	@Override
	public String usageHelp() {
		return getName();
	}
}
