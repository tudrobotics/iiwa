package org.tud.kuka.ioAccess;

import org.tud.kuka.socket.LedCommandMessage;
import org.tud.kuka.socket.LedCommandMessage.LedColor;

public class LedControllerRunnable implements Runnable {

	private SmartFlexFellowIOGroup flexFellowIoGroup;
	private LedCommandMessage message;
	
	public LedControllerRunnable(SmartFlexFellowIOGroup flexFellowIoGroup, LedCommandMessage message) {
		this.flexFellowIoGroup = flexFellowIoGroup;
		this.message = message;
	}
	@Override
	public void run() {
		if(message == null) return;
		if(message.getColor() == null) return;
		if(message.getDuration() <= 0) return;
		if(message.getColor().equals(LedColor.BLUE)) {
			flexFellowIoGroup.setSignalLightBlue(true);
		}
		if(message.getColor().equals(LedColor.RED)) {
			flexFellowIoGroup.setSignalLightRed(true);
		}	
		if(message.getColor().equals(LedColor.YELLOW)) {
			flexFellowIoGroup.setSignalLightYellow(true);
		}	
		if(message.getColor().equals(LedColor.GREEN)) {
			flexFellowIoGroup.setSignalLightGreen(true);
		}		
		try {
			synchronized (this) {
				this.wait(message.getDuration() * 1000);
			}
		} catch (InterruptedException e) {
	
		}
		if(message.getColor().equals(LedColor.BLUE)) {
			flexFellowIoGroup.setSignalLightBlue(false);
		}
		if(message.getColor().equals(LedColor.RED)) {
			flexFellowIoGroup.setSignalLightRed(false);
		}	
		if(message.getColor().equals(LedColor.YELLOW)) {
			flexFellowIoGroup.setSignalLightYellow(false);
		}	
		if(message.getColor().equals(LedColor.GREEN)) {
			flexFellowIoGroup.setSignalLightGreen(false);
		}	
	}

}
