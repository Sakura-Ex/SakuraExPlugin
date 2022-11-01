package cn.sakuraex.sakuraexplug.command.commands;

import cn.sakuraex.sakuraexplug.command.AbstractCommand;
import cn.sakuraex.sakuraexplug.command.NoArgCommand;
import cn.sakuraex.sakuraexplug.util.MessageUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.List;

public final class HelpCommand<T extends Contact> extends NoArgCommand<Contact> {
	
	public static final HelpCommand<Contact> INSTANCE = new HelpCommand<>();
	private MessageEvent event;
	private List<AbstractCommand<T>> commands;
	
	private HelpCommand() {}
	
	public HelpCommand(MessageEvent event, List<AbstractCommand<T>> commands) {
		super(event.getMessage().contentToString(), event.getSubject(), event.getSender(), event.getSource());
		this.event = event;
		this.commands = commands;
	}
	
	@Override
	public String getName() {
		return "/help";
	}
	
	@Override
	public void react() {
		MessageChainBuilder mcb = event instanceof GroupMessageEvent ?
				MessageUtil.groupQuoteAndAtMCB(getSource(), (Member) getUser()).append("\n") : new MessageChainBuilder();
		mcb.append("可用指令:\n");
		for (AbstractCommand<T> command : this.commands) {
			mcb.append(command.usageHelp()).append("\n");
		}
		getContact().sendMessage(mcb.asMessageChain());
	}
}
