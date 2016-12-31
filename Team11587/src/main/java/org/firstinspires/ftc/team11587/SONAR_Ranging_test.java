package org.firstinspires.ftc.team11587;

//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
//import com.qualcomm.robotcore.hardware.PWMOutput;
import com.qualcomm.robotcore.hardware.PWMOutput;
import com.qualcomm.robotcore.hardware.PWMOutputController;
import com.qualcomm.robotcore.hardware.PWMOutputControllerEx;
import com.qualcomm.robotcore.util.ElapsedTime;


import java.util.concurrent.TimeUnit;

@Autonomous(name = "SONAR", group="Linear Opmode")

public class SONAR_Ranging_test extends LinearOpMode {


//final int fwd_xdcr_tx = 7;
//final int fwd_xdcr_rx = 6;

	final int RED_LED_CHANNEL = 1;
	final int BLUE_LED_CHANNEL = 0;

	boolean					HIGH;				// Input State
	private ElapsedTime runtime = new ElapsedTime();
	DeviceInterfaceModule   DEV_IF_MOD_1;			// Device Object
	PWMOutput				xdcr_tx;			// Device Object
	DigitalChannel			fwd_xdcr_rx;
	long PW_Rolling = -1;
	//PWMOutputControllerEx fwd_xdcr_tx;


  @Override
  public void runOpMode() throws InterruptedException {

		// get a reference to a Modern Robotics DIM, and IO channels.
		DEV_IF_MOD_1 = hardwareMap.deviceInterfaceModule.get("DEV_IF_MOD_1");	// build config profile
		xdcr_tx = hardwareMap.pwmOutput.get("xdcr_tx");							// build config profile
		fwd_xdcr_rx  = hardwareMap.digitalChannel.get("fwd_xdcr_rx");			// build config profile

		xdcr_tx.setPulseWidthOutputTime(10);
		xdcr_tx.setPulseWidthPeriod(100000);
		fwd_xdcr_rx.setMode(DigitalChannelController.Mode.INPUT);		  // Set the direction of each channel

	// wait for the start button to be pressed.
	//telemetry.addData(">", "Press play, and then user X button to set DigOut");
	//telemetry.update();
	//waitForStart();
	  waitForStart();
	  runtime.reset();

	while (opModeIsActive()) {


		HIGH = fwd_xdcr_rx.getState();    //  Read the input pin
		long STARTTIME = System.nanoTime();
		long SPACE_WIDTH = 0;
		while (HIGH) {
			HIGH = fwd_xdcr_rx.getState();
			// We're not measuring in the middle of a sample, that's Crazy!
		}
		while (!HIGH && SPACE_WIDTH < 100000) {
			long NEWTIME = System.nanoTime();
			SPACE_WIDTH = NEWTIME - STARTTIME;
			HIGH = fwd_xdcr_rx.getState();
		}
		STARTTIME = System.nanoTime();
		long PULSEWIDTH = 0;
		while (HIGH) {
				HIGH = fwd_xdcr_rx.getState();
				long NEWTIME = System.nanoTime();
				PULSEWIDTH = NEWTIME - STARTTIME;
				PULSEWIDTH = (PULSEWIDTH / 1000);
		}
		if (PULSEWIDTH > 0) {
			if (PW_Rolling < 0) {
				PW_Rolling = PULSEWIDTH;
			}
			else {
				PW_Rolling = PW_Rolling + ((PULSEWIDTH - PW_Rolling)/100);
			}
			telemetry.addData("PULSEWIDTH", PULSEWIDTH);
			telemetry.addData("Rolling_Avg", PW_Rolling);
			telemetry.update();
		}
	}
  }
}
