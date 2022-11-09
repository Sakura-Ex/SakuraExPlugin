package cn.sakuraex.sakuraexplug.command.commands.group;

import cn.sakuraex.sakuraexplug.command.commands.SingleArgGroupCommand;
import cn.sakuraex.sakuraexplug.util.Expression;
import cn.sakuraex.sakuraexplug.util.MessageUtil;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

public final class CalcCommand extends SingleArgGroupCommand {
	
	private static final String argName = "expression";
	public static final CalcCommand INSTANCE = new CalcCommand(argName);
	
	private CalcCommand(String argName) {
		super(argName);
	}
	
	public CalcCommand(GroupMessageEvent event) {
		super(event, argName);
	}
	
	@Override
	public String getName() {
		return "/calc";
	}
	
	@Override
	public void detailedHelp() {
		MessageChainBuilder mcb = new MessageChainBuilder().append(usageHelp()).append("\n");
		mcb.append("expression: 你要计算的表达式\n");
		getContact().sendMessage(mcb.asMessageChain());
	}
	
	@Override
	public void react() {
		if (hasArg()) {
			MessageChainBuilder mcb = MessageUtil.groupQuoteAndAtMCB(getSource(), (Member) getUser());
			String[] raw = getRawMessage();
			StringBuilder expression = new StringBuilder();
			for (int i = 1; i < raw.length; i++) {
				expression.append(raw[i]);
			}
			Expression exp = new Expression(expression.toString());
			try {
				mcb.append("=").append(exp.calculate());
			} catch (Exception e) {
				mcb.append("请检查表达式");
			}
			getContact().sendMessage(mcb.asMessageChain());
		} else {
			detailedHelp();
		}
	}
}
