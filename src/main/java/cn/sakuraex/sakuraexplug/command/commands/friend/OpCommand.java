package cn.sakuraex.sakuraexplug.command.commands.friend;

import cn.sakuraex.sakuraexplug.command.commands.SingleArgFriendCommand;
import cn.sakuraex.sakuraexplug.config.Config;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

public final class OpCommand extends SingleArgFriendCommand {
	private static final String argName = "qq number";
	public static final OpCommand INSTANCE = new OpCommand();
	
	static {
		INSTANCE.argCanOmit();
	}
	
	private OpCommand() {
		super(argName);
	}
	
	public OpCommand(FriendMessageEvent event) {
		super(event, argName);
	}
	
	@Override
	public String getName() {
		return "/op";
	}
	
	@Override
	public void react() {
		MessageChainBuilder mcb = new MessageChainBuilder();
		if (hasArg()) {
			try {
				long qqNumber = Long.parseLong(getArg());
				addQQ(getContact(), mcb, qqNumber);
			} catch (NumberFormatException e) {
				getContact().sendMessage("请输入正确的 qq 号");
			}
		} else {
			long qqNumber = getContact().getId();
			addQQ(getContact(), mcb, qqNumber);
		}
	}
	
	private void addQQ(Friend sender, MessageChainBuilder mcb, long qqNumber) {
		if (!Config.INSTANCE.whiteQQList.get().contains(qqNumber)) {
			Config.INSTANCE.whiteQQList.get().add(qqNumber);
			mcb.append("添加 ").append(Long.toString(qqNumber)).append(" 成功");
			sender.sendMessage(mcb.asMessageChain());
		} else {
			MessageChainBuilder fail = new MessageChainBuilder().append(Long.toString(qqNumber)).append(" 已被添加");
			sender.sendMessage(fail.asMessageChain());
		}
	}
}
