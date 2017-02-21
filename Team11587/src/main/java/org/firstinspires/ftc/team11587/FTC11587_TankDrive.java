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
        //DigitalChannel  upper_limit_switch;
        //DigitalChannel  lower_limit_switch;

        //Define variables for task functions//
        double          lift_motor_power = 0.8;
        double          GrapplePosition = 1.0;      //Grapple initializes to closed//
        //double          rightGrapplePosition = 1.0;     //Grapple initializes to closed//
        final double    GRAPPLE_SPEED = 0.02;           //Grapple actuation speed//

        HardwarePushbot robot = new HardwarePushbot();

    @Override
        public void runOpMode() {
            double left;
            double right;
            double max;

            robot.init(hardwareMap);

            //Initialize aux hardware//
            lift_motor = hardwareMap.dcMotor.get("lift_motor");		// build config profile
            grapple_servo = hardwareMap.servo.get("grapple");	// build config profile
            //right_grapple_servo = hardwareMap.servo.get("right_grapple");	// build config profile
            grapple_servo.setPosition(leftGrapplePosition);
            //right_grapple_servo.setPosition(rightGrapplePosition);
			int WE_WERE_CONTROLLING_LIFT = 0;
			int WWNCLCNT = 0;

            //upper_limit_switch = hardwareMap.digitalChannel.get("upperLimitSwitch");	// build config profile
            //lower_limit_switch = hardwareMap.digitalChannel.get("lowerLimitSwitch");	// build config profile

            telemetry.addData("Say: ", "Hello Knight!");
            telemetry.update();

            waitForStart();

            while (opModeIsActive()) {

                left = -gamepad1.left_stick_y + gamepad1.right_stick_x;
                right = -gamepad1.left_stick_y - gamepad1.right_stick_x;
                max = Math.max(Math.abs(left), Math.abs(right));
                telemetry.addData("Left Motor Pos: ", left);
                telemetry.addData("Right Motor Pos: ", right);
                telemetry.addData("Max Motor Pos: ", max);
                telemetry.update();

                if (max > 1.0) {
                    left /= max;
                    right /= max;
                }
                if (gamepad1.left_trigger > 0.3) {
                    robot.leftMotor.setPower( left/2 );
                    robot.rightMotor.setPower( right/2 );
                }
                else if (gamepad1.left_trigger < 0.3 ) {
                    robot.leftMotor.setPower( (left/3) * 1.2);
                    robot.rightMotor.setPower( (right/3) * 1.2);
                }



            /*Code to open/close Cap Ball grapple servo*/

            //Use Gamepad 1 D-pad L/R to actuate the grapple servo//
            if (gamepad1.dpad_right) {
                GrapplePosition += GRAPPLE_SPEED;
                //rightGrapplePosition -= GRAPPLE_SPEED;
            }
            else if (gamepad1.dpad_left) {
                GrapplePosition -= GRAPPLE_SPEED;
                //rightGrapplePosition += GRAPPLE_SPEED;
            }

            GrapplePosition = Range.clip(GrapplePosition,0.0,1.0);
				grapple_servo.setPosition(GrapplePosition);

            //rightGrapplePosition = Range.clip(rightGrapplePosition,0.0,1.0);
               // right_grapple_servo.setPosition(-rightGrapplePosition);
                telemetry.addData("Grapple Position: ", "%.2f",GrapplePosition);
                //telemetry.addData("Right Grapple Position:", "%.2f",rightGrapplePosition);
                telemetry.update();

            //Use Gamepad 1 D-pad Up/Down to actuate the lift motor//
            //boolean uLim = upper_limit_switch.getState();
            //boolean lLim = lower_limit_switch.getState();


            if (gamepad1.dpad_up == true) {
				//    if (uLim = false)  //Check this...generates caution...always true?
				lift_motor.setPower(lift_motor_power);
				WE_WERE_CONTROLLING_LIFT = 1;
				WWNCLCNT = 0;
				//    else if (uLim = true)
				//        lift_motor.setPower(0.0);
			}
            else if (gamepad1.dpad_down == true)
            //    if (lLim = false)
				lift_motor.setPower(-lift_motor_power);
				WE_WERE_CONTROLLING_LIFT = 1;
				WWNCLCNT = 0;
            //    else if (lLim = false)
            //        lift_motor.setPower(0.0);
			else if (gamepad1.dpad_up == false && gamepad1.dpad_down == false && WE_WERE_CONTROLLING_LIFT == 1 && WWNCLCNT < 300) {
				lift_motor.setPower(.05);
				WWNCLCNT++;
			}
			else if (gamepad1.dpad_up == false && gamepad1.dpad_down == false && WWNCLCNT == 300) {
				lift_motor.setPower(0);
				WE_WERE_CONTROLLING_LIFT = 0;
				//WWNCLCNT = 300;
			}
            	robot.waitForTick(40);
            }
        }
    }

