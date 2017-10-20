package org.tud.kuka.motion;

import static com.kuka.roboticsAPI.motionModel.BasicMotions.positionHold;
import static com.kuka.roboticsAPI.motionModel.BasicMotions.ptp;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.kuka.roboticsAPI.geometricModel.ObjectFrame;
import com.kuka.roboticsAPI.motionModel.Motion;
import com.kuka.roboticsAPI.motionModel.MotionBatch;
import com.kuka.roboticsAPI.motionModel.PositionHold;
import com.kuka.roboticsAPI.motionModel.RobotMotion;
import com.kuka.roboticsAPI.motionModel.controlModeModel.PositionControlMode;

public class MotionFactory {

	/**
	 * 
	 * @param frames
	 * @return
	 */
	public static MotionBatch createPTPMotionBatch(List<ObjectFrame> frames) {
		RobotMotion<?>[] motion = new RobotMotion[frames.size()];
		for(int i = 0; i< frames.size(); i++) {
			motion[i] = ptp(frames.get(i));
		}
		return new MotionBatch(motion);
	}
	
	public static Motion<PositionHold> createHoldMotion(int seconds) {
		return positionHold(new PositionControlMode(),seconds, TimeUnit.SECONDS);
	}
}
