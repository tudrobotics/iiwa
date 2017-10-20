package backgroundTask;

import java.util.concurrent.TimeUnit;

import org.tud.kuka.socket.AbstractRobotResponseMessage.RoboterStatus;
import org.tud.kuka.socket.RobotResponseCartesianMessage;

import sun.misc.FloatingDecimal;

import com.kuka.roboticsAPI.applicationModel.tasks.CycleBehavior;
import com.kuka.roboticsAPI.geometricModel.Frame;

public class CartisianPositionNotificationBackgroundTask extends AbstractBackgroundTask {

	
	private Frame currentPosition;
	private RobotResponseCartesianMessage message;
	
	private static final int priority = 2;
	@Override
	public void initialize() {
		super.initialize();
		
		initializeCyclic(priority * 100, 250, TimeUnit.MILLISECONDS, CycleBehavior.BestEffort);
	}
	@Override
	public void runCyclic() {
		currentPosition = lbr_iiwa.getCurrentCartesianPosition(gripperTool.getDefaultMotionFrame());
		message = new RobotResponseCartesianMessage(currentPosition);
		message.setStatus(RoboterStatus.INFO);
		message.setFingerOpenedRelative(gripper.calculateRelativeWidth(gripper.getWidth()));
		message.setGripperForce(gripper.getForce());
		server.send(message);
	}
}
