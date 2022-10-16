package cn.sakuraex.sakuraexplug.command.commands.friend;

import cn.sakuraex.sakuraexplug.command.commands.SingleArgFriendCommand;
import cn.sakuraex.sakuraexplug.config.Config;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.List;
import java.util.Map;

public final class CheckCommand extends SingleArgFriendCommand {
	private static final String argName = "info";
	public static final CheckCommand INSTANCE = new CheckCommand();
	
	static {
		INSTANCE.argCanOmit();
	}
	
	public CheckCommand(FriendMessageEvent event) {
		super(event, argName);
	}
	
	private CheckCommand() {
		super(argName);
	}
	
	@Override
	public String getName() {
		return "/check";
	}
	
	@Override
	public void react() {
		if (hasArg()) {
			switch (getArg()) {
				case "imageAPIs": {
					MessageChainBuilder mcb = new MessageChainBuilder().append("imageAPIs:\n");
					for (Map.Entry<String, List<String>> entry : Config.INSTANCE.imageAPIs.get().entrySet()) {
						mcb.append("\t-").append(entry.getKey()).append("\n");
						for (String api : entry.getValue()) {
							mcb.append("\t\t-").append(api).append("\n");
						}
					}
					getContact().sendMessage(mcb.asMessageChain());
					break;
				}
				case "whitelist": {
					MessageChainBuilder mcb = new MessageChainBuilder().append("whitelist:\n");
					for (Map.Entry<Long, List<Long>> entry : Config.INSTANCE.whitelist.get().entrySet()) {
						mcb.append("\t-").append(entry.getKey().toString()).append("\n");
						if (entry.getValue().isEmpty()) {
							mcb.append("\t\t-all\n");
						} else {
							for (long qqNumber : entry.getValue()) {
								mcb.append("\t\t-").append(Long.toString(qqNumber)).append("\n");
							}
						}
					}
					getContact().sendMessage(mcb.asMessageChain());
					break;
				}
				case "whiteQQList:": {
					MessageChainBuilder mcb = new MessageChainBuilder().append("whiteQQList:\n");
					for (long qqNumber : Config.INSTANCE.whiteQQList.get()) {
						mcb.append("\t-").append(String.valueOf(qqNumber)).append("\n");
					}
					getContact().sendMessage(mcb.asMessageChain());
					break;
				}
			}
		} else {
			MessageChainBuilder mcb = new MessageChainBuilder().append(usageHelp()).append("\n");
			mcb.append("info: \n").append("\t-imageAPIs\n").append("\t-whitelist\n").append("\t-whiteQQList\n");
			getContact().sendMessage(mcb.asMessageChain());
		}
	}
}
