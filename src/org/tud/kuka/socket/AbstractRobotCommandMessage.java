package org.tud.kuka.socket;

import org.tud.kuka.util.NumberUtil;

public abstract class AbstractRobotCommandMessage extends SocketMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1815504035010493535L;
	
	
	protected Double fingerOpenedRelative;
	protected Double speedRelative;
	
	
	public Double getFingerOpenedRelative() {
		if(fingerOpenedRelative == null) return null;
		return NumberUtil.round(fingerOpenedRelative, 4);
	}
	public void setFingerOpenedRelative(Double fingerOpenedRelative) {
		this.fingerOpenedRelative = fingerOpenedRelative;
	}
	public Double getSpeedRelative() {
		if(speedRelative == null) return null;
		return NumberUtil.round(speedRelative, 6);
	}
	public void setSpeedRelative(Double speedRelative) {
		this.speedRelative = speedRelative;
	}
	
	
	
	
}
