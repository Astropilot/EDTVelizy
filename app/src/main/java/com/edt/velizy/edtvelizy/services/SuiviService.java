package com.edt.velizy.edtvelizy.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.edt.velizy.edtvelizy.R;
import com.edt.velizy.edtvelizy.activities.LoginActivity;
import com.edt.velizy.edtvelizy.timetable.EDTJours;
import com.edt.velizy.edtvelizy.timetable.EDTSemaines;
import com.edt.velizy.edtvelizy.timetable.timetable;
import com.edt.velizy.edtvelizy.utils.AlarmManager;
import com.edt.velizy.edtvelizy.utils.Internet;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Classe permettant de gérer notre service
 */
public class SuiviService extends IntentService {

    /**
     * Le constructeur qui appelle le super constructeur
     */
    public SuiviService() {
       super("CheckerEDT");
    }

    /**
     * Fonction qui est appelée à chaque appel de notre service
     *
     * @param intent l'intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        WakefulBroadcastReceiver.completeWakefulIntent(intent);

        //if(intent.getAction().contains("BOOT_COMPLETED")) {
         //   scheduleAlarm();
           // return;
        //}

        if(!AlarmManager.checkAlarm(this))
            return;


        String EdtID;
        String cID;
        String cPass;

        // On récupère le l'identifiant de l'emploi du temps puis
        // on télécharge l'emploi du temps actuel

        SharedPreferences pref = getSharedPreferences("Prefs",
                Context.MODE_PRIVATE);
        EdtID = pref.getString("EDT_ID", "No");
        cID = pref.getString("USERNAME_LOGIN", "");
        cPass = pref.getString("PASSWORD_LOGIN", "");

        String current_EDT = Internet.retrieve(getString(R.string.edt_url) + EdtID + ".xml", cID, cPass);

        // On vérifie que la connexion internet est présent :)

        if(!current_EDT.contains("<timetable>"))
            return;

        // On récupère l'ancien EDT (oldedt.edt)

        String old_EDT = "";//readFromFile("oldedt.edt");

        // On prépare l'EDT courant

        current_EDT = current_EDT.substring(current_EDT.indexOf("<timetable>"), current_EDT.length());

        // On applique l'algorithme de comparaison

        String notif = "";//CompareEDT(old_EDT, current_EDT);

        // On fait de l'EDT courant l'ancien EDT

        //try {
        //    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("oldedt.edt", Context.MODE_PRIVATE));
        //    outputStreamWriter.write(current_EDT); // TODO: Remplacer par le EDT courant
        //    outputStreamWriter.close();
        //} catch (IOException e) {
        //    Log.e("Exception", e.toString());
        //}

        // Si aucun changement détecté on quitte le service
        if(notif.equals(""))
            notif = "Rien !";
            //return;

        // Sinon on affiche une notification avec en texte les différences trouvées

        NotificationManager mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

         Intent launchNotifiactionIntent = new Intent(this, LoginActivity.class);
         PendingIntent pendingIntent = PendingIntent.getActivity(this,
                1234, launchNotifiactionIntent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder builder = new Notification.Builder(this)
                .setWhen(System.currentTimeMillis())
                .setTicker("Modification de l'EDT !")
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle("L'emploi du temps à été modifé !")
                .setContentText(notif)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent);

        mNotification.notify(101, builder.build());
    }

    /**
     * Permet de comparer deux emploi du temps et rendre les différences trouvées
     *
     * @param edt1 le premier emploi du temps
     * @param edt2 le deuxieme emploi du temps
     * @return toutes les différences
     */
    private String CompareEDT(String edt1, String edt2) {
        String differences = "";
        timetable old_EDT;
        timetable current_EDT;

        //On sérialise nos deux emploi du temps pour les avoir dans des objets

        Serializer serializer = new Persister();
        try {
            old_EDT = serializer.read(timetable.class, edt1, false);
            serializer = new Persister();
            current_EDT = serializer.read(timetable.class, edt2, false);
        } catch (Exception e) {
            return "";
        }

        // On modifie les getRawweeks des cours par la date de la semaine pour les deux emplois du temps

        for(EDTJours cours : old_EDT.getEvent())
        {
            for(EDTSemaines semaine : old_EDT.getSpan())
            {
                if(cours.getRawweeks().equals(semaine.getAlleventweeks()))
                    cours.setRawweeks(semaine.getDate());
            }
        }

        for(EDTJours cours : current_EDT.getEvent())
        {
            for(EDTSemaines semaine : current_EDT.getSpan())
            {
                if(cours.getRawweeks().equals(semaine.getAlleventweeks()))
                    cours.setRawweeks(semaine.getDate());
            }
        }

        // On vérifie si nouvelle semaine disponible

        // On récupère la date de la dernière semaine de l'ancien EDT
        String derniereDate_OldDate = old_EDT.getSpan().get(old_EDT.getSpan().size() - 1).getDate();
        // On regarde si il existe une semaine supérieur dans le nouveau emploi du temps
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        Date OldDate = new Date();
        try {
            OldDate = format.parse(derniereDate_OldDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for(EDTSemaines semaine : current_EDT.getSpan())
        {
            Date date = new Date();
            try {
                date = format.parse(semaine.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(OldDate.compareTo(date) == -1) {
                return "Nouvelle semaine : " + semaine.getDate();
            }
        }

        // Si pas de nouvelle semaine on commence nos différentes vérifications

        // Liste des cours de l'ancien edt non présent dans le nouveau
        List<EDTJours> liste_cours_non_edt_courant = new ArrayList<>();
        // Liste des cours du nouveau edt non présent dans l'ancien
        List<EDTJours> liste_cours_non_edt_ancien = new ArrayList<>();

        // On ajoute les anciens cours dans la première liste si ils ne sont pas dans le nouveau EDT
        for(EDTJours cours : old_EDT.getEvent()) {
            boolean found = false;
            for(EDTJours cours2 : current_EDT.getEvent()) {
                if( cours.getRawweeks().equals(cours2.getRawweeks()) && cours.getStarttime().equals(cours2.getStarttime()) && cours.getEndtime().equals(cours2.getEndtime()) && cours.geteID().equals(cours2.geteID()) && cours.getDay().equals(cours2.getDay())) {
                    found = true;
                }
            }
            if(!found) liste_cours_non_edt_courant.add(cours);
        }

        // On ajoute les nouveaux cours dans la deuxième liste si ils ne sont pas dans l'ancien EDT
        for(EDTJours cours : current_EDT.getEvent()) {
            boolean found = false;
            for(EDTJours cours2 : old_EDT.getEvent()) {
                if( cours.getRawweeks().equals(cours2.getRawweeks()) && cours.getStarttime().equals(cours2.getStarttime()) && cours.getEndtime().equals(cours2.getEndtime()) && cours.geteID().equals(cours2.geteID()) && cours.getDay().equals(cours2.getDay())) {
                    found = true;
                }
            }
            if(!found) liste_cours_non_edt_ancien.add(cours);
        }

        // On vérifie si Suppressions

        // C'est le reste des éléments du liste_cours_non_edt_courant
        for(EDTJours cours : liste_cours_non_edt_courant) {
            if(cours.getResources().getModule() != null)
                differences += "- " + cours.getResources().getModule().toString() + " le " + createFullDate(cours) + "\n";
            else
                differences += "- " + createFullDate(cours) + "\n";
        }

        // On vérifie si Ajout

        // C'est le reste des éléments du liste_cours_non_edt_ancien
        for(EDTJours cours : liste_cours_non_edt_ancien) {
            if(cours.getResources().getModule() != null)
                differences += "+ " + cours.getResources().getModule().toString() + " le " + createFullDate(cours) + "\n";
            else
                differences += "+ " + createFullDate(cours) + "\n";
        }

        return differences;
    }

    /**
     * Retourne la date réelle et complète d'un cours ex: 15/03/2017 (11:00-12:00)
     *
     * @param cours un cours
     * @return la date complète
     */
    private String createFullDate(EDTJours cours) {
        String fullDate = "";
        // Ajoute le jour (début de la semaine + le jour dans la semaine)
        fullDate += String.valueOf(Integer.parseInt(cours.getRawweeks().substring(0, 2)) + Integer.parseInt(cours.getDay()));
        // Ajoute le reste de la date
        fullDate += cours.getRawweeks().substring(2, cours.getRawweeks().length() - 1);
        // Ajoute les horaires
        fullDate += " (" + cours.getPrettytimes() + ")";
        return fullDate;
    }
}