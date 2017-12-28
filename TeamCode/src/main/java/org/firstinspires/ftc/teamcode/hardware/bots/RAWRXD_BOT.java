package org.firstinspires.ftc.teamcode.hardware.bots;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.teamcode.hardware.sensors.IMU;

/**
 * Created by Alex on 11/3/2017.
 */

public class RAWRXD_BOT {
    private HardwareMap hardwareMap;


    public IMU imu = null;

    //
    //DRIVE RELATED VARS
    //

    //Left Motor
    private String Drive_Left_Name = "dl1";
    public DcMotor Drive_Left_Motor = null;
    private DcMotorSimple.Direction Drive_Left_Direction = DcMotorSimple.Direction.FORWARD;

    private String Drive_Left_Name2 = "dl2";
    public DcMotor Drive_Left_Motor2 = null;

    //Right Motor
    private String Drive_Right_Name = "dr1";
    public DcMotor Drive_Right_Motor = null;
    private DcMotorSimple.Direction Drive_Right_Direction = DcMotorSimple.Direction.REVERSE;

    private String Drive_Right_Name2 = "dr2";
    public DcMotor Drive_Right_Motor2 = null;


    //Global DriveManual Settings
    private DcMotor.ZeroPowerBehavior DriveZeroPower = DcMotor.ZeroPowerBehavior.BRAKE;


    private String Phone_Name = "phone";
    public Servo Phone_Servo = null;
    private double PHONE_POS_INSIDE = 0;
    private double PHONE_POS_FRONT = 0.1;
    private double PHONE_POST_PICTO = 0.25;
    private double PHONE_POST_OUTSIDE = 1;

    private String Lift1_Name = "lift1";
    public DcMotor Lift1_Motor = null;
    public DcMotor.Direction Lift1_Direction = DcMotorSimple.Direction.FORWARD;

    private String Lift2_Name = "lift2";
    public DcMotor Lift2_Motor = null;
    public DcMotor.Direction Lift2_Direction = DcMotorSimple.Direction.REVERSE;

    private String Grab_Left_Name = "lg";
    public Servo Grab_Left_Servo = null;
    public final double GRAB_LEFT_CLOSED = 1;
    public final double GRAB_LEFT_MID = 0.85;
    public final double GRAB_LEFT_OPEN   = 0.7;

    private String Grab2_Left_Name = "lg2";
    public Servo Grab2_Left_Servo = null;
    public final double GRAB2_LEFT_CLOSED = 0;
    public final double GRAB2_LEFT_MID = 0.15;
    public final double GRAB2_LEFT_OPEN   = 0.3;

    private String Grab_Right_Name = "rg";
    public Servo Grab_Right_Servo = null;
    public final double GRAB_RIGHT_CLOSED = 1;
    public final double GRAB_RIGHT_MID = 0.85;
    public final double GRAB_RIGHT_OPEN   = 0.7;

    private String Grab2_Right_Name = "rg2";
    public Servo Grab2_Right_Servo = null;
    public final double GRAB2_RIGHT_CLOSED = 0;
    public final double GRAB2_RIGHT_MID   = 0.15;
    public final double GRAB2_RIGHT_OPEN   = 0.3;


    static final double     HEADING_THRESHOLD       = 0.3 ;      // As tight as we can make it with an integer gyro
    static final double     P_TURN_COEFF            = 0.06 ;     // Larger is more responsive, but also less stable
    static final double     P_DRIVE_COEFF           = 0.04;     // Larger is more responsive, but also less stable

    private LinearOpMode opMode;

    private ElapsedTime elapsedTime;

    public RAWRXD_BOT(HardwareMap _hwd, LinearOpMode parent){
        hardwareMap = _hwd;
        opMode = parent;

    }

    public void Init(){

        imu = new IMU();

        imu.InitIMU(hardwareMap,"imu");


        Drive_Left_Motor  = hardwareMap.dcMotor.get(Drive_Left_Name);
        Drive_Right_Motor = hardwareMap.dcMotor.get(Drive_Right_Name);

        Drive_Left_Motor2  = hardwareMap.dcMotor.get(Drive_Left_Name2);
        Drive_Right_Motor2 = hardwareMap.dcMotor.get(Drive_Right_Name2);

        Drive_Left_Motor.setDirection(Drive_Left_Direction);
        Drive_Right_Motor.setDirection(Drive_Right_Direction);

        Drive_Left_Motor2.setDirection(Drive_Left_Direction);
        Drive_Right_Motor2.setDirection(Drive_Right_Direction);

        Drive_Left_Motor.setZeroPowerBehavior(DriveZeroPower);
        Drive_Right_Motor.setZeroPowerBehavior(DriveZeroPower);

        Drive_Left_Motor2.setZeroPowerBehavior(DriveZeroPower);
        Drive_Right_Motor2.setZeroPowerBehavior(DriveZeroPower);

        SetAllMotorsToMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        Phone_Servo = hardwareMap.servo.get(Phone_Name);

        Lift1_Motor = hardwareMap.dcMotor.get(Lift1_Name);
        Lift2_Motor = hardwareMap.dcMotor.get(Lift2_Name);
        Lift1_Motor.setDirection(Lift1_Direction);
        Lift2_Motor.setDirection(Lift2_Direction);

        Lift1_Motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Lift2_Motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        Grab_Left_Servo = hardwareMap.servo.get(Grab_Left_Name);
        Grab2_Left_Servo = hardwareMap.servo.get(Grab2_Left_Name);
        Grab_Right_Servo = hardwareMap.servo.get(Grab_Right_Name);
        Grab2_Right_Servo = hardwareMap.servo.get(Grab2_Right_Name);

        SetAllMotorsToMode(DcMotor.RunMode.RUN_USING_ENCODER);

        while(!imu.imu.isGyroCalibrated()){
            opMode.telemetry.addData("IMU", imu.imu.getSystemStatus());
        }

        elapsedTime = new ElapsedTime();
    }

    public void SetAllMotorsToMode(DcMotor.RunMode mode){
        Drive_Left_Motor.setMode(mode);
        Drive_Left_Motor2.setMode(mode);
        Drive_Right_Motor.setMode(mode);
        Drive_Right_Motor2.setMode(mode);
    }

    public boolean AreMotorsBusy(){
        return (Drive_Left_Motor.isBusy() && Drive_Left_Motor2.isBusy() && Drive_Right_Motor.isBusy() && Drive_Right_Motor2.isBusy());
    }

    public void DriveManual(double LeftVal, double RightVal, double Sensitivity) {
        Drive_Left_Motor.setPower(LeftVal * Sensitivity);
        Drive_Right_Motor.setPower(RightVal * Sensitivity);

        Drive_Left_Motor2.setPower(LeftVal * Sensitivity);
        Drive_Right_Motor2.setPower(RightVal * Sensitivity);
    }

    public void EncoderDrive(int LeftVal, int RightVal, double speed){
        SetAllMotorsToMode(DcMotor.RunMode.RUN_TO_POSITION);

        Drive_Left_Motor.setTargetPosition(-LeftVal);
        Drive_Left_Motor2.setTargetPosition(-LeftVal);
        Drive_Right_Motor.setTargetPosition(-RightVal);
        Drive_Right_Motor2.setTargetPosition(-RightVal);

        Drive_Left_Motor.setPower(speed);
        Drive_Left_Motor2.setPower(speed);
        Drive_Right_Motor.setPower(speed);
        Drive_Right_Motor2.setPower(speed);

        while(AreMotorsBusy()){}

        SetAllMotorsToMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void gyroDrive ( double speed,
                            double distance,
                            double angle) {

        int     newLeftTarget;
        int     newRightTarget;
        int     newLeftTarget2;
        int     newRightTarget2;
        int     moveCounts;
        double  max;
        double  error;
        double  steer;
        double  leftSpeed;
        double  rightSpeed;

        // Ensure that the opmode is still active
        if (opMode.opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            moveCounts = -(int)distance;
            newLeftTarget = Drive_Left_Motor.getCurrentPosition() + moveCounts;
            newRightTarget = Drive_Right_Motor.getCurrentPosition() + moveCounts;

            newLeftTarget2 = Drive_Left_Motor2.getCurrentPosition() + moveCounts;
            newRightTarget2 = Drive_Right_Motor2.getCurrentPosition() + moveCounts;

            // Set Target and Turn On RUN_TO_POSITION
            Drive_Left_Motor.setTargetPosition(newLeftTarget);
            Drive_Right_Motor.setTargetPosition(newRightTarget);

            Drive_Left_Motor2.setTargetPosition(newLeftTarget2);
            Drive_Right_Motor2.setTargetPosition(newRightTarget2);

            SetAllMotorsToMode(DcMotor.RunMode.RUN_TO_POSITION);

            // start motion.
            speed = Range.clip(Math.abs(speed), 0.0, 1.0);
            Drive_Left_Motor.setPower(speed);
            Drive_Right_Motor.setPower(speed);
            Drive_Left_Motor2.setPower(speed);
            Drive_Right_Motor2.setPower(speed);

            // keep looping while we are still active, and BOTH motors are running.
            while (opMode.opModeIsActive() && AreMotorsBusy()){

                // adjust relative speed based on heading error.
                error = imu.getError(angle);
                steer = getSteer(error, P_DRIVE_COEFF);
                opMode.telemetry.addData("ERROR", error);
                opMode.telemetry.addData("Steer", steer);
                opMode.telemetry.update();
                // if driving in reverse, the motor correction also needs to be reversed
                if (distance < 0)
                    steer *= -1.0;

                leftSpeed = speed - steer;
                rightSpeed = speed + steer;

                // Normalize speeds if either one exceeds +/- 1.0;
                max = Math.max(Math.abs(leftSpeed), Math.abs(rightSpeed));
                if (max > 1.0)
                {
                    leftSpeed /= max;
                    rightSpeed /= max;
                }

                Drive_Left_Motor.setPower(leftSpeed);
                Drive_Left_Motor2.setPower(leftSpeed);

                Drive_Right_Motor.setPower(rightSpeed);
                Drive_Right_Motor2.setPower(rightSpeed);

            }

            // Stop all motion;
            Drive_Left_Motor.setPower(0);
            Drive_Left_Motor2.setPower(0);

            Drive_Right_Motor.setPower(0);
            Drive_Right_Motor2.setPower(0);

            // Turn off RUN_TO_POSITION
            SetAllMotorsToMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void WaitForTime(double time){
        elapsedTime.reset();
        elapsedTime.startTime();

        while(elapsedTime.seconds() < time){

        }


    }

    public void gyroHold( double speed, double angle, double holdTime) {



        // keep looping while we have time remaining.
        elapsedTime.reset();
        while (opMode.opModeIsActive() && (elapsedTime.time() < holdTime)) {
            // Update telemetry & Allow time for other processes to run.
            onHeading(speed, angle, P_TURN_COEFF);
            opMode.telemetry.update();
        }

        // Stop all motion;
        Drive_Left_Motor.setPower(0);
        Drive_Left_Motor2.setPower(0);

        Drive_Right_Motor.setPower(0);
        Drive_Right_Motor2.setPower(0);
    }

    public void gyroTurn (  double speed, double angle) {

        // keep looping while we are still active, and not on heading.
        while (opMode.opModeIsActive() && !onHeading(speed, angle, P_TURN_COEFF)) {
            // Update telemetry & Allow time for other processes to run.

        }
    }

    boolean onHeading(double speed, double angle, double PCoeff) {
        double   error ;
        double   steer ;
        boolean  onTarget = false ;
        double leftSpeed;
        double rightSpeed;

        // determine turn power based on +/- error
        error = imu.getError(angle);

        if (Math.abs(error) <= HEADING_THRESHOLD) {
            steer = 0.0;
            leftSpeed  = 0.0;
            rightSpeed = 0.0;
            onTarget = true;
        }
        else {
            steer = getSteer(error, PCoeff);
            leftSpeed  = speed * steer;
            rightSpeed   = -leftSpeed;
        }

        // Send desired speeds to motors.
        Drive_Left_Motor.setPower(leftSpeed);
        Drive_Left_Motor2.setPower(leftSpeed);

        Drive_Right_Motor.setPower(rightSpeed);
        Drive_Right_Motor2.setPower(rightSpeed);

        // Display it for the driver.
        opMode.telemetry.addData("Target", "%5.2f", angle);
        opMode.telemetry.addData("Err/St", "%5.2f/%5.2f", error, steer);
        opMode.telemetry.addData("Speed.", "%5.2f:%5.2f", leftSpeed, rightSpeed);
        opMode.telemetry.update();

        return onTarget;
    }
    private double getSteer(double error, double PCoeff) {
        return Range.clip(error * PCoeff, -1, 1);
    }

    // Phone Settings

    public void SetPhoneInside(){
        Phone_Servo.setPosition(PHONE_POS_INSIDE);
    }

    public void SetPhoneFront(){
        Phone_Servo.setPosition(PHONE_POS_FRONT);
    }

    public void SetPhonePicto(){
        Phone_Servo.setPosition(PHONE_POST_PICTO);
    }
    public void SetPhoneOutside(){
        Phone_Servo.setPosition(PHONE_POST_OUTSIDE);
    }

    // Lift Settings

    public void LiftPower(double power){
        Lift1_Motor.setPower(power);
        Lift2_Motor.setPower(power);
    }

    // Grabbers

    public void CloseGrab(){
        Grab_Right_Servo.setPosition(GRAB_RIGHT_CLOSED);
        Grab_Left_Servo.setPosition(GRAB_LEFT_CLOSED);

        Grab2_Right_Servo.setPosition(GRAB2_RIGHT_CLOSED);
        Grab2_Left_Servo.setPosition(GRAB2_LEFT_CLOSED);
    }

    public void MidGrab(){
        Grab_Right_Servo.setPosition(GRAB_RIGHT_MID);
        Grab_Left_Servo.setPosition(GRAB_LEFT_MID);

        Grab2_Right_Servo.setPosition(GRAB2_RIGHT_MID);
        Grab2_Left_Servo.setPosition(GRAB2_LEFT_MID);
    }


    public void OpenGrab(){
        Grab_Right_Servo.setPosition(GRAB_RIGHT_OPEN);
        Grab_Left_Servo.setPosition(GRAB_LEFT_OPEN);
        Grab2_Right_Servo.setPosition(GRAB2_RIGHT_OPEN);
        Grab2_Left_Servo.setPosition(GRAB2_LEFT_OPEN);
    }

}
