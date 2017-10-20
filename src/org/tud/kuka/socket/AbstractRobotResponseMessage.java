package org.tud.kuka.socket;

public abstract class AbstractRobotResponseMessage extends RobotFrameMessage  {

	
	public enum RoboterStatus {OK, ERROR, INFO, GOAL_REACHED, BUTTON_PRESSED, GRASPING, RELEASING;}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1745194271381724107L;
	
	protected RoboterStatus status;
	protected String message;
	
	protected Double fingerOpenedRelative;
	protected Double speedRelative;
	
	protected Float gripperForce;
	
	public AbstractRobotResponseMessage() {
		status = RoboterStatus.INFO;
		
	}
	public RoboterStatus getStatus() {
		return status;
	}
	public void setStatus(RoboterStatus status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Double getFingerOpenedRelative() {
		return fingerOpenedRelative;
	}
	public void setFingerOpenedRelative(Double fingerOpenedRelative) {
		this.fingerOpenedRelative = fingerOpenedRelative;
	}
	public Double getSpeedRelative() {
		return speedRelative;
	}
	public void setSpeedRelative(Double speedRelative) {
		this.speedRelative = speedRelative;
	}
	public Float getGripperForce() {
		return gripperForce;
	}
	public void setGripperForce(Float gripperForce) {
		this.gripperForce = gripperForce;
	}
	
	
	
}
