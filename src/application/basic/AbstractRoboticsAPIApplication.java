package application.basic;

import java.util.Collection;

import org.tud.kuka.ioAccess.SmartFlexFellowIOGroup;
import org.tud.kuka.ioAccess.SmartMediaFlangeIOGroup;
import org.tud.kuka.socket.SocketServer;
import org.tud.kuka.tool.AdapterPlate;
import org.tud.kuka.tool.Gripper;
import org.tud.kuka.tool.MetalJaws;
import org.tud.kuka.tool.PlasticJaws;
import org.tud.kuka.tool.RubberEgg;
import org.tud.log.Logger;
import org.tud.log.Logger.LogLevel;
import org.tud.schunk.gripper.SchunkGripper;

import backgroundTask.CartisianPositionNotificationBackgroundTask;

import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.conditionModel.ConditionObserver;
import com.kuka.roboticsAPI.conditionModel.ICondition;
import com.kuka.roboticsAPI.conditionModel.IFallingEdgeListener;
import com.kuka.roboticsAPI.conditionModel.IRisingEdgeListener;
import com.kuka.roboticsAPI.conditionModel.NotificationType;
import com.kuka.roboticsAPI.controllerModel.Controller;
import com.kuka.roboticsAPI.controllerModel.StatePortData;
import com.kuka.roboticsAPI.controllerModel.sunrise.ISunriseControllerStateListener;
import com.kuka.roboticsAPI.controllerModel.sunrise.SunriseSafetyState;
import com.kuka.roboticsAPI.deviceModel.Device;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.deviceModel.OperationMode;
import com.kuka.roboticsAPI.geometricModel.ObjectFrame;
import com.kuka.roboticsAPI.motionModel.SplineMotion;
import com.kuka.task.ITask;
import com.kuka.task.ITaskInstance;
import com.kuka.task.ITaskInstanceStateListener;
import com.kuka.task.TaskState;

public abstract class AbstractRoboticsAPIApplication extends RoboticsAPIApplication {

	protected Controller kukaController;
	protected LBR lbr_iiwa;
	protected SmartMediaFlangeIOGroup mediaFlanschIO;
	protected SmartFlexFellowIOGroup flexFellowIoGroup;
	
	protected AdapterPlate adapterPlate;
	protected Gripper gripperTool;
	protected SchunkGripper gripper;
	protected PlasticJaws plasticJaws;
	protected MetalJaws metalJaws;
	protected RubberEgg rubberEgg;
	
	
	private double speedRel = 1.0;
	private boolean ready = false;
	protected SocketServer server = SocketServer.getInstance();
	
	@Override
	public void initialize() {
		getLogger().info("Starting..."+this.getClass().getSimpleName());
		getLogger().info(server.getServerStatusMessage());
		Logger.setLevel(LogLevel.INFO);
		
		kukaController = (Controller) getContext().getControllers().toArray()[0];
        lbr_iiwa = (LBR) kukaController.getDevices().toArray()[0];
        
        mediaFlanschIO = new SmartMediaFlangeIOGroup(kukaController);
        
		flexFellowIoGroup = new SmartFlexFellowIOGroup(kukaController);
		
		adapterPlate = getApplicationData().createFromTemplate("AdapterPlate");
		adapterPlate.attachTo(lbr_iiwa.getFlange());
		gripperTool = getApplicationData().createFromTemplate("Gripper");
		gripperTool.attachTo(adapterPlate.getDefaultMotionFrame());
		rubberEgg = getApplicationData().createFromTemplate("RubberEgg");
		//rubberEgg.attachTo(lbr_iiwa.getFlange())
		gripper = new SchunkGripper();
		
		plasticJaws = getApplicationData().createFromTemplate("PlasticJaws");
		metalJaws = getApplicationData().createFromTemplate("MetalJaws");
		
		if(!lbr_iiwa.isInitialized()) {
			getLogger().error("roboter not initialized ");
			lbr_iiwa.initialize();
		}
		if(!lbr_iiwa.isMastered()) {
			getLogger().error("roboter is not mastered");
		}
		if(!lbr_iiwa.isReadyToMove()) {
			getLogger().error("roboter is ready");
		}
		enableControllerObserver();
		observerBackgroundTask();
		ready = true;
		flexFellowIoGroup.setSignalLightGreen(true);
		
	}
	private void observerBackgroundTask() {
		ITask t = getTaskManager().getTask(CartisianPositionNotificationBackgroundTask.class);
		if(t == null) return;
		Collection<ITaskInstance> in = t.getInstances();
		for(ITaskInstance i:in) {
			getLogger().info("observing BackgroundTask");
			i.addStateListener(new ITaskInstanceStateListener() {
				@Override
				public void onStateChanged(ITaskInstance instance, TaskState oldState,
						TaskState newState) {
					getLogger().info("backgroundTask "+instance+" changed: "+newState.name() );
					
				}
			});
		}
	}
	@Override
	public void dispose() {
		flexFellowIoGroup.setSignalLightRed(true);
	}
	
	protected void setGlobalSpeedRel(double speedRel) {
		this.speedRel = speedRel;
	}
	protected double getGlobalSpeedRel(){
		return speedRel;
	}
	
	public boolean isReady() {
		return ready;
		
	}
	protected void setReady(boolean ready) {
		this.ready = ready;
	}
	protected OperationMode getOperationMode() {
		return lbr_iiwa.getSafetyState().getOperationMode();
	}
	
 	private void enableControllerObserver() {
		kukaController.addControllerListener(new
				ISunriseControllerStateListener() {

					@Override
					public void onIsReadyToMoveChanged(Device device,
							boolean isReadyToMove) {
						ready = isReadyToMove;
						if(isReadyToMove) {
							getLogger().info(device.getName()+" is ready");
						} else {
							getLogger().info(device.getName()+" is not ready");
						}
					}
					@Override
					public void onShutdown(Controller controller) {
						ready = false;
						
					}

					@Override
					public void statePortChanged(Controller controller,
							StatePortData statePort) {
						// TODO Automatisch generierter Methodenstub
						
					}

					@Override
					public void onConnectionLost(Controller controller) {
						controller.initialize();
						getLogger().error("lost connection to controller");
						
					}

					@Override
					public void onSafetyStateChanged(Device device,
							SunriseSafetyState safetyState) {
						//getLogger().info("safety state changed"+safetyState.toString());
						
					}
				
		});
	}
	
	protected void move(ObjectFrame frame, SplineMotion<?> motion) {
		frame.move(adaptMotion(motion));
	}
	protected void moveAsync(ObjectFrame frame, SplineMotion<?> motion) {
		frame.moveAsync(adaptMotion(motion));
	}
	protected SplineMotion<?> adaptMotion(SplineMotion<?> motion) {
		if(motion.getJointVelocityRel() > getGlobalSpeedRel()) {
			motion.setJointVelocityRel(getGlobalSpeedRel());
		}
		return motion;
	}
	protected ConditionObserver createConditionObserver(ICondition condition, IRisingEdgeListener listener) {
		return getObserverManager().createConditionObserver(condition, NotificationType.EdgesOnly, listener);
	}
	protected ConditionObserver createConditionObserver(ICondition condition, IFallingEdgeListener listener) {
		return getObserverManager().createConditionObserver(condition, NotificationType.EdgesOnly, listener);
	}
	
}
