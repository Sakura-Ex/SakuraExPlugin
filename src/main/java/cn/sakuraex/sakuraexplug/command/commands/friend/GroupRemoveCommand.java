package cn.sakuraex.sakuraexplug.command.commands.friend;

import cn.sakuraex.sakuraexplug.command.commands.ComplexFriendCommand;
import cn.sakuraex.sakuraexplug.config.Config;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.List;
import java.util.Map;

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
		mcb.append("group id: The group you want to cancel permission.\n");
		mcb.append("member id: THe group member you want to cancel permission.\n");
		mcb.append("Omit it to refer to all members in the group.");
		getContact().sendMessage(mcb.asMessageChain());
	}
	
	@Override
	public void react() {
		MessageChainBuilder mcb = new MessageChainBuilder();
		Map<Long, List<Long>> whiteGroupList = Config.INSTANCE.whitelist.get();
		if (getArgLength() > 0) {
			long groupNumber;
			try {
				groupNumber = Long.parseLong(getArg(0));
				if (getArgLength() > 1) {
					try {
						long qqNumber = Long.parseLong(getArg(1));
						if (whiteGroupList.containsKey(groupNumber)) {
							List<Long> qqNumberList = whiteGroupList.get(groupNumber);
							if (qqNumberList.remove(qqNumber)) {
								mcb.append("Remove member ").append(String.valueOf(qqNumber)).append(" from group ")
										.append(String.valueOf(groupNumber)).append(" successfully.");
								getContact().sendMessage(mcb.asMessageChain());
							} else {
								getContact().sendMessage("Member " + qqNumber + " does not exist in group " + groupNumber + ".");
							}
						} else {
							getContact().sendMessage("Group " + groupNumber + " does not exist in whitelist.");
						}
					} catch (NumberFormatException e) {
						getContact().sendMessage("Please enter right qq number.");
					}
				} else {
					if (whiteGroupList.containsKey(groupNumber)) {
						whiteGroupList.remove(groupNumber);
						getContact().sendMessage("Remove group " + groupNumber + " successfully.");
					} else {
						getContact().sendMessage("Group number " + groupNumber + " does not exist in whitelist.");
					}
				}
			} catch (NumberFormatException e) {
				getContact().sendMessage("Please enter right group number.");
			}
		} else {
			detailedHelp();
		}
	}
}
