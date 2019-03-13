/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import frc.robot.commands.*;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.*;
import frc.robot.spartanutils.AxisButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {

  private boolean bCompetitionMode = true;  
  public Joystick stick;
  public JoystickButton buttonA;
  public JoystickButton buttonB;
  public JoystickButton buttonX;
  public JoystickButton buttonY;
  public JoystickButton buttonLB;
  public JoystickButton buttonRB;
  public JoystickButton buttonBack;
  public JoystickButton buttonStart;
  public POVButton povButtonUp;
  public POVButton povButtonDown;
  public POVButton povButtonLeft;
  public POVButton povButtonRight;
  public AxisButton axisButtonLT;
  public AxisButton axisButtonRT;
  public Joystick coStick;
  public JoystickButton coButtonA;
  public JoystickButton coButtonB;
  public JoystickButton coButtonX;
  public JoystickButton coButtonY;
  public JoystickButton coButtonLB;
  public JoystickButton coButtonRB;
  public JoystickButton coButtonBack;
  public JoystickButton coButtonStart;
  public POVButton coPovButtonUp;
  public POVButton coPovButtonDown;
  public POVButton coPovButtonLeft;
  public POVButton coPovButtonRight;
  public AxisButton coAxisButtonLT;
  public AxisButton coAxisButtonRT; 


  //// CREATING BUTTONS
  // One type of button is a joystick button which is any button on a
  //// joystick.
  // You create one by telling it which joystick it's on and which button
  // number it is.
  // Joystick stick = new Joystick(port);
  // Button button = new JoystickButton(stick, buttonNumber);

  // There are a few additional built in buttons you can use. Additionally,
  // by subclassing Button you can create custom triggers and bind those to
  // commands the same as any other Button.

  //// TRIGGERING COMMANDS WITH BUTTONS
  // Once you have a button, it's trivial to bind it to a button in one of
  // three ways:

  // Start the command when the button is pressed and let it run the command
  // until it is finished as determined by it's isFinished method.
  // button.whenPressed(new ExampleCommand());

  // Run the command while the button is being held down and interrupt it once
  // the button is released.
  // button.whileHeld(new ExampleCommand());

  // Start the command when the button is released and let it run the command
  // until it is finished as determined by it's isFinished method.
  // button.whenReleased(new ExampleCommand());
  public OI(){
    stick = new Joystick(0);
    
    buttonA = new JoystickButton(stick, 1);
    buttonB = new JoystickButton(stick, 2);
    buttonX = new JoystickButton(stick, 3);
    buttonY = new JoystickButton(stick, 4);
    buttonLB = new JoystickButton(stick, 5);
    buttonRB = new JoystickButton(stick, 6);
    buttonBack = new JoystickButton(stick,7);
    buttonStart = new JoystickButton(stick, 8);
    povButtonUp = new  POVButton(stick, 0);
    povButtonDown = new  POVButton(stick, 180);
    povButtonRight = new  POVButton(stick, 90);
    povButtonLeft = new  POVButton(stick, 270);
    axisButtonLT = new AxisButton(stick, 2);
    axisButtonRT = new AxisButton(stick, 3);
    if(bCompetitionMode){
      coStick = new Joystick(1);      
      coButtonA = new JoystickButton(coStick, 1);
      coButtonB = new JoystickButton(coStick, 2);
      coButtonX = new JoystickButton(coStick, 3);
      coButtonY = new JoystickButton(coStick, 4);
      coButtonLB = new JoystickButton(coStick, 5);
      coButtonRB = new JoystickButton(coStick, 6);
      coButtonBack = new JoystickButton(coStick,7);
      coButtonStart = new JoystickButton(coStick, 8);
      coPovButtonUp = new  POVButton(coStick, 0);
      coPovButtonDown = new  POVButton(coStick, 180);
      coPovButtonRight = new  POVButton(coStick, 90);
      coPovButtonLeft = new  POVButton(coStick, 270);
      coAxisButtonLT = new AxisButton(coStick, 2);
      coAxisButtonRT = new AxisButton(coStick, 3);
    }

    if(!bCompetitionMode){
    // Pneumatics:
    // Toggle hatch
    buttonA.whenPressed(new Command_SetSolenoid("hatch",buttonA));
    // Toggle Compressor
    buttonY.whenPressed(new Command_SetSolenoid("compressor",buttonY));
    // extend both (fwd)
    //buttonB.whenPressed(new Command_RaiseRobot(buttonB));
    // Retract both
    buttonX.whenPressed(new Command_SetSolenoid("retractboth",buttonX));
    // turn off the solenoids (maintain pressure)
    buttonBack.whenPressed(new Command_SetSolenoid("float",buttonBack));
    // run the back wheels to move forward  
    buttonStart.whenPressed(new Command_PneumaticDrive(0.65,buttonStart));
    
    //Intake in and out - negative (left at the moment) is out, right is in
    povButtonLeft.whenPressed(new Command_SetIntake(-0.99, povButtonLeft));
    povButtonRight.whenPressed(new Command_SetIntake(0.5, povButtonRight));
    //Set Elevator PID - fixed the directions, motor has to be inverted
    axisButtonLT.whenPressed(new Command_SetElevatorHeightPID(1.0,axisButtonLT));
    axisButtonRT.whenPressed(new Command_SetElevatorHeightPID(-1.0,axisButtonRT));

    //Shifters - testing buttons
    buttonLB.whenPressed(new Command_Shifters(0, buttonLB)); //low gear
    buttonRB.whenPressed(new Command_Shifters(1, buttonRB)); //high gear
    //Set Wrist - fixed the directions, motor has to be inverted
    // Higher number on encoder lowers the wrist, so raising it is the negative direction 
    povButtonUp.whenPressed(new Command_SetWrist(-1.0, povButtonUp));
    povButtonDown.whenPressed(new Command_SetWrist(1.0, povButtonDown));

    }

    if(bCompetitionMode){
    //Driver
    buttonStart.whenPressed(new Command_SetSolenoid("compressor",buttonStart));  
    buttonRB.whenPressed(new Command_SetSolenoid("togglegear", buttonRB)); //toggle gear
    povButtonUp.whenPressed(new Command_dPadDrive("up",povButtonUp));
    povButtonDown.whenPressed(new Command_dPadDrive("down", povButtonDown));
    povButtonLeft.whenPressed(new Command_dPadDrive("left",povButtonLeft));
    povButtonRight.whenPressed(new Command_dPadDrive("right", povButtonRight));
    //Set Wrist - fixed the directions, motor has to be inverted
    // Higher number on encoder lowers the wrist, so raising it is the negative direction 
    buttonY.whenPressed(new Command_SetWrist(-1.0, buttonY));
    buttonA.whenPressed(new Command_SetWrist(1.0, buttonA));

    buttonLB.whenPressed(new Instant_SetWrist(0.0)); //wrist up
    buttonX.whenPressed(new Instant_SetWrist(0.20*Robot.wrist.getWristMaxPosition())); //wrist cargo elevator clearance 20?
    buttonY.whenPressed(new Instant_SetWrist(0.4*0.20*Robot.wrist.getWristMaxPosition())); //wrist hatch elevator clearance 40?
   // L trigger [hold]: Wrist 45 [for cargo ship]
  //  R trigger [hold]: Wrist out 90 [for intake] 
    
   
    


// start of coButtons
 //Cory these are Bryan's buttons can you check to make sure it work
  //co buttons needs
  //R trigger: Elevator up fast
  coAxisButtonRT.whenPressed(new Command_SetElevatorHeightPID(-1.0,axisButtonRT));
  //L trigger: Elevator down fast
  coAxisButtonLT.whenPressed(new Command_SetElevatorHeightPID(1.0,axisButtonLT));
  //R bumper: Elevator up slow coButtonRB
  coAxisButtonRT.whenPressed(new Command_SetElevatorHeightPID(-1.0,axisButtonRT));
  //L bumper: Elevator down slow coButtonLB
  coAxisButtonLT.whenPressed(new Command_SetElevatorHeightPID(1.0,axisButtonLT));

  //X : Cargo in [intake]
  coButtonX.whenPressed(new Command_SetIntake(0.5, coButtonX));
  //B : Cargo out [output]
  coButtonB.whenPressed(new Command_SetIntake(-0.99, coButtonB));  
  //A : Hatch toggle we will want an indicator on the shuffleboard for this
  coButtonA.whenPressed(new Command_SetSolenoid("hatch",coButtonA));
  //Y run the back wheels to move forward  
  coButtonY.whenPressed(new Command_PneumaticDrive(0.65,coButtonY));
  //  Operator: End game mode
  //Mode: Climber control mode:
  //D pad: Low ride motors - front back
  //B: Front / back (all) piston Down
  //Y: Front Up
  //A: Rear up
  //X: emergency raise pistons
  //Back: Compressor toggle
  coButtonStart.whenPressed(new Command_SetSolenoid("compressor",coButtonStart));



    
  } 
    //Put stuff on the dashboard
    SmartDashboard.putString("J1 Buttons", "Up/Down:Wrist LT/RT:Elevator LB/RB:shifter\n L/R:Intake Y:Comp A:Hatch X:Retract B:? \n Start:PnWheel Back:Float");
    SmartDashboard.putData("Enable Climb", new Command_SetSolenoid("climb"));
    SmartDashboard.putData("Retract Front", new Command_SetSolenoid("retractfront"));
    SmartDashboard.putData("Retract Back", new Command_SetSolenoid("retractback"));

  



  }
}