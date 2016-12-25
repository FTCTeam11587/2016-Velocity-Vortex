package org.firstinspires.ftc.team11587;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="SleepBallCorner_LR", group="Linear Opmode")
public class SleepBallCorner_LR extends LinearOpMode{
    DcMotor leftmotor;
    DcMotor rightmotor;
    private ElapsedTime runtime = new ElapsedTime();
    final double max_fwd = 1.0;
    final double max_rev = -1.0;
    final long waittime = 200;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        leftmotor = hardwareMap.dcMotor.get("left motor");
        rightmotor = hardwareMap.dcMotor.get("right motor");
        rightmotor.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            sleep(8000);
            leftmotor.setPower(max_fwd);
            rightmotor.setPower(max_fwd);
            sleep(2050);
            leftmotor.setPower(0);
            rightmotor.setPower(0);
            sleep(waittime);
            leftmotor.setPower(-0.25);
            rightmotor.setPower(0.25);
            sleep(800);
            leftmotor.setPower(0);
            rightmotor.setPower(0);
            sleep(waittime);
            leftmotor.setPower(max_fwd);
            rightmotor.setPower(max_fwd);
            sleep(1550);
            leftmotor.setPower(0);
            rightmotor.setPower(0);
            sleep(waittime);
            leftmotor.setPower(max_rev);
            rightmotor.setPower(max_rev);
            sleep(1550);
            leftmotor.setPower(0);
            rightmotor.setPower(0);
            sleep(waittime);
            leftmotor.setPower(0.25);
            rightmotor.setPower(-0.25);
            sleep(1400);
            leftmotor.setPower(0);
            rightmotor.setPower(0);
            sleep(waittime);
            leftmotor.setPower(max_rev);
            rightmotor.setPower(max_rev);
            sleep(5200);
            leftmotor.setPower(0);
            rightmotor.setPower(0);
            sleep(waittime);
            leftmotor.setPower(0);
            rightmotor.setPower(0);
            sleep(30000);

        }
        }
}
