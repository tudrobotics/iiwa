package org.tud.kuka.socket;

public interface SocketListerner {

	public enum SocketServerNotification{NEW_CONNECTION, CONNECTION_CLOSED, RUNNING, CLOSED}
	
	void notifyServerStatus(SocketServerNotification notification);
	
	void handleRobotCommand(AbstractRobotCommandMessage message);

	void handleLedCommand(LedCommandMessage message);
}
