package cn.sakuraex.sakuraexplug.command.commands.friend;

import cn.sakuraex.sakuraexplug.command.commands.ComplexFriendCommand;
import cn.sakuraex.sakuraexplug.config.Config;
import cn.sakuraex.sakuraexplug.image.ImgFolder;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.MiraiLogger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class ImgAddCommand extends ComplexFriendCommand {
	
	private static final String[] argNames = {"type", "api link"};
	public static final ImgAddCommand INSTANCE = new ImgAddCommand();
	private File imgFolder;
	private MiraiLogger logger;
	
	private ImgAddCommand() {
		super(argNames);
	}
	
	public ImgAddCommand(FriendMessageEvent event, File imgFolder, MiraiLogger logger) {
		super(event, argNames);
		this.imgFolder = imgFolder;
		this.logger = logger;
	}
	
	@Override
	public String getName() {
		return "/img+";
	}
	
	@Override
	public void detailedHelp() {
		MessageChainBuilder mcb = new MessageChainBuilder().append(usageHelp()).append("\n");
		mcb.append("type: 你想添加的 api 的图片类型\n");
		mcb.append("api link: 直接返回图片的 api 链接");
		getContact().sendMessage(mcb.asMessageChain());
	}
	
	@Override
	public void react() {
		if (getArgLength() > 1) {
			Map<String, Set<String>> apis = Config.INSTANCE.imageAPIs.get();
			Set<String> typedLink;
			if (!apis.containsKey(getArg(0))) {
				try {
					new URL(getArg(1));
					typedLink = new TreeSet<>();
					apis.put(getArg(0), typedLink);
					ImgFolder.createImgFolder(imgFolder, logger);
				} catch (MalformedURLException e) {
					getContact().sendMessage("请检查 api 地址是否正确");
					return;
				}
			}
			if (apis.get(getArg(0)).add(getArg(1))) {
				getContact().sendMessage("添加 api 链接 " + getArg(1) + " 成功");
			} else {
				getContact().sendMessage("api 链接 " + getArg(1) + " 已被添加");
			}
		} else {
			detailedHelp();
		}
	}
}
