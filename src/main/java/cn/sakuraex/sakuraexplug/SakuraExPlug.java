package cn.sakuraex.sakuraexplug;

import cn.sakuraex.sakuraexplug.command.Commands;
import cn.sakuraex.sakuraexplug.command.ImgCommand;
import cn.sakuraex.sakuraexplug.config.Config;
import cn.sakuraex.sakuraexplug.config.Default;
import cn.sakuraex.sakuraexplug.image.ImageKind;
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
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.OnlineMessageSource;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SakuraExPlug extends JavaPlugin {
	
	public static final SakuraExPlug INSTANCE = new SakuraExPlug();
	public static MiraiLogger logger;
	private final CoroutineScope scope = GlobalScope.INSTANCE;
	private long imgCommandTimeFlag = 0;
	
	private SakuraExPlug() {
		super(new JvmPluginDescriptionBuilder("cn.sakuraex.sakuraexplug", "0.1.0")
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
		
		File imgFolder = resolveDataFile("img");
		
		ImgFolder.createImgFolder(imgFolder, logger);
		
		reloadPluginConfig(Config.INSTANCE);
		
		GlobalEventChannel.INSTANCE.registerListenerHost(new SimpleListenerHost() {
			@Override
			public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
				super.handleException(context, exception);
			}
			
			@EventHandler
			public void onMessage(GroupMessageEvent event) {
				String[] rawMessage = event.getMessage().contentToString().split(" ");
				Group group = event.getGroup();
				Member sender = event.getSender();
				OnlineMessageSource source = event.getSource();
				switch (rawMessage[0]) {
					case "/img": {
						Map<Long, List<Long>> whitelist = Config.INSTANCE.whitelist.get();
						boolean hasMember = Config.INSTANCE.whiteQQList.get().contains(sender.getId());
						if (whitelist.containsKey(group.getId())) {
							hasMember = hasMember || whitelist.get(group.getId()).isEmpty();
							hasMember = hasMember || whitelist.get(group.getId()).contains(sender.getId());
						}
						if (hasMember) {
							boolean canDo = false;
							if (rawMessage.length > 1) {
								for (ImageKind value : ImageKind.values()) {
									canDo = canDo || rawMessage[1].equals(value.getName());
								}
							}
							if (canDo) {
								long newTime = System.currentTimeMillis();
								long timeInt = newTime - imgCommandTimeFlag;
								imgCommandTimeFlag = newTime;
								if (timeInt > Config.INSTANCE.imgCD.get()) {
									((ImgCommand) Commands.IMG.get()).setImageFolder(imgFolder);
									Commands.IMG.get().react(group, sender, rawMessage[1], source);
								} else {
									MessageChainBuilder mcb = MessageUtil.groupQuoteAndAtMCB(source, sender)
											.append("\n").append(Commands.IMG.getName())
											.append(" 太频繁了，年轻人要节制哦，请冷静一会儿吧\n").append("Left: ")
											.append(String.valueOf(Config.INSTANCE.imgCD.get() - timeInt))
											.append(" ms");
									group.sendMessage(mcb.asMessageChain());
								}
							} else {
								Commands.IMG.get().help(group, sender, source);
							}
						}
						break;
					}
					case "/calc": {
						MessageChainBuilder mcb = MessageUtil.groupQuoteAndAtMCB(source, sender);
						group.sendMessage(mcb.append("This method might be realized one day").asMessageChain());
						break;
					}
				}
			}
			
			@EventHandler
			public void onMessage(FriendMessageEvent event) {
				String[] rawMessage = event.getMessage().contentToString().split(" ");
				Friend sender = event.getSender();
				OnlineMessageSource source = event.getSource();
				switch (rawMessage[0]) {
					case "/help": {
						sender.sendMessage(Default.HELP_INFO);
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
								}
								break;
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
								}
								break;
							}
							break;
						} else {
							MessageChainBuilder mcb = new MessageChainBuilder().append("Usage: /check <info>\n")
									.append("Where the <info> can be:\n").append("\t-imageAPIs\n").append("\t-whitelist\n")
									.append("\t-whiteQQList\n").append("\t-imgRetryCount\n").append("\t-imgCD");
							sender.sendMessage(mcb.asMessageChain());
						}
						break;
					}
					case "/img": {
						if (rawMessage.length > 3 && ("add".equals(rawMessage[1]) || "remove".equals(rawMessage[1]))) {
							boolean canDo = false;
							for (ImageKind value : ImageKind.values()) {
								canDo = canDo || rawMessage[2].equals(value.getName());
							}
							if (canDo) {
								Map<String, List<String>> apis = Config.INSTANCE.imageAPIs.get();
								List<String> typedLink = apis.get(rawMessage[2]);
								if ("add".equals(rawMessage[1])) {
									if (typedLink.contains(rawMessage[3])) {
										sender.sendMessage("Api Link:" + rawMessage[3] + " is already in the imageAPIs.");
									} else {
										apis.get(rawMessage[2]).add(rawMessage[3]);
										sender.sendMessage("Add api Link: " + rawMessage[3] + " successfully.");
									}
								} else {
									if (apis.get(rawMessage[2]).remove(rawMessage[3])) {
										sender.sendMessage("Remove api Link: " + rawMessage[3] + " successfully.");
									} else {
										sender.sendMessage("Api Link:" + rawMessage[3] + " does not exist the imageAPIs.");
									}
								}
							} else {
								sender.sendMessage("Please enter correct type value.");
							}
						} else {
							sender.sendMessage("Usage: /img (add / remove) <type> <api link>");
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
					case "/op": {
						MessageChainBuilder mcb = new MessageChainBuilder();
						if (rawMessage.length > 1) {
							try {
								long qqNumber = Long.parseLong(rawMessage[1]);
								Utils.addQQ(sender, mcb, qqNumber);
							} catch (NumberFormatException e) {
								sender.sendMessage("Please enter right qq number.");
							}
						} else {
							long qqNumber = sender.getId();
							Utils.addQQ(sender, mcb, qqNumber);
						}
					break;
					}
					default:
						sender.sendMessage("Try use /help to learn what can I do.");
				}
			}
		});
	}
}