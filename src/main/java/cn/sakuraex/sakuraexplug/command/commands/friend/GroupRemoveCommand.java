package cn.sakuraex.sakuraexplug.command.commands.friend;

import cn.sakuraex.sakuraexplug.command.commands.ComplexFriendCommand;
import cn.sakuraex.sakuraexplug.config.Config;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.Map;
import java.util.Set;

public final class GroupRemoveCommand extends ComplexFriendCommand {
	
	private static final String[] argNames = {"group id", "member id"};
	public static final GroupRemoveCommand INSTANCE = new GroupRemoveCommand();
	
	static {
		INSTANCE.argCanOmit();
	}
	
	public GroupRemoveCommand() {
		super(argNames);
	}
	
	public GroupRemoveCommand(FriendMessageEvent event) {
		super(event, argNames);
	}
	
	@Override
	public String getName() {
		return "/group-";
	}
	
	@Override
	public void detailedHelp() {
		argCanOmit();
		MessageChainBuilder mcb = new MessageChainBuilder().append(usageHelp()).append("\n");
		mcb.append("group id: 你想取消权限的群的群号\n");
		mcb.append("member id: 你想取消权限的群成员的 qq 号\n");
		mcb.append("省略 <member id> 以取消整个群的权限。\n注：成员会一并删除，需重新添加");
		getContact().sendMessage(mcb.asMessageChain());
	}
	
	@Override
	public void react() {
		MessageChainBuilder mcb = new MessageChainBuilder();
		Map<Long, Set<Long>> whiteGroupList = Config.INSTANCE.whitelist.get();
		if (getArgLength() > 0) {
			long groupNumber;
			try {
				groupNumber = Long.parseLong(getArg(0));
				if (getArgLength() > 1) {
					try {
						long qqNumber = Long.parseLong(getArg(1));
						if (whiteGroupList.containsKey(groupNumber)) {
							Set<Long> qqNumberList = whiteGroupList.get(groupNumber);
							if (qqNumberList.remove(qqNumber)) {
								mcb.append("从群 ").append(String.valueOf(groupNumber)).append(" 删除成员 ")
										.append(String.valueOf(qqNumber)).append(" 成功");
								getContact().sendMessage(mcb.asMessageChain());
							} else {
								getContact().sendMessage("成员 " + qqNumber + " 不在群 " + groupNumber + " 的授权名单内");
							}
						} else {
							getContact().sendMessage("群 " + groupNumber + " 不在授权名单内");
						}
					} catch (NumberFormatException e) {
						getContact().sendMessage("请输入正确的 qq 号");
					}
				} else {
					if (whiteGroupList.containsKey(groupNumber)) {
						whiteGroupList.remove(groupNumber);
						getContact().sendMessage("删除群 " + groupNumber + " 成功");
					} else {
						getContact().sendMessage("群 " + groupNumber + " 不在授权名单内");
					}
				}
			} catch (NumberFormatException e) {
				getContact().sendMessage("请输入正确的群号");
			}
		} else {
			detailedHelp();
		}
	}
}
