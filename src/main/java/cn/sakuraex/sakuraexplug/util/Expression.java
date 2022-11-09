package cn.sakuraex.sakuraexplug.util;

import java.math.BigInteger;
import java.util.*;

public class Expression {
	public final String raw;
	public final List<Expression> expressionPart;
	public final boolean isSmallest;
	public final boolean illegal;
	public final boolean returnDecimal;
	public final boolean containsBrackets;
	private final Map<Integer, List<Integer>> operation = new HashMap<>(); // key：计算优先级；value：符号所在位置构成的列表
	private final List<Integer> bracketsList = new ArrayList<>();
	private String result = null;
	private String selfOperator = "";
	private final boolean subExpression;
	
	private Expression(String exp, String selfOperator) {
		this(exp, true);
		this.selfOperator = selfOperator;
	}
	
	public Expression(String exp) {
		this(exp, false);
	}
	
	private Expression(String exp, boolean subExpression) {
		this.subExpression = subExpression;
		exp = exp.trim().replaceAll("\\s", "");
		if (exp.equals("")) {
			this.raw = "0";
			this.expressionPart = null;
			this.isSmallest = true;
			this.illegal = false;
			this.returnDecimal = false;
			this.containsBrackets = false;
			return;
		} else if (exp.matches("[+*/^-]")) {
			this.raw = exp;
			this.expressionPart = null;
			this.isSmallest = true;
			this.illegal = false;
			this.returnDecimal = false;
			this.containsBrackets = false;
			return;
		} else if (exp.replaceAll("\\d", "").contains("..")) {
			this.raw = exp;
			this.expressionPart = null;
			this.isSmallest = false;
			this.illegal = true;
			this.returnDecimal = false;
			this.containsBrackets = false;
			return;
		} else if (exp.matches("[*/^].*")) {
			this.raw = exp;
			this.expressionPart = null;
			this.isSmallest = false;
			this.illegal = true;
			this.returnDecimal = false;
			this.containsBrackets = false;
			return;
		}
		this.raw = exp;
		exp = exp.replaceAll("(?<!\\d)(\\.\\d+)", "0$1").replaceAll("(\\d)(\\(|[A-Za-z])", "$1*$2");
		//如果符合 “（符号）（数字）（.）（数字）” ，则为基本单位（数字）
		if (exp.matches("^[+-]?\\d*\\.?\\d*$|\\w+")) {
			this.expressionPart = null;
			this.isSmallest = true;
			this.illegal = false;
			this.returnDecimal = exp.contains(".") || exp.matches("[a-zA-Z]+");
			this.containsBrackets = false;
			return;
		}
		this.isSmallest = false;
		//如果括号总数为奇数，必定不匹配，为非法表达式
		if ((exp.replaceAll("[^(){}\\[\\]]", "").length() & 1) == 1) {
			this.expressionPart = null;
			this.illegal = true;
			this.returnDecimal = false;
			this.containsBrackets = true;
			return;
		}
		//去掉首尾空格，将行首的负号替换为 (0-1)*，将行首的正号去除。（后面会提取括号内的表达式，届时左括号后的正负号会转化为行首的）
		//exp = exp.replaceAll("(^|[+-])-", "(0-1)*").replaceAll("(^|[+-])\\+", "");
		int length = exp.length();
		//利用 map 记录匹配的括号在行内的位置，key 为左括号的位置，value 为对应右括号的位置
		Map<Integer, Integer> matchesBrackets = new HashMap<>();
		//利用堆处理括号匹配问题
		Stack<Character> brackets = new Stack<>();
		Stack<Integer> bracketIndex = new Stack<>();
		boolean containsBrackets = false;
		boolean returnDecimal = exp.contains("e") || exp.contains("pi");
		for (int i = 0; i < length; i++) {
			char present = exp.charAt(i);
			if (isLeftBracket(present)) {
				//将左括号放入堆中
				containsBrackets = true;
				brackets.push(present);
				bracketIndex.push(i);
				//先将左括号的位置加入 map 中，作为键，值在找到对应右括号后再更改
				matchesBrackets.put(i, 0);
				//如果括号前面是字母，则认为是运算符号，返回小数。例如 sqrt()、ln()
				if (returnDecimal || (i != 0 && Character.isLetter(exp.charAt(i - 1)))) {
					returnDecimal = true;
				}
			} else if (!returnDecimal && (present == '.' || present == '/')) {
				//如果有小数或除法，则表达式计算结果应为小数
				returnDecimal = true;
			} else if (isRightBracket(present)) {
				//如果括号不匹配，为非法表达式
				if (brackets.isEmpty() || brackets.pop() != pairBracket(present)) {
					this.expressionPart = null;
					this.illegal = true;
					this.returnDecimal = returnDecimal;
					this.containsBrackets = containsBrackets;
					return;
				}
				//将该右括号的位置与对应左括号位置匹配
				matchesBrackets.replace(bracketIndex.pop(), i);
			}
			
		}
		this.containsBrackets = containsBrackets;
		this.returnDecimal = returnDecimal;
		//读取完整个表达式，若堆中还有括号，则括号不匹配，为非法表达式
		if (!brackets.isEmpty()) {
			this.expressionPart = null;
			this.illegal = true;
			return;
		}
		List<Expression> expressionPart = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		boolean prevIsOperation = false;
		this.operation.put(1, new ArrayList<>());
		this.operation.put(2, new ArrayList<>());
		this.operation.put(3, new ArrayList<>());
		for (int i = 0; i < length; i++) {
			char present = exp.charAt(i);
			if (Character.isDigit(present) || present == '.' || Character.isLetter(present) || (i == 0 && present == '-')) {
				prevIsOperation = false;
				sb.append(present);
			} else if (present == '+' || present == '-' || present == '*' || present == '/' || present == '^') {
				if (prevIsOperation) {
					this.expressionPart = expressionPart;
					this.illegal = true;
					return;
				}
				prevIsOperation = true;
				if (sb.length() != 0) {
					expressionPart.add(new Expression(sb.toString()));
				}
				if (present == '+' || present == '-') {
					this.operation.get(1).add(expressionPart.size());
				} else if (present == '^') {
					this.operation.get(3).add(expressionPart.size());
				} else { // present == '*' || present == '/'
					this.operation.get(2).add(expressionPart.size());
				}
				expressionPart.add(new Expression(String.valueOf(present)));
				sb = new StringBuilder();
			} else if (isLeftBracket(present)) {
				prevIsOperation = false;
				this.bracketsList.add(expressionPart.size());
				expressionPart.add(new Expression(exp.substring(i + 1, matchesBrackets.get(i)), sb.toString()));
				i += (matchesBrackets.get(i) - i);
				sb = new StringBuilder();
			}
			if (i == length - 1) {
				if (sb.length() != 0) {
					expressionPart.add(new Expression(sb.toString()));
				}
			}
		}
		this.expressionPart = expressionPart;
		this.illegal = false;
	}
	
	private boolean isLeftBracket(char c) {
		return c == '(' || c == '[' || c == '{' || c == '（';
	}
	
	private boolean isRightBracket(char c) {
		return c == ')' || c == ']' || c == '}' || c == '）';
	}
	
	private boolean isBracket(char c) {
		return isLeftBracket(c) || isRightBracket(c);
	}
	
	/**
	 * @param bracket 括号的字符
	 * @return 对应的另一侧括号的字符
	 */
	private char pairBracket(char bracket) {
		switch (bracket) {
			case '(':
				return ')';
			case ')':
				return '(';
			case '[':
				return ']';
			case ']':
				return '[';
			case '{':
				return '}';
			case '}':
				return '{';
			case '（':
				return '）';
			case '）':
				return '（';
			default:
				return 0;
		}
	}
	
	public String calculate() {
		if (this.result != null) {
			return this.result;
		}
		if (this.illegal) {
			return "illegal expression";
		}
		if (this.containsBrackets) {
			for (int i : this.bracketsList) {
				this.expressionPart.set(i, new Expression(this.expressionPart.get(i).calculate()));
			}
		}
		List<Expression> expressionPart;
		int shorten = 0;
		if (this.returnDecimal) {
			double result;
			if (!this.isSmallest) {
				expressionPart = new ArrayList<>(this.expressionPart);
				for (int i = 0; i < expressionPart.size(); i += 2) {
					if (expressionPart.get(i).raw.equals("pi")) {
						expressionPart.set(i, new Expression(String.valueOf(Math.PI), expressionPart.get(0).selfOperator));
					} else if (expressionPart.get(i).raw.equals("e")) {
						expressionPart.set(i, new Expression(String.valueOf(Math.E), expressionPart.get(0).selfOperator));
					}
				}
				if (this.operation.get(3) != null) {
					for (int i : this.operation.get(3)) {
						double prevValue = Double.parseDouble(expressionPart.get(i - 1).calculate());
						double nextValue = Double.parseDouble(expressionPart.get(i + 1).calculate());
						expressionPart.set(i - 1 - shorten, new Expression(Double.toString(Math.pow(prevValue, nextValue))));
						expressionPart.remove(i - shorten);
						expressionPart.remove(i - shorten);
						List<Integer> md = this.operation.get(2);
						for (int j = 0; j < md.size(); j++) {
							if (md.get(j) > i - shorten) {
								md.set(j, md.get(j) - 2);
							}
						}
						shorten += 2;
					}
				}
				shorten = 0;
				if (this.operation.get(2) != null) {
					for (int i : this.operation.get(2)) {
						double prevValue = Double.parseDouble(expressionPart.get(i - shorten - 1).calculate());
						double nextValue = Double.parseDouble(expressionPart.get(i - shorten + 1).calculate());
						if (expressionPart.get(i - shorten).raw.equals("*")) {
							expressionPart.set(i - 1 - shorten, new Expression(Double.toString(prevValue * nextValue)));
						} else {
							expressionPart.set(i - 1 - shorten, new Expression(Double.toString(prevValue / nextValue)));
						}
						expressionPart.remove(i - shorten);
						expressionPart.remove(i - shorten);
						shorten += 2;
					}
				}
				result = Double.parseDouble(expressionPart.get(0).raw);
				for (int i = 2; i < expressionPart.size(); i += 2) {
					if (expressionPart.get(i) != null) {
						if (expressionPart.get(i - 1).raw.equals("+")) {
							result += Double.parseDouble(expressionPart.get(i).raw);
						} else {
							result -= Double.parseDouble(expressionPart.get(i).raw);
						}
					}
				}
			} else {
				switch (this.raw) {
					case "pi":
						result = Math.PI;
						break;
					case "e":
						result = Math.E;
						break;
					default:
						if (this.raw.matches("[+-]?\\d+\\.?\\d*")) {
							result = Double.parseDouble(this.raw);
						} else {
							result = 1;
						}
				}
			}
			if (!this.selfOperator.equals("")) {
				result = selfOperate(result, this.selfOperator);
			}
			this.result = String.valueOf(result);
		} else {
			BigInteger result;
			if (!this.isSmallest) {
				expressionPart = new ArrayList<>(this.expressionPart);
				if (this.operation.get(3) != null) {
					for (int i : this.operation.get(3)) {
						BigInteger prevValue = new BigInteger(expressionPart.get(i - 1 - shorten).calculate());
						int nextValue = Integer.parseInt(expressionPart.get(i + 1 - shorten).calculate());
						expressionPart.set(i - 1 - shorten, new Expression(prevValue.pow(nextValue).toString()));
						expressionPart.remove(i - shorten);
						expressionPart.remove(i - shorten);
						List<Integer> md = this.operation.get(2);
						for (int j = 0; j < md.size(); j++) {
							if (md.get(j) > i - shorten) {
								md.set(j, md.get(j) - 2);
							}
						}
						shorten += 2;
					}
				}
				shorten = 0;
				if (this.operation.get(2) != null) {
					for (int i : this.operation.get(2)) {
						BigInteger prevValue = new BigInteger(expressionPart.get(i - shorten - 1).calculate());
						BigInteger nextValue = new BigInteger(expressionPart.get(i - shorten + 1).calculate());
						if (expressionPart.get(i - shorten).raw.equals("*")) {
							expressionPart.set(i - 1 - shorten, new Expression(prevValue.multiply(nextValue).toString()));
						} else {
							expressionPart.set(i - 1 - shorten, new Expression(prevValue.divide(nextValue).toString()));
						}
						expressionPart.remove(i - shorten);
						expressionPart.remove(i - shorten);
						shorten += 2;
					}
				}
				result = new BigInteger(expressionPart.get(0).raw);
				for (int i = 2; i < expressionPart.size(); i += 2) {
					if (expressionPart.get(i) != null) {
						if (expressionPart.get(i - 1).raw.equals("+")) {
							result = result.add(new BigInteger(expressionPart.get(i).raw));
						} else {
							result = result.add(new BigInteger(expressionPart.get(i).raw).negate());
						}
					}
				}
			} else {
				if (this.raw.matches("\\d+")) {
					result = new BigInteger(this.raw);
				} else {
					result = BigInteger.ONE;
				}
			}
			this.result = String.valueOf(result);
		}
		return this.result;
	}
	
	private double selfOperate(double a, String operator) {
		switch (operator) {
			case "ln":
				return Math.log(a);
			case "lg":
				return Math.log10(a);
			case "sin":
				return Math.sin(a);
			case "cos":
				return Math.cos(a);
			case "tan":
				return Math.tan(a);
			case "cot":
				return 1 / Math.tan(a);
			case "sec":
				return 1 / Math.cos(a);
			case "csc":
				return 1 / Math.sin(a);
			case "sqrt":
				return Math.sqrt(a);
			case "sinh":
				return Math.sinh(a);
			case "cosh":
				return Math.cosh(a);
			case "tanh":
				return Math.tanh(a);
			case "abs":
				return Math.abs(a);
			case "arcsin":
				return Math.asin(a);
			case "arccos":
				return Math.acos(a);
			case "arctan":
				return Math.atan(a);
			default:
				return a;
		}
	}
	
	@Override
	public String toString() {
		if (this.illegal) {
			return this.raw;
		}
		if (this.isSmallest) {
			if (this.selfOperator.equals("")) {
				if (this.raw.matches("-.+")) {
					return "(" + this.raw + ")";
				} else {
					return this.raw;
				}
			} else {
				return this.selfOperator + "(" + this.raw + ")";
			}
		}
		StringBuilder sb = new StringBuilder();
		String format = !this.selfOperator.equals("") || this.subExpression ? this.selfOperator + "(%s)" : "%s";
		for (Expression part : this.expressionPart) {
			sb.append(part.toString());
		}
		return format.replace("%s", sb.toString());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Expression)) {
			return false;
		}
		return this.toString().equals(obj.toString());
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
}