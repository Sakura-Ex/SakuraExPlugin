package cn.sakuraex.sakuraexplug.command;

public interface ICommand {
	String getName();
	void react();
	void detailedHelp();
	String usageHelp();
}
