package org.firstinspires.ftc.team11587;

import android.content.Context;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

//@TeleOp(name="Tank Drive with Claw", group="Linear Opmode")

public class TankDriveClaw extends LinearOpMode {
    DcMotor port_motor;
    DcMotor stbd_motor;
    DcMotor lift_motor;
    Servo port_claw;
    Servo stbd_claw;
    double port_motorpower;
    double stbd_motorpower;

    double clawPosition = 0.5;            // Servo mid position
    final double CLAW_SPEED = 0.02;   // Sets rate to move servo

    @Override
    public void runOpMode() throws InterruptedException {
        port_motor = hardwareMap.dcMotor.get("port_motor");	    // build config profile
        stbd_motor = hardwareMap.dcMotor.get("stbd_motor");     // build config profile
        lift_motor = hardwareMap.dcMotor.get("lift_motor");       // build config profile
        port_claw = hardwareMap.servo.get("port_claw");	        // build config profile
        stbd_claw = hardwareMap.servo.get("stbd_claw");	        // build config profile
        stbd_motor.setDirection(DcMotor.Direction.REVERSE);
        port_claw.setPosition(clawPosition);
        stbd_claw.setPosition(clawPosition);
        waitForStart();

        while (opModeIsActive()){

            port_motorpower = -gamepad1.left_stick_y;
            stbd_motorpower = -gamepad1.right_stick_y;

            port_motor.setPower(port_motorpower);
            stbd_motor.setPower(stbd_motorpower);

            if(gamepad1.right_trigger != 0)
                lift_motor.setPower(-gamepad1.right_trigger*0.25);
            else if(gamepad1.left_trigger != 0)
                lift_motor.setPower(gamepad1.left_trigger*0.25);
            else if(gamepad1.left_trigger == 0 && gamepad2.right_trigger == 0)
                lift_motor.setPower(0);

            if (gamepad1.left_bumper)
                clawPosition += CLAW_SPEED;
            else if(gamepad1.right_bumper)
                clawPosition -= CLAW_SPEED;

            stbd_claw.setPosition(1-clawPosition);
            port_claw.setPosition(clawPosition);

            idle();
        }
    }
}



