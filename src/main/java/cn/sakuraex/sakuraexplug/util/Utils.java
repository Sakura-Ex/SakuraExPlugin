package cn.sakuraex.sakuraexplug.util;

import cn.sakuraex.sakuraexplug.SakuraExPlug;
import cn.sakuraex.sakuraexplug.config.Config;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

public class Utils {
	public static String downloadImage(URL url, String imagePath, int tryCount) {
		if (tryCount > 0) {
			String filename = System.currentTimeMillis() + ".jpg";
			try {
				FileChannel.open(
						Paths.get(imagePath + "/" + filename),
						StandardOpenOption.CREATE,
						StandardOpenOption.WRITE
				).transferFrom(Channels.newChannel(url.openStream()), 0, Long.MAX_VALUE);
				return filename;
			} catch (Exception e) {
				SakuraExPlug.logger.error(e.toString());
				downloadImage(url, imagePath, tryCount - 1);
				return "err";
			}
		} else {
			return "err";
		}
	}
	
	public static boolean hasPermission(GroupMessageEvent event) {
		Map<Long, Set<Long>> whitelist = Config.INSTANCE.whitelist.get();
		Member sender = event.getSender();
		Group group = event.getGroup();
		boolean has = Config.INSTANCE.whiteQQList.get().contains(sender.getId());
		if (!has && whitelist.containsKey(group.getId())) {
			has = whitelist.get(event.getGroup().getId()).isEmpty() || whitelist.get(group.getId()).contains(sender.getId());
		}
		return has;
	}
}
