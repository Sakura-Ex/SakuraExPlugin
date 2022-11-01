package cn.sakuraex.sakuraexplug.command.commands.friend;

import cn.sakuraex.sakuraexplug.command.commands.ComplexFriendCommand;
import cn.sakuraex.sakuraexplug.config.Config;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class GroupAddCommand extends ComplexFriendCommand {
	
	private static final String[] argNames = {"group id", "member id"};
	public static final GroupAddCommand INSTANCE = new GroupAddCommand();
	
	static {
		INSTANCE.argCanOmit();
	}
	
	private GroupAddCommand() {
		super(argNames);
	}
	
	public GroupAddCommand(FriendMessageEvent event) {
		super(event, argNames);
	}
	
	@Override
	public String getName() {
		return "/group+";
	}
	
	@Override
	public void detailedHelp() {
		argCanOmit();
		MessageChainBuilder mcb = new MessageChainBuilder().append(usageHelp()).append("\n");
		mcb.append("group id: 你想给予权限的群的群号\n");
		mcb.append("member id: 你想给予权限的群成员的 qq 号\n");
		mcb.append("省略 <member id> 给予该群所有成员权限");
		getContact().sendMessage(mcb.asMessageChain());
	}
	
	@Override
	public void react() {
		MessageChainBuilder mcb = new MessageChainBuilder();
		long groupNumber;
		if (getArgLength() > 0) {
			try {
				groupNumber = Long.parseLong(getArg(0));
				Map<Long, Set<Long>> whiteGroupList = Config.INSTANCE.whitelist.get();
				if (getArgLength() > 1) {
					try {
						long qqNumber = Long.parseLong(getArg(1));
						if (whiteGroupList.containsKey(groupNumber)) {
							whiteGroupList.get(groupNumber).add(qqNumber);
						} else {
							whiteGroupList.put(groupNumber, new TreeSet<Long>() {{
								add(qqNumber);
							}});
						}
						mcb.append("添加成员 ").append(String.valueOf(qqNumber)).append(" 到群 ")
								.append(String.valueOf(groupNumber)).append(" 成功");
						getContact().sendMessage(mcb.asMessageChain());
					} catch (NumberFormatException e) {
						getContact().sendMessage("请输入正确的 qq 号");
					}
				} else {
					whiteGroupList.put(groupNumber, new TreeSet<>());
					getContact().sendMessage("添加群 " + groupNumber + " 成功");
				}
			} catch (NumberFormatException e) {
				getContact().sendMessage("请输入正确的群号");
			}
		} else {
			detailedHelp();
		}
	}
}
