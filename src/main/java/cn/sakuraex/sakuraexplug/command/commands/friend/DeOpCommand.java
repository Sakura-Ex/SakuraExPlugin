package cn.sakuraex.sakuraexplug.command.commands.friend;

import cn.sakuraex.sakuraexplug.command.commands.SingleArgFriendCommand;
import cn.sakuraex.sakuraexplug.config.Config;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

public final class DeOpCommand extends SingleArgFriendCommand {
	private static final String argName = "qq Number";
	public static final DeOpCommand INSTANCE = new DeOpCommand();
	
	static {
		INSTANCE.argCanOmit();
	}
	
	private DeOpCommand() {
		super(argName);
	}
	
	public DeOpCommand(FriendMessageEvent event) {
		super(event, argName);
	}
	
	@Override
	public String getName() {
		return "/deop";
	}
	
	@Override
	public void react() {
		MessageChainBuilder mcb = new MessageChainBuilder();
		if (hasArg()) {
			try {
				long qqNumber = Long.parseLong(getArg());
				removeQQ(getContact(), mcb, qqNumber);
			} catch (NumberFormatException e) {
				getContact().sendMessage("请输入正确的 qq 号");
			}
		} else {
			long qqNumber = getContact().getId();
			removeQQ(getContact(), mcb, qqNumber);
		}
	}
	
	private void removeQQ(Friend sender, MessageChainBuilder mcb, long qqNumber) {
		if (Config.INSTANCE.whiteQQList.get().contains(qqNumber)) {
			Config.INSTANCE.whiteQQList.get().remove(qqNumber);
			mcb.append("移除 ").append(Long.toString(qqNumber)).append(" 成功");
			sender.sendMessage(mcb.asMessageChain());
		} else {
			MessageChainBuilder fail = new MessageChainBuilder().append(Long.toString(qqNumber)).append(" 不存在");
			sender.sendMessage(fail.asMessageChain());
		}
	}
}
