package cn.sakuraex.sakuraexplug.command;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import static cn.sakuraex.sakuraexplug.config.Default.DEFAULT_ARG_NAME;

public abstract class SingleArgCommand<T extends Contact> extends AbstractCommand<T> {
	
	private final String argName;
	private boolean argCanOmit = false;
	
	protected SingleArgCommand() {
		this(DEFAULT_ARG_NAME);
	}
	
	protected SingleArgCommand(String argName) {
		this.argName = argName;
	}
	
	protected SingleArgCommand(String rawMessage, T contact, User user, OnlineMessageSource source) {
		this(rawMessage, contact, user, source, DEFAULT_ARG_NAME);
	}
	
	protected SingleArgCommand(String rawMessage, T contact, User user, OnlineMessageSource source, String argName) {
		super(rawMessage, contact, user, source);
		this.argName = argName;
	}
	
	protected void argCanOmit() {
		this.argCanOmit = true;
	}
	
	protected String getArg() {
		return getRawMessage().length > 1 ? getRawMessage()[1] : "";
	}
	
	protected String getArgName() {
		return this.argName;
	}
	
	protected boolean isArgCanOmit() {
		return this.argCanOmit;
	}
	
	protected boolean hasArg() {
		return getRawMessage().length > 1;
	}
	
	@Override
	public String usageHelp() {
		if (this.argCanOmit) {
			return getName() + " [" + getArgName() + "]";
		} else {
			return getName() + " <" + getArgName() + ">";
		}
	}
}
