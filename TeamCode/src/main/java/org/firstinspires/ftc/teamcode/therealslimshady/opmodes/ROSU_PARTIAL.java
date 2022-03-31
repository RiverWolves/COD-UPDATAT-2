package org.firstinspires.ftc.teamcode.therealslimshady.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.therealslimshady.SConnection;
import org.firstinspires.ftc.teamcode.therealslimshady.SHardware;
import org.firstinspires.ftc.teamcode.therealslimshady.SMiscariRoti;
import org.firstinspires.ftc.teamcode.therealslimshady.SVuforia;
import org.firstinspires.ftc.teamcode.therealslimshady.autonomie.SCPartial;

@Autonomous(name = "ROSU PARTIAL")
public class ROSU_PARTIAL extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        //OPTIONAL Initializam inainte serverul pentru conexiunea externa cu laptopul
        //daca aplicatia se inchide in timpul testului neasteptat, stergeti linia SConnection.init();

//        SConnection.init(); //STERGE LINIA ASTA DACA APLICATIA SE INCHIDE RANDOM

        //In primul rand trebuie sa initializam parte de hardware
        //deci chemam fucntia init din clasa SHardware si pasam argumentul OpMode
        //care este "this", adica clasa asta, intrucat extinde OpMode!!
        SHardware.init(this);

        SCPartial.init(this, 1);
        //Initializam si vuforia pentru ca suntem la autonomie si ne ajuta
        SVuforia.init(this);
        telemetry.addData("POTI SA DAI START","DA");
        telemetry.update();
        //asteptam pana se apasa butonul de start!
        waitForStart();

        //In loc de functia loop vom avea un while care se opreste cand apasam pe butonul de stop
        while(!isStopRequested()){
            SVuforia.loop(this);
//            telemetry.addData("dist",SHardware.distanta.getDistance(DistanceUnit.MM));

            SCPartial.loop(this);
//            SCreier.mergi(7650);
            SMiscariRoti.loop(this);


            telemetry.update();
        }


        SCPartial.stop();
        SVuforia.stop();
        SConnection.stop();
        SHardware.initializat = false;
    }
}
