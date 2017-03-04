package org.firstinspires.ftc.team11587;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
/**
 * Class to calculate encoderDrive output based on start/finish coordinate input
 */

public class xyGeoCalc extends LinearOpMode {
    static final int     START_X     =   0;
    static final int     START_Y     =   0;
    static final int     END_X       =   0;
    static final int     END_Y       =   0;

    int drive_x;
    int drive_y;
    double drive;

    public void calcRoute(){
        drive_x = (int) Math.pow(END_X - START_X, 2);
        drive_y = (int) Math.pow((END_Y-START_Y),2);
        drive = Math.sqrt(drive_x + drive_y);
    }

    @Override
    public void runOpMode() throws InterruptedException {

    }
}
