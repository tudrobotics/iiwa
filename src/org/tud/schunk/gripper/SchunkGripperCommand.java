package org.tud.schunk.gripper;


public class SchunkGripperCommand extends SchunkGripperMessage {
	
	private int[] payLoad;
	
	public SchunkGripperCommand(int commandId) {
		super(commandId);
		payLoad = new int[0];
	}
	
	public enum HomeDirection {
		DefaultSystemValue(0), 
		PositiveMovementDirection(1), 
		NegativeMovementDirection(2);
		
		private final int value;
	    private HomeDirection(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}

	public enum MovementType {
		RelativeMotion(1), AbsoluteMotion(0);
		
		private final int value;
	    private MovementType(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}

	public enum StopType {
		StopOnBlock(1), ClampOnBlock(0);
		
		private final int value;
	    private StopType(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}

	public enum SensitiveUpdate {
		UpdateOnChange(1), 
		UpdateAlways(0);
		
		private final int value;
	    private SensitiveUpdate(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}

	public enum AutomaticUpdate {
		Enabled(1),
		Disabled(0);
		
		private final int value;
	    private AutomaticUpdate(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}

	public enum ResetStatistics {
		ResetGraspingStatisticsAfterReading(1),
		DoNotReset(0);
		
		private final int value;
	    private ResetStatistics(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}
	public enum FingerIndex{
		LEFT(0), RIGHT(1);
		private final int value;
	    private FingerIndex(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}
	public enum FingerType{
		NO_FINGER(0), WSG_FMF(1), WSG_DSA(2);
		
		private final int value;
	    private FingerType(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}
	public enum FingerFlags { 
		POWER_ON(0),
		CONFIG_AVAIL(1),
		COMM_OPEN(2),
		POWER_FAULT(8),
		COMM_FAULT(9);
		private final int value;
	    private FingerFlags(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}
	public enum PowerType {
		ON(1),
		OFF(0);
		
		private final int value;
	    private PowerType(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}
	public void setPayload(int[] payLoad) {
		this.payLoad = payLoad;
		this.size = payLoad.length;
	}
	public int[] toArray() {
		int[] data = new int[8 + size];
		data[0] = preamble0;
		data[1] = preamble1;
		data[2] = preamble2;
		data[3] = commandId;
		data[4] = size;
		data[5] = (size >> 8);
		for(int i = 0; i < payLoad.length ;i++) {
			data[i+6] = payLoad[i];
		}
		crc = generateCrcCode(data);
		data[data.length - 2] = crc[0];
		data[data.length - 1] = crc[1];
		return data;
	}
	@Override
	public String toString() {
		String s = "[Command]:preamble:{"+preamble0+","+preamble1+","+preamble2+"},command:"+commandId+",size:"+size+",payload:{";
		for(int i = 0; i < payLoad.length; i++) {
			s += payLoad[i]+",";
		}
		s += "},crc{"+crc[0]+","+crc[1]+"},crcValid:"+isCrcCodeValid();
		return s;
	}

}
