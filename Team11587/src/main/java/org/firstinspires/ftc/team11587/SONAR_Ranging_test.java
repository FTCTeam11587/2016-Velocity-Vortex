package org.firstinspires.ftc.team11587;

//import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import android.os.Environment;

import com.qualcomm.ftccommon.configuration.RobotConfigFile;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
//import com.qualcomm.robotcore.hardware.DigitalChannel;
//import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogInputController;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.PWMOutput;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.lang.Math.*;
import java.io.File;
import java.io.*;
import java.util.stream.*;

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
	OpticalDistanceSensor 	fwd_xdcr_rx;
	//File path = new File("/storage/emulated/0/FIRST/");


	public SONAR_Ranging_test() throws FileNotFoundException {
	}

	@Override
  public void runOpMode() throws InterruptedException {

		// get a reference to a Modern Robotics DIM, and IO channels.
		DEV_IF_MOD_1 = hardwareMap.deviceInterfaceModule.get("DEV_IF_MOD_1");	// build config profile
		xdcr_tx = hardwareMap.pwmOutput.get("xdcr_tx");							// build config profile
	  	fwd_xdcr_rx = hardwareMap.opticalDistanceSensor.get("fwd_xdcr_rx");				// build config profile

		String sentence = "New Data,	Rolling Average\n";
		String filepath = String.valueOf(Environment.getExternalStorageDirectory()) + File.separator + "FIRST" + File.separator;
		File root = new File(filepath);
		File datfile = new File(root, "ranging_data.dat");

		Writer writer = null;
		try {
			writer = new FileWriter(datfile, false);
			writer.append("NewVal, AvgVal\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


		xdcr_tx.setPulseWidthOutputTime(10);
		xdcr_tx.setPulseWidthPeriod(15000);

	  	waitForStart();
	  	runtime.reset();

	  	int i=1;
		int ii=1;
		int max_i = 10000;
		double RANGE_rolavg = -1;
	  	while (opModeIsActive()) {
			//double VRead = fwd_xdcr_rx.getMaxVoltage();
			if (i < max_i) {
				i++;
			}
			else {
				ii++;
			}
			//double RANGE_new = 1 / Math.sqrt(fwd_xdcr_rx.getLightDetected());
			double RANGE_new_sum = 0;
			for (int count=0; count < 300; count++) {
				RANGE_new_sum = RANGE_new_sum + (fwd_xdcr_rx.getLightDetected() * 2.217069383463489f * 7);
			}
			double RANGE_new = RANGE_new_sum / 300;

			if (RANGE_rolavg < 0) {
				if (!Double.isNaN(RANGE_new) && !Double.isInfinite(RANGE_new)) {
					RANGE_rolavg = RANGE_new;
				}
			}
			else {
				if (!Double.isNaN(RANGE_new) && !Double.isInfinite(RANGE_new)) {
					RANGE_rolavg = RANGE_rolavg + (RANGE_new - RANGE_rolavg) / i;
				}
			}
			//if (!Double.isNaN(RANGE_new) && !Double.isInfinite(RANGE_new)) {
			if (i >= max_i && ii % 1000 == 0) {
				sentence = String.format("%f, %f\n", RANGE_new, RANGE_rolavg);
				try {
					writer = new FileWriter(datfile, true);
					//FileWriter(datfile, boolean append);
					writer = writer.append(sentence);
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				telemetry.addData("NewVal, RolAvg: ",sentence);
				telemetry.addData("Iteration (i)", ii);
				telemetry.update();
			}

	  	}
  }
}
