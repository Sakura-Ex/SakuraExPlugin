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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
			Map<String, List<String>> apis = Config.INSTANCE.imageAPIs.get();
			List<String> typedLink;
			if (apis.containsKey(getArg(0))) {
				typedLink = apis.get(getArg(0));
			} else {
				try {
					new URL(getArg(1));
					typedLink = new ArrayList<>();
					apis.put(getArg(0), typedLink);
					ImgFolder.createImgFolder(imgFolder, logger);
				} catch (MalformedURLException e) {
					getContact().sendMessage("Please check the api link.");
					return;
				}
			}
			if (typedLink.contains(getArg(1))) {
				getContact().sendMessage("Api Link " + getArg(1) + " is already in the imageAPIs.");
			} else {
				apis.get(getArg(0)).add(getArg(1));
				getContact().sendMessage("Add api Link " + getArg(1) + " successfully.");
			}
		} else {
			detailedHelp();
		}
	}
}
