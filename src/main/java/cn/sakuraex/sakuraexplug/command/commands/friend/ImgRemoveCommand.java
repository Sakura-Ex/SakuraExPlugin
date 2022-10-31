package cn.sakuraex.sakuraexplug.command.commands.friend;

import cn.sakuraex.sakuraexplug.command.commands.ComplexFriendCommand;
import cn.sakuraex.sakuraexplug.config.Config;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.MiraiLogger;

import java.io.File;
import java.util.Map;
import java.util.Set;

public final class ImgRemoveCommand extends ComplexFriendCommand {
	
	private static final String[] argNames = {"type", "api link"};
	public static final ImgRemoveCommand INSTANCE = new ImgRemoveCommand();
	private File imgFolder;
	private MiraiLogger logger;
	
	private ImgRemoveCommand() {
		super(argNames);
	}
	
	public ImgRemoveCommand(FriendMessageEvent event, File imgFolder, MiraiLogger logger) {
		super(event, argNames);
		this.imgFolder = imgFolder;
		this.logger = logger;
	}
	
	@Override
	public String getName() {
		return "/img-";
	}
	
	@Override
	public void react() {
		Map<String, Set<String>> apis = Config.INSTANCE.imageAPIs.get();
		if (getArgLength() > 1) {
			if (apis.containsKey(getArg(0))) {
				if (apis.get(getArg(0)).size() > 1) {
					if (apis.get(getArg(0)).remove(getArg(1))) {
						getContact().sendMessage("Remove api Link " + getArg(1) + " successfully.");
					} else {
						getContact().sendMessage("Api Link " + getArg(1) + " does not exist.");
					}
				} else {
					getContact().sendMessage("You can't remove the last api.");
				}
			} else {
				getContact().sendMessage("Type " + getArg(0) + " does not exist.");
			}
		} else if (getArgLength() == 1) {
			if (apis.containsKey(getArg(0))) {
				if (apis.size() > 1) {
					boolean isDeleted = true;
					apis.remove(getArg(0));
					File delete = new File(imgFolder, getArg(0));
					File[] files = delete.listFiles();
					if (files != null) {
						for (File file : files) {
							isDeleted = file.delete() && isDeleted;
						}
					}
					if (delete.delete() && isDeleted) {
						logger.info("Delete type's folder successfully.");
					} else {
						logger.info("Failed to delete type's folder..");
					}
				} else {
					getContact().sendMessage("You can't remove the last type.");
				}
			} else {
				getContact().sendMessage("Type " + getArg(0) + " does not exist.");
			}
		} else {
			detailedHelp();
		}
	}
	
	@Override
	public void detailedHelp() {
		argCanOmit();
		MessageChainBuilder mcb = new MessageChainBuilder().append(usageHelp()).append("\n");
		mcb.append("type: The type you want remove api from.\n");
		mcb.append("api link: A url that you want to remove from the type.\n");
		mcb.append("Omit the api link to remove the type.");
		getContact().sendMessage(mcb.asMessageChain());
	}
}
