/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.drive.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;

/*
 * This OpMode illustrates the basics of TensorFlow Object Detection,
 * including Java Builder structures for specifying Vision parameters.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list.
 */
@Autonomous(name = "Red Front Stage", group = "Concept", preselectTeleOp = "BananaJavaRR")

public class RedFrontStage extends LinearOpMode {
     private DcMotor BR;
    private DcMotor FR;
    private DcMotor FL;
    private DcMotor BL;
    private DcMotor Arm;
    private CRServo RWT;
    private CRServo LWT;
    private Servo Muneca;
    int Initial_Position;
    ElapsedTime timelapse;
    double ticks_per_revolution;
        double wheel_circumference;
        double ticks_per_inch;
        int ticks_to_destination;
        int distance_to_travel;
        double speed;
    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera

    // TFOD_MODEL_ASSET points to a model file stored in the project Asset location,
    // this is only used for Android Studio when using models in Assets.
    //private static final String TFOD_MODEL_ASSET = "MyModelStoredAsAsset.tflite";
    // TFOD_MODEL_FILE points to a model file stored onboard the Robot Controller's storage,
    // this is used when uploading models directly to the RC using the model upload interface.
    private static final String TFOD_MODEL_FILE = "/sdcard/FIRST/tflitemodels/VolcanRojo.tflite";
    // Define the labels recognized in the model for TFOD (must be in training order!)
    private static final String[] LABELS = {
       "volcan",
    };

    /**
     * The variable to store our instance of the TensorFlow Object Detection processor.
     */
    private TfodProcessor tfod;

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {

        initTfod();
        
        int timeBack;
        
        
        BR = hardwareMap.get(DcMotor.class, "BR");
        FR = hardwareMap.get(DcMotor.class, "FR");
        FL = hardwareMap.get(DcMotor.class, "FL");
        BL = hardwareMap.get(DcMotor.class, "BL");
        Arm = hardwareMap.get(DcMotor.class, "Arm");
        RWT = hardwareMap.get(CRServo.class, "Right wheel thingy");
        LWT = hardwareMap.get(CRServo.class, "Left wheel thingy");
        Muneca = hardwareMap.get(Servo.class, "Muneca");
        
        // Wait for the DS start button to be touched.
        telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch Play to start OpMode");
        telemetry.update();
        waitForStart();
        
        wheel_circumference = 11.775;
        ticks_per_revolution = 528;
        ticks_per_inch = ticks_per_revolution / wheel_circumference;
        distance_to_travel = 60;
        ticks_to_destination = (int) (distance_to_travel * ticks_per_inch);

        if (opModeIsActive()) {
            while (opModeIsActive()) {
                BL.setDirection(DcMotor.Direction.REVERSE);
                FL.setDirection(DcMotor.Direction.REVERSE);
                
                Arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                Arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                Arm.setDirection(DcMotor.Direction.REVERSE);
                
                // Using encoders to ensure driving is straight
                BL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                BR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                FL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                FR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                
                  // Motors will brake on stopRobot function
                BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                
                telemetryTfod();
                // Push telemetry to the Driver Station.
                telemetry.update();

                // Save CPU resources; can resume streaming when needed.
                if (gamepad1.dpad_down) {
                    visionPortal.stopStreaming();
                } else if (gamepad1.dpad_up) {
                    visionPortal.resumeStreaming();
                }

                // Share the CPU.
                sleep(20);
            }
        }

        // Save more CPU resources when camera is no longer needed.
        visionPortal.close();

    }   // end runOpMode()

    /**
     * Initialize the TensorFlow Object Detection processor.
     */
    private void initTfod() {

        // Create the TensorFlow processor by using a builder.
        tfod = new TfodProcessor.Builder()

            // With the following lines commented out, the default TfodProcessor Builder
            // will load the default model for the season. To define a custom model to load, 
            // choose one of the following:
            //   Use setModelAssetName() if the custom TF Model is built in as an asset (AS only).
            //   Use setModelFileName() if you have downloaded a custom team model to the Robot Controller.
            //.setModelAssetName(TFOD_MODEL_ASSET)
            .setModelFileName(TFOD_MODEL_FILE)

            // The following default settings are available to un-comment and edit as needed to 
            // set parameters for custom models.
            .setModelLabels(LABELS)
            //.setIsModelTensorFlow2(true)
            //.setIsModelQuantized(true)
            //.setModelInputSize(300)
            //.setModelAspectRatio(16.0 / 9.0)

            .build();

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam vs. built-in RC phone camera).
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        // Choose a camera resolution. Not all cameras support all resolutions.
        //builder.setCameraResolution(new Size(640, 480));

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        //builder.enableLiveView(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        //builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        //builder.setAutoStopLiveView(false);

        // Set and enable the processor.
        builder.addProcessor(tfod);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        // Set confidence threshold for TFOD recognitions, at any time.
        tfod.setMinResultConfidence(0.75f);

        // Disable or re-enable the TFOD processor at any time.
        //visionPortal.setProcessorEnabled(tfod, true);

    }   // end method initTfod()

    /**
     * Add telemetry about TensorFlow Object Detection (TFOD) recognitions.
     */
    private void telemetryTfod() {
        wheel_circumference = 11.775;
        ticks_per_revolution = 528;
        ticks_per_inch = ticks_per_revolution / wheel_circumference;
        distance_to_travel = 60;
        ticks_to_destination = (int) (distance_to_travel * ticks_per_inch);
        speed = 0.25;
        
        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry.addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            String cubePosition = "";
            double x = (recognition.getLeft() + recognition.getRight()) / 2 ;
            double y = (recognition.getTop()  + recognition.getBottom()) / 2 ;
            if((x > 280 && x < 400) && (y>200 && y < 280)){
                
                cubePosition = "Center";
                Muneca.setPosition(1.0);
                sleep(300);
                //Drive to drop purple pixel
                drive(0.3, 36,ticks_per_inch);
                stopR();
                drive(0.3, -5,ticks_per_inch);
                Muneca.setPosition(1.0);
                stopR();
                //Move out of the way of Pixel and Prop
                strafe_left(0.3, 15, ticks_per_inch);
                stopR();
                
                drive(0.3, 28, ticks_per_inch);
                stopR();
                
                //face towards backdrop
                rotateRight(0.15, 20, ticks_per_inch);
                stopR();
                drive(0.3,15, ticks_per_inch);
                stopR();
                strafe_right(0.3, 6, ticks_per_inch);
                stopR();
                drive(0.35,75, ticks_per_inch);
                stopR();
                
                //Move to the backdrop
                strafe_right(0.3, 32, ticks_per_inch);
                stopR();
                
                // Prepare arm to score and move towards backdrop
                armToScore();
                Muneca.setPosition(0.8);
                drive(0.15,15.5, ticks_per_inch);
                stopR();
                RWT.setPower(-1.0);
                LWT.setPower(1.0);
                sleep(2000);
                RWT.setPower(0.0);
                LWT.setPower(0.0);
                driveBack(0.3,5,ticks_per_inch);
                stopR();
                strafe_left(0.3,26, ticks_per_inch);
                stopR();
                
                armDown();
                stopArm();
                drive(0.3, 10, ticks_per_inch);
                stopR();
                break;

            }else if((x > 0 && x < 120) && (y>180 && y < 320)){
                cubePosition = "Left";
                Muneca.setPosition(1.0);
                sleep(300);
                //Drive to drop purple pixel
                drive(0.4, 27,ticks_per_inch);
                stopR();
                rotateLeft(0.15, 20, ticks_per_inch);
                stopR();
                
                drive(0.4, 7.5,ticks_per_inch);
                stopR();
                
                driveBack(0.4, 10, ticks_per_inch);
                stopR();
                
                strafe_right(0.4, 28, ticks_per_inch);
                stopR();

                rotateLeft(0.15, 38, ticks_per_inch);
                stopR();

                drive(0.3, 75, ticks_per_inch);
                stopR();
                
                strafe_right(0.4, 40, ticks_per_inch);
                
                // Prepare arm to score and move towards backdrop
                armToScore();
                Muneca.setPosition(0.8);
                drive(0.15,22, ticks_per_inch);
                stopR();
                RWT.setPower(-1.0);
                LWT.setPower(1.0);
                sleep(1000);
                RWT.setPower(0.0);
                LWT.setPower(0.0);
                driveBack(0.3,5,ticks_per_inch);
                stopR();
                strafe_left(0.3,40, ticks_per_inch);
                stopR();
                
                armDown();
                stopArm();
                drive(0.3, 10, ticks_per_inch);
                stopR();
                break;
            }else if((x > 450 && x < 620) && (y>180 && y < 300)){
                cubePosition = "Right";
                Muneca.setPosition(1.0);
                sleep(200);
                //Drive to drop purple pixel
                drive(0.3, 27,ticks_per_inch);
                stopR();
                rotateRight(0.15, 20, ticks_per_inch);
                stopR();
                
                drive(0.3, 10,ticks_per_inch);
                stopR();
                
                driveBack(0.3, 10, ticks_per_inch);
                stopR();
                
                strafe_left(0.3, 28, ticks_per_inch);
                stopR();
                
                /*rotateLeft(0.15, 40, ticks_per_inch);
                stopR();*/
                
                drive(0.3, 80, ticks_per_inch);
                stopR();
                
                strafe_right(0.3, 34, ticks_per_inch);
                
                // Prepare arm to score and move towards backdrop
                armToScore();
                Muneca.setPosition(0.8);
                drive(0.15,17.5, ticks_per_inch);
                stopR();
                RWT.setPower(-1.0);
                LWT.setPower(1.0);
                sleep(2000);
                RWT.setPower(0.0);
                LWT.setPower(0.0);
                driveBack(0.3,5,ticks_per_inch);
                stopR();
                strafe_left(0.3,35, ticks_per_inch);
                stopR();
                
                armDown();
                stopArm();
                drive(0.3, 10, ticks_per_inch);
                stopR();
                break;

                
                
            }
            telemetry.addData(""," ");
            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry.addData("- Position", "%.0f / %.0f", x, y);
            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
            telemetry.addData("- PositionCube","%s", cubePosition);
        }   // end for() loop

    }   // end method telemetryTfod()
    private void stopArm(){
        Arm.setPower(0.0);
    }
    private void armToScore(){
        Arm.setTargetPosition(Arm.getCurrentPosition() + 700);
        Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Arm.setPower(1);
    }
    
    private void armDown(){
        Arm.setTargetPosition(Arm.getCurrentPosition() - 715);
        Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Arm.setPower(1);
        while (Arm.isBusy()) {
                  
                }
    }
    private void stopR(){
        BR.setPower(0.0);
        FR.setPower(0.0);
        BL.setPower(0.0);
        FL.setPower(0.0);
    }
    /**
   * Drive Backwards / Forward
   */
  private void drive(double Power, double distance_to_travel, double ticks_per_inch) {
    int ticks_to_destination;
    ticks_to_destination = (int) (distance_to_travel * ticks_per_inch);
    BL.setTargetPosition(BL.getCurrentPosition() + ticks_to_destination);
    FL.setTargetPosition(FL.getCurrentPosition()+ticks_to_destination);
    FR.setTargetPosition(FR.getCurrentPosition()+ticks_to_destination);
    BR.setTargetPosition(BR.getCurrentPosition()+ticks_to_destination);
    FR.setPower(Power);
    BR.setPower(Power);
    BL.setPower(Power);
    FL.setPower(Power);
    BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
     while (BL.isBusy() && BR.isBusy() &&
                FL.isBusy() && FR.isBusy()) {

                }
  }
    private void driveBack(double Power, double distance_to_travel, double ticks_per_inch) {
    int ticks_to_destination;
    ticks_to_destination = (int) (distance_to_travel * ticks_per_inch);
    BL.setTargetPosition(BL.getCurrentPosition() - ticks_to_destination);
    FL.setTargetPosition(FL.getCurrentPosition()-ticks_to_destination);
    FR.setTargetPosition(FR.getCurrentPosition()-ticks_to_destination);
    BR.setTargetPosition(BR.getCurrentPosition()-ticks_to_destination);
    FR.setPower(Power);
    BR.setPower(Power);
    BL.setPower(Power);
    FL.setPower(Power);
    BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
         while (BL.isBusy() && BR.isBusy() &&
                FL.isBusy() && FR.isBusy()) {
                   
                }
  }
  private void rotateLeft(double Power, double distance_to_travel, double ticks_per_inch) {
    int ticks_to_destination;
    ticks_to_destination = (int) (distance_to_travel * ticks_per_inch);
    BL.setTargetPosition(BL.getCurrentPosition() - ticks_to_destination);
    FL.setTargetPosition(FL.getCurrentPosition()-ticks_to_destination);
    FR.setTargetPosition(FR.getCurrentPosition()+ticks_to_destination);
    BR.setTargetPosition(BR.getCurrentPosition()+ticks_to_destination);
    FR.setPower(Power);
    BR.setPower(Power);
    BL.setPower(Power);
    FL.setPower(Power);
    BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
         while (BL.isBusy() && BR.isBusy() &&
                FL.isBusy() && FR.isBusy()) {
                  
                }
  }
    private void rotateRight(double Power, double distance_to_travel, double ticks_per_inch) {
    int ticks_to_destination;
    ticks_to_destination = (int) (distance_to_travel * ticks_per_inch);
    BL.setTargetPosition(BL.getCurrentPosition() + ticks_to_destination);
    FL.setTargetPosition(FL.getCurrentPosition()+ticks_to_destination);
    FR.setTargetPosition(FR.getCurrentPosition()-ticks_to_destination);
    BR.setTargetPosition(BR.getCurrentPosition()-ticks_to_destination);
    FR.setPower(Power);
    BR.setPower(Power);
    BL.setPower(Power);
    FL.setPower(Power);
    BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
         while (BL.isBusy() && BR.isBusy() &&
                FL.isBusy() && FR.isBusy()) {
                  
                }
  }
  /**
   * Strafe Right 
   */
  private void strafe_left(double Power, double distance_to_travel, double ticks_per_inch) {
    int ticks_to_destination;
    ticks_to_destination = (int) (distance_to_travel * ticks_per_inch);
    BL.setTargetPosition(BL.getCurrentPosition() + ticks_to_destination);
    FL.setTargetPosition(FL.getCurrentPosition() - ticks_to_destination);
    FR.setTargetPosition(FR.getCurrentPosition() + ticks_to_destination);
    BR.setTargetPosition(BR.getCurrentPosition() - ticks_to_destination);
    FR.setPower(Power);
    BR.setPower(Power);
    BL.setPower(Power);
    FL.setPower(Power);
    BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
         while (BL.isBusy() && BR.isBusy() &&
                FL.isBusy() && FR.isBusy()) {
                  
                }
  }
  private void strafe_right(double Power, double distance_to_travel, double ticks_per_inch) {
    int ticks_to_destination;
    ticks_to_destination = (int) (distance_to_travel * ticks_per_inch);
    BL.setTargetPosition(BL.getCurrentPosition() - ticks_to_destination);
    FL.setTargetPosition(FL.getCurrentPosition() + ticks_to_destination);
    FR.setTargetPosition(FR.getCurrentPosition() - ticks_to_destination);
    BR.setTargetPosition(BR.getCurrentPosition() + ticks_to_destination);
    FR.setPower(Power);
    BR.setPower(Power);
    BL.setPower(Power);
    FL.setPower(Power);
    BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
         while (BL.isBusy() && BR.isBusy() &&
                FL.isBusy() && FR.isBusy()) {
                  
                }
  }
}   // end class
