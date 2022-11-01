package cn.sakuraex.sakuraexplug.command;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Arrays;
import java.util.HashMap;

public abstract class ComplexCommand<T extends Contact> extends AbstractCommand<T> {
	
	private final String[] argNames;
	private final String[] args;
	private final int argLength;
	private final HashMap<String, String> argMap = new HashMap<>();
	private boolean argCanOmit = false;
	
	protected ComplexCommand(int argLength) {
		this.argNames = initArgName(argLength);
		this.argLength = argLength;
		this.args = new String[argLength];
		for (int i = 0; i < argLength; i++) {
			this.args[i] = "";
		}
	}
	
	protected ComplexCommand(String... argNames) {
		this.argNames = argNames;
		this.argLength = argNames.length;
		this.args = new String[this.argLength];
		for (int i = 0; i < this.argLength; i++) {
			this.args[i] = "";
		}
	}
	
	public ComplexCommand(String rawMessage, T contact, User user, OnlineMessageSource source) {
		super(rawMessage, contact, user, source);
		this.argNames = initArgName(getRawMessage().length - 1);
		this.argLength = this.argNames.length;
		this.args = Arrays.copyOfRange(getRawMessage(), 1, this.argLength + 1);
		for (int i = 0; i < this.argNames.length; i++) {
			this.argMap.put(this.argNames[i], this.args[i]);
		}
	}
	
	protected ComplexCommand(String rawMessage, T contact, User user, OnlineMessageSource source, String... argNames) {
		super(rawMessage, contact, user, source);
		this.argNames = argNames;
		this.argLength = getRawMessage().length - 1;
		this.args = Arrays.copyOfRange(getRawMessage(), 1, this.argLength + 1);
		for (int i = 0; i < this.args.length; i++) {
			if (i < argNames.length) {
				this.argMap.put(argNames[i], this.args[i]);
			} else {
				this.argMap.put("arg" + (i + 1), this.args[i]);
			}
		}
	}
	
	protected void argCanOmit() {
		this.argCanOmit = true;
	}
	
	private String[] initArgName(int argLength) {
		String[] argNames = new String[argLength];
		for (int i = 0; i < argLength; i++) {
			argNames[i] = "arg" + (i + 1);
		}
		return argNames;
	}
	
	protected String[] getArgNames() {
		return this.argNames;
	}
	
	protected String getArgName(int index) {
		return this.argNames[Math.max(Math.min(0, index), this.argLength - 1)];
	}
	
	protected boolean isArgCanOmit() {
		return this.argCanOmit;
	}
	
	protected int getArgLength() {
		return this.argLength;
	}
	
	protected String getArg(int index) {
		return this.args[Math.min(Math.max(0, index), this.argLength - 1)];
	}
	
	protected String getArg(String argName) {
		return this.argMap.get(argName);
	}
	
	@Override
	public String usageHelp() {
		StringBuilder stringBuilder = new StringBuilder(getName());
		for (int i = 0; i < this.argNames.length; i++) {
			boolean flag = i == this.argNames.length - 1 && this.argCanOmit;
			stringBuilder.append(flag ? " [" : " <").append(this.argNames[i]).append(flag ? "]" : ">");
		}
		return stringBuilder.toString();
	}
}