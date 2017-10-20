package org.tud.schunk.gripper;

import org.tud.kuka.util.ByteUtil;


/**
 * 0..3 STROKE float Gripper stroke in mm
4..7 MIN_SPEED float Minimum speed in mm/s
8..11 MAX_SPEED float Maximum speed in mm/s
12..15 MIN_ACC float Minimum acceleration in mm/s²
16..19 MAX_ACC float Maximum acceleration in mm/s²
20..23 MIN_FORCE float Minimum grasping force in N
24..27 NOM_FORCE float Nominal grasping force in N (duty cycle of 100%)
28..31 OVR_FORCE float
Maximum overdrive grasping force in N (corresponds to nominal
grasping force
 * @author Jan
 *
 */
public class SchunkGripperSystemLimits {
	// das ist die maximale öffnungsbreite beider Backen also 110 mm
	private float stroke;
	private float minSpeed;
	private float maxSpeed;
	private float minAcceleration;
	private float maxAcceleration;
	private float minForce;
	private float maxForce;
	private float nomForce;
	/**
	 * set default values from specification
	 * @param ack 
	 */
	public SchunkGripperSystemLimits() {
		this.stroke = 110;
		this.minSpeed = 10;
		this.maxSpeed = 420;
		this.minAcceleration = 100;
		this.maxAcceleration = 800;
		this.minForce = 5;
		this.nomForce = 80;
		this.maxForce = 120;
	}
	public SchunkGripperSystemLimits(SchunkGripperAcknowledge ack) {
		int[] t = ack.getParameter();
		this.stroke = ByteUtil.array2Float(new int[] {t[0],t[1],t[2],t[3]});
		this.minSpeed = ByteUtil.array2Float(new int[] {t[4],t[5],t[6],t[7]});
		this.maxSpeed = ByteUtil.array2Float(new int[] {t[8],t[9],t[10],t[11]});
		this.minAcceleration = ByteUtil.array2Float(new int[] {t[12],t[13],t[14],t[15]});
		this.maxAcceleration = ByteUtil.array2Float(new int[] {t[16],t[17],t[18],t[19]});
		this.minForce = ByteUtil.array2Float(new int[] {t[20],t[21],t[22],t[23]});
		this.nomForce = ByteUtil.array2Float(new int[] {t[24],t[25],t[26],t[27]});
		this.maxForce = ByteUtil.array2Float(new int[] {t[28],t[29],t[30],t[31]});
		
	}
	public float getStroke() {
		return stroke;
	}
	public void setStroke(float stroke) {
		this.stroke = stroke;
	}
	public float getMinSpeed() {
		return minSpeed;
	}
	public void setMinSpeed(float minSpeed) {
		this.minSpeed = minSpeed;
	}
	public float getMaxSpeed() {
		return maxSpeed;
	}
	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	public float getMinAcceleration() {
		return minAcceleration;
	}
	public void setMinAcceleration(float minAcceleration) {
		this.minAcceleration = minAcceleration;
	}
	public float getMaxAcceleration() {
		return maxAcceleration;
	}
	public void setMaxAcceleration(float maxAcceleration) {
		this.maxAcceleration = maxAcceleration;
	}
	public float getMinForce() {
		return minForce;
	}
	public void setMinForce(float minForce) {
		this.minForce = minForce;
	}
	public float getMaxForce() {
		return maxForce;
	}
	public void setMaxForce(float maxForce) {
		this.maxForce = maxForce;
	}
	public float getNomForce() {
		return nomForce;
	}
	public void setNomForce(float nomForce) {
		this.nomForce = nomForce;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("stroke:"+stroke+"\n");
		b.append("minSpeed:"+minSpeed+"\n");
		b.append("maxSpeed:"+maxSpeed+"\n");
		b.append("minAcceleration:"+minAcceleration+"\n");
		b.append("maxAcceleration:"+maxAcceleration+"\n");
		b.append("minForce"+minForce+"\n");
		b.append("maxForce"+maxForce+"\n");
		b.append("nomForce:"+nomForce+"\n");
		
		return b.toString();
	}
	
	
}
