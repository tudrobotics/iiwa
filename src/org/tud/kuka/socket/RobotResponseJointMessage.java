package org.tud.kuka.socket;

import com.kuka.roboticsAPI.deviceModel.JointPosition;


public class RobotResponseJointMessage extends AbstractRobotResponseMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5096566066145510317L;
	
	private Double a0;
	private Double a1;
	private Double a2;
	private Double a3;
	private Double a4;
	private Double a5;
	private Double a6;
	
	public RobotResponseJointMessage() {
		super();
	}

	public RobotResponseJointMessage(JointPosition joints) {
		super();
		a0 = joints.get(0);
		a1 = joints.get(1);
		a2 = joints.get(2);
		a3 = joints.get(3);
		a4 = joints.get(4);
		a5 = joints.get(5);
		a6 = joints.get(6);
		
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
	
	
	
	
	
	
	
	
	
}
