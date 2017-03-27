package com.edt.velizy.edtvelizy.utils;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.edt.velizy.edtvelizy.services.MyAlarmReceiver;

/**
 * Une classe regroupant des fonctions liées aux AlarmManager
 */
public class AlarmManager {

    /**
     * Créé une alarme toutes les 2h pour executer notre service
     * @param context le contexte
     */
    public static void scheduleAlarm(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), MyAlarmReceiver.class);
        intent.setAction(MyAlarmReceiver.ACTION);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, MyAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        long firstMillis = System.currentTimeMillis();
        android.app.AlarmManager alarm = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(android.app.AlarmManager.RTC_WAKEUP, firstMillis, 60000, pIntent);
    }

    /**
     * Arrête l'alarme
     * @param context le contexte
     */
    public static void cancelAlarm(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), MyAlarmReceiver.class);
        intent.setAction(MyAlarmReceiver.ACTION);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, MyAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        android.app.AlarmManager alarm = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
        pIntent.cancel();
    }

    /**
     * Permet de vérifier si une alarme est active
     * @param context le contexte
     * @return false si l'arme est inactive, true si elle est active
     */
    public static boolean checkAlarm(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), MyAlarmReceiver.class);
        intent.setAction(MyAlarmReceiver.ACTION);
        return (PendingIntent.getBroadcast(context, MyAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_NO_CREATE) != null);
    }
}
