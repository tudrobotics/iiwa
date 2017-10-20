package backgroundTask;

import java.util.concurrent.TimeUnit;

import org.tud.kuka.socket.AbstractRobotResponseMessage.RoboterStatus;
import org.tud.kuka.socket.RobotResponseJointMessage;

import sun.misc.FloatingDecimal;

import com.kuka.roboticsAPI.applicationModel.tasks.CycleBehavior;

public class JointPositionNotificationBackgroundTask extends AbstractBackgroundTask {
	
	private RobotResponseJointMessage message;
	
	private static final  int priority = 1;
	
	@Override
	public void initialize() {
		super.initialize();
		
		initializeCyclic(priority *100, 250, TimeUnit.MILLISECONDS, CycleBehavior.BestEffort);

	}
	@Override
	public void runCyclic() {
		message = new RobotResponseJointMessage(lbr_iiwa.getCurrentJointPosition());
		message.setStatus(RoboterStatus.INFO);
		message.setFingerOpenedRelative(new FloatingDecimal(new Float(gripper.getWidth()).floatValue()/(gripper.getSystemLimits().getStroke())).doubleValue());
		message.setGripperForce(gripper.getForce());
		server.send(message);
	}
	
}
