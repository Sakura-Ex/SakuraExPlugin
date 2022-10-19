package cn.sakuraex.sakuraexplug.command.commands.group;

import cn.sakuraex.sakuraexplug.command.commands.NoArgGroupCommand;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public final class GitHubCommand extends NoArgGroupCommand {
	public static final GitHubCommand INSTANCE = new GitHubCommand();
	
	private GitHubCommand() {
	}
	
	public GitHubCommand(GroupMessageEvent event) {
		super(event);
	}
	
	{
		setInfo("GitHub repository link.");
	}
	
	@Override
	public String getName() {
		return "/github";
	}
	
	@Override
	public void react() {
		getContact().sendMessage("https://github.com/Sakura-Ex/SakuraExPlugin");
	}
}
