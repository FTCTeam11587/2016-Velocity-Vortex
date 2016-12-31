package org.firstinspires.ftc.team11587;

//import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
//import com.qualcomm.robotcore.hardware.DigitalChannel;
//import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogInputController;
import com.qualcomm.robotcore.hardware.PWMOutput;
import com.qualcomm.robotcore.util.ElapsedTime;

//import com.qualcomm.robotcore.hardware.PWMOutput;

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
	AnalogInput				fwd_xdcr_rx;

  @Override
  public void runOpMode() throws InterruptedException {

		// get a reference to a Modern Robotics DIM, and IO channels.
		DEV_IF_MOD_1 = hardwareMap.deviceInterfaceModule.get("DEV_IF_MOD_1");	// build config profile
		xdcr_tx = hardwareMap.pwmOutput.get("xdcr_tx");							// build config profile
	  	fwd_xdcr_rx = hardwareMap.analogInput.get("fwd_xdcr_rx");				// build config profile

		xdcr_tx.setPulseWidthOutputTime(10);
		xdcr_tx.setPulseWidthPeriod(15000);

	  	waitForStart();
	  	runtime.reset();

	  	double VOLTAGE = -1;
	  	double DISTANCE;
	  	int i=1;
	  	while (opModeIsActive()) {
		  	i++;
		  	double VRead = fwd_xdcr_rx.getVoltage();
		  	if (VOLTAGE > -1) {
			  	VOLTAGE = VOLTAGE + ((VRead - VOLTAGE)/i);
		  	}
		  	else {
			  	VOLTAGE = VRead;
		  	}
		  	DISTANCE = (VRead * 15.509259259259f)/12.000000f;
		  	telemetry.addData("VOLTAGE", VOLTAGE);
		  	telemetry.addData("iteration", i);
		  	telemetry.addData("Distance",DISTANCE);
		  	telemetry.update();
	  	}
  }
}
