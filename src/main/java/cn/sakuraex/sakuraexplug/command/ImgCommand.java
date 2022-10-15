package cn.sakuraex.sakuraexplug.command;

import cn.sakuraex.sakuraexplug.SakuraExPlug;
import cn.sakuraex.sakuraexplug.config.Config;
import cn.sakuraex.sakuraexplug.util.MessageUtil;
import cn.sakuraex.sakuraexplug.util.Utils;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.OnlineMessageSource;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

public final class ImgCommand extends SingleArgGroupCommand {
	public static final ImgCommand INSTANCE = new ImgCommand();
	private File imageFolder;
	private long timeFlag;
	public final String name = "/img";
	
	private ImgCommand() {
		super();
	}
	
	public ImgCommand(String rawMessage, Group contact, User user, OnlineMessageSource source, File imageFolder, long timeFlag) {
		super(rawMessage, contact, user, source);
		this.imageFolder = imageFolder;
		this.timeFlag = timeFlag;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	public long getTimeFlag() {
		return timeFlag;
	}
	
	public void send() {
		MessageChainBuilder mcb = MessageUtil.groupQuoteAndAtMCB(getSource(), (Member) getUser());
		File thisImageFolder = new File(imageFolder, getArg());
		String filename = Utils.downloadImage(getDownloadURL(), thisImageFolder.getAbsolutePath(), Config.INSTANCE.imgRetryCount.get());
		if ("err".equals(filename)) {
			SakuraExPlug.logger.error("未能正确下载图片，尝试次数" + Config.INSTANCE.imgRetryCount);
			getContact().sendMessage(mcb.append(" 图片获取失败 >_<").asMessageChain());
		} else {
			SakuraExPlug.logger.info("获取到图片" + filename);
			Image img = ExternalResource.uploadAsImage(new File(thisImageFolder, filename), getContact(), "jpg");
			getContact().sendMessage(mcb.append(img).asMessageChain());
		}
	}
	
	public URL getDownloadURL() {
		try {
			List<String> urlList = Config.INSTANCE.imageAPIs.get().get(getArg());
			return new URL(urlList.get((int) (Math.random() * urlList.size())));
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public void help() {
		MessageChainBuilder mcb = new MessageChainBuilder()
				.append("Usage: /img <type>\n")
				.append("Where the <type> can be:\n");
		for (Map.Entry<String, List<String>> entry : Config.INSTANCE.imageAPIs.get().entrySet()) {
			mcb.append("- ").append(entry.getKey()).append("\n");
		}
		getContact().sendMessage(mcb.asMessageChain());
	}
	
	@Override
	public void react() {
		Member sender = (Member) getUser();
		Group group = getContact();
		OnlineMessageSource source = getSource();
		Map<Long, List<Long>> whitelist = Config.INSTANCE.whitelist.get();
		boolean hasMember = Config.INSTANCE.whiteQQList.get().contains(sender.getId());
		if (whitelist.containsKey(group.getId())) {
			hasMember = hasMember || whitelist.get(group.getId()).isEmpty();
			hasMember = hasMember || whitelist.get(group.getId()).contains(sender.getId());
		}
		if (hasMember) {
			boolean canDo = false;
			if (getRawMessage().length > 1) {
				for (Map.Entry<String, List<String>> entry : Config.INSTANCE.imageAPIs.get().entrySet()) {
					canDo = canDo || getArg().equals(entry.getKey());
				}
			}
			if (canDo) {
				long newTime = System.currentTimeMillis();
				long timeInt = newTime - timeFlag;
				if (timeInt > Config.INSTANCE.imgCD.get()) {
					send();
					// 成功执行，更新执行时间
					timeFlag = newTime;
				} else {
					MessageChainBuilder mcb = MessageUtil.groupQuoteAndAtMCB(source, sender)
							.append("\n").append(Commands.IMG.getName())
							.append(" 太频繁了，年轻人要节制哦，请冷静一会儿吧\n").append("Left: ")
							.append(String.valueOf(Config.INSTANCE.imgCD.get() - timeInt))
							.append(" ms");
					group.sendMessage(mcb.asMessageChain());
				}
			} else {
				help();
			}
		}
	}
}
