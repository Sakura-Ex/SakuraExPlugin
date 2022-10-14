package cn.sakuraex.sakuraexplug.command;

import cn.sakuraex.sakuraexplug.SakuraExPlug;
import cn.sakuraex.sakuraexplug.config.Config;
import cn.sakuraex.sakuraexplug.image.ImageKind;
import cn.sakuraex.sakuraexplug.util.MessageUtil;
import cn.sakuraex.sakuraexplug.util.Utils;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.OnlineMessageSource;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.net.URL;
import java.util.List;

public final class ImgCommand extends SingleArgGroupCommand {
	static final ImgCommand INSTANCE = new ImgCommand();
	private File imageFolder;
	
	private ImgCommand() {
	}
	
	@Override
	public String getName() {
		return "/img";
	}
	
	public void react(Group group, Member sender, String type, OnlineMessageSource source) {
		MessageChainBuilder mcb = MessageUtil.groupQuoteAndAtMCB(source, sender);
		File thisImageFolder = new File(imageFolder, type);
		String filename = Utils.downloadImage(getDownloadURL(type), thisImageFolder.getAbsolutePath(), Config.INSTANCE.imgRetryCount.get());
		if ("err".equals(filename)) {
			SakuraExPlug.logger.error("未能正确下载图片，尝试次数" + Config.INSTANCE.imgRetryCount);
			group.sendMessage(mcb.append("图片获取失败 >_<").asMessageChain());
		} else {
			SakuraExPlug.logger.info("获取到图片" + filename);
			Image img = ExternalResource.uploadAsImage(new File(thisImageFolder, filename), group, "jpg");
			group.sendMessage(mcb.append(img).asMessageChain());
		}
	}
	
	public URL getDownloadURL(String arg) {
		try {
			List<String> urlList = Config.INSTANCE.imageAPIs.get().get(arg);
			return new URL(urlList.get((int) (Math.random() * urlList.size())));
		} catch (Exception e) {
			return null;
		}
	}
	
	public void setImageFolder(File imageFolder) {
		this.imageFolder = imageFolder;
	}
	
	public void help(Group group, Member sender, OnlineMessageSource source) {
		MessageChainBuilder mcb = new MessageChainBuilder()
				.append("Usage: /img <type>\n")
				.append("Where the <type> can be:\n");
		for (ImageKind value : ImageKind.values()) {
			mcb.append("- ").append(value.getName()).append("\n");
		}
		group.sendMessage(mcb.asMessageChain());
	}
}
