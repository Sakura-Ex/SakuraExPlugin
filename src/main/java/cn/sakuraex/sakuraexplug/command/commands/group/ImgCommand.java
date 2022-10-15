package cn.sakuraex.sakuraexplug.command.commands.group;

import cn.sakuraex.sakuraexplug.SakuraExPlug;
import cn.sakuraex.sakuraexplug.command.commands.SingleArgGroupCommand;
import cn.sakuraex.sakuraexplug.config.Config;
import cn.sakuraex.sakuraexplug.util.MessageUtil;
import cn.sakuraex.sakuraexplug.util.Utils;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.OnlineMessageSource;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ImgCommand extends SingleArgGroupCommand {
	private static final String argName = "type";
	public static final ImgCommand INSTANCE = new ImgCommand();
	public final String name = "/img";
	private File imageFolder;
	private long timeFlag;
	private String thisArg;
	
	private ImgCommand() {
		super(argName);
	}
	
	public ImgCommand(GroupMessageEvent event, File imageFolder, long timeFlag) {
		super(event, argName);
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
		if (getArg().equals("random")) {
			List<String> typeName = new ArrayList<>();
			for (Map.Entry<String, List<String>> entry : Config.INSTANCE.imageAPIs.get().entrySet()) {
				typeName.add(entry.getKey());
			}
			thisArg = typeName.get((int) (Math.random() * typeName.size()));
		} else {
			thisArg = getArg();
		}
		File thisImageFolder = new File(imageFolder, thisArg);
		String filename = Utils.downloadImage(getDownloadURL(), thisImageFolder.getAbsolutePath(), Config.INSTANCE.imgRetryCount.get());
		if ("err".equals(filename)) {
			SakuraExPlug.logger.error("未能正确下载图片，尝试次数" + Config.INSTANCE.imgRetryCount);
			getContact().sendMessage(mcb.append(" 图片获取失败 >_<").asMessageChain());
		} else {
			SakuraExPlug.logger.info("获取到图片" + filename);
			Image img = ExternalResource.uploadAsImage(new File(thisImageFolder, filename), getContact(), "jpg");
			mcb.append(img);
			getContact().sendMessage((getArg().equals("random") ? mcb.append("type: ").append(thisArg) : mcb).asMessageChain());
		}
	}
	
	private URL getDownloadURL() {
		try {
			List<String> urlList = Config.INSTANCE.imageAPIs.get().get(thisArg);
			return new URL(urlList.get((int) (Math.random() * urlList.size())));
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public void detailedHelp() {
		MessageChainBuilder mcb = new MessageChainBuilder().append("Usage: /img <type>\n").append("Where the <type> can be:\n");
		for (Map.Entry<String, List<String>> entry : Config.INSTANCE.imageAPIs.get().entrySet()) {
			mcb.append("- ").append(entry.getKey()).append("\n");
		}
		getContact().sendMessage(mcb.append("- random\n").asMessageChain());
	}
	
	@Override
	public void react() {
		Member sender = (Member) getUser();
		Group group = getContact();
		OnlineMessageSource source = getSource();
		boolean canDo = false;
		if (getRawMessage().length > 1) {
			for (Map.Entry<String, List<String>> entry : Config.INSTANCE.imageAPIs.get().entrySet()) {
				canDo = canDo || getArg().equals(entry.getKey());
			}
			canDo = canDo || getArg().equals("random");
		}
		if (canDo) {
			long newTime = System.currentTimeMillis();
			long timeInt = newTime - timeFlag;
			if (timeInt > Config.INSTANCE.imgCD.get()) {
				send();
				// 成功执行，更新执行时间
				timeFlag = newTime;
			} else {
				MessageChainBuilder mcb = MessageUtil.groupQuoteAndAtMCB(source, sender).append("\n").append(getName())
						.append(" 太频繁了，年轻人要节制哦，请冷静一会儿吧\n").append("Left: ")
						.append(String.valueOf(Config.INSTANCE.imgCD.get() - timeInt)).append(" ms");
				group.sendMessage(mcb.asMessageChain());
			}
		} else {
			detailedHelp();
		}
	}
}
