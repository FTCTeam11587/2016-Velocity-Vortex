package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * FTC Team 11587
 * Basic Tank Drive mode incorporating the Cap Ball grapple servo & DC lift motor.
 */
@TeleOp(name="TeleOp:  Tank Drive + Cap Ball", group="TeleOp")

public class FTC11587_TankDrive extends LinearOpMode {

    HardwarePushbot robot               =new HardwarePushbot();

    @Override
    public void runOpMode() {
        double  left;
        double  right;
        double  max;

        robot.init(hardwareMap);

        telemetry.addData("Say","Hello Knight!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            left = -gamepad1.left_stick_y + gamepad1.right_stick_x;
            right = -gamepad1.left_stick_y - gamepad1.right_stick_x;
            max = Math.max(Math.abs(left), Math.abs(right));
            telemetry.addData("left:",left);
            telemetry.addData("Right:",right);
            telemetry.addData("max:",max);
            telemetry.update();

            if (max > 1.0)
            {
                left /= max;
                right /= max;
            }

            robot.leftMotor.setPower(left);
            robot.rightMotor.setPower(right);

            /*Code to open/close Cap Ball grapple servo


             */

            /*Code to raise/lower Cap Ball lift


             */

            robot.waitForTick(40);
        }
    }
}
