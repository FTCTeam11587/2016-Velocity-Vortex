package org.firstinspires.ftc.team11587;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.PWMOutput;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.lasarobotics.vision.android.Cameras;
import org.lasarobotics.vision.ftc.resq.Beacon;
import org.lasarobotics.vision.opmode.LinearVisionOpMode;
import org.lasarobotics.vision.opmode.extensions.CameraControlExtension;
import org.lasarobotics.vision.util.ScreenOrientation;
import org.opencv.core.Mat;
import org.opencv.core.Size;

// Stuff I've Added
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;

import java.io.*;
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

	// Hardware Variables
	DcMotor port_motor;
	DcMotor stbd_motor;
	Servo SONAR_Pedestal_Drive;
	double Pedestal_Position = 0.5;            // Servo mid position
	final double Pedestal_Speed = 0.02;   // Sets rate to move servo

	// Encoder Stuff
	static final double     COUNTS_PER_MOTOR_REV        =1440;  /*Adjust to CPR * 4*/
	static final double     DRIVE_GEAR_REDUCTION        =2.0;   /*Motor gear = 40 tooth + wheel gear = 80 tooth*/
	static final double     WHEEL_DIAMETER_INCHES       =(4.357 + 4.382 ) / 2; //4.439;
	static final double     COUNTS_PER_INCH             =(COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION /                                                                 (WHEEL_DIAMETER_INCHES * 3.141592654));
	static final double     DRIVE_SPEED                 =0.4;
	static final double     TURN_SPEED                  =0.5;


	private ElapsedTime runtime     =new ElapsedTime();

	//Frame counter
	int frameCount = 0;

	public Beacon_Pusher() throws FileNotFoundException {
	}

	@Override
	public void runOpMode() throws InterruptedException {

		waitForVisionStart();
		port_motor = hardwareMap.dcMotor.get("port_motor");	    						// build config profile
		stbd_motor = hardwareMap.dcMotor.get("stbd_motor");     						// build config profile
		SONAR_Pedestal_Drive = hardwareMap.servo.get("SONAR_Pedestal_Drive");	        // build config profile
		stbd_motor.setDirection(DcMotor.Direction.REVERSE);
		SONAR_Pedestal_Drive.setPosition(Pedestal_Position);

		port_motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		stbd_motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

		port_motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		stbd_motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

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


		int POSITION = 0;
		Make_New_CalDat_file();

		double Rh[] = new double[4];
		int BAD_Rh = 1;
		ElapsedTime runtime     =new ElapsedTime();

		while (opModeIsActive()) {
			//Log a few things
			//telemetry.addData("Beacon Color", beacon.getAnalysis().getColorString());
			//telemetry.addData("Beacon Center", beacon.getAnalysis().getLocationString());
			//telemetry.addData("Beacon Confidence", beacon.getAnalysis().getConfidenceString());
			//telemetry.addData("Beacon Buttons", beacon.getAnalysis().getButtonString());
			//telemetry.addData("Screen Rotation", rotation.getScreenOrientationActual());
			//telemetry.addData("Frame Rate", fps.getFPSString() + " FPS");
			//telemetry.addData("Frame Size", "Width: " + width + " Height: " + height);
			//telemetry.addData("Frame Counter", frameCount);

			// you need to keep a reference to the task so you can cancel it later
			if (Rh_Finder_Running == null) {
				// task doesn't exist -- make a new task and start it
				Rh_Finder_Running = new Rh_Finder();
				Rh_Finder_Running.execute();
				// this will make doInBackground() run on background thread
			}

			encoderDrive (DRIVE_SPEED, -12, -12, 4.0);
			POSITION++;

			runtime.reset();
			while (runtime.seconds() < 60) {
				BAD_Rh = 1;

				while (BAD_Rh == 1) {
					Rh = Get_Rh_Data();
					if (Rh[0] > 0 && !(Double.isNaN(Rh[0])) && !(Double.isInfinite(Rh[0]))) {
						BAD_Rh = 0;
					}
				}
				telemetry.addData("Position: ",POSITION);
				telemetry.addData("Rh: ",Rh[0]);
				telemetry.addData("Time: ", runtime.seconds());
				telemetry.update();
			}


			// Write a Calibration Lookup Table for either regression analysis or linear interpolation

			Write_PositionRh(POSITION,Rh[0]);


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

			//Wait for a hardware cycle to allow other processes to run
			waitOneFullHardwareCycle();
		}

	}

	public void Make_New_CalDat_file() {
		String filepath = String.valueOf(Environment.getExternalStorageDirectory()) + File.separator + "FIRST" + File.separator;
		File root = new File(filepath);
		File datfile = new File(root, "Rh_Cal_Data.dat");
		Writer writer = null;
		try {
			writer = new FileWriter(datfile, false);
			writer.append("POSITION, Fwd_Rh\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void Write_PositionRh(int POSITION, double Range) {
		String P_and_Rh_Sentence = String.format("%s, %f\n", Integer.toString((int) POSITION), Range);
		String filepath = String.valueOf(Environment.getExternalStorageDirectory()) + File.separator + "FIRST" + File.separator;
		File root = new File(filepath);
		File datfile = new File(root, "Rh_Cal_Data.dat");
		Writer writer = null;
		try {
			writer = new FileWriter(datfile, true);
			writer.append(P_and_Rh_Sentence);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public double[] Get_Rh_Data() {

		String Rh_Data_String = "";
		String filepath = String.valueOf(Environment.getExternalStorageDirectory()) + File.separator + "FIRST" + File.separator;
		String datfile = filepath + "ranging_data.dat";

		try {
			InputStream FID = new FileInputStream(datfile);
			InputStreamReader ISR = new InputStreamReader(FID);
			BufferedReader BR = new BufferedReader(ISR);
			String tline = BR.readLine();
			FID.close();
			//Scanner Rh_scan = new Scanner(tline).useDelimiter("\\D");
			double Rh[] = {-1, -1, -1, -1};

			if (tline != null && !tline.isEmpty()) {
				String[] Rh_str = tline.split(",");
				for (int RhSC = 0; RhSC <= 3; RhSC++) {
					//Rh[RhSC] = Rh_scan.nextDouble();
					Rh[RhSC] = Double.parseDouble(Rh_str[RhSC]);
				}
			}
			//Rh_scan.close();
			return Rh;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			double Rh[] = {-1,-1,-1,-1};
			return Rh;
		} catch (IOException e) {
			e.printStackTrace();
			double Rh[] = {-1,-1,-1,-1};
			return Rh;
		}

	}

	public void encoderDrive (double speed, double leftInches, double rightInches, double timeoutS) {

		int newLeftTarget;
		int newRightTarget;
		double CurrentPosition_left;
		double CurrentPosition_right;


		if (opModeIsActive()) {

			//Sets new target position using current position//
			newLeftTarget  = port_motor.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
			newRightTarget = stbd_motor.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
			port_motor.setTargetPosition(newLeftTarget);
			stbd_motor.setTargetPosition(newRightTarget);

			//Set motors to Encoder drive mode//
			port_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
			stbd_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

			runtime.reset();
			port_motor.setPower(Math.abs(speed));
			stbd_motor.setPower(Math.abs(speed));

			while (opModeIsActive() &&
					(runtime.seconds() < timeoutS) &&
					(port_motor.isBusy() && stbd_motor.isBusy())) {
				telemetry.addData("Path1", "Driving to %7d :%7d",newLeftTarget,newRightTarget);
				telemetry.addData("Path2", "Driving at %7d :%7d",
						port_motor.getCurrentPosition(),
						stbd_motor.getCurrentPosition());
				telemetry.update();
			}

			//Stop all motion and reset motors//
			port_motor.setPower(0);
			stbd_motor.setPower(0);
			port_motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
			stbd_motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
			stbd_xdcr_rx = hardwareMap.opticalDistanceSensor.get("stbd_xdcr_rx");				// build config profile
			aft_xdcr_rx = hardwareMap.opticalDistanceSensor.get("aft_xdcr_rx");				// build config profile
			port_xdcr_rx = hardwareMap.opticalDistanceSensor.get("port_xdcr_rx");				// build config profile

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
			xdcr_tx.setPulseWidthPeriod(20000);

			int i=1;
			int ii=1;
			int max_i = 1000;
			double[] RANGE_rolavg = {-1,-1,-1,-1};

			while (! isCancelled()) {
			//while (opModeIsActive())
                synchronized (Rh) {
					//double VRead = fwd_xdcr_rx.getMaxVoltage();
					if (i < max_i) {
						i++;
					}
					else {
						ii++;
					}
					//double RANGE_new = 1 / Math.sqrt(fwd_xdcr_rx.getLightDetected());
					double[] RANGE_new_sum = {0,0,0,0};

					//double[] new RANGE_new_sum = 0;
					//double RANGE_new_sum = 0;
					for (int count=0; count < 300; count++) {
						// old Cal_Factor 9.951450243819872f
						//  Noticing that at around 4 to 6 ft, it gets a little weird.
						//  Decreasing frequency, and removing cal factor for writing data to cal data file
						// Data appears like it could roughly fit a quadratic function, we'll see.  Though it does still do the same around 5 ft
						RANGE_new_sum[0] = RANGE_new_sum[0] + fwd_xdcr_rx.getLightDetected();
						RANGE_new_sum[1] = RANGE_new_sum[1] + stbd_xdcr_rx.getLightDetected();
						RANGE_new_sum[2] = RANGE_new_sum[2] + aft_xdcr_rx.getLightDetected();
						RANGE_new_sum[3] = RANGE_new_sum[3] + port_xdcr_rx.getLightDetected();
					}
					double[] RANGE_new = {0,0,0,0};
					RANGE_new[0] = RANGE_new_sum[0] / 300;
					RANGE_new[1] = RANGE_new_sum[1] / 300;
					RANGE_new[2] = RANGE_new_sum[2] / 300;
					RANGE_new[3] = RANGE_new_sum[3] / 300;

					for (int count1 = 0; count1 <= 3; count1++) {
						RANGE_new[count1] = (14 * RANGE_new[count1] * RANGE_new[count1]) + (14 * RANGE_new[count1]) - 0.86;
					}

					for (int count1=0; count1 <= 3; count1++) {
						if (RANGE_rolavg[count1] < 0) {
							if (!Double.isNaN(RANGE_new[count1]) && !Double.isInfinite(RANGE_new[count1])) {
								RANGE_rolavg[count1] = RANGE_new[count1];
								Rh = RANGE_rolavg;
							}
						} else {
							if (!Double.isNaN(RANGE_new[count1]) && !Double.isInfinite(RANGE_new[count1])) {
								RANGE_rolavg[count1] = RANGE_rolavg[count1] + (RANGE_new[count1] - RANGE_rolavg[count1]) / i;
								Rh = RANGE_rolavg;
							}
						}
					}

					sentence = String.format("%f, %f, %f, %f\n", RANGE_rolavg[0], RANGE_rolavg[1], RANGE_rolavg[2], RANGE_rolavg[3]);
					try {
						writer = new FileWriter(datfile, false);
						//FileWriter(datfile, boolean append);
						writer = writer.append(sentence);
						writer.flush();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

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
