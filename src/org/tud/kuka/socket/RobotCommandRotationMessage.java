package org.tud.kuka.socket;


public class RobotCommandRotationMessage extends AbstractRobotCommandMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3690839170544094214L;
	
	private Double x;
	private Double y;
	private Double z;
	
	private Double rot00;
	private Double rot10;
	private Double rot20;
	private Double rot01;
	private Double rot11;
	private Double rot21;
	private Double rot02;
	private Double rot12;
	private Double rot22;

	
	public Double getRot00() {
		return rot00;
	}
	public void setRot00(Double rot00) {
		this.rot00 = rot00;
	}
	public Double getRot10() {
		return rot10;
	}
	public void setRot10(Double rot10) {
		this.rot10 = rot10;
	}
	public Double getRot20() {
		return rot20;
	}
	public void setRot20(Double rot20) {
		this.rot20 = rot20;
	}
	public Double getRot01() {
		return rot01;
	}
	public void setRot01(Double rot01) {
		this.rot01 = rot01;
	}
	public Double getRot11() {
		return rot11;
	}
	public void setRot11(Double rot11) {
		this.rot11 = rot11;
	}
	public Double getRot21() {
		return rot21;
	}
	public void setRot21(Double rot21) {
		this.rot21 = rot21;
	}
	public Double getRot02() {
		return rot02;
	}
	public void setRot02(Double rot02) {
		this.rot02 = rot02;
	}
	public Double getRot12() {
		return rot12;
	}
	public void setRot12(Double rot12) {
		this.rot12 = rot12;
	}
	public Double getRot22() {
		return rot22;
	}
	public void setRot22(Double rot22) {
		this.rot22 = rot22;
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
	@Override
	public String toString() {
		return "RobotCommandRotationMessage [x=" + x + ", y=" + y + ", z=" + z
				+ ", rot00=" + rot00 + ", rot10=" + rot10 + ", rot20=" + rot20
				+ ", rot01=" + rot01 + ", rot11=" + rot11 + ", rot21=" + rot21
				+ ", rot02=" + rot02 + ", rot12=" + rot12 + ", rot22=" + rot22
				+ "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rot00 == null) ? 0 : rot00.hashCode());
		result = prime * result + ((rot01 == null) ? 0 : rot01.hashCode());
		result = prime * result + ((rot02 == null) ? 0 : rot02.hashCode());
		result = prime * result + ((rot10 == null) ? 0 : rot10.hashCode());
		result = prime * result + ((rot11 == null) ? 0 : rot11.hashCode());
		result = prime * result + ((rot12 == null) ? 0 : rot12.hashCode());
		result = prime * result + ((rot20 == null) ? 0 : rot20.hashCode());
		result = prime * result + ((rot21 == null) ? 0 : rot21.hashCode());
		result = prime * result + ((rot22 == null) ? 0 : rot22.hashCode());
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
		RobotCommandRotationMessage other = (RobotCommandRotationMessage) obj;
		if (rot00 == null) {
			if (other.rot00 != null)
				return false;
		} else if (!rot00.equals(other.rot00))
			return false;
		if (rot01 == null) {
			if (other.rot01 != null)
				return false;
		} else if (!rot01.equals(other.rot01))
			return false;
		if (rot02 == null) {
			if (other.rot02 != null)
				return false;
		} else if (!rot02.equals(other.rot02))
			return false;
		if (rot10 == null) {
			if (other.rot10 != null)
				return false;
		} else if (!rot10.equals(other.rot10))
			return false;
		if (rot11 == null) {
			if (other.rot11 != null)
				return false;
		} else if (!rot11.equals(other.rot11))
			return false;
		if (rot12 == null) {
			if (other.rot12 != null)
				return false;
		} else if (!rot12.equals(other.rot12))
			return false;
		if (rot20 == null) {
			if (other.rot20 != null)
				return false;
		} else if (!rot20.equals(other.rot20))
			return false;
		if (rot21 == null) {
			if (other.rot21 != null)
				return false;
		} else if (!rot21.equals(other.rot21))
			return false;
		if (rot22 == null) {
			if (other.rot22 != null)
				return false;
		} else if (!rot22.equals(other.rot22))
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
	

	

	
}
