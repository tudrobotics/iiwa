package org.tud.schunk.gripper;

public interface SchunkGripperServerListener {

	public enum SchunkGripperServerState {RUNNING, STOPPING, ERROR, CONNECTED}
	
	public void notify(SchunkGripperServerState state);
}
