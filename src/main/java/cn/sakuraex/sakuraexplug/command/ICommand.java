package cn.sakuraex.sakuraexplug.command;

public interface ICommand {
	/**
	 * Write your command name here.
	 * <p>
	 * For example
	 * </p>
	 * {@code /example}
	 *
	 * @return your command name
	 */
	String getName();
	void react();
	void detailedHelp();
	String usageHelp();
}
