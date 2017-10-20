package org.tud.kuka.socket;

public abstract class RobotFrameMessage extends SocketMessage{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4615542343301512948L;
	
	private String frameName;
	private String framePath;
	
	
	public RobotFrameMessage() {
		frameName = "";
		framePath = "/";
	}

	public String getFrameName() {
		return frameName;
	}
	public void setFrameName(String frameName) {
		if(frameName == null) {
			this.frameName = "";
		} else {
			this.frameName = frameName;
		}
	}
	public String getFramePath() {
		return framePath;
	}
	public void setFramePath(String framePath) {
		if(frameName == null) {
			this.framePath = "/";
		} else {
			this.framePath = framePath;
		}
	}
	
	
	
}
