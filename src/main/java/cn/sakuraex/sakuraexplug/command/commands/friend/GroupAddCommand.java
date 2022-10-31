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
		mcb.append("group id: The group you want to give permission to.\n");
		mcb.append("member id: THe group member you want to give permission to.\n");
		mcb.append("Omit it to refer to all members in the group.");
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
						mcb.append("Add member ").append(String.valueOf(qqNumber)).append(" to group ")
								.append(String.valueOf(groupNumber)).append(" successfully.");
						getContact().sendMessage(mcb.asMessageChain());
					} catch (NumberFormatException e) {
						getContact().sendMessage("Please enter right qq number.");
					}
				} else {
					whiteGroupList.put(groupNumber, new TreeSet<>());
					getContact().sendMessage("Add group " + groupNumber + " successfully.");
				}
			} catch (NumberFormatException e) {
				getContact().sendMessage("Please enter right group number.");
			}
		} else {
			detailedHelp();
		}
	}
}
