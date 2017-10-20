package application.weir;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.kuka.common.ThreadUtil;
import com.kuka.connectivity.motionModel.directServo.IDirectServoRuntime;
import com.kuka.roboticsAPI.deviceModel.JointPosition;

public class DirectServoThread extends Thread {

	private static final long TIMEOUT = 100;
	
	private IDirectServoRuntime runtime;
	private BlockingQueue<JointPosition> positions;
	public DirectServoThread(IDirectServoRuntime runtime) {
		this.positions = new LinkedBlockingQueue<JointPosition>();
		this.runtime = runtime;
	}
	public void run() {
		JointPosition p = null; 
		while(!isInterrupted()) {
			try {
				p = positions.poll(TIMEOUT, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Automatisch generierter Erfassungsblock
				e.printStackTrace();
			}
			if(p != null) {
				try {
					runtime.updateWithRealtimeSystem();
					runtime.setDestination(p);
					ThreadUtil.milliSleep(10);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void addPosition(JointPosition position) {
		this.positions.add(position);
	}
	public void clearPositions() {
		this.positions.clear();
		
	}
}
