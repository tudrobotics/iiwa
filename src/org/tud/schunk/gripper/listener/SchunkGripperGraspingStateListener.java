package org.tud.schunk.gripper.listener;

import java.util.HashMap;
import java.util.Map;

public interface SchunkGripperGraspingStateListener {

	public enum GraspingState {
		IDLE(0),
		GRASPING(1),
		NO_PART(2),
		PART_LOST(3),
		HOLDING(4),
		RELEASING(5),
		POSITIONING(6),
		ERROR(7);
		
		private final int value;
	    private GraspingState(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	    private static final Map<Integer, GraspingState> intToTypeMap = new HashMap<Integer, GraspingState>();
	    static {
	        for (GraspingState type : GraspingState.values()) {
	            intToTypeMap.put(type.value, type);
	        }
	    }

	    public static GraspingState fromInt(int i) {
	    	GraspingState type = intToTypeMap.get(Integer.valueOf(i));
	        if (type == null) 
	            return GraspingState.ERROR;
	        return type;
	    }
	}
	void notifyGraspring(GraspingState state);
}
