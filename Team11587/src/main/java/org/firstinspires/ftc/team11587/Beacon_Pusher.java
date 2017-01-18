package org.firstinspires.ftc.team11587;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.PWMOutput;

import org.lasarobotics.vision.android.Cameras;
import org.lasarobotics.vision.ftc.resq.Beacon;
import org.lasarobotics.vision.opmode.LinearVisionOpMode;
import org.lasarobotics.vision.opmode.VisionOpMode;
import org.lasarobotics.vision.opmode.extensions.CameraControlExtension;
import org.lasarobotics.vision.util.ScreenOrientation;
import org.opencv.core.Mat;
import org.opencv.core.Size;

// Stuff I've Added
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;


/**
 * Linear Vision Sample
 * <p/>
 * Use this in a typical linear op mode. A LinearVisionOpMode allows using
 * Vision Extensions, which do a lot of processing for you. Just enable the extension
 * and set its options to your preference!
 * <p/>
 * Please note that the LinearVisionOpMode is specially designed to target a particular
 * version of the FTC Robot Controller app. Changes to the app may break the LinearVisionOpMode.
 * Should this happen, open up an issue on GitHub. :)
 */
@Autonomous(name = "Beacon Pusher", group="Linear Opmode")

public class Beacon_Pusher extends LinearVisionOpMode {

	//Frame counter
	int frameCount = 0;

	public Beacon_Pusher() throws FileNotFoundException {
	}

	@Override
	public void runOpMode() throws InterruptedException {

		waitForVisionStart();

		this.setCamera(Cameras.PRIMARY);

		this.setFrameSize(new Size(900, 900));

		enableExtension(Extensions.BEACON);         //Beacon detection
		enableExtension(Extensions.ROTATION);       //Automatic screen rotation correction
		enableExtension(Extensions.CAMERA_CONTROL); //Manual camera control

		beacon.setAnalysisMethod(Beacon.AnalysisMethod.FAST);

		beacon.setColorToleranceRed(0);
		beacon.setColorToleranceBlue(0);

		rotation.setIsUsingSecondaryCamera(false);
		rotation.disableAutoRotate();
		rotation.setActivityOrientationFixed(ScreenOrientation.PORTRAIT);

		cameraControl.setColorTemperature(CameraControlExtension.ColorTemperature.AUTO);
		cameraControl.setAutoExposureCompensation();

		AsyncTask Rh_Finder_Running = new Rh_Finder().execute();
		waitForStart();

		while (opModeIsActive()) {
			//Log a few things
			telemetry.addData("Beacon Color", beacon.getAnalysis().getColorString());
			telemetry.addData("Beacon Center", beacon.getAnalysis().getLocationString());
			telemetry.addData("Beacon Confidence", beacon.getAnalysis().getConfidenceString());
			telemetry.addData("Beacon Buttons", beacon.getAnalysis().getButtonString());
			telemetry.addData("Screen Rotation", rotation.getScreenOrientationActual());
			telemetry.addData("Frame Rate", fps.getFPSString() + " FPS");
			telemetry.addData("Frame Size", "Width: " + width + " Height: " + height);
			telemetry.addData("Frame Counter", frameCount);

			if (hasNewFrame()) {
				//Get the frame
				Mat rgba = getFrameRgba();
				Mat gray = getFrameGray();

				//Discard the current frame to allow for the next one to render
				discardFrame();

				//Do all of your custom frame processing here
				//For this demo, let's just add to a frame counter
				frameCount++;
			}

			// you need to keep a reference to the task so you can cancel it later
			if (Rh_Finder_Running == null) {
				// task doesn't exist -- make a new task and start it
				Rh_Finder_Running = new Rh_Finder();
				Rh_Finder_Running.execute();
				// this will make doInBackground() run on background thread
			}

			//Wait for a hardware cycle to allow other processes to run
			waitOneFullHardwareCycle();
		}
	}

    private class Rh_Finder extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {

			double Rh[] = new double[4];

			DeviceInterfaceModule DEV_IF_MOD_1;			// Device Object
			PWMOutput xdcr_tx;			// Device Object
			OpticalDistanceSensor fwd_xdcr_rx;
			OpticalDistanceSensor 	port_xdcr_rx;
			OpticalDistanceSensor 	aft_xdcr_rx;
			OpticalDistanceSensor 	stbd_xdcr_rx;

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

			int i=1;
			int ii=1;
			int max_i = 10000;
			double RANGE_rolavg = -1;

			while (! isCancelled()) {

                synchronized (Rh) {
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
						RANGE_new_sum = RANGE_new_sum + (fwd_xdcr_rx.getLightDetected() * 9.951450243819872f);
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

					Rh[0] = 0;
					Rh[1] = 0;
					Rh[2] = 0;
					Rh[3] = 0;
                }
				publishProgress(null);
            }
            return null;
        }

        protected void onPostExecute() {

        }

        protected void onPreExecute() {

        }


        protected Double[] onProgressUpdate(Double[] Rh) {
			synchronized(Rh) {}
			return Rh;
        }
    }

}
