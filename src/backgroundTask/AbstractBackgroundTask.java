package backgroundTask;

import org.tud.kuka.ioAccess.SmartFlexFellowIOGroup;
import org.tud.kuka.ioAccess.SmartMediaFlangeIOGroup;
import org.tud.kuka.socket.SocketServer;
import org.tud.kuka.tool.AdapterPlate;
import org.tud.kuka.tool.Gripper;
import org.tud.schunk.gripper.SchunkGripper;
import org.tud.schunk.gripper.SchunkGripperServerListener;

import com.kuka.roboticsAPI.applicationModel.tasks.RoboticsAPICyclicBackgroundTask;
import com.kuka.roboticsAPI.controllerModel.Controller;
import com.kuka.roboticsAPI.deviceModel.LBR;

public abstract class AbstractBackgroundTask extends RoboticsAPICyclicBackgroundTask implements SchunkGripperServerListener {

	protected Controller kukaController;
	protected LBR lbr_iiwa;
	protected SmartMediaFlangeIOGroup mediaFlanschIO;
	protected SmartFlexFellowIOGroup flexFellowIoGroup;
	
	protected AdapterPlate adapterPlate;
	protected Gripper gripperTool;
	protected SchunkGripper gripper;
	protected SocketServer server = SocketServer.getInstance();

	
	@Override
	public void initialize() {
		kukaController = (Controller) getContext().getControllers().toArray()[0];
        lbr_iiwa = (LBR) kukaController.getDevices().toArray()[0];
     
        mediaFlanschIO = new SmartMediaFlangeIOGroup(kukaController);
		flexFellowIoGroup = new SmartFlexFellowIOGroup(kukaController);
		
		adapterPlate = getApplicationData().createFromTemplate("AdapterPlate");
		gripperTool = getApplicationData().createFromTemplate("Gripper");
		adapterPlate.attachTo(lbr_iiwa.getFlange());
		gripperTool.attachTo(adapterPlate.getDefaultMotionFrame());
		
		gripper = new SchunkGripper();
		flexFellowIoGroup.turnOffSignalLights();
		flexFellowIoGroup.setSignalLightBlue(true);
		
		gripper.addServerStateListener(this);
		gripper.connect();

	}
	
	
	@Override
	public void dispose() {
		gripper.disconnect();
		server.stopServer();
		flexFellowIoGroup.setSignalLightBlue(true);
		flexFellowIoGroup.setSignalLightGreen(true);
		flexFellowIoGroup.setSignalLightRed(true);
		flexFellowIoGroup.setSignalLightYellow(true);
	}
	
	protected void resetGripper() throws InterruptedException {
		if(mediaFlanschIO == null) return;
		mediaFlanschIO.setSwitchOffX3Voltage(true);
			Thread.sleep(100);
		mediaFlanschIO.setSwitchOffX3Voltage(false);
			Thread.sleep(5000);
	}
	
	@Override
	public void notify(SchunkGripperServerState state) {
		getLogger().info("schunkGripperServer State changed: "+state.toString());
		if(state.equals(SchunkGripperServerState.ERROR)) {
			try {
				resetGripper();
			} catch (InterruptedException e) {
			}
		}
		
	}
}
