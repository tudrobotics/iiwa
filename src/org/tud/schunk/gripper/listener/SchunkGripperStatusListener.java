package org.tud.schunk.gripper.listener;

import org.tud.schunk.gripper.SchunkGripperAcknowledge.StatusCode;

public interface SchunkGripperStatusListener {

	void notifyStatus(StatusCode statusCode);
}
