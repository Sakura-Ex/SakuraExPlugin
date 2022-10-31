package cn.sakuraex.sakuraexplug.image;

import cn.sakuraex.sakuraexplug.config.Config;
import net.mamoe.mirai.utils.MiraiLogger;

import java.io.File;

public class ImgFolder {
	
	public static void createImgFolder(File imgFolder, MiraiLogger logger) {
		if (folderCreated(imgFolder)) {
			logger.info("ImgFolder: " + imgFolder.getPath());
		} else {
			logger.info("Create ImgFolder: " + imgFolder.getPath());
		}
		for (String type : Config.INSTANCE.imageAPIs.get().keySet()) {
			extendFolder(imgFolder, type, logger);
		}
	}
	
	private static boolean folderCreated(File folder) {
		return folder.exists();
	}
	
	private static void extendFolder(File folder, String name, MiraiLogger logger) {
		File nextFolder = new File(folder, name);
		String tips = capitalizeFirstLetter(name) + "Folder: " + nextFolder.getPath();
		if (folderCreated(nextFolder)) {
			logger.info(tips);
		} else {
			logger.info("Create " + tips);
		}
	}
	
	public static String capitalizeFirstLetter(String origin) {
		StringBuilder word = new StringBuilder(origin.toLowerCase());
		char firstLetter = word.charAt(0);
		word.replace(0, 1, String.valueOf(firstLetter));
		return word.toString();
	}
}

