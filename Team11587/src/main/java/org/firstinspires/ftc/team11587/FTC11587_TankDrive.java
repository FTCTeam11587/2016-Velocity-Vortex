package org.firstinspires.ftc.team11587;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * FTC Team 11587
 * Basic Tank Drive mode incorporating the Cap Ball grapple servo & DC lift motor.
 */
@TeleOp(name="TeleOp:  Tank Drive + Cap Ball", group="TeleOp")
public class FTC11587_TankDrive extends LinearOpMode {

	//Define hardware for task functions//
	DcMotor lift_motor;
	Servo grapple_finger;

	//DigitalChannel  upper_limit_switch;
	//DigitalChannel  lower_limit_switch;

	//Define variables for task functions//
	double lift_motor_power = 0.8;
	double GrapplePosition = 1.0;      //Grapple initializes to closed//
	final double GRAPPLE_SPEED = 0.02;           //Grapple actuation speed//

	HardwarePushbot robot = new HardwarePushbot();

	@Override
	public void runOpMode() {
		double left = 0;
		double right = 0;
		double max = 1;

		// Fix problem
		double limit_max;
		double motor_data;
		robot.init(hardwareMap);

		//Initialize aux hardware//

		lift_motor = hardwareMap.dcMotor.get("lift_motor");        // build config profile
		grapple_finger = hardwareMap.servo.get("grapple_finger");    // build config profile
		grapple_finger.setPosition(GrapplePosition);
		int WE_WERE_CONTROLLING_LIFT = 0;
		int WWNCLCNT = 0;

		telemetry.addData("Say: ", "Hello Knight!");
		telemetry.update();

		waitForStart();

		while (opModeIsActive()) {

			//Code to enable Cap Ball Drive - cuts drive power by 75% and inverts the gamepad1 stick Y-axis//

			left = -gamepad1.left_stick_y + gamepad1.right_stick_x;
			right = -gamepad1.left_stick_y + -gamepad1.right_stick_x;

			telemetry.addData("Right Trigger: ",gamepad1.right_trigger);
			telemetry.update();
			if (gamepad1.right_trigger > 0) {
				left = gamepad1.left_stick_y + gamepad1.right_stick_x;
				right = gamepad1.left_stick_y + -gamepad1.right_stick_x;

				robot.leftMotor.setPower(left / 8);
				robot.rightMotor.setPower(right / 8);

				telemetry.addData("Op Mode: ", "Cap Ball Drive");
				motor_data(left,right,max);
			}
			else {

				robot.leftMotor.setPower((left / 3) * 1.2);
				robot.rightMotor.setPower((right / 3) * 1.2);

				telemetry.addData("Op Mode: ", "Normal Drive");
				motor_data(left,right,max);
			}

			max = Math.max(Math.abs(left), Math.abs(right));
			if (max > 1.0) {
				left /= max;
				right /= max;
			}
				/*Code to open/close Cap Ball grapple servo*/

			//Use Gamepad 1 D-pad L/R to actuate the grapple servo//
			if (gamepad1.dpad_right) {
				GrapplePosition += GRAPPLE_SPEED;
			}
			else if (gamepad1.dpad_left) {
				GrapplePosition -= GRAPPLE_SPEED;
			}
			if (GrapplePosition < 0.0) {
				GrapplePosition = 0.0;
			}
			else if (GrapplePosition > 1.0) {
				GrapplePosition = 1.0;
			}
			//GrapplePosition = Range.clip(GrapplePosition, 0.0, 1.0);
			grapple_finger.setPosition(GrapplePosition);

			//telemetry.addData("Grapple Position: ", "%.2f", GrapplePosition);
			//telemetry.update();

			//Code to control pulley lift//

			if (gamepad1.dpad_up == true) {
				lift_motor.setPower(lift_motor_power);
				WE_WERE_CONTROLLING_LIFT = 1;
				WWNCLCNT = 0;
			} else if (gamepad1.dpad_down == true) {
				lift_motor.setPower(-lift_motor_power);
				WE_WERE_CONTROLLING_LIFT = 1;
				WWNCLCNT = 0;
			} else if (gamepad1.dpad_up == false && gamepad1.dpad_down == false && WE_WERE_CONTROLLING_LIFT == 1 && WWNCLCNT < 300) {
				lift_motor.setPower(.01);
				WWNCLCNT++;
			} else if (gamepad1.dpad_up == false && gamepad1.dpad_down == false && WWNCLCNT == 300) {
				lift_motor.setPower(0);
				WE_WERE_CONTROLLING_LIFT = 0;
			}

			robot.waitForTick(40);

		}
	}

	public void motor_data(double max, double left, double right) {
		telemetry.addData("Right Bumper: ", gamepad1.right_bumper);
		telemetry.addData("Left Motor: ", left);
		telemetry.addData("Right Motor: ", right);
		telemetry.addData("Max Motor: ", max);
		telemetry.update();
	}

}
	
	
