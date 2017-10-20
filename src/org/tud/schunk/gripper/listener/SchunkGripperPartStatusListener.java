package org.tud.schunk.gripper.listener;

import org.tud.schunk.gripper.SchunkGripperAcknowledge.StatusCode;

public interface SchunkGripperPartStatusListener {

	void notifyPartStatus(StatusCode statusCode);
}
