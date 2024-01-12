package org.firstinspires.ftc.teamcode.drive.opmode;

import android.media.Image;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.JavaUtil;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@TeleOp(name = "Banana JavaRR")
public class BananaJavaRR extends LinearOpMode {

  private DcMotor BR;
  private DcMotor FR;
  private DcMotor FL;
  private DcMotor BL;
  private DcMotor Arm;
  private CRServo RWT;
  private CRServo LWT;
  private Servo Muneca;
  private Servo PlaneLauncher;

  /**
   * This function is executed when this OpMode is selected from the Driver Station.
   */
  @Override
  public void runOpMode() {
    double speed;
    double wristPosition;
    BR = hardwareMap.get(DcMotor.class, "BR");
    FR = hardwareMap.get(DcMotor.class, "FR");
    FL = hardwareMap.get(DcMotor.class, "FL");
    BL = hardwareMap.get(DcMotor.class, "BL");
    Arm = hardwareMap.get(DcMotor.class, "Arm");
    RWT = hardwareMap.get(CRServo.class, "Right wheel thingy");
    LWT = hardwareMap.get(CRServo.class, "Left wheel thingy");
    Muneca = hardwareMap.get(Servo.class, "Muneca");
    PlaneLauncher = hardwareMap.get(Servo.class, "AP");
    SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
    drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    // Put initialization blocks here.
    waitForStart();
    if (opModeIsActive()) {
      //Get initialPosition from the Arm Current Position.
      int initialPosition = Arm.getCurrentPosition();
      //Initialize the Arm to run using encoders and it's direction (REVERSE)
      Arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
      Arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      Arm.setDirection(DcMotor.Direction.REVERSE);

      //Set the movement speed of the robot
      speed=0.7;
      wristPosition = Muneca.getPosition();

      while (opModeIsActive()) {
        //We use the Roadrunner Weighted Drive
        drive.setWeightedDrivePower(
                new Pose2d(
                        gamepad1.left_stick_y * speed,
                        gamepad1.left_stick_x * speed,
                        gamepad1.right_stick_x * speed
                )
        );

        drive.update();
        //Arm Movement Code

        if(gamepad1.y){
          // Arm to Score in a Medium High Position
          Arm.setTargetPosition(initialPosition + 1100);
          Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
          Arm.setPower(1);
        }else if(gamepad1.x){
          // Set Arm to score in the lowest position
          Arm.setTargetPosition(initialPosition + 600);
          Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
          Arm.setPower(1);
        }else if(gamepad1.a){
          //Arm to Initial Position / Arm Lifted to Keep Hanging
          Arm.setTargetPosition(initialPosition);
          Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
          Arm.setPower(1);
        }else if(gamepad1.b){
          // Position Arm to Hang
          Arm.setTargetPosition(initialPosition + 2200);
          Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
          Arm.setPower(1);
        }else if(gamepad1.dpad_left){
          //Manually move arm up by 100
          Arm.setTargetPosition(initialPosition + 100);
          Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
          Arm.setPower(0.8);
        }else if(gamepad1.dpad_right){
          //Manually move arm down by 100
          Arm.setTargetPosition(initialPosition - 100);
          Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
          Arm.setPower(0.8);

        }else if(gamepad1.right_stick_button){
          //Manually set up the initial position to the Arm Current Position
          //In case the Arm starts in a very off initial position
          initialPosition = Arm.getCurrentPosition();
        }

        //Intake/Outtake Code
        if (gamepad1.right_trigger > 0.0) {
          //Outtake
          RWT.setPower(gamepad1.right_trigger);
          LWT.setPower(-gamepad1.right_trigger);
        } else if (gamepad1.left_trigger > 0.0) {
          //Intake
          RWT.setPower(-gamepad1.left_trigger);
          LWT.setPower(gamepad1.left_trigger);
        }else{
          //Stop the intake/outtake
          RWT.setPower(0.0);
          LWT.setPower(0.0);
        }

        //Wrist Code
        if (gamepad1.dpad_down) {
          //Set the Wrist to score
          Muneca.setPosition(0.65);
        } else if (gamepad1.dpad_up) {
          //Set the Wrist up
          Muneca.setPosition(1.0);
        } else if (gamepad1.right_bumper && wristPosition>0.10) {
          Muneca.setPosition(wristPosition - 0.10);
          wristPosition = Muneca.getPosition();
        } else if (gamepad1.left_bumper && wristPosition<1.0) {
          Muneca.setPosition(wristPosition + 0.10);
          wristPosition = Muneca.getPosition();
        }

        //Drone Launcher Code
        if (gamepad1.share){
          //Release the Drone
          PlaneLauncher.setPosition(1.0);
        }

        //The Telemetry data shows the current position and initial position in case the Arm is not setup.Also the Muneca Position.
        telemetry.addData("InitialPosition: ", initialPosition);
        telemetry.addData("Arm Current Position: ", Arm.getCurrentPosition());
        telemetry.addData("Muneca Current Position: ", Muneca.getPosition());
        telemetry.update();
      }
    }
  }
}