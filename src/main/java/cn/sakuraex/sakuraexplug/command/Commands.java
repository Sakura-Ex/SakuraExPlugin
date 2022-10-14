package cn.sakuraex.sakuraexplug.command;

public enum Commands {
	IMG(ImgCommand.INSTANCE);
	
	private final ICommand command;
	private final String name;
	
	Commands(ICommand command) {
		this.command = command;
		this.name = this.command.getName();
	}
	
	public ICommand get() {
		return this.command;
	}
	
	public String getName() {
		return this.name;
	}
}
