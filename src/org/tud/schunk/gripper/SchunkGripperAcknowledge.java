package org.tud.schunk.gripper;


import java.util.HashMap;
import java.util.Map;

public class SchunkGripperAcknowledge extends SchunkGripperMessage {

	private StatusCode statusCode;
	private int[] parameter;
	
	public enum StatusCode {
		E_SUCCESS(0), 
		E_NOT_AVAILABLE(1), 
		E_NO_SENSOR(2), 
		E_NOT_INITIALIZED(3), 
		E_ALREADY_RUNNING(4), 
		E_FEATURE_NOT_SUPPORTED(5), 
		E_INCONSISTENT_DATA(6), 
		E_TIMEOUT(7), 
		E_READ_ERROR(8), 
		E_WRITE_ERROR(9), 
		E_INSUFFICIENT_RESOURCES(10), 
		E_CHECKSUM_ERROR(11), 
		E_NO_PARAM_EXPECTED(12), 
		E_NOT_ENOUGH_PARAMS(13), 
		E_CMD_UNKNOWN(14), 
		E_CMD_FORMAT_ERROR(15), 
		E_ACCESS_DENIED(16), 
		E_ALREADY_OPEN(17), 
		E_CMD_FAILED(18), 
		E_CMD_ABORTED(19), 
		E_INVALID_HANDLE(20), 
		E_NOT_FOUND(21), 
		E_NOT_OPEN(22), 
		E_IO_ERROR(23), 
		E_INVALID_PARAMETER(24), 
		E_INDEX_OUT_OF_BOUNDS(25), 
		E_CMD_PENDING(26), 
		E_OVERRUN(27), 
		E_RANGE_ERROR(28), 
		E_AXIS_BLOCKED(29), 
		E_FILE_EXISTS(30);
		
		private final int value;
	    private StatusCode(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	    private static final Map<Integer, StatusCode> intToTypeMap = new HashMap<Integer, StatusCode>();
	    static {
	        for (StatusCode type : StatusCode.values()) {
	            intToTypeMap.put(type.value, type);
	        }
	    }

	    public static StatusCode fromInt(int i) {
	    	StatusCode type = intToTypeMap.get(Integer.valueOf(i));
	        if (type == null) 
	            return StatusCode.E_NOT_AVAILABLE;
	        return type;
	    }
	}
	
	public SchunkGripperAcknowledge(int commandId) {
		super(commandId);
		parameter = new int [0];
		statusCode = StatusCode.E_NOT_AVAILABLE;
	}

	public StatusCode getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(StatusCode statusCode) {
		this.statusCode = statusCode;
	}

	public int[] getParameter() {
		return parameter;
	}

	public void setParameter(int[] parameter) {
		this.parameter = parameter;
	}
	
	public void setCrc(int[] crc) {
		this.crc[0] = crc[0];
		this.crc[1] = crc[1];
	}
	
	public int[] toArray() {
		int[] data = new int[6 + 2 + 2 + parameter.length];
		data[0] = preamble0;
		data[1] = preamble1;
		data[2] = preamble2;
		data[3] = commandId;
		data[4] = size;
		data[5] = (size >> 8);
		data[6] = statusCode.getValue();
		data[7] = (statusCode.getValue() >> 8);
		for(int i = 0; i < parameter.length;i++) {
			data[i+8] = parameter[i];
		}
		int[] generatedCrC = generateCrcCode(data);
		data[data.length-2] = generatedCrC[0];
		data[data.length-1] = generatedCrC[1];
		return data;
	}
	public boolean isValid() {
		int[] crc = generateCrcCode(toArray());
		// TODO die crc condes von dem gerät sind fehlerhaft das muss noch untersucht werden
		return true;
	}

	public boolean matches(int commandId) {
		return this.commandId == commandId;
	}

	public boolean isSuccess() {
		return statusCode.equals(StatusCode.E_SUCCESS) || statusCode.equals(StatusCode.E_CMD_PENDING) || statusCode.equals(StatusCode.E_ALREADY_RUNNING);
	}
	@Override
	public String toString() {
		String s = "[Ack]:preamble:{"+preamble0+","+preamble1+","+preamble2+"},command:"+commandId+",size:"+size+",status:"+statusCode.toString()+",parameter:{";
		for(int i = 0; i < parameter.length; i++) {
			s += parameter[i]+",";
		}
		s += "},crc{"+crc[0]+","+crc[1]+"}";
		return s;
	}
	
}
