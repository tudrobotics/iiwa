package org.tud.kuka.motion;

import java.util.Set;

import com.kuka.roboticsAPI.conditionModel.ICondition;
import com.kuka.roboticsAPI.conditionModel.JointTorqueCondition;
import com.kuka.roboticsAPI.deviceModel.JointEnum;
import com.kuka.roboticsAPI.deviceModel.LBR;

public class ConditionFactory {

	public static ICondition createTorqueCondition(LBR roboter, Set<JointEnum> joints, double torqueAbsolute) {
		ICondition conditions = null;
		JointTorqueCondition c = null;
		for(JointEnum e: joints) {
			c = new JointTorqueCondition(roboter, e, -Math.abs(torqueAbsolute), Math.abs(torqueAbsolute));
			if(conditions == null) {
				conditions = c;
			} else {
				conditions.or(c);
			}
			
		}
		return conditions;
	}

}
