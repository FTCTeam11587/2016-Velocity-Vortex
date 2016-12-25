package org.firstinspires.ftc.team11587;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Tank Drive with Claw", group="Linear Opmode")

public class TankDriveClaw extends LinearOpMode {

    DcMotor leftmotor;
    DcMotor rightmotor;
    DcMotor armmotor;
    Servo leftClaw;
    Servo rightClaw;
    double leftmotorpower;
    double rightmotorpower;

    double clawPosition = 0.5;            // Servo mid position
    final double CLAW_SPEED = 0.02;   // Sets rate to move servo

    @Override
    public void runOpMode() throws InterruptedException {
        leftmotor = hardwareMap.dcMotor.get("left motor");
        rightmotor = hardwareMap.dcMotor.get("right motor");
        armmotor = hardwareMap.dcMotor.get("arm");
        leftClaw = hardwareMap.servo.get("left claw");
        rightClaw = hardwareMap.servo.get("right claw");
        rightmotor.setDirection(DcMotor.Direction.REVERSE);
        leftClaw.setPosition(clawPosition);
        rightClaw.setPosition(clawPosition);
        waitForStart();

        while (opModeIsActive()){

            leftmotorpower = -gamepad1.left_stick_y;
            rightmotorpower = -gamepad1.right_stick_y;

            leftmotor.setPower(leftmotorpower);
            rightmotor.setPower(rightmotorpower);

            if(gamepad1.right_trigger != 0)
                armmotor.setPower(-gamepad1.right_trigger*0.25);
            else if(gamepad1.left_trigger != 0)
                armmotor.setPower(gamepad1.left_trigger*0.25);
            else if(gamepad1.left_trigger == 0 && gamepad2.right_trigger == 0)
                armmotor.setPower(0);

            if (gamepad1.left_bumper)
                clawPosition += CLAW_SPEED;
            else if(gamepad1.right_bumper)
                clawPosition -= CLAW_SPEED;

            rightClaw.setPosition(1-clawPosition);
            leftClaw.setPosition(clawPosition);

            idle();
        }
    }
}



