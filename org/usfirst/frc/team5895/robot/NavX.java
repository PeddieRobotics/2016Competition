package org.usfirst.frc.team5895.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;

public class NavX {

	private AHRS ahrs;
	private double lastRawAngle;
	private double offset;
	
	/**
	 * Constructs a new NavX object plugged into the serial MXP port
	 */
	public NavX() {
		ahrs = new AHRS(SerialPort.Port.kMXP);
		lastRawAngle = ahrs.getAngle();
		offset = 0;
	}
	
	/**
	 * Resets the angle of the NavX
	 */
	public void reset() {
		offset = 0;
		ahrs.reset();
	}

	/**
	 * Returns the angle of the NavX.
	 * Turning past 360 degrees will go onto 361
	 * Turning before 0 degrees will go to -1
	 * 
	 * @return The angle, in degrees, of the NavX
	 */
	public double getAngle() {
		double rawAngle = ahrs.getAngle();
		
		if (rawAngle > 340 && lastRawAngle < 20) {
			offset -= 360.0;
		}
		else if (rawAngle < 20 && lastRawAngle > 340) {
			offset += 360;
		}
		
		lastRawAngle = rawAngle;
		
		return rawAngle + offset;
	}
}
