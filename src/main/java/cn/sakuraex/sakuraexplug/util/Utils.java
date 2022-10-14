package cn.sakuraex.sakuraexplug.util;

import cn.sakuraex.sakuraexplug.SakuraExPlug;
import cn.sakuraex.sakuraexplug.config.Config;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Utils {
	public static String downloadImage(URL url, String imagePath, int tryCount) {
		if (tryCount > 0) {
			String filename = System.currentTimeMillis() + ".jpg";
			try {
				FileChannel.open(
						Paths.get(imagePath+"/"+filename),
						StandardOpenOption.CREATE,
						StandardOpenOption.WRITE
				).transferFrom(Channels.newChannel(url.openStream()),0, Long.MAX_VALUE);
				return filename;
			} catch (Exception e) {
				SakuraExPlug.logger.error(e.toString());
				downloadImage(url, imagePath, tryCount - 1);
			}
		} else {
			return "err";
		}
		return "";
	}
	
	public static void addQQ(Friend sender, MessageChainBuilder mcb, long qqNumber) {
		if (!Config.INSTANCE.whiteQQList.get().contains(qqNumber)) {
			Config.INSTANCE.whiteQQList.get().add(qqNumber);
			mcb.append("Add ").append(Long.toString(qqNumber)).append(" successfully.");
			sender.sendMessage(mcb.asMessageChain());
		} else {
			MessageChainBuilder fail = new MessageChainBuilder().append(Long.toString(qqNumber)).append(" is already in whiteQQList.");
			sender.sendMessage(fail.asMessageChain());
		}
	}
	
	public static void currentWhiteQQList(Friend sender, MessageChainBuilder mcb, long qqNumber) {
	
	}
}
