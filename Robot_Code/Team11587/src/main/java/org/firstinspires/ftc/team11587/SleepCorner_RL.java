package org.firstinspires.ftc.team11587;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="SleepCorner_RL", group="Linear Opmode")
public class SleepCorner_RL extends LinearOpMode{
    DcMotor port_motor;
    DcMotor stbd_motor;
    private ElapsedTime runtime = new ElapsedTime();
    final double max_fwd = 1.0 / 4;
    final double max_rev = -1.0 / 4;
    final long waittime = 200;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        port_motor = hardwareMap.dcMotor.get("port_motor");
        stbd_motor = hardwareMap.dcMotor.get("stbd_motor");
        stbd_motor.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            sleep(10000);
            port_motor.setPower(max_fwd);
            stbd_motor.setPower(max_fwd);
            sleep(1500);
            port_motor.setPower(0);
            stbd_motor.setPower(0);
            sleep(waittime);
            port_motor.setPower(-(max_fwd / 4));
            stbd_motor.setPower(max_fwd / 4);
            sleep(950);
            port_motor.setPower(0);
            stbd_motor.setPower(0);
            sleep(waittime);
            port_motor.setPower(max_rev);
            stbd_motor.setPower(max_rev);
            sleep(5000);
            port_motor.setPower(0);
            stbd_motor.setPower(0);
            sleep(30000);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }
}

