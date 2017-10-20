package org.tud.kuka.ioAccess;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.kuka.generated.ioAccess.FlexFellowIOGroup;
import com.kuka.roboticsAPI.controllerModel.Controller;

/**
 * Automatically generated class to abstract I/O access to I/O group <b>FlexFellow</b>.<br>
 * <i>Please, do not modify!</i>
 * <p>
 * <b>I/O group description:</b><br>
 * FelxFellow IOGroup
 */
@Singleton
public class SmartFlexFellowIOGroup extends FlexFellowIOGroup
{
	private boolean isSignalLightBlueOn;
	private boolean isSignalLightGreenOn;
	private boolean isSignalLightRedOn;
	private boolean isSignalLightYellowOn;
	
	/**
	 * Constructor to create an instance of class 'FlexFellow'.<br>
	 * <i>This constructor is automatically generated. Please, do not modify!</i>
	 *
	 * @param controller
	 *            the controller, which has access to the I/O group 'FlexFellow'
	 */
	@Inject
	public SmartFlexFellowIOGroup(Controller controller)
	{
		super(controller);
		isSignalLightBlueOn = super.getSignalLightBlue();
		isSignalLightGreenOn = super.getSignalLightGreen();
		isSignalLightRedOn = super.getSignalLightRed();
		isSignalLightYellowOn = super.getSignalLightYellow();
	}


	/**
	 * Gets the value of the <b>digital output '<i>SignalLightBlue</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [false; true]
	 *
	 * @return current value of the digital output 'SignalLightBlue'
	 */
	@Override
	public boolean getSignalLightBlue()
	{
		return isSignalLightBlueOn;
	}

	/**
	 * Sets the value of the <b>digital output '<i>SignalLightBlue</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [false; true]
	 *
	 * @param value
	 *            the value, which has to be written to the digital output 'SignalLightBlue'
	 */
	public void setSignalLightBlue(java.lang.Boolean value)
	{
		if(isSignalLightBlueOn == value) return;
		isSignalLightBlueOn = value;
		super.setSignalLightBlue(value);
	}

	/**
	 * Gets the value of the <b>digital output '<i>SignalLightGreen</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [false; true]
	 *
	 * @return current value of the digital output 'SignalLightGreen'
	 */
	public boolean getSignalLightGreen()
	{
		return isSignalLightGreenOn;
	}

	/**
	 * Sets the value of the <b>digital output '<i>SignalLightGreen</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [false; true]
	 *
	 * @param value
	 *            the value, which has to be written to the digital output 'SignalLightGreen'
	 */
	public void setSignalLightGreen(java.lang.Boolean value)
	{
		if(isSignalLightGreenOn == value) return;
		isSignalLightGreenOn = value;
		super.setSignalLightGreen(value);
	}

	/**
	 * Gets the value of the <b>digital output '<i>SignalLightRed</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [false; true]
	 *
	 * @return current value of the digital output 'SignalLightRed'
	 */
	public boolean getSignalLightRed()
	{
		return isSignalLightRedOn;
	}

	/**
	 * Sets the value of the <b>digital output '<i>SignalLightRed</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [false; true]
	 *
	 * @param value
	 *            the value, which has to be written to the digital output 'SignalLightRed'
	 */
	public void setSignalLightRed(java.lang.Boolean value)
	{
		if(isSignalLightRedOn == value) return;
		isSignalLightRedOn = value;
		super.setSignalLightRed(value);
	}

	/**
	 * Gets the value of the <b>digital output '<i>SignalLightYellow</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [false; true]
	 *
	 * @return current value of the digital output 'SignalLightYellow'
	 */
	public boolean getSignalLightYellow()
	{
		return isSignalLightYellowOn;
	}

	/**
	 * Sets the value of the <b>digital output '<i>SignalLightYellow</i>'</b>.<br>
	 * <i>This method is automatically generated. Please, do not modify!</i>
	 * <p>
	 * <b>I/O direction and type:</b><br>
	 * digital output
	 * <p>
	 * <b>User description of the I/O:</b><br>
	 * ./.
	 * <p>
	 * <b>Range of the I/O value:</b><br>
	 * [false; true]
	 *
	 * @param value
	 *            the value, which has to be written to the digital output 'SignalLightYellow'
	 */
	public void setSignalLightYellow(java.lang.Boolean value)
	{
		if(isSignalLightYellowOn == value) return;
		isSignalLightYellowOn = value;
		super.setSignalLightYellow(value);
	}
	
	public void turnOffSignalLights() {
		setSignalLightBlue(false);
		setSignalLightGreen(false);
		setSignalLightRed(false);
		setSignalLightYellow(false);
}
}
