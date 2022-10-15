package cn.sakuraex.sakuraexplug.command.commands.friend;

import cn.sakuraex.sakuraexplug.command.SingleArgFriendCommand;
import cn.sakuraex.sakuraexplug.config.Config;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

public final class OpCommand extends SingleArgFriendCommand {
	private static final String argName = "qq number";
	public static final OpCommand INSTANCE = new OpCommand();
	
	private OpCommand() {
		super(argName, true);
	}
	
	public OpCommand(FriendMessageEvent event) {
		super(event, argName, true);
	}
	
	@Override
	public String getName() {
		return "/op";
	}
	
	@Override
	public void react() {
		MessageChainBuilder mcb = new MessageChainBuilder();
		if (getArg().equals("")) {
			long qqNumber = getContact().getId();
			addQQ(getContact(), mcb, qqNumber);
		} else {
			try {
				long qqNumber = Long.parseLong(getArg());
				addQQ(getContact(), mcb, qqNumber);
			} catch (NumberFormatException e) {
				getContact().sendMessage("Please enter right qq number.");
			}
		}
	}
	
	private void addQQ(Friend sender, MessageChainBuilder mcb, long qqNumber) {
		if (!Config.INSTANCE.whiteQQList.get().contains(qqNumber)) {
			Config.INSTANCE.whiteQQList.get().add(qqNumber);
			mcb.append("Add ").append(Long.toString(qqNumber)).append(" successfully.");
			sender.sendMessage(mcb.asMessageChain());
		} else {
			MessageChainBuilder fail = new MessageChainBuilder().append(Long.toString(qqNumber)).append(" is already in whiteQQList.");
			sender.sendMessage(fail.asMessageChain());
		}
	}
}
