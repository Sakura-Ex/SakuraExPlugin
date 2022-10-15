package cn.sakuraex.sakuraexplug.command;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.OnlineMessageSource;

public abstract class SingleArgCommand<T extends Contact> extends AbstractCommand<T> {
	
	private final String argName;
	private final boolean argOmissible;
	
	protected SingleArgCommand(String argName) {
		this(argName, false);
	}
	protected SingleArgCommand(String argName, boolean argOmissible) {
		this.argName = argName;
		this.argOmissible = argOmissible;
	}
	
	protected SingleArgCommand(String rawMessage, T contact, User user, OnlineMessageSource source, String argName) {
		this(rawMessage, contact, user, source, argName, false);
	}
	
	protected SingleArgCommand(String rawMessage, T contact, User user, OnlineMessageSource source, String argName, boolean argOmissible) {
		super(rawMessage, contact, user, source);
		this.argName = argName;
		this.argOmissible = argOmissible;
	}
	
	protected String getArg() {
		return getRawMessage().length > 1 ? getRawMessage()[1] : "";
	}
	
	protected String getArgName() {
		return argName;
	}
	
	protected boolean isArgOmissible() {
		return argOmissible;
	}
	
	@Override
	public String usageHelp() {
		if (argOmissible) {
			return getName() + " [" + getArgName() + "]";
		} else {
			return getName() + " <" + getArgName() + ">";
		}
	}
}
