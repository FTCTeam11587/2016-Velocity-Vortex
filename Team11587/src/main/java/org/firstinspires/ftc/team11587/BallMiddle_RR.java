package org.firstinspires.ftc.team11587;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="BallMiddle_RR", group="Linear Opmode")
public class BallMiddle_RR extends LinearOpMode{
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
            leftmotor.setPower(max_fwd);
            rightmotor.setPower(max_fwd);
            sleep(1500);
            leftmotor.setPower(0);
            rightmotor.setPower(0);
            sleep(waittime);
            leftmotor.setPower(-0.25);
            rightmotor.setPower(0.25);
            sleep(1000);
            leftmotor.setPower(0);
            rightmotor.setPower(0);
            sleep(waittime);
            leftmotor.setPower(max_fwd);
            rightmotor.setPower(max_fwd);
            sleep(2100);
            leftmotor.setPower(0.25);
            rightmotor.setPower(-0.25);
            sleep(1700);
            leftmotor.setPower(0);
            rightmotor.setPower(0);
            sleep(waittime);
            leftmotor.setPower(max_fwd);
            rightmotor.setPower(max_fwd);
            sleep(1600);
            leftmotor.setPower(0);
            rightmotor.setPower(0);
            sleep(waittime);
            /*leftmotor.setPower(max_rev);
            rightmotor.setPower(max_rev);
            sleep(1300);
            leftmotor.setPower(0);
            rightmotor.setPower(0);
            sleep(waittime);
            leftmotor.setPower(-0.25);
            rightmotor.setPower(0.25);
            sleep(1500);
            leftmotor.setPower(max_rev);
            rightmotor.setPower(max_rev);
            sleep(4500);
            leftmotor.setPower(0);
            rightmotor.setPower(0);
            sleep(waittime);*/
            leftmotor.setPower(0);
            rightmotor.setPower(0);
            sleep(30000);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
        }
}
