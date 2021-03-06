package org.usfirst.frc.team5895.robot;

import org.usfirst.frc.team5895.robot.FlywheelCounter.BadFlywheelException;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

public class Flywheel {

	private Talon topMotor;
	private Talon bottomMotor;
	private Solenoid mySolenoid;
	
	private FlywheelCounter topCounter;
	private FlywheelCounter bottomCounter;
	
	private TakeBackHalf topController;
	private TakeBackHalf bottomController;

	private boolean upDown;
	
	private enum Mode_Type {AUTO_SHOOT, OVERRIDE};
	
	private Mode_Type mode = Mode_Type.AUTO_SHOOT;
	
	private int atSpeed;
	private double lastTime;
	
	private int atSpeedLoose;
	
	double overrideSpeed;
	
	/**
	 * Creates a new flywheel
	 */
	public Flywheel() {
		topMotor = new Talon(ElectricalLayout.FLYWHEEL_TOPMOTOR);
		bottomMotor = new Talon(ElectricalLayout.FLYWHEEL_BOTTOMMOTOR);
		mySolenoid = new Solenoid(ElectricalLayout.FLYWHEEL_SOLENOID);
		
		topController = new TakeBackHalf(0.00005, 6150/60, 1.0);
		bottomController = new TakeBackHalf(0.00005, 6050/60, 1.0);
			
		
		topCounter = new FlywheelCounter(ElectricalLayout.FLYWHEEL_TOPCOUNTER);
		bottomCounter = new FlywheelCounter(ElectricalLayout.FLYWHEEL_BOTTOMCOUNTER);
		
		atSpeed = 0;
		atSpeedLoose = 0;
		lastTime = Timer.getFPGATimestamp();
	}
	
	/**
	 * Sets the flywheel's speed
	 * @param speed The desired speed of the flywheel, in rpm
	 */
	public void setSpeed(double speed) {
		mode = Mode_Type.AUTO_SHOOT;
		atSpeed = 0;
		atSpeedLoose = 0;
		
		bottomController.set(speed/60);
		
		topController.set(speed/60);
	}
	
	/**
	 * Sets the flywheel's speed
	 * @param topSpeed The desired speed of the top flywheel, in rpm
	 * @param bottomSpeed The desired speed of the top flywheel, in rpm
	 */
	public void setSpeed(double topSpeed, double bottomSpeed) {
		mode = Mode_Type.AUTO_SHOOT;
		atSpeed = 0;
		atSpeedLoose = 0;
		bottomController.set(bottomSpeed/60);
		topController.set(topSpeed/60);
		
	}
	
	public void override(double speed){
		mode = Mode_Type.OVERRIDE;
		overrideSpeed = speed;
	}
	
	/**
	 * Returns the speed that the flywheel is moving at
	 * @return The speed of the flywheel, in rpm
	 */
	public double getTopSpeed() {
		try {
			return topCounter.getRate()*60;
		}
		catch (BadFlywheelException e){
			return e.getLastSpeed();
		}
	}
	
	public double getBottomSpeed() {
		try {
			return bottomCounter.getRate()*60;
		}
		catch (BadFlywheelException e){
			return e.getLastSpeed();
		}
	}
	
	/**
	 * Returns if the flywheel is at the desired speed
	 * @return True if the flywheel is within 15 rpm of the setpoint for the last 75ms
	 */
	public boolean atSpeed(){
		return atSpeed > 75;
	}
	
	/**
	 * Returns if the flywheel is at the desired speed
	 * @return True if the flywheel is within 35 rpm of the setpoint for the last 75ms
	 */
	public boolean atSpeedLoose(){
		return atSpeedLoose > 75;
	}
		
	public void up(){
		upDown = true;
	}
	
	public void down(){
		upDown = false;
	}
	
	public boolean getUpDown(){
		return upDown;
	}
	
	public void update() {
		double bottomOutput;
		double topOutput;
		double bottomSpeed;
		double topSpeed;
		try {
			bottomSpeed = bottomCounter.getRate();
			topSpeed = topCounter.getRate();
			
		DriverStation.reportError("bottom:" + bottomSpeed*60+" top:" + topSpeed*60 +"\n", false);
	
			
			bottomOutput = bottomController.getOutput(bottomSpeed);
			topOutput = topController.getOutput(topSpeed);
			switch (mode){
			case AUTO_SHOOT:
				bottomMotor.set(-bottomOutput);
				topMotor.set(topOutput);
			break;
			case OVERRIDE:
				bottomMotor.set(-overrideSpeed);
				topMotor.set(overrideSpeed);
				break;
			}
			double dt = (Timer.getFPGATimestamp() - lastTime)*1000;
			lastTime = Timer.getFPGATimestamp();
			if (Math.abs(bottomSpeed-bottomController.getSetpoint()) < 25.0/60 &&
					Math.abs(topSpeed-topController.getSetpoint()) < 25.0/60) {
				atSpeed += dt;
			} else {
				atSpeed = 0;
			}
			if (Math.abs(bottomSpeed-bottomController.getSetpoint()) < 35.0/60 &&
					Math.abs(topSpeed-topController.getSetpoint()) < 35.0/60) {
				atSpeedLoose += dt;
			} else {
				atSpeed = 0;
			}
		} catch (BadFlywheelException e) {
		} 
		mySolenoid.set(upDown);
	}
}
