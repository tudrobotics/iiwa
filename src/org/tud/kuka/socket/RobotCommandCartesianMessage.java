package org.tud.kuka.socket;

import com.kuka.roboticsAPI.geometricModel.Frame;

public class RobotCommandCartesianMessage extends AbstractRobotCommandMessage {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((alphaRad == null) ? 0 : alphaRad.hashCode());
		result = prime * result + ((betaRad == null) ? 0 : betaRad.hashCode());
		result = prime * result
				+ ((frameName == null) ? 0 : frameName.hashCode());
		result = prime * result
				+ ((framePath == null) ? 0 : framePath.hashCode());
		result = prime * result
				+ ((gammaRad == null) ? 0 : gammaRad.hashCode());
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		result = prime * result + ((z == null) ? 0 : z.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RobotCommandCartesianMessage other = (RobotCommandCartesianMessage) obj;
		if (alphaRad == null) {
			if (other.alphaRad != null)
				return false;
		} else if (!alphaRad.equals(other.alphaRad))
			return false;
		if (betaRad == null) {
			if (other.betaRad != null)
				return false;
		} else if (!betaRad.equals(other.betaRad))
			return false;
		if (frameName == null) {
			if (other.frameName != null)
				return false;
		} else if (!frameName.equals(other.frameName))
			return false;
		if (framePath == null) {
			if (other.framePath != null)
				return false;
		} else if (!framePath.equals(other.framePath))
			return false;
		if (gammaRad == null) {
			if (other.gammaRad != null)
				return false;
		} else if (!gammaRad.equals(other.gammaRad))
			return false;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		if (z == null) {
			if (other.z != null)
				return false;
		} else if (!z.equals(other.z))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "RobotCommandCartesianMessage [x=" + x + ", y=" + y + ", z=" + z
				+ ", alphaRad=" + alphaRad + ", betaRad=" + betaRad
				+ ", gammaRad=" + gammaRad + ", frameName=" + frameName
				+ ", framePath=" + framePath + "]";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3690839170544094214L;
	
	private Double x;
	private Double y;
	private Double z;
	private Double alphaRad;
	private Double betaRad;
	private Double gammaRad;
	
	private String frameName;
	private String framePath;
	
	public RobotCommandCartesianMessage(Frame frame) {
		this.setFrameName(frame.getName());
		this.setFramePath(frame.getPath());
		this.x = frame.getX();
		this.y = frame.getY();
		this.z = frame.getZ();
		this.alphaRad = frame.getAlphaRad();
		this.betaRad = frame.getBetaRad();
		this.gammaRad = frame.getGammaRad();
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
