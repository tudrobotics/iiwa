package org.tud.kuka.socket;

import com.kuka.roboticsAPI.deviceModel.JointPosition;

public class RobotCommandJointMessage extends AbstractRobotCommandMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2595003261530925352L;
	private Double a0;
	private Double a1;
	private Double a2;
	private Double a3;
	private Double a4;
	private Double a5;
	private Double a6;
	
	public RobotCommandJointMessage() {
		super();
	}
	public RobotCommandJointMessage(JointPosition position) {
	}
	public Double getA0() {
		return a0;
	}
	public void setA0(Double a0) {
		this.a0 = a0;
	}
	public Double getA1() {
		return a1;
	}
	public void setA1(Double a1) {
		this.a1 = a1;
	}
	public Double getA2() {
		return a2;
	}
	public void setA2(Double a2) {
		this.a2 = a2;
	}
	public Double getA3() {
		return a3;
	}
	public void setA3(Double a3) {
		this.a3 = a3;
	}
	public Double getA4() {
		return a4;
	}
	public void setA4(Double a4) {
		this.a4 = a4;
	}
	public Double getA5() {
		return a5;
	}
	public void setA5(Double a5) {
		this.a5 = a5;
	}
	public Double getA6() {
		return a6;
	}
	public void setA6(Double a6) {
		this.a6 = a6;
	}
	@Override
	public String toString() {
		return "RobotCommandJointMessage [a0=" + a0 + ", a1=" + a1 + ", a2="
				+ a2 + ", a3=" + a3 + ", a4=" + a4 + ", a5=" + a5 + ", a6="
				+ a6 + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a0 == null) ? 0 : a0.hashCode());
		result = prime * result + ((a1 == null) ? 0 : a1.hashCode());
		result = prime * result + ((a2 == null) ? 0 : a2.hashCode());
		result = prime * result + ((a3 == null) ? 0 : a3.hashCode());
		result = prime * result + ((a4 == null) ? 0 : a4.hashCode());
		result = prime * result + ((a5 == null) ? 0 : a5.hashCode());
		result = prime * result + ((a6 == null) ? 0 : a6.hashCode());
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
		RobotCommandJointMessage other = (RobotCommandJointMessage) obj;
		if (a0 == null) {
			if (other.a0 != null)
				return false;
		} else if (!a0.equals(other.a0))
			return false;
		if (a1 == null) {
			if (other.a1 != null)
				return false;
		} else if (!a1.equals(other.a1))
			return false;
		if (a2 == null) {
			if (other.a2 != null)
				return false;
		} else if (!a2.equals(other.a2))
			return false;
		if (a3 == null) {
			if (other.a3 != null)
				return false;
		} else if (!a3.equals(other.a3))
			return false;
		if (a4 == null) {
			if (other.a4 != null)
				return false;
		} else if (!a4.equals(other.a4))
			return false;
		if (a5 == null) {
			if (other.a5 != null)
				return false;
		} else if (!a5.equals(other.a5))
			return false;
		if (a6 == null) {
			if (other.a6 != null)
				return false;
		} else if (!a6.equals(other.a6))
			return false;
		return true;
	}
	
	

	
}
