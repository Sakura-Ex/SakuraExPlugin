package cn.sakuraex.sakuraexplug.command;

import net.mamoe.mirai.contact.Contact;

public abstract class NoArgCommand<T extends Contact> extends AbstractCommand<T> {
	
	@Override
	public String usageHelp() {
		return getName();
	}
}
