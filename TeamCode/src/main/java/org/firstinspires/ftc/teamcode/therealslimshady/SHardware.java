package org.firstinspires.ftc.teamcode.therealslimshady;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class SHardware {

    public static boolean initializat = false;

    public static DcMotor ss, sf, ds, df;
    public static DcMotor lift;
    public static DcMotor matura_exterior, matura_interior;

    public static Servo cutie;
    public static DcMotor carusel;

    public static BNO055IMU imu;

    public static Servo par,perp;

    public static Telemetry telemetry;

    public static void init(OpMode opMode){
        //GIROSCOP
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = opMode.hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);




        //MOTOARE
        ss = (DcMotor) opMode.hardwareMap.get("ss");
        sf = (DcMotor) opMode.hardwareMap.get("sf");
        ds = (DcMotor) opMode.hardwareMap.get("ds");
        df = (DcMotor) opMode.hardwareMap.get("df");

        ds.setDirection(DcMotorSimple.Direction.REVERSE);
        df.setDirection(DcMotorSimple.Direction.REVERSE);

        lift = (DcMotor) opMode.hardwareMap.get("lift");

        matura_exterior = (DcMotor) opMode.hardwareMap.get("matura_exterior");
        matura_interior = (DcMotor) opMode.hardwareMap.get("matura_interior");

        cutie = (Servo) opMode.hardwareMap.get("cutie");

        carusel = (DcMotor) opMode.hardwareMap.get("carusel");

        par = (Servo) opMode.hardwareMap.get("pars");
        perp = (Servo) opMode.hardwareMap.get("perps");


        telemetry = opMode.telemetry;

        //LA SFARSITUL INITIALIZARII SETAM VARIABILA LA TRUE
        initializat = true;
    }

}
