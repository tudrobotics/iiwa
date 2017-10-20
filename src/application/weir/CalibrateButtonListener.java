package application.weir;

import org.tud.kuka.socket.Robot2FogCommandMessage;
import org.tud.kuka.socket.Robot2FogCommandMessage.FogCommand;
import org.tud.kuka.socket.SocketServer;

import com.kuka.roboticsAPI.uiModel.userKeys.IUserKey;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyListener;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyEvent;

public class CalibrateButtonListener implements IUserKeyListener {

	private SocketServer server;
	
	public CalibrateButtonListener(SocketServer server) {
		this.server = server;
	}
	@Override
	public void onKeyEvent(IUserKey key, UserKeyEvent event) {
		if(event.equals(UserKeyEvent.KeyUp)) {
			Robot2FogCommandMessage m = new Robot2FogCommandMessage();
			m.setCommand(FogCommand.CALIBRATE);
			server.send(m);
			System.out.println("calibrate pressed");
		}

	}

}
