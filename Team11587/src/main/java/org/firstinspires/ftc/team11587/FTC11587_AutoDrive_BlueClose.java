package org.firstinspires.ftc.team11587;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.lang.Math;
import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

import org.lasarobotics.vision.android.Cameras;
import org.lasarobotics.vision.ftc.resq.Beacon;
import org.lasarobotics.vision.opmode.LinearVisionOpMode;
import org.lasarobotics.vision.opmode.extensions.CameraControlExtension;
import org.lasarobotics.vision.util.ScreenOrientation;
import org.opencv.core.Mat;
import org.opencv.core.Size;


/*
 * FTC Team 11587
 * AutoDrive from Blue Near position (0,48) to lead-in line to near Blue Beacon
 * (0,48) is the starting position on the 2nd floor tile joint, but up against the wall
 */

@Autonomous(name="AutoDrive: Blue Close", group="AutoDrive")
public class FTC11587_AutoDrive_BlueClose extends LinearVisionOpMode {

    HardwarePushbot     robot       =new HardwarePushbot();
    private ElapsedTime runtime     =new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV        =1440;  /*Adjust to CPR * 4*/
    static final double     DRIVE_GEAR_REDUCTION        =0.5;   /*Motor gear = 80 tooth + wheel gear = 40 tooth*/
    static final double     WHEEL_DIAMETER_INCHES       =(4.357 + 4.382 ) / 2; //4.439;
    static final double     COUNTS_PER_INCH             =(COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION /
															(WHEEL_DIAMETER_INCHES * 3.141592653589793));
    static final double     DRIVE_SPEED                 =0.5;
    static final double     TURN_SPEED                  =0.4;
	static final double 	Enc_D_CF					=3.2;


	//Frame counter
	int frameCount = 0;


    @Override
    public void runOpMode() throws InterruptedException {
		// Camera stuff
		waitForVisionStart();
		this.setCamera(Cameras.PRIMARY);
		this.setFrameSize(new Size(900, 900));
		enableExtension(Extensions.BEACON);         //Beacon detection
		enableExtension(Extensions.ROTATION);       //Automatic screen rotation correction
		enableExtension(Extensions.CAMERA_CONTROL); //Manual camera control
		beacon.setAnalysisMethod(Beacon.AnalysisMethod.FAST);
		beacon.setColorToleranceRed(0);
		beacon.setColorToleranceBlue(0);
		rotation.setIsUsingSecondaryCamera(false);
		rotation.disableAutoRotate();
		rotation.setActivityOrientationFixed(ScreenOrientation.PORTRAIT);
		cameraControl.setColorTemperature(CameraControlExtension.ColorTemperature.AUTO);
		cameraControl.setAutoExposureCompensation();


        robot.init(hardwareMap);

		// Encoder Stuff
		robot.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Path0", "Starting at %7d : %7d",        //Feedback to DS that AutoDrive is starting//
                            robot.leftMotor.getCurrentPosition(),
                            robot.rightMotor.getCurrentPosition());
        telemetry.update();

        waitForStart();     //Wait until DS presses PLAY//

		// 0,	48
		//60,	48
		//60,	24
		//60,	0

        //Drives from Blue Near (0,48) to start of near Blue Beacon lead-in line (60,24)\

		//Zero and Square up to a wall
		encoderDrive (DRIVE_SPEED, -2, -2, 5.0);

        encoderDrive (DRIVE_SPEED, 60,	60,	5.0);
		encoderTurn(TURN_SPEED, 90.0, 5.0);  // 90 clockwise
		encoderDrive(DRIVE_SPEED, 48, 48, 10000);  	    // Drive to the wall (60,0) and then
		encoderDrive(DRIVE_SPEED, -24, -24, 10000);  	// 24 inches away from the wall

		// Analyze Color

		// Turn parallel to wall, and drive along
		encoderTurn(TURN_SPEED, -90.0, 4.0);

		encoderDrive(DRIVE_SPEED, 48, 48, 10000); 					// drive 48 inches to next beacon

		// Oh MY!!!, theres a beacon here!
		encoderTurn(TURN_SPEED, 90.0, 4.0);
		encoderDrive(DRIVE_SPEED, 24, 24, 10000); 					// we're 24 inches away, so we can drive 24 inches to.
		encoderDrive(DRIVE_SPEED, -36,-36, 10000);					// the next beacon is located 36 inches, in the y axis


		encoderTurn(TURN_SPEED, -90.0, 4.0);

        telemetry.addData("Path", "Complete");
        telemetry.update();


		if (hasNewFrame()) {
			//Get the frame
			Mat rgba = getFrameRgba();
			Mat gray = getFrameGray();

			//Discard the current frame to allow for the next one to render
			discardFrame();

			//Do all of your custom frame processing here
			//For this demo, let's just add to a frame counter
			frameCount++;
		}

		//Wait for a hardware cycle to allow other processes to run
		waitOneFullHardwareCycle();

    }


    public void encoderTurn (double SPEED, double theta, double Timeout) {
		telemetry.addData("turning: ","%.3f degrees",theta);
		telemetry.update();
		double DBDW = 14.0; // Distance between Drive wheels
		double arclength = (theta * 3.141592653589793 * DBDW) /  360;
		encoderDrive(SPEED,arclength,-arclength,Timeout);
	}


	public void encoderDrive (double speed, double leftInches, double rightInches, double timeoutS) {

            int newLeftTarget;
            int newRightTarget;
			double CurrentPosition_left;
			double CurrentPosition_right;


        if (opModeIsActive()) {

            //Sets new target position using current position//
			CurrentPosition_right = robot.leftMotor.getCurrentPosition() *  Enc_D_CF;
			CurrentPosition_left = robot.rightMotor.getCurrentPosition() *  Enc_D_CF;

			// Determine new target position, and pass to motor controller
			newLeftTarget = robot.leftMotor.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
			newRightTarget = robot.rightMotor.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
			robot.leftMotor.setTargetPosition(newLeftTarget);
			robot.rightMotor.setTargetPosition(newRightTarget);

            //Set motors to Encoder drive mode//
            robot.leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();
            robot.leftMotor.setPower(Math.abs(speed));
            robot.rightMotor.setPower(Math.abs(speed));

            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (robot.leftMotor.isBusy() && robot.rightMotor.isBusy())) {
                telemetry.addData("Path1", "Driving to %7d :%7d",newLeftTarget,newRightTarget);
                telemetry.addData("Path2", "Driving at %7d :%7d",
                                            robot.leftMotor.getCurrentPosition(),
                                            robot.rightMotor.getCurrentPosition());
                telemetry.update();
				//idle();
            }

            //Stop all motion and reset motors//
            robot.leftMotor.setPower(0);
            robot.rightMotor.setPower(0);
            robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        }
    }
}
