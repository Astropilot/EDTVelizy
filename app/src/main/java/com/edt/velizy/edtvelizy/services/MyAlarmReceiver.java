package com.edt.velizy.edtvelizy.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Classe qui est instanciée et appelée par l'alarme ou d'autres evenements
 */
public class MyAlarmReceiver extends WakefulBroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.edt.velizy.edtvelizy.alarm";

    /**
     * Réécriture de la fonction onReceive pour lancer notre service
     * Cette fonction est appelée automatique donc on ne se préoccupe
     * pas des arguments
     *
     * @param context le ocntexte
     * @param intent l'intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // On créé un nouvel Intent qui pointe sur notre service puis on le démarre
        Intent i = new Intent(context, SuiviService.class);
        i.putExtra("action", intent.getAction());
        context.startService(i);
    }
}