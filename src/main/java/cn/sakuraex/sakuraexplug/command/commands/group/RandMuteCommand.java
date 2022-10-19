package cn.sakuraex.sakuraexplug.command.commands.group;

import cn.sakuraex.sakuraexplug.command.commands.NoArgGroupCommand;
import cn.sakuraex.sakuraexplug.util.MessageUtil;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.PermissionDeniedException;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.Random;

public final class RandMuteCommand extends NoArgGroupCommand {
	public static final RandMuteCommand INSTANCE = new RandMuteCommand();
	
	public RandMuteCommand() {
	}
	
	public RandMuteCommand(GroupMessageEvent event) {
		super(event);
	}
	
	{
		setInfo("Randomly mute yourself from 1 to 3600 seconds.");
	}
	
	@Override
	public String getName() {
		return "/randmute";
	}
	
	@Override
	public void react() {
		int duration = new Random().nextInt(3600);
		MessageChainBuilder mcb = MessageUtil.groupQuoteAndAtMCB(getSource(), (Member) getUser());
		try {
			((Member) getUser()).mute(duration);
			mcb.append(" Enter a ").append(String.valueOf(duration)).append("-second mediation state.");
		} catch (PermissionDeniedException e) {
			mcb.append(" You are the superior, I couldn't mute you.");
		}
		getContact().sendMessage(mcb.asMessageChain());
	}
}
