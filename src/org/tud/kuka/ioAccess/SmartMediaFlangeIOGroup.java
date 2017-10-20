package org.tud.kuka.ioAccess;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.kuka.generated.ioAccess.MediaFlangeIOGroup;
import com.kuka.roboticsAPI.controllerModel.Controller;

/**
 * Automatically generated class to abstract I/O access to I/O group <b>MediaFlange</b>.<br>
 * <i>Please, do not modify!</i>
 * <p>
 * <b>I/O group description:</b><br>
 * This I/O Group contains the In-/Outputs for the Multimedia Flange Touch Pneumatic.
 */
@Singleton
public class SmartMediaFlangeIOGroup extends MediaFlangeIOGroup
{
	private boolean isMedienFlanchLedOn;
	/**
	 * Constructor to create an instance of class 'MediaFlange'.<br>
	 * <i>This constructor is automatically generated. Please, do not modify!</i>
	 *
	 * @param controller
	 *            the controller, which has access to the I/O group 'MediaFlange'
	 */
	@Inject
	public SmartMediaFlangeIOGroup(Controller controller)
	{
		super(controller);
		isMedienFlanchLedOn = super.getLEDBlue();
	}
	/**
	 * Gets the value of the <b>digital output '<i>LEDBlue</i>'</b>.<br>
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
	 * @return current value of the digital output 'LEDBlue'
	 */
	public boolean getLEDBlue()
	{
		return isMedienFlanchLedOn;
	}

	/**
	 * Sets the value of the <b>digital output '<i>LEDBlue</i>'</b>.<br>
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
	 *            the value, which has to be written to the digital output 'LEDBlue'
	 */
	public void setLEDBlue(java.lang.Boolean value)
	{
		if(value == null) return;
		if(isMedienFlanchLedOn == value.booleanValue()) return;
		isMedienFlanchLedOn = value;
		super.setLEDBlue(value);
	}
}
