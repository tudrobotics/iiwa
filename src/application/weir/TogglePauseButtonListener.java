package application.weir;

import org.tud.kuka.socket.Robot2FogCommandMessage;
import org.tud.kuka.socket.Robot2FogCommandMessage.FogCommand;
import org.tud.kuka.socket.SocketServer;

import com.kuka.roboticsAPI.uiModel.userKeys.IUserKey;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyListener;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyEvent;

public class TogglePauseButtonListener implements IUserKeyListener {

	private SocketServer server;
	
	public TogglePauseButtonListener(SocketServer server) {
		this.server = server;
	}
	@Override
	public void onKeyEvent(IUserKey arg0, UserKeyEvent arg1) {
		if(arg1.equals(UserKeyEvent.KeyUp)) {
			Robot2FogCommandMessage m = new Robot2FogCommandMessage();
			m.setCommand(FogCommand.TOGGLEPAUSE);
			server.send(m);
		}

	}

}
