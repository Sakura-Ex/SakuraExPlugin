package cn.sakuraex.sakuraexplug.command;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.OnlineMessageSource;

public interface ICommand {
	String getName();
	void react(Group group, Member sender, String arg, OnlineMessageSource source);
	void help(Group group, Member sender, OnlineMessageSource source);
}
