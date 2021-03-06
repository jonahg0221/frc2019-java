/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * Add your docs here.
 */
public class Wrist extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.  
  //AMT encoder - use the brown on B 1 and white on A on 0 ??
  private Encoder wristEncoder = new Encoder(4,5,false, Encoder.EncodingType.k4X);
  double distancePerPulse = 2048;
  private TalonSRX wristTalon = new TalonSRX(5);
  private final double WRIST_POWER_FORWARD_LIMIT = 0.65;
  private final double WRIST_POWER_REVERSE_LIMIT = -0.65;
  //Don't let the talon apply power past certian encoder limits
  private final int WRIST_SOFT_FORWARD_LIMIT = 330000;
  private final int WRIST_SOFT_REVERSE_LIMIT = -10000;
  //private final double WRIST_CURRENT_LIMIT = 12;  // Need to try this
  private int counter = 0;

  public Wrist(){
    super();
    /*wristEncoder.setDistancePerPulse(distancePerPulse);
    wristEncoder.setDistancePerPulse((1.0/31.0)/distancePerPulse);
    wristEncoder.setReverseDirection(true);
    wristEncoder.setSamplesToAverage(7);
    wristEncoder.reset();*/

    // Initial config for the Talon 
    wristTalon.setSensorPhase(false);  //This is what stripped the belt!
    wristTalon.setInverted(false);
    // I actually never actually let the code ask for limits higher than this, and they may do
    // more harm than good if there is a slip of some sort.
    wristTalon.configForwardSoftLimitEnable(true, 10);
    wristTalon.configReverseSoftLimitEnable(true, 10);
		wristTalon.setSelectedSensorPosition(0, 0, 0);  //Position is zero on on the encoder when we boot up
    //set the output limits
    wristTalon.configPeakOutputForward(WRIST_POWER_FORWARD_LIMIT, 10);
    wristTalon.configPeakOutputReverse(WRIST_POWER_REVERSE_LIMIT, 10);  //Gravity is our reverse
    //Set the soft limits on position
    wristTalon.configForwardSoftLimitThreshold(WRIST_SOFT_FORWARD_LIMIT,10);
    wristTalon.configReverseSoftLimitThreshold(WRIST_SOFT_REVERSE_LIMIT, 10);
    //See if the Voltage compensation will give us more reliable performance
    wristTalon.configVoltageCompSaturation(11, 10);
    wristTalon.enableVoltageCompensation(true);
    //Try not to let it jerk - it seems to do that sometimes.  Give it 1s to get to full.
    //wristTalon.configClosedloopRamp(1.0, 10);
    //Start us at zero
    wristTalon.set(ControlMode.Position, 0);
    
    
		//Set the gains - do this after testing it with the joystick
		//FWD, P, I, D, I limits that work ok for position mode (in Talon SLOT 0)
    wristTalon.config_kP(0, 0.01, 10);  // 0.01 works ... 0.15 alone oscillates
    wristTalon.config_kI(0, 0.0001, 10);
    wristTalon.config_kD(0, 0.0, 10);
    wristTalon.config_kF(0, 0, 10);
    
    // UNTESTED FWD, P, I, D, I limits that work ok for Magic/current/velocity mode (in Talon SLOT 1)
    wristTalon.config_kP(1, 0.02, 10);
    wristTalon.config_kI(1, 0.000, 10);
    wristTalon.config_kD(1, 3.0, 10);
    wristTalon.config_kF(1, 0.0040, 10);

    wristTalon.selectProfileSlot(0, 0);
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }

  //failsafe if the robot fails and the wrist falls on the ground
  public void set_Encoder(int pos){
    wristTalon.setSelectedSensorPosition(pos, 0, 10);
  }

  public void reset(){
    wristEncoder.reset();
    wristTalon.selectProfileSlot(0, 0);
    wristTalon.setSelectedSensorPosition(0, 0, 10);
    wristTalon.set(ControlMode.Position, 0);
  }

  private double getWristPosition(){
    double distance = 0;
    //distance =  wristEncoder.getDistance();
    //distance = wristTalon.getSelectedSensorPosition(0) / 182000.0;
    distance = wristTalon.getSelectedSensorPosition(0);
    return distance;
  }
  
  //This is for controlling via the TalonSRX in position mode
  public void setPosition(double position) {
    //Position we put in Slot 0, PID 0
    wristTalon.selectProfileSlot(0, 0);
    wristTalon.set(ControlMode.Position, position);
  }
  public void setWristMagic(double setpoint) {
    wristTalon.configMotionCruiseVelocity(50000, 10);
    wristTalon.configMotionAcceleration(50000, 10);
    wristTalon.selectProfileSlot(1, 0);
    wristTalon.configMotionSCurveStrength(0);
    wristTalon.set(ControlMode.MotionMagic, setpoint);
  }

  public void setWristPower(double pow) {
    wristTalon.set(ControlMode.PercentOutput, pow);
  }
  public void setCurrent(double current){
    wristTalon.set(ControlMode.Current, current);
  }

  public double getWristSetpoint() {
   return wristTalon.getClosedLoopTarget(0);
  }


  public void holdPosition() {
    double pos = wristTalon.getSelectedSensorPosition(0);
    setPosition(pos);
    System.out.println("Holding wrist at " + String.format("%.2f",pos) + "s");
    
  }

  public void moveWrist(double direction) {
    double maxPower = 0.4;
    //move up - pull hardest at the bottom (encoder = 1)
    if (direction > 0){
      wristTalon.set(ControlMode.PercentOutput, Math.max( maxPower ,0.25*getWristPosition()));
    }
    //move down - try to get it to balance on the way down by giving a bit of positive
    else if (direction < 0){
      wristTalon.set(ControlMode.PercentOutput, -0.2 + 0.4*getWristPosition());
    }
    else{
      wristTalon.set(ControlMode.PercentOutput, 0);
    }
  }

  public double getWristMaxPosition() {
    return WRIST_SOFT_FORWARD_LIMIT;
  }

    @Deprecated
    //This is for controlling via the TalonSRX in velocity mode
    //NEEDS A LOT OF WORK IF WE WANT TO TRY IT.  POSITION IS SAFER.
  public void setVelocity(double vel) {
    //Velocity we put in Slot 1, PID 0
    wristTalon.selectProfileSlot(1, 0);
    if (vel < 0){
      //FWD, P, I, D, F limits that work ok for velocity mode (in Talon SLOT 1)
      wristTalon.config_kP(1, 1.0, 10);
      wristTalon.config_kI(1, 0.000, 10);
      wristTalon.config_kD(1, 0.0, 10);
      wristTalon.config_kF(0, 0, 10);
    }
    else {
    //FWD, P, I, D, F limits that work ok for velocity mode (in Talon SLOT 1)
      wristTalon.config_kP(1, 1.0, 10);
      wristTalon.config_kI(1, 0.000, 10);
      wristTalon.config_kD(1, 0.0, 10);
      wristTalon.config_kF(0, 0, 10);
    }
    wristTalon.configOpenloopRamp(0.01, 10); 
    wristTalon.set(ControlMode.Velocity, vel ,1);
  }


  
  public void log() {
    counter ++;
    if (Math.floorMod(counter, 10) == 0) {
      //SmartDashboard.putBoolean("Wrist Top", !elevatorLimitLow.get());
      //SmartDashboard.putNumber("Wrist Distance", ((int)(100*getWristPosition()))/100.0);
      double wristPos = wristTalon.getSelectedSensorPosition(0);
      SmartDashboard.putNumber("Wrist Talon", ((int)(100*wristPos))/100.0);
      SmartDashboard.putNumber("Wrist Velocity", (int)(100*wristTalon.getSelectedSensorVelocity(0)/100.0));
      SmartDashboard.putNumber("Wrist Current", (int)(100*wristTalon.getOutputCurrent()/100.0));
      SmartDashboard.putNumber("Wrist Setpoint", (int)(100*getWristSetpoint()/100.0));
      SmartDashboard.putNumber("Wrist Output", (int)(100*wristTalon.getMotorOutputPercent()/100.0));
    }
  }
  
}
