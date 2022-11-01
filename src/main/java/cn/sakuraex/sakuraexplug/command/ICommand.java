package cn.sakuraex.sakuraexplug.command;

public interface ICommand {
	/**
	 * 在这里写下你的指令名称。
	 * <p>
	 * 例如：
	 * </p>
	 * {@code /example}
	 * <p>
	 * 写法：
	 * </p>
	 * {@code return "/example";}
	 *
	 * @return 你的指令名称
	 */
	String getName();
	
	void react();
	
	void detailedHelp();
	
	String usageHelp();
}
