package org.tud.schunk.gripper.listener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface SchunkGripperSystemStateListener {

	public enum SystemState {
		SF_SCRIPT_FAILURE(20),
		SF_SCRIPT_RUNNING(19),
		SF_CMD_FAILURE(18),
		SF_FINGER_FAULT(17),
		SF_CURR_FAULT(16),
		SF_POWER_FAULT(15),
		SF_TEMP_FAULT(14),
		SF_TEMP_WARNING(13),
		SF_FAST_STOP(12),
		SF_FORCECNTL_MODE(9),
		SF_TARGET_POS_REACHED(7),
		SF_AXIS_STOPPED(6),
		SF_SOFT_LIMIT_PLUS(5),
		SF_SOFT_LIMIT_MINUS(4),
		SF_BLOCKED_PLUS(3),
		SF_BLOCKED_MINUS(2),
		SF_MOVING(1),
		SF_REFERENCED(0),
		UNKNOWN(-1);
		
		private final int value;
	    private SystemState(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	    private static final Map<Integer, SystemState> intToTypeMap = new HashMap<Integer, SystemState>();
	    static {
	        for (SystemState type : SystemState.values()) {
	            intToTypeMap.put(type.value, type);
	        }
	    }

	    public static List<SystemState> fromIntArray(int[] array) {
	    	List<SystemState> results = new LinkedList<SystemState>();
	    	if(array.length != 4) {
	    		results.add(SystemState.UNKNOWN);
	    		return results;
	    	}
	    	for(int i = 0; i < 4;i++) {
	    		String s = Integer.toBinaryString(array[i]);
	    		for(int j = 0; j < s.length(); j++) {		
	    			if(s.charAt(j) == '1') {
	    				SystemState r = intToTypeMap.get((i*8)+7-j);
	    				if(r != null) results.add(r);
	    			}
	    		}
	    	}
	    	if(results.isEmpty()) results.add(SystemState.UNKNOWN);
	    	return results;
	    }
	}
	
	void notifySystemState(List<SystemState> states);
}
