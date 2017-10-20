package org.tud.kuka.socket;

public class LedCommandMessage extends SocketMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5834165166002809735L;

	public enum LedColor { BLUE, RED, YELLOW, GREEN;}
	
	private LedColor color;
	/**
	 * duration in s
	 */
	private int duration = 2;

	public LedColor getColor() {
		return color;
	}

	public void setColor(LedColor color) {
		this.color = color;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	
}
