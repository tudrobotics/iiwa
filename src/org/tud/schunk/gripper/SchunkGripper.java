package org.tud.schunk.gripper;

import java.util.LinkedList;
import java.util.List;

import org.tud.kuka.util.ByteUtil;
import org.tud.log.Logger;
import org.tud.schunk.gripper.SchunkGripperCommand.AutomaticUpdate;
import org.tud.schunk.gripper.SchunkGripperCommand.HomeDirection;
import org.tud.schunk.gripper.SchunkGripperCommand.MovementType;
import org.tud.schunk.gripper.SchunkGripperCommand.SensitiveUpdate;
import org.tud.schunk.gripper.SchunkGripperCommand.StopType;
import org.tud.schunk.gripper.listener.SchunkGripperAcknowledgementListener;
import org.tud.schunk.gripper.listener.SchunkGripperForceListener;
import org.tud.schunk.gripper.listener.SchunkGripperGraspingStateListener;
import org.tud.schunk.gripper.listener.SchunkGripperGraspingStateListener.GraspingState;
import org.tud.schunk.gripper.listener.SchunkGripperSpeedListener;
import org.tud.schunk.gripper.listener.SchunkGripperSystemStateListener;
import org.tud.schunk.gripper.listener.SchunkGripperSystemStateListener.SystemState;
import org.tud.schunk.gripper.listener.SchunkGripperWidthListener;


public class SchunkGripper implements SchunkGripperListener {

	private static final int autoUpdateTimeMs = 250;
	private SchunkSocket socket;
	private SchunkGripperCommandSet commands;
	private List<SchunkGripperForceListener> forceListener;
	private List<SchunkGripperGraspingStateListener> graspingListener;
	private List<SchunkGripperSpeedListener> speedListener;
	private List<SchunkGripperSystemStateListener> systemListener;
	private List<SchunkGripperWidthListener> widthListener;
	private List<SchunkGripperAcknowledgementListener> ackListener;
	
	private float force;
	private GraspingState graspingState;
	private float speed;
	private List<SystemState> systemStates;
	private float width;
	private int temperature;

	private SchunkGripperSystemInformation information;
	private SchunkGripperSystemLimits systemLimits;

	public SchunkGripper(){
		socket = SchunkSocket.getInstance();
		commands = new SchunkGripperCommandSet();
		forceListener = new LinkedList<SchunkGripperForceListener>();
		graspingListener = new LinkedList<SchunkGripperGraspingStateListener>();
		speedListener = new LinkedList<SchunkGripperSpeedListener>();
		systemListener = new LinkedList<SchunkGripperSystemStateListener>();
		widthListener = new LinkedList<SchunkGripperWidthListener>();
		ackListener = new LinkedList<SchunkGripperAcknowledgementListener>();
		
		information = new SchunkGripperSystemInformation();
		systemLimits = new SchunkGripperSystemLimits();

		graspingState = GraspingState.IDLE;
		systemStates = new LinkedList<SystemState>();
		socket.addListener(this);
		
	}

	/* -------------------- Listeners -------------------------- */
	public void addForceListener(SchunkGripperForceListener listener) {
		forceListener.add(listener);
	}

	private void enableForceListener() {
		Logger.trace("enableForceListener");
		SchunkGripperAcknowledge ack = socket.sendCommandSync(commands
				.getForceCommand(SensitiveUpdate.UpdateOnChange,
						AutomaticUpdate.Enabled, autoUpdateTimeMs));

		if (ack != null && ack.isSuccess()) {
			Logger.info("enable ForceListener");
		} else {
			Logger.error("enable ForceListener");
		}
		
	}

	public void removeForceListener(SchunkGripperForceListener listener) {
		forceListener.remove(listener);
	}

	public void addGraspingListener(SchunkGripperGraspingStateListener listener) {
		graspingListener.add(listener);
	}

	private void enableGraspingListener() {
		Logger.trace("enableGraspingListener");
		SchunkGripperAcknowledge ack = socket.sendCommandSync(commands
				.getGraspingStateCommand(SensitiveUpdate.UpdateOnChange,
						AutomaticUpdate.Enabled, autoUpdateTimeMs));
		if (ack != null && ack.isSuccess()) {
			Logger.info("enable GraspingListener");
		} else {
			Logger.error("enable GraspingListener");
		}
	}

	public void removeGraspingListener(
			SchunkGripperGraspingStateListener listener) {
		graspingListener.remove(listener);
	}

	public void addSpeedListener(SchunkGripperSpeedListener listener) {
		speedListener.add(listener);
	}

	private void enableSpeedListener() {
		Logger.trace("enableSpeedListener");
		SchunkGripperAcknowledge ack = socket.sendCommandSync(commands
				.getSpeedCommand(SensitiveUpdate.UpdateOnChange,
						AutomaticUpdate.Enabled, autoUpdateTimeMs));
		if (ack != null && ack.isSuccess()) {
			Logger.info("enable SpeedListener");
		} else {
			Logger.error("enable SpeedListener");
		}
	}

	public void removeSpeedListener(SchunkGripperForceListener listener) {
		speedListener.remove(listener);
	}

	public void addSystemStateListener(SchunkGripperSystemStateListener listener) {
		systemListener.add(listener);
	}

	private void enableSystemListener() {
		Logger.trace("enableSystemListener");
		SchunkGripperAcknowledge ack = socket.sendCommandSync(commands
				.getSystemStateCommand(SensitiveUpdate.UpdateOnChange,
						AutomaticUpdate.Enabled, autoUpdateTimeMs));
		if (ack != null && ack.isSuccess()) {
			Logger.info("enable SystemListener");
		} else {
			Logger.error("enable SystemListener");
		}
	}

	public void removeSystemStateListener(
			SchunkGripperSystemStateListener listener) {
		systemListener.remove(listener);
	}

	public void addWidthListener(SchunkGripperWidthListener listener) {
		widthListener.add(listener);
	}

	private void enableWidthListener() {
		SchunkGripperAcknowledge ack = socket.sendCommandSync(commands
				.getOpeningWidthCommand(SensitiveUpdate.UpdateOnChange,
						AutomaticUpdate.Enabled, autoUpdateTimeMs));
		if (ack != null && ack.isSuccess()) {
			Logger.info("enable WidthListener");
		} else {
			Logger.error("enable WidthListener");
		}
	}

	public void removeWidthListener(SchunkGripperWidthListener listener) {
		widthListener.remove(listener);
	}

	public void addServerStateListener( SchunkGripperServerListener listener) {
		socket.addServerListener(listener);
	}
	public void removeServerStateListener(SchunkGripperServerListener listener) {
		socket.removeServerListener(listener);
	}
	
	public void addAckListener(SchunkGripperAcknowledgementListener listener) {
		ackListener.add(listener);
	}
	public void removeAckListener(SchunkGripperAcknowledgementListener listener) {
		ackListener.remove(listener);
	}
	/* ---------------------- Ende Listeners ------------------------ */

	public float getForce() {
		return force;
	}

	public GraspingState getGraspingState() {
		return graspingState;
	}

	public float getSpeed() {
		return speed;
	}

	public List<SystemState> getSystemState() {
		return systemStates;
	}

	public float getWidth() {
		return width;
	}
	public SchunkGripperSystemLimits getSystemLimits() {
		return systemLimits;
	}
	public int getTemperature() {
		SchunkGripperAcknowledge ack = sendCommandSync(commands.getTemperatureCommand());
		if (ack != null && ack.isSuccess()) {
			temperature = ByteUtil.array2Int(ack.getParameter());
		}
		return temperature;
	}

	public boolean connect() {
		Logger.info("connecting SchunkGripper...");
		socket.connect();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		boolean success = testConnection();
		if(!success) return false;
		switchToOperatingMode();
		moveToHomePosition();
		clearLimits();
		setDefaultForceLimit();
		loadSystemLimits();
		loadSystemState();
		//enableSystemListener();
		enableForceListener();
		//enableGraspingListener();
		//enableSpeedListener();
		enableWidthListener();
		
		return true;
	}


	private boolean testConnection() {
		Logger.trace("testConnection");
		int[] payLoad = new int[] { 0, 1, 2, 3 };
		SchunkGripperAcknowledge ack = sendCommandSync(commands.loopCommand(payLoad));
		if (ack == null)
			return false;
		for (int i = 0; i < payLoad.length; i++) {
			if (payLoad[i] != ack.getParameter()[i]) {
				return false;
			}
		}
		return true;
	}

	private boolean switchToOperatingMode() {
		Logger.trace("switchToOpertingMode");
		SchunkGripperAcknowledge ack = sendCommandSync(commands.acknowledgeingFastStopOrFaultCommand());
		if (ack == null)
			return false;
		return ack.isSuccess();
	}

	private boolean loadSystemState() {
		Logger.trace("loadSystemState");
		SchunkGripperAcknowledge ack = sendCommandSync(commands.getSystemStateCommand(SensitiveUpdate.UpdateAlways,
						AutomaticUpdate.Disabled, 1));
		if (ack == null || !ack.isSuccess())
			return false;
		notify(ack);
		return ack.isSuccess();

	}

	private void loadSystemLimits() {
		Logger.trace("loadSystemLimits");
		SchunkGripperAcknowledge ack = sendCommandSync(commands.getSystemLimitsCommand());
		if (ack != null && ack.isSuccess()) {
			notify(ack);
		} else {
			Logger.warn("loading System Limits using defaults");
		}
	}
	
	private void setDefaultForceLimit() {
		Logger.trace("setDefaultForceLimit");
		sendCommandSync(commands.setForceLimitCommand(80));
	}
	private boolean clearLimits() {
		Logger.trace("clearLimits");
		SchunkGripperAcknowledge ack = sendCommandSync(commands.clearSoftLimitsCommand());
		if (ack == null)
			return false;
		return ack.isSuccess();
	}

	private boolean moveToHomePosition() {
		Logger.trace("moveToHomePosition");
		SchunkGripperAcknowledge ack;
		ack = sendCommandSync(commands.homePositionCommand(HomeDirection.PositiveMovementDirection));
		//ack = sendCommandSync(commands.homePositionCommand(HomeDirection.NegativeMovementDirection));
		// ack =
		// sendCommand(commands.homePositionCommand(HomeDirection.DefaultSystemValue));
		if (ack == null)
			return false;
		return ack.isSuccess();
	}
	
	/*
	 * ----------------------------------- Funktionen
	 * ----------------------------------------
	 */
	public boolean openSync() {
		return openSync(systemLimits.getStroke(), systemLimits.getMaxSpeed() - 2);
	}

	public boolean openSync(float openWidth) {
		return openSync(openWidth, systemLimits.getMaxSpeed() - 2);
	}
	public boolean openSync(float openWidth, float speed) {
		SchunkGripperAcknowledge ack;
		ack = sendCommandSync(commands.stopCommand());
		ack = sendCommandSync(commands.releasePartCommand(validateWidth(openWidth),
				validateSpeed(speed)));
		if (ack == null)
			return false;
		return ack.isSuccess();

	}
	public boolean openSync(double openWidthRelative) {
		return openSync(openWidthRelative, systemLimits.getMaxSpeed() - 2);
	}
	public boolean openSync(double openWidthRelative, float speed) {
		return openSync(calculateAbsoluteWidth(openWidthRelative), speed);
	}
	// asynchrones open
	public void open() {
		open(systemLimits.getStroke(), systemLimits.getMaxSpeed() - 2);
	}

	public void open(float openWidth) {
		open(openWidth, systemLimits.getMaxSpeed() - 2);
	}
	public void open(float openWidth, float speed) {
		sendCommand(commands.releasePartCommand(validateWidth(openWidth),
				validateSpeed(speed)));
	}
	public void open(double openWidthRelative) {
		open(openWidthRelative, systemLimits.getMaxSpeed() - 2);
	}
	public void open(double openWidthRelative, float speed) {
		open(calculateAbsoluteWidth(openWidthRelative), speed);
	}
	/**
	 * 
	 * @param width
	 *            : Nominal width of the part to be grasped in mm.
	 * @param speed
	 *            :Grasping speed in mm/s
	 */
	public boolean gripSync(float width, float speed) {
		SchunkGripperAcknowledge ack;
		//ack = sendCommandSync(commands.stopCommand());
		//ack = sendCommandSync(commands.setForceLimitCommand(80));
		ack = sendCommandSync(commands.prePositionFingersCommand(StopType.ClampOnBlock, MovementType.AbsoluteMotion, validateWidth(width), validateSpeed(speed)));
		ack = sendCommandSync(commands.graspPartCommand(validateWidth(width),validateSpeed(speed)));
		if (ack == null)
			return false;
		return ack.isSuccess();
	}
	public void gripSync(float width) {
		gripSync(validateWidth(width),systemLimits.getMaxSpeed()/2);
	}
	
	public void grip(float width, float speed) {
		sendCommand(commands.graspPartCommand(validateWidth(width),validateSpeed(speed)));
	}
	public void grip(float width) {
		grip(validateWidth(width),systemLimits.getMaxSpeed()/2);
	}
	public void grip(double widthRelative) {
		grip(calculateAbsoluteWidth(widthRelative));
	}
	
	public void clamp(double openWidthRelative) {
		clamp(calculateAbsoluteWidth(openWidthRelative));
	}
	public void clamp(double openWidthRelative, float speed) {
		clamp(calculateAbsoluteWidth(openWidthRelative) , speed);
	}
	public void clamp(float openWidth) {
		clamp(openWidth, systemLimits.getMaxSpeed()-2);
	}
	public void clamp(float openWidth, float speed) {
		sendCommand(commands.prePositionFingersCommand(StopType.ClampOnBlock, MovementType.AbsoluteMotion, validateWidth(openWidth), validateSpeed(speed)));
	}
	
	public boolean clampSync(double openWidthRelative) {
		return clampSync(openWidthRelative, systemLimits.getMaxSpeed()/2);
	}
	public boolean clampSync(double openWidthRelative, float speed) {
		return clampSync(calculateAbsoluteWidth(openWidthRelative), speed);
	}
	
	public boolean clampSync(float openWidth) {
		return clampSync(width, systemLimits.getMaxSpeed()/2);
	}
	public boolean clampSync(float openWidth, float speed) {
		SchunkGripperAcknowledge ack = sendCommandSync(commands.prePositionFingersCommand(StopType.ClampOnBlock, MovementType.AbsoluteMotion, validateWidth(openWidth), validateSpeed(speed)));
		if (ack == null)
			return false;
		
		return ack.isSuccess();
	}
	
	private float validateWidth(float width) {
		if (width < 0)
			width = 0;
		if (width > (systemLimits.getStroke()))
			width = systemLimits.getStroke();
		return width;
	}

	private float validateSpeed(float speed) {
		if (speed < systemLimits.getMinSpeed())
			speed = systemLimits.getMinSpeed();
		if (speed > (systemLimits.getMaxSpeed() - 2))
			speed = systemLimits.getMaxSpeed() - 2;
		return speed;
	}

	/*
	 * -------------------------------------- Ende Funktionen
	 * ---------------------------------------------
	 */

	public void disconnect() {
		Logger.info("disconnecting..");
		socket.sendCommand(commands.disconnectCommand());
		socket.close();
	}

	@Override
	public void notify(SchunkGripperAcknowledge ack) {
		if (ack == null)
			return;
		for(SchunkGripperAcknowledgementListener l :ackListener) {
			if(l.listenTo() != null && l.listenTo().contains(ack.getCommandId())) {
				l.notifyAcknowledgement(ack.getCommandId(), ack);
			}
		}
		// die nachfolgenden Acknowledgements mussen erfplgreich gewesen sein um verarbeitet zu werden
		if (!ack.isSuccess())
			return;
		// die nachricht ist ein ForceCommand
		if (ack.matches(SchunkGripperCommandSet.FORCE_COMMAND)) {
			force = Math.round(ByteUtil.array2Float(ack.getParameter())*100f)/100f;
			for (SchunkGripperForceListener l : forceListener) {
				l.notifyForce(force);
			}
			return;
		}
		// die Nachricht ist eine Grasping
		if (ack.matches(SchunkGripperCommandSet.GRASPINGSTATE_COMMAND)) {
			graspingState = GraspingState.fromInt(ack.getParameter()[0]);
			for (SchunkGripperGraspingStateListener l : graspingListener) {
				l.notifyGraspring(graspingState);
			}
			return;
		}
		// nachricht ist eine Speed Nachricht
		if (ack.matches(SchunkGripperCommandSet.SPEED_COMMAND)) {
			speed = Math.round(ByteUtil.array2Float(ack.getParameter())*100f)/100f;
			for (SchunkGripperSpeedListener l : speedListener) {
				l.notifySpeed(speed);
			}
			return;
		}
		// Nachicht ist ein Width
		if (ack.matches(SchunkGripperCommandSet.OPENINGWIDTH_COMMAND)) {
			width = Math.round(ByteUtil.array2Float(ack.getParameter())*100f)/100f;
			for (SchunkGripperWidthListener l : widthListener) {
				l.notifyWidth(width);
			}
			return;
		}
		// �nderung im SystemState
		if(ack.matches(SchunkGripperCommandSet.SYSTEMSTATE_COMMAND)) {
			List<SystemState> states = SystemState.fromIntArray(ack.getParameter());
			systemStates = states;
			for(SchunkGripperSystemStateListener l: systemListener) {
				l.notifySystemState(states);
			}
			return;
		}
		//initiales  holen der SystemLimits
		if(ack.matches(SchunkGripperCommandSet.SYSTEMLIMITS_COMMAND)) {
			this.systemLimits = new SchunkGripperSystemLimits(ack);
			return;
		}
	}

	private SchunkGripperAcknowledge sendCommandSync(SchunkGripperCommand command) {
		return socket.sendCommandSync(command);
	}
	private void sendCommand(SchunkGripperCommand command) {
		socket.sendCommand(command);
	}
	public float calculateAbsoluteWidth(double widthRelative) {
		if(widthRelative < 0.0) return 0.0f;
		if(widthRelative > 1.0) return getSystemLimits().getStroke();
		return (float) widthRelative * getSystemLimits().getStroke();
	}
	
	
	public double calculateRelativeWidth(float widthAbsolute) {
		if(widthAbsolute < 0) return 0.0f;
		if(widthAbsolute > getSystemLimits().getStroke()) return getSystemLimits().getStroke();
		return widthAbsolute / getSystemLimits().getStroke();
	}
}