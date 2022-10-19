package cn.sakuraex.sakuraexplug;

import cn.sakuraex.sakuraexplug.command.AbstractCommand;
import cn.sakuraex.sakuraexplug.command.commands.HelpCommand;
import cn.sakuraex.sakuraexplug.command.commands.friend.*;
import cn.sakuraex.sakuraexplug.command.commands.group.CalcCommand;
import cn.sakuraex.sakuraexplug.command.commands.group.GitHubCommand;
import cn.sakuraex.sakuraexplug.command.commands.group.ImgCommand;
import cn.sakuraex.sakuraexplug.command.commands.group.RandMuteCommand;
import cn.sakuraex.sakuraexplug.config.Config;
import cn.sakuraex.sakuraexplug.image.ImgFolder;
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
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class SakuraExPlug extends JavaPlugin {
	
	public static final SakuraExPlug INSTANCE = new SakuraExPlug();
	public static MiraiLogger logger;
	private final CoroutineScope scope = GlobalScope.INSTANCE;
	private long imgCommandTimeFlag = 0;
	
	private SakuraExPlug() {
		super(new JvmPluginDescriptionBuilder("cn.sakuraex.sakuraexplug", "0.2.1")
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
			//add(CalcCommand.INSTANCE);
			add(RandMuteCommand.INSTANCE);
			add(GitHubCommand.INSTANCE);
		}};
		
		List<AbstractCommand<Friend>> friendCommands = new ArrayList<AbstractCommand<Friend>>() {{
			add(CheckCommand.INSTANCE);
			add(ImgAddCommand.INSTANCE);
			add(ImgRemoveCommand.INSTANCE);
			add(GroupAddCommand.INSTANCE);
			add(GroupRemoveCommand.INSTANCE);
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
					String commandName = event.getMessage().contentToString().split(" ")[0];
					switch (commandName) {
						case "/img":
							ImgCommand imgCommand = new ImgCommand(event, imgFolder, imgCommandTimeFlag);
							imgCommand.react();
							imgCommandTimeFlag = imgCommand.getTimeFlag();
							break;
						case "/calc":
							AbstractCommand.react(new CalcCommand(event));
							break;
						case "/randmute":
							AbstractCommand.react(new RandMuteCommand(event));
							break;
						case "/github":
							AbstractCommand.react(new GitHubCommand(event));
							break;
						case "/help":
							AbstractCommand.react(new HelpCommand<>(event, groupCommands));
							break;
					}
				}
			}
			
			@EventHandler
			public void onMessage(FriendMessageEvent event) {
				String commandName = event.getMessage().contentToString().split(" ")[0];
				switch (commandName) {
					case "/help":
						AbstractCommand.react(new HelpCommand<>(event, friendCommands));
						break;
					case "/check":
						AbstractCommand.react(new CheckCommand(event));
						break;
					case "/img+":
						AbstractCommand.react(new ImgAddCommand(event, imgFolder, logger));
						break;
					case "/img-":
						AbstractCommand.react(new ImgRemoveCommand(event, imgFolder, logger));
						break;
					case "/group+":
						AbstractCommand.react(new GroupAddCommand(event));
						break;
					case "/group-":
						AbstractCommand.react(new GroupRemoveCommand(event));
						break;
					case "/op":
						AbstractCommand.react(new OpCommand(event));
						break;
					case "/deop":
						AbstractCommand.react(new DeOpCommand(event));
						break;
					default:
						event.getSender().sendMessage("Try using /help to learn what can I do.");
				}
			}
			
			@EventHandler
			public void onJoin(MemberJoinEvent event) {
			
			}
		});
	}
}