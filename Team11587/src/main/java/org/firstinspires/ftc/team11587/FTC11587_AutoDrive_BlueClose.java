package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.lang.Math;
import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * FTC Team 11587
 * AutoDrive from Blue Near position (0,60) to lead-in line to near Blue Beacon
 */

@Autonomous(name="AutoDrive: Blue Close", group="AutoDrive")
public class FTC11587_AutoDrive_BlueClose extends LinearOpMode {

    HardwarePushbot     robot       =new HardwarePushbot();
    private ElapsedTime runtime     =new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV        =1440;  /*Adjust to CPR * 4*/
    static final double     DRIVE_GEAR_REDUCTION        =2.0;   /*Motor gear = 40 tooth + wheel gear = 80 tooth*/
    static final double     WHEEL_DIAMETER_INCHES       =(4.357 + 4.382 ) / 2; //4.439;
    static final double     COUNTS_PER_INCH             =(COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION /                                                                 (WHEEL_DIAMETER_INCHES * 3.141592654));
    static final double     DRIVE_SPEED                 =0.6;
    static final double     TURN_SPEED                  =0.5;
	static final double 	Enc_D_CF					= 3.2;

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        telemetry.addData("Status","Resetting Encoders");  //Send telemetry indicating encoder reset//
        telemetry.update();

        robot.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        idle();

        robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Path0", "Starting at %7d : %7d",        //Feedback to DS that AutoDrive is starting//
                            robot.leftMotor.getCurrentPosition(),
                            robot.rightMotor.getCurrentPosition());
        telemetry.update();

        waitForStart();     //Wait until DS presses PLAY//

        encoderDrive (DRIVE_SPEED, 108,108,10000);  //Drives from Blue Near (0,60) to start of near Blue Beacon lead-in line (60,24)
        //encoderDrive (TURN_SPEED, 12,-12,3);
        //encoderDrive (DRIVE_SPEED, 36,36,3);

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }

    public void encoderDrive (double speed, double leftInches, double rightInches, double timeoutS) {

            int newLeftTarget;
            int newRightTarget;
			double CurrentPosition_left;
			double CurrentPosition_right;


        if (opModeIsActive()) {

            //Sets new target position using current position//

			CurrentPosition_right = robot.leftMotor.getCurrentPosition() *  Enc_D_CF;
			CurrentPosition_left = robot.rightMotor.getCurrentPosition() *  Enc_D_CF;

            newLeftTarget  = (int) (CurrentPosition_left + (int)(leftInches * COUNTS_PER_INCH) + 0.5);
			newRightTarget = (int) (CurrentPosition_right + (int)(rightInches * COUNTS_PER_INCH) + 0.5);







            robot.leftMotor.setTargetPosition(newLeftTarget);    // + 0.5, because rounding to nearest is better!!!
            robot.rightMotor.setTargetPosition(newRightTarget);

            //Set motors to Encoder drive mode//
            robot.leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();
            robot.leftMotor.setPower(Math.abs(speed));
            robot.rightMotor.setPower(Math.abs(speed));

            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (robot.leftMotor.isBusy() && robot.rightMotor.isBusy())) {
                telemetry.addData("Path1", "Driving to %7d :%7d",newLeftTarget,newRightTarget);
                telemetry.addData("Path2", "Driving at %7d :%7d",
                                            robot.leftMotor.getCurrentPosition(),
                                            robot.rightMotor.getCurrentPosition());
                telemetry.update();
            }

            //Stop all motion and reset motors//
            robot.leftMotor.setPower(0);
            robot.rightMotor.setPower(0);
            robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
}
