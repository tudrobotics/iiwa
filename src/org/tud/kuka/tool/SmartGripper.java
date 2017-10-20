package org.tud.kuka.tool;

import java.util.LinkedList;
import java.util.List;

import org.tud.kuka.util.LimitedQueue;
import org.tud.log.Logger;
import org.tud.schunk.gripper.SchunkGripper;
import org.tud.schunk.gripper.SchunkGripperAcknowledge;
import org.tud.schunk.gripper.SchunkGripperAcknowledge.StatusCode;
import org.tud.schunk.gripper.SchunkGripperCommand;
import org.tud.schunk.gripper.SchunkGripperCommandSet;
import org.tud.schunk.gripper.listener.SchunkGripperForceListener;
import org.tud.schunk.gripper.listener.SchunkGripperGraspingStateListener.GraspingState;
import org.tud.schunk.gripper.listener.SchunkGripperAcknowledgementListener;
import org.tud.schunk.gripper.listener.SchunkGripperWidthListener;

public class SmartGripper extends Thread implements SchunkGripperAcknowledgementListener, SchunkGripperWidthListener {
	private SchunkGripper gripper;
	private LimitedQueue<Float> lastWidths;
	private LimitedQueue<Float> lastForce;
	private volatile float grippingWidth = -1;
	private List<SmartGripperListener> listeners;
	private volatile double widthRelative;
	private double lastWidthRelative;
	
	private static final List<Integer> listenToList = new LinkedList<Integer>();
	
	public SmartGripper(SchunkGripper gripper) {
		this.gripper = gripper;
		if(!this.gripper.connect()) {
			Logger.error("connecting to gripper failed --> power off");
		}
		listenToList.add(SchunkGripperCommandSet.PREPOSFINGERS_COMMAND);
		
		this.gripper.addAckListener(this);
		lastWidths = new LimitedQueue<Float>(3);
		lastForce = new LimitedQueue<Float>(3);
		this.gripper.addWidthListener(this);
		//this.gripper.addForceListener(this);
		
		listeners = new LinkedList<SmartGripperListener>();
	}
	
	public void addListener(SmartGripperListener listener) {
		listeners.add(listener);
	}
	public void removeListener(SmartGripperListener listener) {
		listeners.remove(listener);
	}
	
	
	// das ist der fall wenn 3 mal nacheinander keine 0 Kraft angelegen hat
	// das funktioniert nicht !!
	// denn bei jeden open Befehl geht die Kraft von 0 über einen hohen Wert wieder auf 0
	@Deprecated
	private boolean isForceApplied() {
		if(lastForce.size() != 3) return false;
		for(int i = 0; i < 2; i++) {
			if(lastForce.get(i) == null) return false;
			if(Math.abs(lastForce.get(i)) < 0.01f) return false;
		}
		return true;
	}
	// das ist der fall wenn 3 mal hinterenander eine sehr ähniche öffnungsweite des Greifers gemessen wurde
	// auf die weitenabgabe kann man sich auch nicht verlassen
	@Deprecated
	private boolean isConstantWidth() {
		if(lastWidths.size() != 3) return false;
		for(int i = 0; i < 2; i++) {
			if(lastWidths.get(i) == null) return false;
		}
		if(Math.abs(lastWidths.get(0) - lastWidths.get(1)) > 2.0) return false;
		if(Math.abs(lastWidths.get(1) - lastWidths.get(2)) > 2.0) return false;
		return true;
	}
	
	public void work(double widthRelative) {
		this.widthRelative = widthRelative;
		synchronized (this) {
			this.notify();
		}
	}
	private void workPrivate(double widthRelative) {
		lastWidthRelative = widthRelative;
		if(grippingWidth != -1  && gripper.calculateAbsoluteWidth(widthRelative) > grippingWidth){
			//System.out.println("RELEASE");
			grippingWidth = -1;
			gripper.clampSync(widthRelative, 200);
			notifyListeners(GraspingState.RELEASING);
			return;
		}
		if(grippingWidth == -1) {
			//System.out.println("Moving");
			gripper.clampSync(widthRelative);
			return;
		}
	}
	

	@Override
	public void notifyWidth(float width) {
		lastWidths.add(gripper.getWidth());
	}
	
	private void notifyListeners(GraspingState state) {
		for(SmartGripperListener l: listeners) {
			l.notify(state);
		}
	}
	@Override
	public void run() {
		Logger.info("SmartGripper is running");
		while(!isInterrupted()) {
			if(widthRelative != lastWidthRelative) {
				workPrivate(widthRelative);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Automatisch generierter Erfassungsblock
					e.printStackTrace();
				}
			} else {
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						// TODO Automatisch generierter Erfassungsblock
						e.printStackTrace();
					}
				}
			}
		}
		Logger.info("SmartGripper is finished");
	}

	public void disconnect() {
		this.interrupt();
		synchronized (this) {
			this.notify();
		}
		
	}

	@Override
	public List<Integer> listenTo() {
		return listenToList;
	}

	@Override
	public void notifyAcknowledgement(int command, SchunkGripperAcknowledge ack) {
		if(command == SchunkGripperCommandSet.PREPOSFINGERS_COMMAND && ack.getStatusCode().equals(StatusCode.E_AXIS_BLOCKED)) {
			grippingWidth = gripper.getWidth();
			notifyListeners(GraspingState.GRASPING);
			//System.out.println("Gripping@:"+gripper.getWidth());
		}	
		
	}
}