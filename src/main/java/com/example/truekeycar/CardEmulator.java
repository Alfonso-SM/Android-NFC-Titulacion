package com.example.truekeycar;

import android.content.Context;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import java.util.HashMap;

public class CardEmulator extends HostApduService {


    String STATUS_SUCCESS = "9898";
    String STATUS_FAILED = "4040";
    String CLA_NOT_SUPPORTED = "6E00";
    String INS_NOT_SUPPORTED = "6D00";
    String AID = "A000000";
    String SELECT_INS = "A4";
    String DEFAULT_CLA = "00";
    int MIN_APDU_LENGTH = 12;

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        SessionManager sessionManager =  new SessionManager(this,SessionManager.SESSION_USERSESSION);
        HashMap<String,String> last = sessionManager.getUsersDetailFromSession();
        String LastCar =  last.get(SessionManager.KEY_LASTCARCODE);

        if (commandApdu == null) {
            return Utils.Companion.hexStringToByteArray(STATUS_FAILED);
        }

        if (LastCar != null) {
            AID = AID + LastCar;
        }

        String hexCommandApdu = Utils.Companion.toHex(commandApdu);
        if (hexCommandApdu.length() < MIN_APDU_LENGTH) {
            return Utils.Companion.hexStringToByteArray(STATUS_FAILED);
        }

        if (!hexCommandApdu.substring(0, 2).equals(DEFAULT_CLA)) {
            return Utils.Companion.hexStringToByteArray(CLA_NOT_SUPPORTED);
        }

        if (!hexCommandApdu.substring(2, 4).equals(SELECT_INS)) {
            return Utils.Companion.hexStringToByteArray(INS_NOT_SUPPORTED);
        }

        if (hexCommandApdu.substring(10, 24).equals(AID))  {
            Toast.makeText(this, "Abierto o cerrado", Toast.LENGTH_LONG).show();
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.EFFECT_HEAVY_CLICK));
            return Utils.Companion.hexStringToByteArray(STATUS_SUCCESS);
        } else {
            Toast.makeText(this, "Telefono no Autorizado", Toast.LENGTH_LONG).show();
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createOneShot(800,VibrationEffect.EFFECT_DOUBLE_CLICK));
            return Utils.Companion.hexStringToByteArray(STATUS_FAILED);
        }

    }

    @Override
    public void onDeactivated(int i) {

    }
}
