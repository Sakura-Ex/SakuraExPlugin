package cn.sakuraex.sakuraexplug;

import cn.sakuraex.sakuraexplug.command.AbstractCommand;
import cn.sakuraex.sakuraexplug.command.commands.friend.CheckCommand;
import cn.sakuraex.sakuraexplug.command.commands.friend.DeOpCommand;
import cn.sakuraex.sakuraexplug.command.commands.friend.OpCommand;
import cn.sakuraex.sakuraexplug.command.commands.group.CalcCommand;
import cn.sakuraex.sakuraexplug.command.commands.group.ImgCommand;
import cn.sakuraex.sakuraexplug.config.Config;
import cn.sakuraex.sakuraexplug.image.ImgFolder;
import cn.sakuraex.sakuraexplug.util.MessageUtil;
import cn.sakuraex.sakuraexplug.util.Utils;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.GlobalScope;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SakuraExPlug extends JavaPlugin {
	
	public static final SakuraExPlug INSTANCE = new SakuraExPlug();
	public static MiraiLogger logger;
	private final CoroutineScope scope = GlobalScope.INSTANCE;
	private long imgCommandTimeFlag = 0;
	
	private SakuraExPlug() {
		super(new JvmPluginDescriptionBuilder("cn.sakuraex.sakuraexplug", "0.1.1")
				.name("SakuraExPluginQQ")
				.author("SakuraEx")
				.info("SakuraEx's assistant")
				.build());
	}
	
	@Override
	public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
		super.onLoad($this$onLoad);
		logger = getLogger();
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		// Prepare for /img
		File imgFolder = resolveDataFile("img");
		ImgFolder.createImgFolder(imgFolder, logger);
		// Load config.
		reloadPluginConfig(Config.INSTANCE);
		// Storage command types.
		List<AbstractCommand<Group>> groupCommands = new ArrayList<AbstractCommand<Group>>() {{
			add(ImgCommand.INSTANCE);
			add(CalcCommand.INSTANCE);
		}};
		
		List<AbstractCommand<Friend>> friendCommands = new ArrayList<AbstractCommand<Friend>>() {{
			add(CheckCommand.INSTANCE);
			add(OpCommand.INSTANCE);
			add(DeOpCommand.INSTANCE);
		}};
		
		GlobalEventChannel.INSTANCE.registerListenerHost(new SimpleListenerHost() {
			@Override
			public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
				super.handleException(context, exception);
			}
			
			// Group Message
			@EventHandler
			public void onMessage(GroupMessageEvent event) {
				if (Utils.hasPermission(event)) {
					String commandType = event.getMessage().contentToString().split(" ")[0];
					switch (commandType) {
						case "/img":
							ImgCommand imgCommand = new ImgCommand(event, imgFolder, imgCommandTimeFlag);
							imgCommand.react();
							imgCommandTimeFlag = imgCommand.getTimeFlag();
							break;
						case "/calc":
							new CalcCommand(event).react();
							break;
						case "/help":
							MessageChainBuilder mcb = MessageUtil.groupQuoteAndAtMCB(event.getSource(), event.getSender());
							mcb.append("Available Command:\n");
							for (AbstractCommand<Group> command : groupCommands) {
								mcb.append(command.usageHelp()).append("\n");
							}
							event.getGroup().sendMessage(mcb.asMessageChain());
							break;
					}
				}
			}
			
			@EventHandler
			public void onMessage(FriendMessageEvent event) {
				String[] rawMessage = event.getMessage().contentToString().split(" ");
				Friend sender = event.getSender();
				switch (rawMessage[0]) {
					case "/help": {
						MessageChainBuilder mcb = new MessageChainBuilder();
						mcb.append("Available Command:\n");
						for (AbstractCommand<Friend> command : friendCommands) {
							mcb.append(command.usageHelp()).append("\n");
						}
						event.getSender().sendMessage(mcb.asMessageChain());
						break;
					}
					case "/check": {
						if (rawMessage.length > 1) {
							switch (rawMessage[1]) {
								case "imageAPIs": {
									MessageChainBuilder mcb = new MessageChainBuilder().append("imageAPIs:\n");
									for (Map.Entry<String, List<String>> entry : Config.INSTANCE.imageAPIs.get().entrySet()) {
										mcb.append("\t-").append(entry.getKey()).append("\n");
										for (String api : entry.getValue()) {
											mcb.append("\t\t-").append(api).append("\n");
										}
									}
									sender.sendMessage(mcb.asMessageChain());
									break;
								}
								case "whitelist": {
									MessageChainBuilder mcb = new MessageChainBuilder().append("whitelist:\n");
									for (Map.Entry<Long, List<Long>> entry : Config.INSTANCE.whitelist.get().entrySet()) {
										mcb.append("\t-").append(entry.getKey().toString()).append("\n");
										if (entry.getValue().isEmpty()) {
											mcb.append("\t\t-all\n");
										} else {
											for (long qqNumber : entry.getValue()) {
												mcb.append("\t\t-").append(Long.toString(qqNumber)).append("\n");
											}
										}
									}
									sender.sendMessage(mcb.asMessageChain());
									break;
								}
								case "whiteQQList:": {
									MessageChainBuilder mcb = new MessageChainBuilder().append("whiteQQList:\n");
									for (long qqNumber : Config.INSTANCE.whiteQQList.get()) {
										mcb.append("\t-").append(String.valueOf(qqNumber)).append("\n");
									}
									sender.sendMessage(mcb.asMessageChain());
									break;
								}
							}
							break;
						} else {
							MessageChainBuilder mcb = new MessageChainBuilder().append("Usage: /check <info>\n")
									.append("Where the <info> can be:\n").append("\t-imageAPIs\n").append("\t-whitelist\n")
									.append("\t-whiteQQList\n");
							sender.sendMessage(mcb.asMessageChain());
						}
						break;
					}
					case "/img": {
						Map<String, List<String>> apis = Config.INSTANCE.imageAPIs.get();
						if (rawMessage.length > 3 && ("add".equals(rawMessage[1]) || "remove".equals(rawMessage[1]))) {
							if ("add".equals(rawMessage[1])) {
								List<String> typedLink;
								if (apis.containsKey(rawMessage[2])) {
									typedLink = apis.get(rawMessage[2]);
								} else {
									try {
										new URL(rawMessage[3]);
										typedLink = new ArrayList<>();
										apis.put(rawMessage[2], typedLink);
										ImgFolder.createImgFolder(imgFolder, logger);
									} catch (MalformedURLException e) {
										sender.sendMessage("Please check the api link.");
										break;//若URL不正确，则结束switch
									}
								}
								if (typedLink.contains(rawMessage[3])) {
									sender.sendMessage("Api Link " + rawMessage[3] + " is already in the imageAPIs.");
								} else {
									apis.get(rawMessage[2]).add(rawMessage[3]);
									sender.sendMessage("Add api Link " + rawMessage[3] + " successfully.");
								}
							} else {
								if (apis.containsKey(rawMessage[2])) {
									if (apis.get(rawMessage[2]).remove(rawMessage[3])) {
										sender.sendMessage("Remove api Link " + rawMessage[3] + " successfully.");
									} else {
										sender.sendMessage("Api Link " + rawMessage[3] + " does not exist.");
									}
								} else {
									sender.sendMessage("Type value " + rawMessage[2] + " does not exist.");
								}
							}
						} else if ("remove".equals(rawMessage[1]) && rawMessage.length == 3) {
							if (apis.containsKey(rawMessage[2])) {
								boolean isDeleted = true;
								apis.remove(rawMessage[2]);
								File delete = new File(imgFolder, rawMessage[2]);
								File[] files = delete.listFiles();
								if (files != null) {
									for (File file : files) {
										isDeleted = file.delete() && isDeleted;
									}
								}
								if (delete.delete() && isDeleted) {
									logger.info("Delete type folder successfully.");
								} else {
									logger.info("Type folder deletion failed.");
								}
							} else {
								sender.sendMessage("Type value " + rawMessage[2] + " does not exist.");
							}
						} else {
							sender.sendMessage("Usage: /img add <type> <api link>\n/img remove <type> [api link]");
						}
						break;
					}
					case "/group": {
						MessageChainBuilder mcb = new MessageChainBuilder();
						if (rawMessage.length > 2 && ("add".equals(rawMessage[1])) || "remove".equals(rawMessage[1])) {
							Map<Long, List<Long>> whiteGroupList = Config.INSTANCE.whitelist.get();
							long groupNumber;
							try {
								groupNumber = Long.parseLong(rawMessage[2]);
								if ("remove".equals(rawMessage[1])) {
									if (rawMessage.length > 3) {
										try {
											long qqNumber = Long.parseLong(rawMessage[3]);
											if (whiteGroupList.containsKey(groupNumber)) {
												List<Long> qqNumberList = whiteGroupList.get(groupNumber);
												if (qqNumberList.remove(qqNumber)) {
													mcb.append("Remove member ").append(String.valueOf(qqNumber)).append(" from group ")
															.append(String.valueOf(groupNumber)).append(" successfully.");
													sender.sendMessage(mcb.asMessageChain());
												} else {
													sender.sendMessage("Member " + qqNumber + " does not exist in group " + groupNumber + ".");
												}
											} else {
												sender.sendMessage("Group " + groupNumber + " does not exist in whitelist.");
											}
										} catch (NumberFormatException e) {
											sender.sendMessage("Please enter right qq number.");
										}
									} else {
										if (whiteGroupList.containsKey(groupNumber)) {
											whiteGroupList.remove(groupNumber);
											sender.sendMessage("Remove group " + groupNumber + " successfully.");
										} else {
											sender.sendMessage("Group number " + groupNumber + " does not exist in whitelist.");
										}
									}
								} else {
									if (rawMessage.length > 3) {
										try {
											long qqNumber = Long.parseLong(rawMessage[3]);
											if (whiteGroupList.containsKey(groupNumber)) {
												List<Long> qqNumberList = whiteGroupList.get(groupNumber);
												qqNumberList.add(qqNumber);
											} else {
												whiteGroupList.put(groupNumber, new ArrayList<Long>() {{
													add(qqNumber);
												}});
											}
											mcb.append("Add member ").append(String.valueOf(qqNumber)).append(" to group ")
													.append(String.valueOf(groupNumber)).append(" successfully.");
											sender.sendMessage(mcb.asMessageChain());
										} catch (NumberFormatException e) {
											sender.sendMessage("Please enter right qq number.");
										}
									} else {
										whiteGroupList.put(groupNumber, new ArrayList<>());
										sender.sendMessage("Add group " + groupNumber + " successfully.");
									}
								}
							} catch (NumberFormatException e) {
								sender.sendMessage("Please enter right group number.");
							}
						} else {
							sender.sendMessage("Usage: /group (add / remove) <group id> [member id]");
						}
						break;
					}
					case "/op":
						new OpCommand(event).react();
						break;
					case "/deop":
						new DeOpCommand(event).react();
						break;
					default:
						sender.sendMessage("Try using /help to learn what can I do.");
				}
			}
		});
	}
}