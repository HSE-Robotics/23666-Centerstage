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
    double x;
    double y;
    double z;
    double denominator;
    double speed;
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
    drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    // Put initialization blocks here.
    waitForStart();
    if (opModeIsActive()) {
      // Put run blocks here.
      int initialPosition = Arm.getCurrentPosition();
      Arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
      Arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      Arm.setDirection(DcMotor.Direction.REVERSE);
      speed=0.7;

      //slider.setTargetPosition((initialPosition)+224);
      //slider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      while (opModeIsActive()) {
        // Put loop blocks here.
        telemetry.addData("InitialPosition",initialPosition);
        telemetry.addData("CurrentPosition",Arm.getCurrentPosition());
        drive.setWeightedDrivePower(
                new Pose2d(
                        gamepad1.left_stick_y * speed,
                        gamepad1.left_stick_x * speed,
                        gamepad1.right_stick_x * speed
                )
        );

        drive.update();
        /* Old Code Driving
        BR.setDirection(DcMotor.Direction.REVERSE);
        FR.setDirection(DcMotor.Direction.REVERSE);
        x = gamepad1.left_stick_x / 2;
        y = gamepad1.left_stick_y / 2;
        z = (gamepad1.right_stick_x / 2) * 1.1;
        denominator = JavaUtil.averageOfList(JavaUtil.createListWith(JavaUtil.sumOfList(JavaUtil.createListWith(Math.abs(y), Math.abs(x), Math.abs(z))), 1));
        FL.setPower((y - x - z) / denominator);
        BL.setPower((y + x - z) / denominator);
        FR.setPower((y + x + z) / denominator);
        BR.setPower((y - x + z) / denominator);
        */
        if(gamepad1.y){
          Arm.setTargetPosition(initialPosition + 1100);
          Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
          Arm.setPower(1);
        }else if(gamepad1.x){
          Arm.setTargetPosition(initialPosition + 600);
          Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
          Arm.setPower(1);
        }else if(gamepad1.a){
          Arm.setTargetPosition(initialPosition);
          Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
          Arm.setPower(1);
        }else if(gamepad1.b){
          Arm.setTargetPosition(initialPosition + 2200);
          Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
          Arm.setPower(1);
        }
        if (gamepad1.right_bumper) {
          RWT.setPower(1.0);
          LWT.setPower(-1.0);
          
        } else if (gamepad1.left_bumper) {
          RWT.setPower(-1.0);
          LWT.setPower(1.0);
        }else{
          RWT.setPower(0.0);
          LWT.setPower(0.0);
        }
        if (gamepad1.dpad_down) {
          Muneca.setPosition(0.65);
        } else if (gamepad1.dpad_up) {
          Muneca.setPosition(1.0);
        }
        if (gamepad1.share){
          PlaneLauncher.setPosition(1.0);
        }
        telemetry.addData("key", "text");

        telemetry.update();
      }
    }
  }
}