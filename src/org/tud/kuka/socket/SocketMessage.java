package org.tud.kuka.socket;

import java.io.Serializable;

public abstract class SocketMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 326710625405087985L;
	
	private String type;
	
	public SocketMessage() {
		type = getClass().getSimpleName();
	}
	
	public String getType() {
		return type;
	}
	
}
