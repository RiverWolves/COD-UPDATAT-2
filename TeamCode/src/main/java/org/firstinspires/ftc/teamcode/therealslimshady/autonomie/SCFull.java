package org.firstinspires.ftc.teamcode.therealslimshady.autonomie;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.therealslimshady.SHardware;
import org.firstinspires.ftc.teamcode.therealslimshady.SMiscariRoti;
import org.firstinspires.ftc.teamcode.therealslimshady.SVuforia;
import org.firstinspires.ftc.teamcode.therealslimshady.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.therealslimshady.roadrunner.trajectorysequence.TrajectorySequence;

import java.util.List;

public class SCFull {

    private static int FAZA = 0;
    private static ElapsedTime et, ett;
    private static int poz = 0;
    private static SampleMecanumDrive drive;
    private static double unghi = 0;

    private static double MERGI, MERGI_INAINTE;

    private static Telemetry telemetry;

    public static void init(OpMode opMode, int INVERS){
        MERGI_INAINTE = Configuratie.MERGI_INAINTE;
        MERGI = Configuratie.MERGI;
        unghi=0;
        FAZA=0;
        poz = 0;
        et = null;
        ett = null;
        SMiscariRoti.setVelXYR(0,0,0);
        drive = null;
        SHardware.cutie.setPosition(0);

        telemetry = new MultipleTelemetry(opMode.telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    public static void loop(OpMode opMode){
        telemetry.addData("POZITIE",poz);
        telemetry.addData("FAZA",FAZA);
        telemetry.addData("lift",SHardware.lift.getCurrentPosition());

        if(drive != null){
            drive.update();
            telemetry.addData("XU",drive.getPoseEstimate().getX());
            telemetry.addData("YU",drive.getPoseEstimate().getY());
//            ((DcMotorEx)SHardware.matura_exterior).getVelocity()

        }else{
            drive = new SampleMecanumDrive(opMode.hardwareMap);
        }
        if(et == null) {
            et = new ElapsedTime();
            ett = new ElapsedTime();
            et.reset();
        }
        SMiscariRoti.indreaptaSpre(Configuratie.PUTERE_REDRESARE,unghi,AngleUnit.DEGREES);

        if(poz != 0){
            int target = Configuratie.LIFT_1;
            if(poz == 2)
                target = Configuratie.LIFT_2;
            if(poz == 3)
                target = Configuratie.LIFT_3;

            mergi(target);
        }else{
            mergi(poz);
        }

        if(FAZA == 0){
            if(et.seconds() < Configuratie.ASTEAPTA_RECUNOASTERE) {
                List<Recognition> recognitions = SVuforia.tfod.getRecognitions();
                for (Recognition recognition : recognitions) {
                    if (recognition.getLabel().equals(SVuforia.LABELS[0])) {
                        if(getMijloc(recognition)<(recognition.getImageWidth()/2f+50)){
                            poz = Configuratie.POZ_STANGA;
                        }else{
                            poz = Configuratie.POZ_DREAPTA;
                        }
                        et.reset();
                        FAZA = 1;
                    }
                }
            }else{
                poz = Configuratie.POZ_NEVAZUTA;
                et.reset();
                FAZA = 1;
            }
        }
        if(FAZA==1){
            SHardware.carusel.setPower(Configuratie.PUTERE_CARUSEL);
            unghi = Configuratie.UNGHI_CARUSEL;
            if (SMiscariRoti.eSpre(unghi) || et.seconds() > Configuratie.TIMP_ROTIRE_CARUSEL) {
                if(et.seconds() > Configuratie.TIMP_ROTIRE_CARUSEL+0.5) {
                    SMiscariRoti.setVelXY(0,0);
                    et.reset();
                    FAZA = 2;
                }else{
                    SMiscariRoti.setVelXY(0,Configuratie.PUTERE_SPRE_CARUSEL);
                }

            }
        }

        if(FAZA==2){
            SMiscariRoti.setVelXY(0,0);
            if(et.seconds() > 1){
                unghi = 0;
                if(SMiscariRoti.eSpre(unghi) || et.seconds() > 3.5) {
                    FAZA = 3;
                    et.reset();
                    SHardware.carusel.setPower(0);
                }
            }
        }

        if(FAZA==3){
            if(et.seconds() > 0.5) {
                SMiscariRoti.setVelXY(0, 0);
                et.reset();
                FAZA = 4;
            }
        }
        if(FAZA==4) {
            double x = Configuratie.DIRECTIE-1*drive.getPoseEstimate().getX();

            if (x < MERGI_INAINTE-19) {
                SMiscariRoti.setVelY(Configuratie.DIRECTIE*-0.4f);
            } else if(x<MERGI_INAINTE-9){
                SMiscariRoti.setVelY(Configuratie.DIRECTIE*-0.3f);
            }else{
                SMiscariRoti.setVelY(Configuratie.DIRECTIE*-0.15f);
            }
            if (x >= MERGI_INAINTE) {
                SMiscariRoti.setVelXY(0, 0);
                FAZA = 5;
            }
        }
//        if(FAZA==200) {
//            double x = -drive.getPoseEstimate().getX();
//            double y = -drive.getPoseEstimate().getY();
////            SMiscariRoti.mergiLa(52,30,x,y,0,-24);
//            SMiscariRoti.mergiLa(0,30,x,y,0,0);
//            if(et.seconds()>5){
//                et.reset();
//                FAZA=4;
//            }
            //TODO AICI M-AM OPRIT
//            if (x < 25) {
//                SMiscariRoti.setVelY(-0.6f);
//            } else {
//                SMiscariRoti.setVelY(-0.2f);
//            }
//            if (x > 52) {
//                SMiscariRoti.setVelXYR(0, 0, 0);
//                FAZA = 5;
//            }
//        }

        if(FAZA==5) {
            double x = Configuratie.DIRECTIE-1*drive.getPoseEstimate().getX();
            if(x>MERGI_INAINTE+15){
                SMiscariRoti.setVelY(Configuratie.DIRECTIE*0.15f);
            }else {
                double y = -1*drive.getPoseEstimate().getY();
                double target = MERGI;
                if (poz == 2) {
                    target = MERGI+Configuratie.MERGI_ADD_2;
                }
                if (poz == 3) {
                    target = MERGI+Configuratie.MERGI_ADD_3;
                }
                if (y < target-20) {
                    SMiscariRoti.setVelXY(0.2f, 0);
                } else if(y < target) {
                    SMiscariRoti.setVelXY(0.15f,0);
                }else {
//                unghi=10;
                    if (SMiscariRoti.eSpre(unghi) || et.seconds() > 2) {
                        SMiscariRoti.setVelXY(0, 0);
                        SHardware.cutie.setPosition(0.9);
                        et.reset();
                        FAZA = 6;
                    }
                }
            }


//            double x = -drive.getPoseEstimate().getX();
//            double y = -drive.getPoseEstimate().getY();
//            SMiscariRoti.mergiLa(30,27,x,y,90,0);
//            if((Math.abs(SMiscariRoti.getPuteri()[0]) < 0.1 && Math.abs(SMiscariRoti.getPuteri()[1]) < 0.1) || et.seconds() > 6){
//                et.reset();
//
//            }

        }

        if(FAZA==6) {
            if(et.seconds() > 2) {

                SHardware.cutie.setPosition(0);
                if(et.seconds() > 3) {
                    et.reset();
                    FAZA = 7;
                }
            }
        }
        if(FAZA==7) {
            poz = 0;
            double y = -1*drive.getPoseEstimate().getY();
            unghi = Configuratie.DIRECTIE*5;
            if(y > Configuratie.DISTANTA_PERETE_FULL){
                if (SMiscariRoti.eSpre(unghi) || et.seconds() > 2) {
                    if(y > 15) {
                        SMiscariRoti.setVelXY(-0.4f, 0);
                    }else{
                        SMiscariRoti.setVelXY(-0.2f, 0);
                    }
                }
            }else {
                SMiscariRoti.setVelXY(0, 0);
                et.reset();
                FAZA = 8;
            }
        }
        if(FAZA==8) {
            poz = 0;
            unghi = Configuratie.DIRECTIE*-6;

            double x = Configuratie.DIRECTIE*-1*drive.getPoseEstimate().getX();
            if(x > Configuratie.D1_FULL){
                FAZA = 9;
                et.reset();
                SMiscariRoti.setVelXY(0, 0);
                if(Configuratie.DIRECTIE < 1) {
                    SHardware.matura_exterior.setPower(-0.6);
                    SHardware.matura_interior.setPower(-0.9);
                }
            }else{
                SMiscariRoti.setVelXY(0, Configuratie.DIRECTIE*-0.4f);
            }
        }

        if(FAZA==9){
            double x = Configuratie.DIRECTIE*-1*drive.getPoseEstimate().getX();
            unghi = Configuratie.DIRECTIE*-3;
            poz = 0;
            if(x < Configuratie.D2_FULL){
                SMiscariRoti.setVelXY(0, Configuratie.DIRECTIE*-0.4f);
            }else{
                SMiscariRoti.setVelXY(0, 0);
                FAZA=10;
            }
        }

        if(FAZA==10){
            SHardware.cutie.setPosition(0.252);
        }
        if(ett.seconds() > 29){
            SHardware.matura_exterior.setPower(0);
            SHardware.matura_interior.setPower(0);
        }
//        if(FAZA == 14){
//            if(et.seconds() < 2)
//                SMiscariRoti.setVelXYR(0.2f, 0, 0);
//            else
//                SMiscariRoti.setVelXY(0,0);
//        }
    }

    private static float getMijloc(Recognition recognition){
        return recognition.getLeft()+(recognition.getWidth()/2);
    }

    public static int mergi(int pozitie){
        DcMotor motor = SHardware.lift;
        int pozitie_motor = -motor.getCurrentPosition();

        int eroare = pozitie - pozitie_motor;
        double putere = 0.5;
        if(Math.abs(eroare) < 500)
            putere = 0.2;
        if(pozitie_motor < pozitie-40)
            motor.setPower(putere);
        else if(pozitie_motor > pozitie+40)
            motor.setPower(-putere);
        else
            motor.setPower(0);

        return 0;
    }

    public static void stop(){
        et = null;
        FAZA = 0;
        poz = 0;
        SMiscariRoti.setVelXYR(0,0,0);
    }

}
