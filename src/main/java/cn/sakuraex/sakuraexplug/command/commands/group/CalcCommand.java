package cn.sakuraex.sakuraexplug.command.commands.group;

import cn.sakuraex.sakuraexplug.command.commands.ComputeCommand;
import cn.sakuraex.sakuraexplug.util.MessageUtil;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public final class CalcCommand extends ComputeCommand {
	
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
		mcb.append("*目前仅支持四则及幂运算*\n");
		mcb.append("*方法采用浮点数运算，高精度请使用 /comp*");
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
				double temp = calc(resolveExpr(transformExp(expression.toString())));
				if (Double.isNaN(temp)) {
					throw new ArithmeticException();
				}
				BigDecimal result = BigDecimal.valueOf(temp);
				String message = String.valueOf(result.setScale(7, RoundingMode.HALF_UP)).replaceAll("0*$", "");
				if (message.endsWith(".")) {
					message = message.substring(0, message.length() - 1);
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
	private double calcE(List<String> exp) {
		List<String> process = new LinkedList<>(exp);
		//计算乘方
		for (int i = 0; i < process.size(); i++) {
			String p = process.get(i);
			if (p.equals("^")) {
				double temp;
				temp = Math.pow(Double.parseDouble(process.get(i - 1)), Double.parseDouble(process.get(i + 1)));
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
				double temp;
				if (p.equals("*")) {
					temp = Double.parseDouble(process.get(i - 1)) * Double.parseDouble(process.get(i + 1));
				} else {
					temp = Double.parseDouble(process.get(i - 1)) / Double.parseDouble(process.get(i + 1));
				}
				process.remove(i - 1);
				process.remove(i - 1);
				process.set(i - 1, String.valueOf(temp));
				i--;
			}
		}
		//计算加减
		double result = Double.parseDouble(process.get(0));
		for (int i = 1; i < process.size() - 1; i += 2) {
			if (process.get(i).equals("+")) {
				result += Double.parseDouble(process.get(i + 1));
			} else if (process.get(i).equals("-")) {
				result -= Double.parseDouble(process.get(i + 1));
			}
		}
		return result;
	}
	
	/**
	 * 将括号内的表达式值计算出来并替换原括号位置
	 *
	 * @return 计算结果
	 */
	private double calc(List<String> exp) {
		List<String> next = new ArrayList<>();
		Stack<String> brackets = new Stack<>();
		for (int i = 0; i < exp.size(); i++) {
			String temp = exp.get(i);
			// 读取匹配的括号间的表达式
			if (temp.equals("(")) {
				List<String> nextExp = new ArrayList<>();
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
