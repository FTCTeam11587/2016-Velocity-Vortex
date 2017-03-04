package org.firstinspires.ftc.team11587;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by hitchcme on 1/28/17.
 */

@Autonomous(name="AutoDrive: Sleep_CornerPark_BF_Enc", group="AutoDrive")

public class Sleep_CornerPark_BF_Enc extends LinearOpMode {

	HardwarePushbot robot = new HardwarePushbot();
	private ElapsedTime runtime = new ElapsedTime();

	Servo	grapple_finger;
	double	GrapplePosition = 1.0;
	double  GRAPPLE_SPEED = 0.02;

	static final double COUNTS_PER_MOTOR_REV = 1440;  /*Adjust to CPR * 4*/
	static final double DRIVE_GEAR_REDUCTION = 0.5;   /*Motor gear = 80 tooth + wheel gear = 40 tooth*/
	static final double WHEEL_DIAMETER_INCHES = 3.75;
	static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION /
			(WHEEL_DIAMETER_INCHES * 3.141592653589793));
	static final double DRIVE_SPEED = 0.125;
	static final double TURN_SPEED = 0.08;
	static final double Enc_D_CF = 3.2;

	double arclength;
	double theta;

	@Override
	public void runOpMode() throws InterruptedException {

		robot.init(hardwareMap);

		// Encoder Stuff
		robot.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		robot.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

		grapple_finger = hardwareMap.servo.get("grapple_finger");
		GrapplePosition += GRAPPLE_SPEED;
		grapple_finger.setPosition(GrapplePosition);

		telemetry.addData("Path0", "Starting at %7d : %7d",        //Feedback to DS that AutoDrive is starting//
				robot.leftMotor.getCurrentPosition(),
				robot.rightMotor.getCurrentPosition());
		telemetry.update();

		waitForStart();     //Wait until DS presses PLAY//

		sleep(10000);
		encoderDrive(DRIVE_SPEED, 36, 36, 10000);        //Start @ 48,0
		encoderTurn(TURN_SPEED, -90.0, 10000);        //Pivot tail towards corner
		encoderDrive(DRIVE_SPEED, -48, -48, 10000);        //Back into corner
		encoderTurn(TURN_SPEED, 45.0, 10000);
		encoderDrive(DRIVE_SPEED, -24, -24, 10000);
		encoderDrive(DRIVE_SPEED * 0.025, -100, -100, 20000);
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
			CurrentPosition_right = robot.leftMotor.getCurrentPosition();
			CurrentPosition_left = robot.rightMotor.getCurrentPosition();

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
