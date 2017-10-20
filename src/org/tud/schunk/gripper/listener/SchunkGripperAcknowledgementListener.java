package org.tud.schunk.gripper.listener;

import java.util.List;

import org.tud.schunk.gripper.SchunkGripperAcknowledge;

public interface SchunkGripperAcknowledgementListener {

	List<Integer> listenTo();
	
	void notifyAcknowledgement(int command, SchunkGripperAcknowledge ack);
}
