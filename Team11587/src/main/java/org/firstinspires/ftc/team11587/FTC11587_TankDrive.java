package org.firstinspires.ftc.team11587;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
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
        double          lift_motor_power = 0.5;
        double          grapplePosition = 0.5;      //Grapple initializes to mid-position...needs to be changed//
        final double    GRAPPLE_SPEED = 0.02;       //Grapple actuation speed//

        HardwarePushbot robot = new HardwarePushbot();

    @Override
        public void runOpMode() {
            double left;
            double right;
            double max;

            robot.init(hardwareMap);

            //Initialize aux hardware//
            lift_motor = hardwareMap.dcMotor.get("lift_motor");
            grapple_servo = hardwareMap.servo.get("grapple_servo");
            grapple_servo.setPosition(grapplePosition);

            telemetry.addData("Say", "Hello Knight!");
            telemetry.update();

            waitForStart();

            while (opModeIsActive()) {

                left = -gamepad1.left_stick_y + gamepad1.right_stick_x;
                right = -gamepad1.left_stick_y - gamepad1.right_stick_x;
                max = Math.max(Math.abs(left), Math.abs(right));
                telemetry.addData("left:", left);
                telemetry.addData("Right:", right);
                telemetry.addData("max:", max);
                telemetry.update();

                if (max > 1.0) {
                    left /= max;
                    right /= max;
                }

                robot.leftMotor.setPower(left);
                robot.rightMotor.setPower(right);

            /*Code to open/close Cap Ball grapple servo*/

            //Use Gamepad 1 D-pad L/R to actuate the grapple servo//
            if (gamepad1.dpad_right)
                grapplePosition += GRAPPLE_SPEED;

            else if (gamepad1.dpad_left)
                grapplePosition -= GRAPPLE_SPEED;

            grapplePosition = Range.clip(grapplePosition,0,1);
                grapple_servo.setPosition(grapplePosition);

                telemetry.addData("Grapple: ", "%.2f",grapplePosition);
                telemetry.update();

            //Use Gamepad 1 D-pad Up/Down to actuate the lift motor//
            if (gamepad1.dpad_up)
                lift_motor.setPower(lift_motor_power);  //Need to add limit switch logic//
            else if (gamepad1.dpad_down)
                lift_motor.setPower(-lift_motor_power);

                 robot.waitForTick(40);
            }
        }
    }
