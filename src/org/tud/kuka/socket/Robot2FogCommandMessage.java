package org.tud.kuka.socket;

public class Robot2FogCommandMessage extends SocketMessage{

	public enum FogCommand {PAUSE, UNPAUSE, CALIBRATE, TOGGLEPAUSE, SPEEDUP, SPEEDDOWN }
	private FogCommand command;

	public FogCommand getCommand() {
		return command;
	}

	public void setCommand(FogCommand command) {
		this.command = command;
	}
	
	
	
}
