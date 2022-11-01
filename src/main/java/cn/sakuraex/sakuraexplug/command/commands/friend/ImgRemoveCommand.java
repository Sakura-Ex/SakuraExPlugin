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
						getContact().sendMessage("移除 api 链接 " + getArg(1) + " 成功");
					} else {
						getContact().sendMessage("api 链接 " + getArg(1) + " 不存在");
					}
				} else {
					getContact().sendMessage("你不能移除最后一个 api");
				}
			} else {
				getContact().sendMessage("图片类型 " + getArg(0) + " 不存在");
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
						logger.info("删除文件夹成功");
					} else {
						logger.info("删除文件夹失败");
					}
				} else {
					getContact().sendMessage("你不能移除最后一种图片类型");
				}
			} else {
				getContact().sendMessage("图片类型 " + getArg(0) + " 不存在");
			}
		} else {
			detailedHelp();
		}
	}
	
	@Override
	public void detailedHelp() {
		argCanOmit();
		MessageChainBuilder mcb = new MessageChainBuilder().append(usageHelp()).append("\n");
		mcb.append("type: 你想移除的图片 api 的类型\n");
		mcb.append("api link: 你想移除的图片 api\n");
		mcb.append("省略 <api link> 以移除图片类型");
		getContact().sendMessage(mcb.asMessageChain());
	}
}
