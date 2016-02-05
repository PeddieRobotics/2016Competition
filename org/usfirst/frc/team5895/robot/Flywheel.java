package org.usfirst.frc.team5895.robot;

import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.FormatterClosedException;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;

public class Flywheel {

	Spark myMotor;
	PID myController;
	Encoder e;
	Counter c;
	private double Kp;
	private double Ki;
	private double Kd;
	private double dV;
	private double currentSpeed;
	
	public Flywheel(){
		myMotor = new Spark(0);
		myController = new PID(Kp, Ki, Kd, dV);
		
		e = new Encoder(0,1);
		e.setDistancePerPulse(1);
		
		c = new Counter(8);
		c.setDistancePerPulse(1);
		c.setSamplesToAverage(2);
		
	}
	
	public void setSpeed(double speed){
		myController.set(speed/60);
	}
	
	public double getSpeed() {
		return e.getRate()*60;
	}
	
	public void update() {
		myMotor.set(myController.getOutput(c.getRate()));
		currentSpeed= getSpeed();
	}
}
