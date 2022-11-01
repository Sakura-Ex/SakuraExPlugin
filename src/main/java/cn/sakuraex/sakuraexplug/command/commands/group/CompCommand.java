package cn.sakuraex.sakuraexplug.command.commands.group;

import cn.sakuraex.sakuraexplug.command.commands.ComputeCommand;
import cn.sakuraex.sakuraexplug.util.MessageUtil;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class CompCommand extends ComputeCommand {
	private static final String argName = "expression";
	public static final CompCommand INSTANCE = new CompCommand(argName);
	
	@Override
	public String getName() {
		return "/comp";
	}
	
	private CompCommand(String argName) {
		super(argName);
	}
	
	public CompCommand(GroupMessageEvent event) {
		super(event, argName);
	}
	
	@Override
	public void detailedHelp() {
		MessageChainBuilder mcb = new MessageChainBuilder().append(usageHelp()).append("\n");
		mcb.append("expression: 你要计算的表达式\n");
		mcb.append("*目前仅支持四则及幂运算*\n");
		mcb.append("*幂大小必须小于").append(String.valueOf(Integer.MAX_VALUE)).append("且为整数*");
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
			try {
				BigDecimal result = calc(resolveExpr(transformExp(expression.toString())));
				String message = result.setScale(32, RoundingMode.HALF_UP).toString().replaceAll("0*$", "");
				if (message.endsWith(".")) {
					message = message.replace(".", "");
				}
				mcb.append(String.valueOf(expression)).append("=");
				mcb.append(message);
			} catch (ArithmeticException e) {
				mcb.append("数学错误");
			} catch (IllegalArgumentException e) {
				mcb.append("语法错误");
			} catch (Exception e) {
				mcb.append("出错啦");
			}
			getContact().sendMessage(mcb.asMessageChain());
		} else {
			detailedHelp();
		}
	}
	
	/**
	 * 计算只含加减乘除和幂的式子
	 *
	 * @return 计算结果
	 */
	private BigDecimal calcE(List<String> exp) {
		List<String> process = new LinkedList<>(exp);
		//计算乘方
		for (int i = 0; i < process.size(); i++) {
			String p = process.get(i);
			if (p.equals("^")) {
				BigDecimal temp;
				temp = new BigDecimal(process.get(i - 1)).pow(Integer.parseInt(process.get(i + 1)));
				process.remove(i - 1);
				process.remove(i - 1);
				process.set(i - 1, String.valueOf(temp));
				i--;
			}
		}
		//计算乘除
		for (int i = 0; i < process.size(); i++) {
			String p = process.get(i);
			if (p.matches("[*/]")) {
				BigDecimal temp;
				if (p.equals("*")) {
					temp = new BigDecimal(process.get(i - 1)).multiply(new BigDecimal(process.get(i + 1)));
				} else {
					temp = new BigDecimal(process.get(i - 1)).divide(new BigDecimal(process.get(i + 1)), new MathContext(64, RoundingMode.HALF_UP));
				}
				process.remove(i - 1);
				process.remove(i - 1);
				process.set(i - 1, String.valueOf(temp));
				i--;
			}
		}
		//计算加减
		BigDecimal result = new BigDecimal(process.get(0));
		for (int i = 1; i < process.size() - 1; i += 2) {
			if (process.get(i).equals("+")) {
				result = result.add(new BigDecimal(process.get(i + 1)));
			} else if (process.get(i).equals("-")) {
				result = result.subtract(new BigDecimal(process.get(i + 1)));
			}
		}
		return result;
	}
	
	/**
	 * 将括号内的表达式值计算出来并替换原括号位置
	 *
	 * @return 计算结果
	 */
	private BigDecimal calc(List<String> exp) {
		List<String> next = new ArrayList<>();
		List<String> nextExp = new ArrayList<>();
		Stack<String> brackets = new Stack<>();
		for (int i = 0; i < exp.size(); i++) {
			String temp = exp.get(i);
			// 读取匹配的括号间的表达式
			if (temp.equals("(")) {
				for (int j = i; j < exp.size(); j++) {
					if (exp.get(j).equals(")")) {
						brackets.pop();
					}
					if (!brackets.isEmpty()) {
						nextExp.add(exp.get(j));
					}
					if (exp.get(j).equals("(")) {
						brackets.push("(");
					}
					if (brackets.isEmpty()) {
						i = j;
						break;
					}
				}
				// 递归调用
				temp = String.valueOf(calc(nextExp));
				nextExp.clear();
			}
			next.add(temp);
		}
		// 不含括号，返回计算结果
		return calcE(next);
	}
}
