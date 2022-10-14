package cn.sakuraex.sakuraexplug.config;

import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

public class Default {
	public static final int DEFAULT_RETRY_COUNT = 5;
	public static final int DEFAULT_CD = 10000;
	
	public static final MessageChain HELP_INFO = new MessageChainBuilder()
			.append("---\n" +
					"/help\n\n" +
					"Get help message.\n" +
					"---\n" +
					"/check <info>\n\n" +
					"Check the config.\n" +
					"---\n" +
					"/img add <type> <api link>\n\n" +
					"Add an api to specific type.\n" +
					"---\n" +
					"/img remove <type> <api link>\n\n" +
					"Remove an api from specific type.\n"+
					"---\n" +
					"/group add <group id> [member id]\n\n" +
					"Let specific group member can use the commands.\n" +
					"Omit the [member id] to give all members permission.\n" +
					"---\n" +
					"/group remove <group id>\n\n" +
					"Stop a group from using all the commands.\n" +
					"---\n" +
					"/op [qq number]\n\n" +
					"Let specific qq could use the commands in all groups.\n" +
					"---\n" +
					"/deop [qq number]\n\n" +
					"The opposite effect on /op.\n\n" +
					"Use the command without argument to get further information.").asMessageChain();
}
