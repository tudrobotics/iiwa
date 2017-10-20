package org.tud.schunk.gripper;

import org.tud.kuka.util.ByteUtil;
import org.tud.schunk.gripper.SchunkGripperCommand.AutomaticUpdate;
import org.tud.schunk.gripper.SchunkGripperCommand.FingerIndex;
import org.tud.schunk.gripper.SchunkGripperCommand.HomeDirection;
import org.tud.schunk.gripper.SchunkGripperCommand.MovementType;
import org.tud.schunk.gripper.SchunkGripperCommand.PowerType;
import org.tud.schunk.gripper.SchunkGripperCommand.ResetStatistics;
import org.tud.schunk.gripper.SchunkGripperCommand.SensitiveUpdate;
import org.tud.schunk.gripper.SchunkGripperCommand.StopType;


public class SchunkGripperCommandSet {


	public static final int LOOP_COMMAND = 0x06;
	public static final int DISCONNECT_COMMAND = 0x07;
	public static final int HOMEPOSITION_COMMAND = 0x20;
	public static final int PREPOSFINGERS_COMMAND = 0x21;
	public static final int STOP_COMMAND = 0x22;
	public static final int FASTSTOP_COMMAND = 0x23;
	public static final int ACKFASTSTOPORFAULTCOMMAND = 0x24;
	public static final int GRASPPART_COMMAND = 0x25;
	
	public SchunkGripperCommand loopCommand(int[] payLoad) {
		SchunkGripperCommand s = new SchunkGripperCommand(LOOP_COMMAND);
		s.setPayload(payLoad);
		return s;
	}
	public SchunkGripperCommand disconnectCommand() {
		return new SchunkGripperCommand(DISCONNECT_COMMAND);
	}
	public SchunkGripperCommand homePositionCommand(HomeDirection homeDirection) {
		SchunkGripperCommand s = new SchunkGripperCommand(HOMEPOSITION_COMMAND);
		s.setPayload(new int[]{homeDirection.getValue()});
		return s;
	}
	/**
	 * 
	 * @param stopType
	 * @param movementType
	 * @param width
	 *            in mm
	 * @param speed
	 *            in mm/s
	 * @return
	 */
	public SchunkGripperCommand prePositionFingersCommand(StopType stopType,
			MovementType movementType, float width, float speed) {
		SchunkGripperCommand s = new SchunkGripperCommand(PREPOSFINGERS_COMMAND);
		int[] payLoad = new int[9];
		if (movementType == MovementType.RelativeMotion)
			payLoad[0] |= 0x01;
		if (stopType == StopType.StopOnBlock)
			payLoad[0] |= 0x02;
		int[] byteArray = ByteUtil.float2IntArray(width);
		payLoad[1] = byteArray[0];
		payLoad[2] = byteArray[1];
		payLoad[3] = byteArray[2];
		payLoad[4] = byteArray[3];
		byteArray = ByteUtil.float2IntArray(speed);
		payLoad[5] = byteArray[0];
		payLoad[6] = byteArray[1];
		payLoad[7] = byteArray[2];
		payLoad[8] = byteArray[3];
		s.setPayload(payLoad);
		return s;
	}
	/**
	 * Immediately stops any ongoing finger movement. The command sets the SF_AXIS_STOPPED flag. The
AXIS STOPPED state does not need to be acknowledged; it is cleared automatically by the next motion-
related command.
	 * @return
	 */
	public SchunkGripperCommand stopCommand() {
		return new SchunkGripperCommand(STOP_COMMAND);
	}
	/**
	 * This function is similar to an “Emergency Stop”. It immediately stops any finger movement the fastest
	 * way and prevents further motion-related commands from being executed. The FAST STOP state
can only be left by issuing a FAST STOP Acknowledge message. All motion-related commands are
prohibited during FAST STOP and will produce an E_ACCESS_DENIED error.
The FAST STOP state is indicated in the System Flags and logged in the system’s log file, so this command
should in general be used to react on certain error conditions.
	 * @return
	 */
	public SchunkGripperCommand fastStopCommand() {
		return new SchunkGripperCommand(FASTSTOP_COMMAND);
	}
	/**
	 * A previously issued FAST STOP or a severe error condition must be acknowledged using this command
	 * to bring the WSG back into normal operating mode.
	 * 
	 * @return
	 */
	public SchunkGripperCommand acknowledgeingFastStopOrFaultCommand() {
		SchunkGripperCommand s = new SchunkGripperCommand(ACKFASTSTOPORFAULTCOMMAND);
		s.setPayload(new int[] {0x61, 0x63, 0x6B});
		return s;
	}
	/**
	 * 
	 * @param width
	 *            in mm
	 * @param speed
	 *            in mm/s
	 * @return
	 */
	public SchunkGripperCommand graspPartCommand(float width, float speed) {
		SchunkGripperCommand s = new SchunkGripperCommand(GRASPPART_COMMAND);
		int[] payLoad = new int[8];
		int[] byteArray = ByteUtil.float2IntArray(width);
		payLoad[0] = byteArray[0];
		payLoad[1] = byteArray[1];
		payLoad[2] = byteArray[2];
		payLoad[3] = byteArray[3];
		byteArray = ByteUtil.float2IntArray(speed);
		payLoad[4] = byteArray[0];
		payLoad[5] = byteArray[1];
		payLoad[6] = byteArray[2];
		payLoad[7] = byteArray[3];
		s.setPayload(payLoad);
		return s;
	}

	
	/**
	 * 
	 * @param openWidth
	 *            in mm
	 * @param speed
	 *            in mm/s
	 * @return
	 */
	public SchunkGripperCommand releasePartCommand(float openWidth, float speed) {
		int[] payLoad = new int[8];
		int[] byteArray = ByteUtil.float2IntArray(openWidth);
		payLoad[0] = byteArray[0];
		payLoad[1] = byteArray[1];
		payLoad[2] = byteArray[2];
		payLoad[3] = byteArray[3];
		byteArray = ByteUtil.float2IntArray(speed);
		payLoad[4] = byteArray[0];
		payLoad[5] = byteArray[1];
		payLoad[6] = byteArray[2];
		payLoad[7] = byteArray[3];
		SchunkGripperCommand s = new SchunkGripperCommand(0x26);
		s.setPayload(payLoad);
		return s;
	}

	public static final int SETACCELERATION_COMMAND = 0x30;
	/**
	 * 
	 * @param acceleration
	 *            in mm/s^2
	 * @return
	 */
	public SchunkGripperCommand setAccelerationCommand(float acceleration) {
		int[] payLoad = ByteUtil.float2IntArray(acceleration);
		SchunkGripperCommand s = new SchunkGripperCommand(SETACCELERATION_COMMAND);
		s.setPayload(payLoad);
		return s;
	}

	public static final int GETACCELERATION_COMMAND = 0x31;
	public SchunkGripperCommand getAccelerationCommand() {
		return new SchunkGripperCommand(GETACCELERATION_COMMAND);
	}

	public static final int SETFORCELIMIT_COMMAND = 0x32;
	/**
	 * 
	 * @param force in N
	 * @return
	 */
	public SchunkGripperCommand setForceLimitCommand(float force) {
		int[] payLoad = ByteUtil.float2IntArray(force);
		SchunkGripperCommand s = new SchunkGripperCommand(SETFORCELIMIT_COMMAND);
		s.setPayload(payLoad);
		return s;
	}
	public static final int GETFORCELIMIT_COMMAND = 0x33;
	/**
	 * Return the force limit that was previously set by the Set Force Limit command.
	 * @return
	 */
	public SchunkGripperCommand getForceLimitCommand() {
		return new SchunkGripperCommand(GETFORCELIMIT_COMMAND);
	}
	
	public static final int SETSOFTLIMIT_COMMAND = 0x34;
	/**
	 * 
	 * @param minusLimit
	 *            in mm
	 * @param plusLimit
	 *            in mm
	 * @return
	 */
	public SchunkGripperCommand getSoftLimitCommand(float minusLimit, float plusLimit) {
		int[] payLoad = new int[8];
		int[] byteArray = ByteUtil.float2IntArray(minusLimit);
		payLoad[0] = byteArray[0];
		payLoad[1] = byteArray[1];
		payLoad[2] = byteArray[2];
		payLoad[3] = byteArray[3];
		byteArray = ByteUtil.float2IntArray(plusLimit);
		payLoad[4] = byteArray[0];
		payLoad[5] = byteArray[1];
		payLoad[6] = byteArray[2];
		payLoad[7] = byteArray[3];
		SchunkGripperCommand s = new SchunkGripperCommand(GETSOFTLIMIT_COMMAND);
		s.setPayload(payLoad);
		return s;
	}

	public static final int GETSOFTLIMIT_COMMAND = 0x35;
	
	public SchunkGripperCommand getSoftLimitsCommand() {
		return new SchunkGripperCommand(GETSOFTLIMIT_COMMAND);
	}

	public static final int CLEARSOFTLIMITS_COMMAND = 0x36;
	public SchunkGripperCommand clearSoftLimitsCommand() {
		return new SchunkGripperCommand(CLEARSOFTLIMITS_COMMAND);
	}

	private static final int TAREFORCESENSOR_COMMAND = 0x38;
	/**
	 * Zeroes the connected force sensor used for the force control loop.
	 * This command is only allowed if not in force control mode (i.e. the grasping state must not be
		HOLDING when issuing this command)
	 * @return
	 */
	public SchunkGripperCommand tareForceSensorCommand() {
		return new SchunkGripperCommand(TAREFORCESENSOR_COMMAND);
	}

	public static final int SYSTEMSTATE_COMMAND = 0X40;
	/** System State Commands
	 */

	/**
	 * Get the current system state. This command supports the automatic transmission of update packets
		in either fixed time intervals or if the system state changes. This gives you a precise control over the
		bus load of your system.
		When sending this command with automatic updates disabled (FLAGS’0=0), one return packet containing
		the current system state is immediately returned.
	 * @param sensitiveUpdate
	 * @param autoUpdate
	 * @param milliseconds
	 * @return
	 */
	public SchunkGripperCommand getSystemStateCommand(SensitiveUpdate sensitiveUpdate,
			AutomaticUpdate autoUpdate, int milliseconds) {
		SchunkGripperCommand s = new SchunkGripperCommand(SYSTEMSTATE_COMMAND);
		int[] payLoad = new int[3];
		if (sensitiveUpdate == SensitiveUpdate.UpdateOnChange)
			payLoad[0] |= 0x02;
		if (autoUpdate == AutomaticUpdate.Enabled)
			payLoad[0] |= 0x01;
		payLoad[1] = milliseconds & 0xFF;
		payLoad[2] = (milliseconds >> 8) & 0xFF;
		s.setPayload(payLoad);
		return s;
	}
	
	public static final int GRASPINGSTATE_COMMAND = 0x41;
	/**
	 * Get the current grasping state. The grasping state can be used to control and monitor the grasping
process. The following states are possible and will be encoded into a single number:

Idle (0)
The grasping process is in idle state and is waiting for a command.
Grasping (1)
The fingers are currently closing to grasp a part. The part has not been grasped, yet
No part found (2)
The fingers have been closed, but no part was found at the specified nominal width within
the given clamping range. This state will persist until the next Grasp Part, Release Part or Preposition
Fingers command is issued.
Part lost (3)
A part was grasped but then lost before the fingers have been opened again. This state will
persist until the next Grasp Part, Release Part or Pre-position Fingers command is issued.
Holding (4)
A part was grasped successfully and is now being hold with the grasping force.
Releasing (5)
The fingers are currently opening towards the opening width to release a part.
Positioning (6)
The fingers are currently pre-positioned using a Pre-position Fingers command.
Error (7)
An error occurred during the grasping process. This state will persist until the next Grasp
Part, Release Part or Pre-position Fingers command is issued.

	 * @param sensitiveUpdate
	 * @param autoUpdate
	 * @param milliseconds
	 * @return
	 */
	public SchunkGripperCommand getGraspingStateCommand(SensitiveUpdate sensitiveUpdate,
			AutomaticUpdate autoUpdate, int milliseconds) {
		SchunkGripperCommand s = new SchunkGripperCommand(GRASPINGSTATE_COMMAND);
		int[] payLoad = new int[3];
		if (sensitiveUpdate == SensitiveUpdate.UpdateOnChange)
			payLoad[0] |= 0x02;
		if (autoUpdate == AutomaticUpdate.Enabled)
			payLoad[0] |= 0x01;
		payLoad[1] = milliseconds & 0xFF;
		payLoad[2] = (milliseconds >> 8) & 0xFF;
		s.setPayload(payLoad);
		return s;
	}


	public static final int GRASPINGSTATISTICS_COMMAND = 0x42;
	
	public SchunkGripperCommand getGraspingStatisticsCommand(
			ResetStatistics resetStatistics) {
		int[] payLoad = new int[1];
		if (resetStatistics == ResetStatistics.ResetGraspingStatisticsAfterReading)
			payLoad[0] |= 0x01;
		SchunkGripperCommand s = new SchunkGripperCommand(GRASPINGSTATISTICS_COMMAND);
		s.setPayload(payLoad);
		return s;
	}

	public static final int OPENINGWIDTH_COMMAND = 0x43;
	
	public SchunkGripperCommand getOpeningWidthCommand(SensitiveUpdate sensitiveUpdate,
			AutomaticUpdate autoUpdate, int milliseconds) {
		SchunkGripperCommand s = new SchunkGripperCommand(OPENINGWIDTH_COMMAND);
		int[] payLoad = new int[3];
		if (sensitiveUpdate == SensitiveUpdate.UpdateOnChange)
			payLoad[0] |= 0x02;
		if (autoUpdate == AutomaticUpdate.Enabled)
			payLoad[0] |= 0x01;
		payLoad[1] = milliseconds & 0xFF;
		payLoad[2] = (milliseconds >> 8) & 0xFF;
		s.setPayload(payLoad);
		return s;
	}

	public static final int SPEED_COMMAND = 0x44;
	
	public SchunkGripperCommand getSpeedCommand(SensitiveUpdate sensitiveUpdate,
			AutomaticUpdate autoUpdate, int milliseconds) {
		SchunkGripperCommand s = new SchunkGripperCommand(SPEED_COMMAND);
		int[] payLoad = new int[3];
		if (sensitiveUpdate == SensitiveUpdate.UpdateOnChange)
			payLoad[0] |= 0x02;
		if (autoUpdate == AutomaticUpdate.Enabled)
			payLoad[0] |= 0x01;
		payLoad[1] = milliseconds & 0xFF;
		payLoad[2] = (milliseconds >> 8) & 0xFF;
		s.setPayload(payLoad);
		return s;
	}

	public static final int FORCE_COMMAND = 0x45;
	
	public SchunkGripperCommand getForceCommand(SensitiveUpdate sensitiveUpdate,
			AutomaticUpdate autoUpdate, int milliseconds) {
		SchunkGripperCommand s = new SchunkGripperCommand(FORCE_COMMAND);
		int[] payLoad = new int[3];
		if (sensitiveUpdate == SensitiveUpdate.UpdateOnChange)
			payLoad[0] |= 0x02;
		if (autoUpdate == AutomaticUpdate.Enabled)
			payLoad[0] |= 0x01;
		payLoad[1] = milliseconds & 0xFF;
		payLoad[2] = (milliseconds >> 8) & 0xFF;
		s.setPayload(payLoad);
		return s;
	}

	public static final int TEMPERATURE_COMMAND = 0x46;
	
	public SchunkGripperCommand getTemperatureCommand() {
		return new SchunkGripperCommand(TEMPERATURE_COMMAND);
	}
	
	// System Configuration
	
	public static final int SYSTEMINFORAMTION_COMMAND = 0x50;

	public SchunkGripperCommand getSystemInformationCommand() {
		return new SchunkGripperCommand(SYSTEMINFORAMTION_COMMAND);
	}
	
	public static final int SETDEVICETAG_COMMAND = 0x51;
	public SchunkGripperCommand setDeviceTagCommand(String tag) {
		if(tag.length() > 64) tag = tag.substring(0, 63);
		SchunkGripperCommand s = new SchunkGripperCommand(SETDEVICETAG_COMMAND);
		s.setPayload(ByteUtil.byteArray2IntArray(tag.getBytes()));
		return s;
	}
	
	public static final int GETDEVICETAG_COMMAND = 0x52;
	public SchunkGripperCommand getDeviceTagCommand() {
		return new SchunkGripperCommand(GETDEVICETAG_COMMAND);
	}
	public static final int SYSTEMLIMITS_COMMAND = 0x53;
	public SchunkGripperCommand getSystemLimitsCommand() {
		return new SchunkGripperCommand(SYSTEMLIMITS_COMMAND);
	}

	// Finger Interface
	public static final int FINGERINFO_COMMAND = 0x60;
	public SchunkGripperCommand getFingerInfoCommand(FingerIndex finger) {
		SchunkGripperCommand s = new SchunkGripperCommand(FINGERINFO_COMMAND);
		s.setPayload(new int[]{finger.getValue()});
		return s;
		
	}
	public static final int FINGERFLAGS_COMMAND = 0x61;
	public SchunkGripperCommand getFingerFlags(FingerIndex finger) {
		SchunkGripperCommand s = new SchunkGripperCommand(FINGERFLAGS_COMMAND);
		s.setPayload(new int[]{finger.getValue()});
		return s;
	}
	public static final int FINGERPOWERCONTROLL_COMMAND = 0x62;
	public SchunkGripperCommand getFingerPowerControlCommand(FingerIndex finger, PowerType power){
		SchunkGripperCommand s = new SchunkGripperCommand(FINGERPOWERCONTROLL_COMMAND);
		int[] payLoad = new int[2];
		payLoad[0] = finger.getValue();
		payLoad[1] = power.getValue();
		s.setPayload(payLoad);
		return s;
	}
	public static final int FINGERDATA_COMMAND = 0x63;
	
	public SchunkGripperCommand getFingerDataCommand(FingerIndex finger) {
		SchunkGripperCommand s = new SchunkGripperCommand(FINGERDATA_COMMAND);
		s.setPayload(new int[]{finger.getValue()});
		return s;
	}
	
}
