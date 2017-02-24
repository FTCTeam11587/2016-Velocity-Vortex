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
        DcMotor         lift_motor;
        Servo           grapple_servo;

        //Define variables for task functions//
        double          lift_motor_power = 0.8;
        double          GrapplePosition = 1.0;      //Grapple initializes to closed//
        final double    GRAPPLE_SPEED = 0.02;       //Grapple actuation speed//

        HardwarePushbot robot = new HardwarePushbot();

    @Override
        public void runOpMode() {
            double left;
            double right;
            double max;

            robot.init(hardwareMap);

            //Initialize aux hardware//

            lift_motor = hardwareMap.dcMotor.get("lift_motor");		// build config profile
            grapple_servo = hardwareMap.servo.get("grapple_servo");	// build config profile
            grapple_servo.setPosition(GrapplePosition);
	        int WE_WERE_CONTROLLING_LIFT = 0;
		int WWNCLCNT = 0;

            telemetry.addData("Say: ", "Hello Starry Knight!");
            telemetry.update();

            waitForStart();

            while (opModeIsActive()) {

               public static limit_max() {
                   max = Math.max(Math.abs(left), Math.abs(right));
		   if (max > 1.0) {
                       left /= max;
                       right /= max;
                   }
	       }

	       public static motor_data() {
		    telemetry.addData("Left Trigger: ", "%.2f", gamepad1.left_trigger);
	            telemetry.addData("Left Motor: ", left);
	            telemetry.addData("Right Motor: ", right);
	            telemetry.addData("Max Motor: ", max);
		    telemetry.update();
	       }

	    //Code to enable Cap Ball Drive - cuts drive power by 75% and inverts the gamepad1 stick Y-axis//

            if (gamepad1.left_trigger > 0.3) {
                robot.leftMotor.setPower( left/4 );
                robot.rightMotor.setPower( right/4 );
		left = gamepad1.left_stick_y + gamepad1.right_stick_x;
		right = gamepad1.right_stick_y - gamepad1.right_stick_x;
		limit_max();

		telemetry.addData("Op Mode: ","Cap Ball Drive");
		motor_data();
                }
            else {
                robot.leftMotor.setPower( (left/3) * 1.2);
                robot.rightMotor.setPower( (right/3) * 1.2);
	 	left = -gamepad1.left_stick_y + gamepad1.right_stick_x;
                right = -gamepad1.left_stick_y - gamepad1.right_stick_x;
		limit_max();

		telemetry.addData("Op Mode: ","Normal Drive");
		motor_data();
                }

            /*Code to open/close Cap Ball grapple servo*/

            //Use Gamepad 1 D-pad L/R to actuate the grapple servo//
            if (gamepad1.dpad_right) {
                GrapplePosition += GRAPPLE_SPEED;
            else if (gamepad1.dpad_left) {
                GrapplePosition -= GRAPPLE_SPEED;
             }
            GrapplePosition = Range.clip(GrapplePosition,0.0,1.0);
		grapple_servo.setPosition(GrapplePosition);

            telemetry.addData("Grapple Position: ", "%.2f",GrapplePosition);
            telemetry.update();

	    //Code to control pulley lift//

            if (gamepad1.dpad_up == true) {
		lift_motor.setPower(lift_motor_power);
		WE_WERE_CONTROLLING_LIFT = 1;
		WWNCLCNT = 0;
	    }

            else if (gamepad1.dpad_down == true) {
		lift_motor.setPower(-lift_motor_power);
		WE_WERE_CONTROLLING_LIFT = 1;
		WWNCLCNT = 0;
	    }
		
	    else if (gamepad1.dpad_up == false && gamepad1.dpad_down == false && WE_WERE_CONTROLLING_LIFT == 1 && WWNCLCNT < 300) {
		lift_motor.setPower(.01);
		WWNCLCNT++;
	    }
		
	    else if (gamepad1.dpad_up == false && gamepad1.dpad_down == false && WWNCLCNT == 300) {
		lift_motor.setPower(0);
		WE_WERE_CONTROLLING_LIFT = 0;
	    }
            
	    robot.waitForTick(40);
            }
        }
    }


