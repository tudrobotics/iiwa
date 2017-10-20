package application;

import static com.kuka.roboticsAPI.motionModel.BasicMotions.linRel;
import static com.kuka.roboticsAPI.motionModel.BasicMotions.ptp;

import java.util.Date;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import org.tud.kuka.ioAccess.LedControllerRunnable;
import org.tud.kuka.motion.ConditionFactory;
import org.tud.kuka.socket.AbstractRobotCommandMessage;
import org.tud.kuka.socket.AbstractRobotResponseMessage.RoboterStatus;
import org.tud.kuka.socket.LedCommandMessage;
import org.tud.kuka.socket.LedCommandMessage.LedColor;
import org.tud.kuka.socket.RobotCommandCartesianMessage;
import org.tud.kuka.socket.RobotCommandJointMessage;
import org.tud.kuka.socket.RobotCommandRotationMessage;
import org.tud.kuka.socket.RobotResponseCartesianMessage;
import org.tud.kuka.socket.RobotResponseJointMessage;
import org.tud.kuka.socket.SocketListerner;
import org.tud.kuka.socket.SocketServer;
import org.tud.kuka.tool.GripperJaws;
import org.tud.kuka.tool.SmartGripper;
import org.tud.kuka.tool.SmartGripperListener;
import org.tud.schunk.gripper.listener.SchunkGripperGraspingStateListener.GraspingState;

import application.basic.AbstractRoboticsAPIApplication;
import application.weir.CalibrateButtonListener;
import application.weir.RoboterSpeedButtonListener;
import application.weir.TogglePauseButtonListener;
import application.weir.WeirHelper;

import com.kuka.connectivity.motionModel.directServo.DirectServo;
import com.kuka.connectivity.motionModel.smartServo.IServoOnGoalReachedEvent;
import com.kuka.connectivity.motionModel.smartServo.ISmartServoRuntime;
import com.kuka.connectivity.motionModel.smartServo.ServoMotion;
import com.kuka.connectivity.motionModel.smartServo.SmartServo;
import com.kuka.roboticsAPI.conditionModel.BooleanIOCondition;
import com.kuka.roboticsAPI.conditionModel.ConditionObserver;
import com.kuka.roboticsAPI.conditionModel.ICallbackAction;
import com.kuka.roboticsAPI.conditionModel.ICondition;
import com.kuka.roboticsAPI.conditionModel.IFallingEdgeListener;
import com.kuka.roboticsAPI.conditionModel.IRisingEdgeListener;
import com.kuka.roboticsAPI.conditionModel.JointTorqueCondition;
import com.kuka.roboticsAPI.controllerModel.Controller;
import com.kuka.roboticsAPI.controllerModel.StatePortData;
import com.kuka.roboticsAPI.controllerModel.sunrise.ISunriseControllerStateListener;
import com.kuka.roboticsAPI.controllerModel.sunrise.SunriseSafetyState;
import com.kuka.roboticsAPI.deviceModel.Device;
import com.kuka.roboticsAPI.deviceModel.JointEnum;
import com.kuka.roboticsAPI.deviceModel.JointPosition;
import com.kuka.roboticsAPI.executionModel.CommandInvalidException;
import com.kuka.roboticsAPI.executionModel.IFiredTriggerInfo;
import com.kuka.roboticsAPI.geometricModel.Frame;
import com.kuka.roboticsAPI.geometricModel.ObjectFrame;
import com.kuka.roboticsAPI.geometricModel.Workpiece;
import com.kuka.roboticsAPI.motionModel.IMotionContainer;
import com.kuka.roboticsAPI.motionModel.controlModeModel.CartesianImpedanceControlMode;
import com.kuka.roboticsAPI.sensorModel.ForceSensorData;
import com.kuka.roboticsAPI.uiModel.ApplicationDialogType;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKey;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyBar;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyListener;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyAlignment;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyLED;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyLEDSize;

public class _WeirApplication extends AbstractRoboticsAPIApplication implements SocketListerner, SmartGripperListener {

	private SocketServer server = SocketServer.getInstance();
	
	private static final double maxSpeedRel = 0.95;
	private static final double torqueAbsolute = 10;
	// in s
	private static final int defaultMotionTimeout = 30;
	private static final boolean sendAnswerMessage = false;
	private ObjectFrame defaultMotionFrame;
	private GripperJaws attachedGripperJaws;
	private ObjectFrame gripperAttacheFrame;
	
	private SmartServo servoMotion;
	private ISmartServoRuntime theServoRuntime;
	
	private volatile AbstractRobotCommandMessage message;
	private volatile AbstractRobotCommandMessage lastMessage;
	
	// conditions for sensitivity
	private ICondition sensitivityCondition;
	private ConditionObserver sensitivityObserver;
	private ICallbackAction sensitivityAction;
	
	// goal Reached Listener
	// conditions for medienflanch Userbutton
	private ICondition userButtonCondition;
	private ConditionObserver userButtonReleasedObserver;
	
	private SmartGripper smartGripper;
	
	private Date lastUpdateTime;
	
	private Frame currentPosition;
	private JointPosition currentJointPosition;
	
	private static CartesianImpedanceControlMode controlMode; 
	
	private volatile boolean sensitivityActivated;
	// werkstück
	private Workpiece workPiece;
	// userKeyBar
	private IUserKeyBar userKeyBar;
	private IUserKeyListener togglePauseKeyListener;
	private IUserKeyListener calibrateKeyListener;
	private IUserKeyListener speedKeyListener;
	
	@Override
	public void initialize() {
		super.initialize();
		server.addListener(this);
		setGlobalSpeedRel(maxSpeedRel);
		sensitivityCondition = createSenitivityCondition();
		sensitivityObserver = createConditionObserver(sensitivityCondition, new SenitivityListener());
		sensitivityObserver.enable();
		sensitivityAction = new TorquePrinterAction();
		
		userButtonCondition = new BooleanIOCondition(mediaFlanschIO.getIO("UserButton", false),true);
		userButtonReleasedObserver = createConditionObserver(userButtonCondition, createStartSmartMotionListener());
		userButtonReleasedObserver.enable();
		
		smartGripper = new SmartGripper(gripper);
		smartGripper.addListener(this);
		smartGripper.start();
		// setzen default werte
		defaultMotionFrame = gripperTool.getDefaultMotionFrame();
		gripperAttacheFrame = gripperTool.getFrame("/GripperRoot/GripperJawsTCP");
		attachedGripperJaws = metalJaws;
		
		addControllerObserver();
		lastUpdateTime = new Date();
		controlMode = WeirHelper.createCartesianImpedanceControlMode();
		
		setupUserKeybar();
	}
	private void notifyApp() {
		synchronized(this) {
			this.notify();
		}
	}
	
	private void showJawsDialog() {
		int dialog = getApplicationUI().displayModalDialog(ApplicationDialogType.QUESTION, "Welche Backen sind am Gripper befestigt?", "Metall", "Plastik", "Keine");
		switch (dialog)
		{
			case 0: metalJaws.attachTo(gripperAttacheFrame);
				attachedGripperJaws = metalJaws;
				break;
			case 1: plasticJaws.attachTo(gripperAttacheFrame);
				attachedGripperJaws = plasticJaws;
				break;
			case 2: // tue nichts
					break;
			default: // tue nichts
		}
	}
	/**
     * Move to an initial Position
     * 
     */
    private void moveToInitialPosition(){
        lbr_iiwa.move(ptp(0., Math.PI / 180 * 30.0, 0.0, -Math.PI / 180 * 60.0, 0.0,
                Math.PI / 180 * 90.0, 0.0).setJointVelocityRel(0.3));
    }
    
	private boolean isSmartServoAvailable() {
		try {
            if (!ServoMotion.validateForImpedanceMode(lbr_iiwa)) {
                getLogger().info("Validation of torque model failed - correct your mass property settings");
                getLogger().info("SmartServo will be available for position controlled mode only, until validation is performed");
                return false;
            }
        }
        catch (IllegalStateException e) {
            getLogger().error("Omitting validation failure for this sample\n"
                    + e.getMessage());
            return false;
        }
        return true;
	}
	@Override
	public void run() {
		if(attachedGripperJaws == null) {
			showJawsDialog();
		}
		else {
			attachedGripperJaws.attachTo(gripperAttacheFrame);
		}
		
		moveToInitialPosition();
		
		if(!isSmartServoAvailable()) {
			return;
		}
		while(isReady()) {
			startSmartServoMotion();
			waitForRestartMotion();
		}

		stopMotion();
		
		
		getLogger().info("BEENDE PROGRAM");
		
	}

	@Override
	public void handleRobotCommand(AbstractRobotCommandMessage message) {
		if(isReady() && !sensitivityActivated && message != null) {
			if(!message.equals(lastMessage)) {
				//getLogger().info(message.toString());
				this.message = message;
				notifyApp();
			} else {
				sendGoalReachedEvent(message);
			}
		} 
	}
	
	private void startSmartServoMotion() {
		getLogger().info("Starting SmartServo motion in position control mode");
		flexFellowIoGroup.turnOffSignalLights();
		flexFellowIoGroup.setSignalLightGreen(true);
        
		IMotionContainer motionContainer = initSmartServoMotion();
        
		Frame gripperFrame = theServoRuntime.getCurrentCartesianDestination(defaultMotionFrame);
		
		// falls eine cartisianPosition verwnet wird
		Frame destFrame = null;
		// falls  eine JointPosition als Ziel verwendet wird
		JointPosition jointDestination = null;
		while(isReady()) {
			if(message != null){
				try {
					mediaFlanschIO.setLEDBlue(true);
					flexFellowIoGroup.setSignalLightYellow(false);
					flexFellowIoGroup.setSignalLightGreen(true);
					
					//  setzte die neuen Fingerposition
					// das wird zuerst gemacht, weil die setDestination fehlschlagen kann
					if(message.getFingerOpenedRelative() != null) {
						if(lastMessage == null) {
							executeGripperCommand();
						}
						else if(lastMessage.getFingerOpenedRelative() != null && !message.getFingerOpenedRelative().equals(lastMessage.getFingerOpenedRelative())) {
							executeGripperCommand();
						}
					}
					
					
		            // wenn die letzte Position erreicht wurde und in der neuen nachricht die Geschwindigkeit geändert wird, 
					//dann wir die neue Höchstgeschindigkeit
		            // gesetzt und die motion neu gestartet
					if(theServoRuntime.isDestinationReached() && message.getSpeedRelative() != null && message.getSpeedRelative() >= 0.0 && (maxSpeedRel * message.getSpeedRelative()) != getGlobalSpeedRel()) {
						stopMotion();
						getLogger().info("old speed:"+getGlobalSpeedRel()+" --> new speed:"+maxSpeedRel * message.getSpeedRelative());
						setGlobalSpeedRel(maxSpeedRel * message.getSpeedRelative());
						motionContainer = initSmartServoMotion();
						flexFellowIoGroup.setSignalLightRed(false);
					}
					// WORKAROUND weil noch keiner eine LEDCommand Nachricht mir schickt
					// zum LED ansteuern
					if(message instanceof RobotCommandCartesianMessage)
					if(((RobotCommandCartesianMessage )message).getX() == null && ((RobotCommandCartesianMessage )message).getFrameName().toLowerCase().equals("blink")) {
						LedCommandMessage m = new LedCommandMessage();
						m.setDuration(1);
						if(((RobotCommandCartesianMessage )message).getFramePath().equals("BLUE")) {
							m.setColor(LedColor.BLUE);
						}
						if(((RobotCommandCartesianMessage )message).getFramePath().equals("RED")) {
							m.setColor(LedColor.RED);
						}
						if(((RobotCommandCartesianMessage )message).getFramePath().equals("YELLOW")) {
							m.setColor(LedColor.YELLOW);
						}
						if(((RobotCommandCartesianMessage )message).getFramePath().equals("GREEN")) {
							m.setColor(LedColor.GREEN);
						}
						new Thread(new LedControllerRunnable(flexFellowIoGroup, m)).start();
						lastUpdateTime = new Date();
						message = null;
						continue;
					}
					// berechnen der neuen Position
					theServoRuntime.updateWithRealtimeSystem();
					
					// RobotCommandJointMessage
					if(message instanceof RobotCommandJointMessage) {
						jointDestination = WeirHelper.createJointPosition((RobotCommandJointMessage) message);
						destFrame = null;
					} else if(message instanceof RobotCommandCartesianMessage) {
						// TODO WICHTIG die Frameinformationen aus der Nachricht werden nicht verwendet sondern es wird immer der gleiche Frame angesteuert
						destFrame = WeirHelper.createFrame(gripperFrame.copyWithRedundancy(), (RobotCommandCartesianMessage) message);
		                jointDestination = null;
					} else if(message instanceof RobotCommandRotationMessage){
						// das das ist eine Transformationsanweisung
						destFrame = WeirHelper.createRotationFrame((RobotCommandRotationMessage)message);
						jointDestination = null;
					}
					// wenn die Momentanige Position schon der neuen Position entspricht, dann feuere nur das goal reached
					if(destFrame != null) {
						currentPosition = getCurrentPosition();
						if(!currentPosition.hasPathTo(destFrame)) {
							// TODO Schauen ob wir damit noch was machen können
							getLogger().error("kein Pfad zum Ziel");
						}
						/*
						if(currentPosition.isCloseTo(destFrame, 2, Math.toRadians(1))) {
							sendGoalReachedEvent(message);
							//getLogger().info("i dont move");	
						} else */{
						
							// setzen der neuen Position
							theServoRuntime.setDestination(destFrame);
							// sende da aktuelle Kommando wieder zurück
							if(sendAnswerMessage && message instanceof RobotCommandCartesianMessage) {
								RobotResponseCartesianMessage m = new RobotResponseCartesianMessage((RobotCommandCartesianMessage) message);
						        m.setStatus(RoboterStatus.OK);
						        m.setMessage("");
						        server.send(m);
							}
						}
					} else if(jointDestination != null) {
						currentJointPosition = getCurrentJointPosition();
						/*if(currentJointPosition.isNearlyEqual(jointDestination)) {
							sendGoalReachedEvent(message);
						} else */{
							theServoRuntime.setDestination(jointDestination);
							if(sendAnswerMessage) {
								RobotResponseJointMessage m = new RobotResponseJointMessage(jointDestination);
						        m.setStatus(RoboterStatus.OK);
						        m.setMessage("");
						        server.send(m);
							}
						}
					}
					lastMessage = message;
					lastUpdateTime = new Date();
					message = null;
				} catch (Throwable e) {
					if(e instanceof Throwable) {
						getLogger().info(lbr_iiwa.getCurrentJointPosition().toString());
						e.printStackTrace();
					}
					// die Motion wurde aus nicht nachvollziehbaren Gründen gestoppt dann muss sie neu gestartet werden
		            if(e instanceof CommandInvalidException) {
		            	//getLogger().error(e.getMessage());
		            	getLogger().info("restart motion after timeout");
		            	motionContainer = initSmartServoMotion();
		            	// führe de letzen befehl noch mal aus
		            	continue;
		            }
		            //getLogger().error(destFrame.toStringInWorld());
		            //getLogger().error(e.getMessage());
		            // bei einem anderen Fehler wird die Position zurückgegeben, damit man weiß das dieser Punkt ist nicht anfahrbar
		            if(message instanceof RobotCommandCartesianMessage) {
			            RobotResponseCartesianMessage m = new RobotResponseCartesianMessage((RobotCommandCartesianMessage)message);
			            m.setStatus(RoboterStatus.ERROR);
			            m.setMessage(e.getLocalizedMessage());
			            server.send(m);
		            }
		            message = null;
		            //flexFellowIoGroup.setSignalLightGreen(false);
		            //flexFellowIoGroup.setSignalLightYellow(true);
		        }
			} else {
				// es wird auf eine neue nachricht gewartet
				try {
					synchronized(this) {
						this.wait(3000);
					}
					// wenn 2 sekunden lang keine Nachricht gekommen ist, dann machen das LED Licht aus;
					if(lastUpdateTime.getTime() < (new Date().getTime() - 3000)) {
						mediaFlanschIO.setLEDBlue(false);
					}
					/*if(lastUpdateTime.getTime() < (new Date().getTime() - 30000)) {
						//waitForRestartServoMotion();
					}*/
				} catch (InterruptedException e) {
					System.out.println("interrupted while waiting on new command");
				}
			}
			// die erste Bedingung feuert nur, wenn die motion durch die Sensitivity beendet wurde
			// es kann aber auch sein, dass die Motion durch etwas anderes beendet wurde, desween wurd auch noch die Variable sensitiyitaActivated
			// überprüft
			
			if((motionContainer.isFinished() && motionContainer.hasFired(sensitivityCondition)) || sensitivityActivated ) {
				sensitivityActivated = true;
				// nach der sensitivity erstmal die geschwindigkeit heruntersetzen
				setGlobalSpeedRel(0.5);
				return;
			}
		}
	}
	
	private JointPosition getCurrentJointPosition() {
		return lbr_iiwa.getCurrentJointPosition();
	}
	private void waitForRestartMotion() {
			try {
				// wenn 3 sekunden lang keine Nachricht gekommen ist, dann machen das LED Licht aus;
				mediaFlanschIO.setLEDBlue(false);
				synchronized(this) {
					this.wait();
				}
			} catch (InterruptedException e) {
				getLogger().error("interrupted while waiting for restart motion");
			}
			
	}
	private void executeGripperCommand() {
		try {
			smartGripper.work(message.getFingerOpenedRelative());
		} catch(Exception e) {
			
		}
	}
	@Override
	public void dispose() {
		flexFellowIoGroup.turnOffSignalLights();
		stopMotion();
		setReady(false);
		server.removeListener(this);
		smartGripper.disconnect();
		
	}
	private void stopMotion() {
		mediaFlanschIO.setLEDBlue(false);
		if(theServoRuntime == null) return;
		try {
			theServoRuntime.stopMotion();
		} catch (IllegalArgumentException e) {
			
		}
		theServoRuntime = null;
		servoMotion = null;
	}
	private class SmartGoalReachedEventListener implements IServoOnGoalReachedEvent
	{
		@Override
		public void onGoalReachedEvent(String state, double[] currentAxisPos,
				int[] osTimestamp, int targetId) {
			if (theServoRuntime.isDestinationReached() && lastMessage != null){
				sendGoalReachedEvent(lastMessage);
			}
			
		}
	}
	
	private void sendGoalReachedEvent(AbstractRobotCommandMessage message) {
		RobotResponseCartesianMessage r;
		if( message instanceof RobotCommandCartesianMessage) {
			r = new RobotResponseCartesianMessage((RobotCommandCartesianMessage)message);
		} else {
			r = new RobotResponseCartesianMessage(getCurrentPosition());
		}
		r.setStatus(RoboterStatus.GOAL_REACHED);
		r.setMessage("position is reached");
		//getLogger().info(RoboterCommandStatus.GOAL_REACHED.toString());
		server.send(r);
	}
	private class SenitivityListener implements IRisingEdgeListener {
		@Override
		public void onRisingEdge(ConditionObserver conditionObserver,
				Date time, int missedEvents) {
			sensitivityActivated = true;
			flexFellowIoGroup.turnOffSignalLights();
			flexFellowIoGroup.setSignalLightRed(true);
			getLogger().info("sensitivity activated");
		}
	}
	
	private class TorquePrinterAction implements ICallbackAction {

		@Override
		public void onTriggerFired(IFiredTriggerInfo triggerInformation) {
			getLogger().info(lbr_iiwa.getExternalTorque().toString());
			
		}
		
	}
	private SmartServo createSmartServo() {
		SmartServo aSmartServoMotion = new SmartServo(lbr_iiwa.getCurrentJointPosition());
		aSmartServoMotion.setTimeoutAfterGoalReach(defaultMotionTimeout,TimeUnit.SECONDS);
		aSmartServoMotion.useTrace(false);
		
		aSmartServoMotion.setMinimumTrajectoryExecutionTime(5e-3);
		aSmartServoMotion.setJointAccelerationRel(getGlobalSpeedRel());
        aSmartServoMotion.setJointVelocityRel(getGlobalSpeedRel());
        
        aSmartServoMotion.setSpeedTimeoutAfterGoalReach(0.05);
        return aSmartServoMotion;
	}
	
	private IMotionContainer initSmartServoMotion() {
		servoMotion = createSmartServo();
		servoMotion.breakWhen(sensitivityCondition);
		servoMotion.triggerWhen(sensitivityCondition, sensitivityAction);
		IMotionContainer motionContainer = gripperTool.moveAsync(servoMotion);

		//Get the runtime of the SmartServo motion
		theServoRuntime = servoMotion.getRuntime();
		
        theServoRuntime.activateVelocityPlanning(true);
        theServoRuntime.setGoalReachedEventHandler(new SmartGoalReachedEventListener());
        
        return motionContainer;
	}
	private DirectServo createDirectServo() {
		DirectServo aDirectServoMotion = new DirectServo(lbr_iiwa.getCurrentJointPosition());
		aDirectServoMotion.setTimeoutAfterGoalReach(defaultMotionTimeout,TimeUnit.SECONDS);
		aDirectServoMotion.useTrace(false);
		aDirectServoMotion.setMinimumTrajectoryExecutionTime(40e-3);
		aDirectServoMotion.setJointVelocityRel(getGlobalSpeedRel());
		return aDirectServoMotion;
	}
	
	private ICondition createSenitivityCondition() {
		JointTorqueCondition cond1 = new JointTorqueCondition(lbr_iiwa, JointEnum.J1, -torqueAbsolute, torqueAbsolute);
		JointTorqueCondition cond2 = new JointTorqueCondition(lbr_iiwa, JointEnum.J2, -torqueAbsolute, torqueAbsolute);
		JointTorqueCondition cond3 = new JointTorqueCondition(lbr_iiwa, JointEnum.J3, -torqueAbsolute, torqueAbsolute);
		JointTorqueCondition cond4 = new JointTorqueCondition(lbr_iiwa, JointEnum.J4, -torqueAbsolute, torqueAbsolute);
		JointTorqueCondition cond5 = new JointTorqueCondition(lbr_iiwa, JointEnum.J5, -torqueAbsolute, torqueAbsolute);
		JointTorqueCondition cond6 = new JointTorqueCondition(lbr_iiwa, JointEnum.J6, -torqueAbsolute, torqueAbsolute);
		JointTorqueCondition cond7 = new JointTorqueCondition(lbr_iiwa, JointEnum.J7, -torqueAbsolute, torqueAbsolute);
		ICondition cond = cond1.or(cond2, cond3,cond4,cond5,cond6,cond7);
		EnumSet<JointEnum> joints = EnumSet.of(JointEnum.J1, JointEnum.J2, JointEnum.J3,JointEnum.J4,JointEnum.J5,JointEnum.J6,JointEnum.J7);
		cond = ConditionFactory.createTorqueCondition(lbr_iiwa, joints, torqueAbsolute);
        return cond;
	}
	
	@Override
	public void notifyServerStatus(SocketServerNotification notification) {
		getLogger().info(server.getServerStatusMessage());
	}
	
	private IFallingEdgeListener createStartSmartMotionListener() {
		return new IFallingEdgeListener() {			
			@Override
			public void onFallingEdge(ConditionObserver conditionObserver,
					Date time, int missedEvents) {
				getLogger().info("user button pressed");
				RobotResponseCartesianMessage r = new RobotResponseCartesianMessage(getCurrentPosition());
				r.setStatus(RoboterStatus.BUTTON_PRESSED);
				server.send(r);
				flexFellowIoGroup.turnOffSignalLights();
				flexFellowIoGroup.setSignalLightGreen(true);
				// wenn die sensitivity aktiviert wurde, dann machen wir hier noch was
				if(sensitivityActivated) {
					deactivateSensitivity();
				}
			}			
		};
	}
	
	private void deactivateSensitivity() {
		
		ForceSensorData force = lbr_iiwa.getExternalForceTorque(lbr_iiwa.getFlange());
        getLogger().info("current force "+force.getForce().toString());
	    lbr_iiwa.move(linRel(force.getForce().getX()/10.0,force.getForce().getY()/10.0,force.getForce().getZ()/10.0,lbr_iiwa.getFlange()).setCartVelocity(5));
		force = lbr_iiwa.getExternalForceTorque(lbr_iiwa.getFlange());
		getLogger().info("current force after releasing "+force.getForce().toString());
		sensitivityActivated = false;
		notifyApp();
	}
	private void addControllerObserver() {
			kukaController.addControllerListener(new
					ISunriseControllerStateListener() {

						@Override
						public void onIsReadyToMoveChanged(Device device,
								boolean isReadyToMove) {
							notifyApp();
						}
						@Override
						public void onShutdown(Controller controller) {
							notifyApp();		
						}

						@Override
						public void statePortChanged(Controller controller,
								StatePortData statePort) {

							
						}

						@Override
						public void onConnectionLost(Controller controller) {
							
						}

						@Override
						public void onSafetyStateChanged(Device device,
								SunriseSafetyState safetyState) {
							notifyApp();		
						}
			});
	}
	
	private Frame getCurrentPosition() {
		return lbr_iiwa.getCurrentCartesianPosition(defaultMotionFrame);
	}
	@Override
	public void handleLedCommand(final LedCommandMessage message) {	
		LedControllerRunnable r = new LedControllerRunnable(flexFellowIoGroup, message);
		new Thread(r).start();
	}
	@Override
	public void notify(GraspingState state) {
		/*if(state.equals(GraspingState.GRASPING)) {
			RobotResponseCartesianMessage r = new RobotResponseCartesianMessage(getCurrentPosition());
			r.setStatus(RoboterStatus.valueOf(state.toString()));
			server.send(r);
			getLogger().info("gripping workpiece");
			// TODO Testen
			// das gripperJaw defaultMotionFrame ist das GripperJawTCP datan wird der Woodpies dran befestigt
		}
		if(state.equals(GraspingState.RELEASING)) {
			RobotResponseCartesianMessage r = new RobotResponseCartesianMessage(getCurrentPosition());
			r.setStatus(RoboterStatus.valueOf(state.toString()));
			server.send(r);
			getLogger().info("releasing workpiece");
			// TODO Testen
		}*/
	}
	
	private void setupUserKeybar() {
		// userbar
				togglePauseKeyListener = new TogglePauseButtonListener(server);
				calibrateKeyListener = new CalibrateButtonListener(server);
				speedKeyListener = new RoboterSpeedButtonListener(server);
				userKeyBar = getApplicationUI().createUserKeyBar("WEIR Control");
				IUserKey k = userKeyBar.addUserKey(0, togglePauseKeyListener, true);
				k.setLED(UserKeyAlignment.Middle, UserKeyLED.Green, UserKeyLEDSize.Normal);
				k.setText(UserKeyAlignment.Middle, "toggle pause");
				k = userKeyBar.addUserKey(1, calibrateKeyListener, true);	
				k.setLED(UserKeyAlignment.Middle, UserKeyLED.Yellow, UserKeyLEDSize.Normal);
				k.setText(UserKeyAlignment.Middle, "calibrate");
				k = userKeyBar.addDoubleUserKey(2, speedKeyListener, false);
				k.setText(UserKeyAlignment.Middle, "speed up/down");
				userKeyBar.publish();
	}
}
