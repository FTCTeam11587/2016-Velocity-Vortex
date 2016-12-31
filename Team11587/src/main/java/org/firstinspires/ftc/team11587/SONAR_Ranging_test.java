package org.firstinspires.ftc.team11587;

//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
//import com.qualcomm.robotcore.hardware.PWMOutput;
import com.qualcomm.robotcore.hardware.PWMOutputController;
import com.qualcomm.robotcore.hardware.PWMOutputControllerEx;


import java.util.concurrent.TimeUnit;

@Autonomous(name = "SONAR", group="Linear Opmode")

public class SONAR_Ranging_test extends LinearOpMode {


//final int fwd_xdcr_tx = 7;
//final int fwd_xdcr_rx = 6;

	final int RED_LED_CHANNEL = 1;
	final int BLUE_LED_CHANNEL = 0;

	boolean					HIGH;				// Input State
	DeviceInterfaceModule   DEV_IF_MOD_1;			// Device Object
	DigitalChannel			fwd_xdcr_tx;			// Device Object
	DigitalChannel			fwd_xdcr_rx;
	//PWMOutputControllerEx fwd_xdcr_tx;


  @Override
  public void runOpMode() throws InterruptedException {

		// get a reference to a Modern Robotics DIM, and IO channels.
		DEV_IF_MOD_1 = hardwareMap.deviceInterfaceModule.get("DEV_IF_MOD_1");	// build config profile
		fwd_xdcr_tx  = hardwareMap.digitalChannel.get("fwd_xdcr_tx");			// build config profile
		//fwd_xdcr_tx = hardwareMap.pwmOutput.get("fwd_xdcr_tx");				// build config profile
		fwd_xdcr_rx  = hardwareMap.digitalChannel.get("fwd_xdcr_rx");			// build config profile
		fwd_xdcr_tx.setMode(DigitalChannelController.Mode.OUTPUT);
		//fwd_xdcr_tx.resetDeviceConfigurationForOpMode();
		//fwd_xdcr_tx.setPulseWidthOutputTime(10);
		//fwd_xdcr_tx.setPulseWidthPeriod(1000);
		fwd_xdcr_rx.setMode(DigitalChannelController.Mode.INPUT);		  // Set the direction of each channel

	// wait for the start button to be pressed.
	//telemetry.addData(">", "Press play, and then user X button to set DigOut");
	//telemetry.update();
	//waitForStart();

	//while (opModeIsActive())  {
	while (true) {
		//outputPin = gamepad1.x ;		//  Set the output pin based on x button
		//digOut.setState(outputPin);



		long STARTTIME = System.nanoTime();
		long PULSEWIDTH = 0;
		fwd_xdcr_tx.setState(true);
		while (PULSEWIDTH / 1000 < 8) {
			long NEWTIME = System.nanoTime();
			PULSEWIDTH = NEWTIME - STARTTIME;
		}
		fwd_xdcr_tx.setState(false);
		for ( int i = 0; i < 450; i += 50) {
			TimeUnit.MICROSECONDS.sleep(50);
		}

		HIGH = fwd_xdcr_rx.getState();	//  Read the input pin

		// Display input pin state on LEDs
		if (HIGH) {
			//while (HIGH) {
				HIGH = fwd_xdcr_rx.getState();
				long NEWTIME = System.nanoTime();
				PULSEWIDTH = NEWTIME - STARTTIME;
			//}
			PULSEWIDTH = (PULSEWIDTH / 1000) - 60;
			DEV_IF_MOD_1.setLED(RED_LED_CHANNEL, true);
			DEV_IF_MOD_1.setLED(BLUE_LED_CHANNEL, false);

		}

		else {
			DEV_IF_MOD_1.setLED(BLUE_LED_CHANNEL, true);
			DEV_IF_MOD_1.setLED(RED_LED_CHANNEL, false);
		}
		//TimeUnit.MICROSECONDS.sleep(100000);
		//telemetry.addData("PULSEWIDTH", PULSEWIDTH );
		//telemetry.addData("LED",   HIGH ? "Red" : "Blue" );
		//telemetry.update();
		sleep(100);
	}
  }
}
