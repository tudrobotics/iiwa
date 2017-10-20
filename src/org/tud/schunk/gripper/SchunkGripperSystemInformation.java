package org.tud.schunk.gripper;

import java.util.HashMap;
import java.util.Map;

public class SchunkGripperSystemInformation {

	public enum DeviceType {
		UNKNOWN(0),
		WSG_50(1),
		WSG_32(2),
		Force_Torqe_Sensor_KMS_40(3),
		Tactile_Sensing_Module_WTS(4);
		
		private final int value;
	    private DeviceType(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	    private static final Map<Integer, DeviceType> intToTypeMap = new HashMap<Integer, DeviceType>();
	    static {
	        for (DeviceType type : DeviceType.values()) {
	            intToTypeMap.put(type.value, type);
	        }
	    }

	    public static DeviceType fromInt(int i) {
	    	DeviceType type = intToTypeMap.get(Integer.valueOf(i));
	        if (type == null) 
	            return DeviceType.UNKNOWN;
	        return type;
	    }
	}
	
	private DeviceType type;
	private int hardwareRevision;
	private int firmwareNumber;
	private int serialNumber;
	public DeviceType getType() {
		return type;
	}
	public void setType(DeviceType type) {
		this.type = type;
	}
	public int getHardwareRevision() {
		return hardwareRevision;
	}
	public void setHardwareRevision(int hardwareRevision) {
		this.hardwareRevision = hardwareRevision;
	}
	public int getFirmwareNumber() {
		return firmwareNumber;
	}
	public void setFirmwareNumber(int firmwareNumber) {
		this.firmwareNumber = firmwareNumber;
	}
	public int getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	
	
}
