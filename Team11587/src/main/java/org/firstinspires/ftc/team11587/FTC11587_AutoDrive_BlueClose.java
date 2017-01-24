package org.firstinspires.ftc.team11587;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.lang.Math;
import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * FTC Team 11587
 * AutoDrive from Blue Near position (0,60) to lead-in line to near Blue Beacon
 */

@Autonomous(name="AutoDrive: Blue Close 1", group="AutoDrive")
public class FTC11587_AutoDrive_BlueClose extends LinearOpMode {

    HardwarePushbot     robot       =new HardwarePushbot();
    private ElapsedTime runtime     =new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV        =1440;  /*Adjust to CPR * 4*/
    static final double     DRIVE_GEAR_REDUCTION        =2.0;   /*Motor gear = 40 tooth + wheel gear = 80 tooth*/
    static final double     WHEEL_DIAMETER_INCHES       =(4.357 + 4.382 ) / 2; //4.439;
    static final double     COUNTS_PER_INCH             =(COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION /
															(WHEEL_DIAMETER_INCHES * 3.141592653589793));
    static final double     DRIVE_SPEED                 =0.6;
    static final double     TURN_SPEED                  =0.5;
	static final double 	Enc_D_CF					= 3.2;

	static final double		DBDW 						= 14.0; // Distance between Drive wheels


	double arclength;
	double theta;


    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        telemetry.addData("Status","Resetting Encoders");  //Send telemetry indicating encoder reset//
        telemetry.update();

        robot.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        idle();

        robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Path0", "Starting at %7d : %7d",        //Feedback to DS that AutoDrive is starting//
                            robot.leftMotor.getCurrentPosition(),
                            robot.rightMotor.getCurrentPosition());
        telemetry.update();

        waitForStart();     //Wait until DS presses PLAY//


		// 0,	60
		// 60,	0
		// 60,	0
		// 60,	24
		// 108,	24



        encoderDrive (DRIVE_SPEED, 60,	60,	4.0);  //Drives from Blue Near (0,60) to start of near Blue Beacon lead-in line (60,24)

		theta = 90.0;
		arclength = (theta * 3.141592653589793 * DBDW) /  360;

		encoderDrive(TURN_SPEED, arclength, -(arclength),	4.0);

		encoderDrive(DRIVE_SPEED, 60, 60, 4.0);  		// Drive to the wall and then

		encoderDrive(DRIVE_SPEED, -24, -24, 4.0);  	// 24 inches away from the wall

		// Turn parallel to wall, and drive along
		theta = -90.0;
		arclength = (theta * 3.141592653589793 * DBDW) /  360;
		encoderDrive(TURN_SPEED, arclength, -(arclength),	4.0);  // turn 90 degrees away from wall to parallel the wall

		encoderDrive(DRIVE_SPEED, 48, 48, 4.0); 					// drive 48 inches to next beacon


		// Oh MY!!!, theres a beacon here!
		theta = 90.0;
		arclength = (theta * 3.141592653589793 * DBDW) /  360;
		encoderDrive(TURN_SPEED, arclength, -(arclength),	4.0);	// turn to wall

		encoderDrive(DRIVE_SPEED, 24, 24, 4.0);					// we're 24 inches away, so we can drive 24 inches to.

		encoderDrive(DRIVE_SPEED, -36,-36, 4.0);					// the next beacon is located 36 inches, in the y axis

		theta = -90.0;
		arclength = (theta * 3.141592653589793 * DBDW) /  360;
		encoderDrive(TURN_SPEED, arclength, -(arclength),	4.0);	// turn to wall




		//encoderDrive (TURN_SPEED, 12,-12,10000);
		//encoderDrive (DRIVE_SPEED, 36,36,50);

        telemetry.addData("Path", "Complete");
        telemetry.update();
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
            }

            //Stop all motion and reset motors//
            robot.leftMotor.setPower(0);
            robot.rightMotor.setPower(0);
            robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
}
