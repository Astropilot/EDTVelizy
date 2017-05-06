package com.edt.velizy.edtvelizy.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.edt.velizy.edtvelizy.R;
import com.edt.velizy.edtvelizy.activities.LoginActivity;
import com.edt.velizy.edtvelizy.activities.NavigateActivity;
import com.edt.velizy.edtvelizy.historique.Evenement;
import com.edt.velizy.edtvelizy.historique.Historique;
import com.edt.velizy.edtvelizy.timetable.EDTJours;
import com.edt.velizy.edtvelizy.timetable.EDTSemaines;
import com.edt.velizy.edtvelizy.timetable.timetable;
import com.edt.velizy.edtvelizy.utils.AlarmManager;
import com.edt.velizy.edtvelizy.utils.FileIO;
import com.edt.velizy.edtvelizy.utils.Internet;
import com.edt.velizy.edtvelizy.utils.PrefManager;

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
import java.util.Calendar;
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

        PrefManager prefs = new PrefManager(this);

        // Si on detecte le démarrage du téléphone et si le suivi a été activé avant et que l'utilisateur l'autorise, on active notre alarme
        if(intent.getStringExtra("action").contains("BOOT_COMPLETED") && prefs.getSuiviActive() && prefs.getSuiviOnBoot()){
            AlarmManager.scheduleAlarm(this);
            return;
        }

        // Si on n'est pas au démarrage, on vérifie alors que l'alarme est bien lancé pour poursuivre
        if(!AlarmManager.checkAlarm(this))
            return;


        String EdtID;
        String cID;
        String cPass;

        // On récupère le l'identifiant de l'emploi du temps puis
        // on télécharge l'emploi du temps actuel

        EdtID = prefs.getEdtID();
        cID = prefs.getUsername();
        cPass = prefs.getPassword();

        String current_EDT = Internet.retrieve(getString(R.string.edt_url) + EdtID + ".xml", cID, cPass);

        // On vérifie que la connexion internet est présent :)

        if(!current_EDT.contains("<timetable>"))
            return;

        // On récupère l'ancien EDT (oldedt.edt)

        String old_EDT = FileIO.ReadFile(this, "oldedt.edt");

        // On prépare l'EDT courant

        current_EDT = current_EDT.substring(current_EDT.indexOf("<timetable>"), current_EDT.length());

        // On applique l'algorithme de comparaison

        boolean notif = CompareEDT(old_EDT, current_EDT);

        // On fait de l'EDT courant l'ancien EDT

        FileIO.WriteFile(this, "oldedt.edt", current_EDT);

        // Si aucun changement détecté on quitte le service
        if(!notif)
            return;

        // Sinon on affiche une notification

        NotificationManager mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent launchNotifiactionIntent = new Intent(this, NavigateActivity.class);
        launchNotifiactionIntent.putExtra("action", "HISTO");
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                1234, launchNotifiactionIntent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setWhen(System.currentTimeMillis())
                .setTicker("Modification de l'EDT !")
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle("L'emploi du temps à été modifé !")
                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                .setContentText("Cliquez pour voir les changements")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        mNotification.notify(101, builder.build());
    }

    /**
     * Permet de comparer deux emploi du temps et de sauvegarder les différences trouvées
     *
     * @param edt1 le premier emploi du temps
     * @param edt2 le deuxieme emploi du temps
     * @return true si une/plusieurs différence(s), sinon false
     */
    private boolean CompareEDT(String edt1, String edt2) {
        String differences = "";
        timetable old_EDT;
        timetable current_EDT;

        // On initialise tout ce qui concerne l'historique

        Historique historique;

        Evenement evenement = new Evenement();
        evenement.setDate(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRANCE).format(Calendar.getInstance().getTime()));

        if (FileIO.isHistoriqueExist(this))
            historique = FileIO.getHistorique(this);
        else {
            historique = new Historique(new ArrayList<Evenement>());
        }

        //On sérialise nos deux emploi du temps pour les avoir dans des objets

        Serializer serializer = new Persister();
        try {
            old_EDT = serializer.read(timetable.class, edt1, false);
            serializer = new Persister();
            current_EDT = serializer.read(timetable.class, edt2, false);
        } catch (Exception e) {
            return false;
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
        String semaine_blacklisted = "";
        String semaine_blacklisted2 = "";
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
                differences += "Nouvelle semaine : " + semaine.getDate() + "{n}{n}";
                semaine_blacklisted = semaine.getDate();
                semaine_blacklisted2 = old_EDT.getSpan().get(0).getDate();
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
                if( cours.getRawweeks().equals(cours2.getRawweeks()) && cours.getStarttime().equals(cours2.getStarttime()) && cours.getEndtime().equals(cours2.getEndtime()) && cours.getDay().equals(cours2.getDay())) {
                    found = true;
                }
            }
            if(!found && !cours.getRawweeks().equals(semaine_blacklisted2)) liste_cours_non_edt_courant.add(cours);
        }

        // On ajoute les nouveaux cours dans la deuxième liste si ils ne sont pas dans l'ancien EDT
        // On ignore les cours qui sont dans la semaine black listée
        for(EDTJours cours : current_EDT.getEvent()) {
            boolean found = false;
            for(EDTJours cours2 : old_EDT.getEvent()) {
                if( cours.getRawweeks().equals(cours2.getRawweeks()) && cours.getStarttime().equals(cours2.getStarttime()) && cours.getEndtime().equals(cours2.getEndtime()) && cours.getDay().equals(cours2.getDay())) {
                    found = true;
                }
            }
            if(!found && !cours.getRawweeks().equals(semaine_blacklisted)) liste_cours_non_edt_ancien.add(cours);
        }

        // On vérifie si Suppressions

        // C'est le reste des éléments du liste_cours_non_edt_courant
        for(EDTJours cours : liste_cours_non_edt_courant) {
            cours.setAjout(false);
            if(cours.getResources().getModule() != null)
                differences += "- " + cours.getResources().getModule().toString() + " le " + createFullDate(cours) + "{n}";
            else
                differences += "- " + createFullDate(cours) + "{n}";
        }

        // On vérifie si Ajout

        // C'est le reste des éléments du liste_cours_non_edt_ancien
        for(EDTJours cours : liste_cours_non_edt_ancien) {
            cours.setAjout(true);
            if(cours.getResources().getModule() != null)
                differences += "+ " + cours.getResources().getModule().toString() + " le " + createFullDate(cours) + "{n}";
            else
                differences += "+ " + createFullDate(cours) + "{n}";
        }

        // On écrit l'historique si il y a eu du nouveau

        if(!differences.equals("")) {
            evenement.setDescription(differences);
            historique.addEvent(evenement);
            boolean test = FileIO.setHistorique(this, historique);
            Log.i("TEST_HISTO", String.valueOf(test));
        }


        return (!differences.equals(""));
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