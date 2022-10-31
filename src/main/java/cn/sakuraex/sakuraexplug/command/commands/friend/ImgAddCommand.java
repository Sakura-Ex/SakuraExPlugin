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
		mcb.append("type: The type you want put api in.\n");
		mcb.append("api link: A url that return an image.");
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
					getContact().sendMessage("Please check the api link.");
					return;
				}
			}
			if (apis.get(getArg(0)).add(getArg(1))) {
				getContact().sendMessage("Add api Link " + getArg(1) + " successfully.");
			} else {
				getContact().sendMessage("Api Link " + getArg(1) + " is already in the imageAPIs.");
			}
		} else {
			detailedHelp();
		}
	}
}
