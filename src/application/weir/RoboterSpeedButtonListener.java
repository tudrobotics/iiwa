package application.weir;

import org.tud.kuka.socket.Robot2FogCommandMessage;
import org.tud.kuka.socket.Robot2FogCommandMessage.FogCommand;
import org.tud.kuka.socket.SocketServer;

import com.kuka.roboticsAPI.uiModel.userKeys.IUserKey;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyListener;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyEvent;

public class RoboterSpeedButtonListener implements IUserKeyListener {

	private SocketServer server;
	
	public RoboterSpeedButtonListener(SocketServer server) {
		this.server = server;
	}
	@Override
	public void onKeyEvent(IUserKey key, UserKeyEvent event) {
		if(event.equals(UserKeyEvent.FirstKeyUp)) {
			Robot2FogCommandMessage m = new Robot2FogCommandMessage();
			m.setCommand(FogCommand.SPEEDUP);
			server.send(m);
		} else if (event.equals(UserKeyEvent.SecondKeyUp)){
			Robot2FogCommandMessage m = new Robot2FogCommandMessage();
			m.setCommand(FogCommand.SPEEDDOWN);
			server.send(m);
		}

	}

}
