package org.tud.kuka.socket;

import com.kuka.roboticsAPI.geometricModel.Frame;


public class RobotResponseCartesianMessage extends AbstractRobotResponseMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3163041289200865349L;
	
	private Double x;
	private Double y;
	private Double z;
	private Double alphaRad;
	private Double betaRad;
	private Double gammaRad;
	
	public RobotResponseCartesianMessage(RobotCommandCartesianMessage message) {
		super();
		this.setX(message.getX());
		this.setY(message.getY());
		this.setZ(message.getZ());
		this.setAlphaRad(message.getAlphaRad());
		this.setBetaRad(message.getBetaRad());
		this.setGammaRad(message.getGammaRad());
		this.setFingerOpenedRelative(message.getFingerOpenedRelative());
		this.setFrameName(message.getFrameName());
		this.setFramePath(message.getFramePath());
		this.setSpeedRelative(message.getSpeedRelative());
	}
	
	public RobotResponseCartesianMessage(Frame currentPosition) {
		super();
		this.setX(currentPosition.getX());
		this.setY(currentPosition.getY());
		this.setZ(currentPosition.getZ());
		this.setAlphaRad(currentPosition.getAlphaRad());
		this.setBetaRad(currentPosition.getBetaRad());
		this.setGammaRad(currentPosition.getGammaRad());
		this.setFrameName(currentPosition.getName());
		this.setFramePath(currentPosition.getPath());
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public Double getZ() {
		return z;
	}

	public void setZ(Double z) {
		this.z = z;
	}

	public Double getAlphaRad() {
		return alphaRad;
	}

	public void setAlphaRad(Double alphaRad) {
		this.alphaRad = alphaRad;
	}

	public Double getBetaRad() {
		return betaRad;
	}

	public void setBetaRad(Double betaRad) {
		this.betaRad = betaRad;
	}

	public Double getGammaRad() {
		return gammaRad;
	}

	public void setGammaRad(Double gammaRad) {
		this.gammaRad = gammaRad;
	}

	
	
	
}
