package com.edt.velizy.edtvelizy.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import com.edt.velizy.edtvelizy.R;
import com.edt.velizy.edtvelizy.timetable.EDTJours;
import com.edt.velizy.edtvelizy.timetable.EDTSemaines;
import com.edt.velizy.edtvelizy.timetable.timetable;
import com.edt.velizy.edtvelizy.utils.AlarmManager;
import com.edt.velizy.edtvelizy.utils.FileIO;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Classe permettant de gérer l'affichage et la gestion
 * de l'emploi du temps
 *
 */
public class EDTFragment extends Fragment implements MonthLoader.MonthChangeListener, WeekView.EventClickListener {
    /**
     * L'agument de l'emploi du temps
     */
    private static final String ARG_XMLEDT = "xmledt";

    /**
     * L'argument de l'identifiant de l'emploi du temps
     */
    private static final String ARG_EDTID = "edtid";

    /**
     * Mode de vue par journée
     */
    private static final int TYPE_VUE_JOUR = 1;

    /**
     * Mode de vue par 3 jours
     */
    private static final int TYPE_VUE_3_JOURS = 2;

    /**
     * Mode de vue par semaine
     */
    private static final int TYPE_VUE_SEMAINE = 3;

    /**
     * L'heure de début pour l'affichage
     */
    private static final double HEURE_DEBUT = 8.00d;

    /**
     * Le type de vue pour l'emploi du temps
     */
    private int mWeekViewType = TYPE_VUE_3_JOURS;

    /**
     * Notre objet emploi du temps
     */
    private WeekView mWeekView;

    /**
     * L'emploi du temps au format texte
     */
    private String xmlEDT;

    /**
     * L'identifiant de l'emploi du temps
     */
    private String EdtID;

    /**
     * L'emploi du temps extrait dans une classe
     */
    private timetable EmploiDuTemps;

    /**
     * Le menu de la fenêtre
     */
    private Menu menu;

    /**
     * Le constructeur par défaut
     */
    public EDTFragment() {
        // Required empty public constructor
    }

    /**
     * Fonction qui créé un objet EDTFragment en lui fournissant des informations
     * @param param1 l'emploi du temps
     * @param param2 l'identifiant de l'emploi du temps
     * @return un objet de cette classe
     */
    public static EDTFragment newInstance(String param1, String param2) {
        EDTFragment fragment = new EDTFragment();
        Bundle args = new Bundle();
        args.putString(ARG_XMLEDT, param1);
        args.putString(ARG_EDTID, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Réécriture de la fonction onCreate qui va récupéré les informations
     * donnés
     * @param savedInstanceState l'éventuelle sauvegarde de la dernière instance
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            xmlEDT = getArguments().getString(ARG_XMLEDT);
            EdtID = getArguments().getString(ARG_EDTID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edt, container, false);

        // On met à jour l'id de l'emploi du temps dans les préférences
        SharedPreferences.Editor pref = getActivity().getSharedPreferences("Prefs", Context.MODE_PRIVATE).edit();
        pref.putString("EDT_ID", EdtID);
        pref.commit();

        // On sauvegarde l'emploi du temps qu'on vient de télécharger
        FileIO.WriteFile(getActivity(), "oldedt.edt", xmlEDT);

        // On sérialise l'emploi du temps
        Serializer serializer = new Persister();
        try {
            EmploiDuTemps = serializer.read(timetable.class, xmlEDT, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Conversion des données de l'emploi du temps

        for(EDTJours cours : EmploiDuTemps.getEvent())
        {
            for(EDTSemaines semaine : EmploiDuTemps.getSpan())
            {
                if(cours.getRawweeks().equals(semaine.getAlleventweeks()))
                    cours.setRawweeks(semaine.getDate());
            }
        }

        // Initialisation de l'emploi du temps

        mWeekView = (WeekView) view.findViewById(R.id.weekViewFrag);

        setupDateTimeInterpreter(false);

        mWeekView.setNumberOfVisibleDays(3);
        mWeekView.setHourHeight(120);
        mWeekView.invalidate();

        mWeekView.setMonthChangeListener(this);
        mWeekView.setOnEventClickListener(this);

        mWeekView.goToHour(HEURE_DEBUT);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        MenuItem suiviItem = menu.findItem(R.id.suivi_edt);
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id){
            case R.id.action_today:
                mWeekView.goToToday();
                mWeekView.goToHour(HEURE_DEBUT);
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_VUE_JOUR) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_VUE_JOUR;
                    mWeekView.setNumberOfVisibleDays(1);

                    // On change les dimensions pour les adapter à l'affichage
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setHourHeight(120);
                    mWeekView.invalidate();
                    mWeekView.goToHour(HEURE_DEBUT);
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_VUE_3_JOURS) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_VUE_3_JOURS;
                    mWeekView.setNumberOfVisibleDays(3);

                    // On change les dimensions pour les adapter à l'affichage
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setHourHeight(120);
                    mWeekView.invalidate();
                    mWeekView.goToHour(HEURE_DEBUT);
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_VUE_SEMAINE) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_VUE_SEMAINE;
                    mWeekView.setNumberOfVisibleDays(7);

                    // On change les dimensions pour les adapter à l'affichage
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setHourHeight(120);
                    mWeekView.invalidate();
                    mWeekView.goToHour(HEURE_DEBUT);
                }
                return true;
            case R.id.suivi_edt:
                if(suiviItem.getTitle() == "Suivre") {
                    /*SharedPreferences pref = getActivity().getSharedPreferences("Prefs",
                            Context.MODE_PRIVATE);
                    EdtID = pref.getString("EDT_ID", "No");
                    String cID = pref.getString("USERNAME_LOGIN", "");
                    String cPass = pref.getString("PASSWORD_LOGIN", "");
                    if(cID.equals("") || cPass.equals("")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Attention");
                        alertDialog.setMessage("Vous devez cocher l'option \"Se souvenir des identifiants\" pour activer cette fonctionnalité !");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        scheduleAlarm();
                        suiviItem.setTitle("Stop suivi");
                    }*/
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Information");
                    alertDialog.setMessage("Cette fonctionnalité n'est pas encore disponible, elle le sera bientôt :)");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    AlarmManager.cancelAlarm(getActivity());
                    suiviItem.setTitle("Suivre");
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Avant l'affichage des items du menu, on change le bouton "Suivi" selon
        // si le suivi est déjà lancé ou non
        MenuItem suiviItem = menu.findItem(R.id.suivi_edt);
        if(AlarmManager.checkAlarm(getActivity())) {
            suiviItem.setTitle("Stop suivi");
        } else {
            suiviItem.setTitle("Suivre");
        }
        super.onPrepareOptionsMenu(menu);
    }

    /**
     * Fonction qui charge nos cours dans l'emploi du temps
     *
     * @param newYear l'année
     * @param newMonth le mois
     * @return la liste des éléments à ajouter à l'emploi du temps
     */
    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        // On prépare notre liste d'évenements à ajouter dans l'emploi du temps
        List<WeekViewEvent> events = new ArrayList<>();
        int i = 1;
        // Pour chaque cours de l'emploi du temps récupéré
        for(EDTJours cours : EmploiDuTemps.getEvent())
        {
            // On défini le début du cours
            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(cours.getRawweeks().substring(0, 2)) + Integer.parseInt(cours.getDay()));
            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(cours.getStarttime().split(":")[0]));
            startTime.set(Calendar.MINUTE, Integer.parseInt(cours.getStarttime().split(":")[1]));
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);

            // Puis on défini la fin du cours
            Calendar endTime = (Calendar) startTime.clone();
            endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(cours.getEndtime().split(":")[0]));
            endTime.set(Calendar.MINUTE, Integer.parseInt(cours.getEndtime().split(":")[1]));
            endTime.set(Calendar.MONTH, newMonth-1);

            // On définit son titre
            String title = cours.getPrettytimes() + "\n";
            String salle = "";

            // Si le cours à un nom de module on l'ajoute au titre
            if(cours.getResources().getModule() != null)
                title += cours.getResources().getModule().toString() + " ";
            // Si le cours à un/des professeur(s) on le/les ajoute
            if(cours.getResources().getStaff() != null)
                title += cours.getResources().getStaff().toString() + " ";
            // Si le cours à une/des salle(s) on l'/les ajoute
            if(cours.getResources().getRoom() != null)
                salle = cours.getResources().getRoom().toString();

            // On créé notre évènement et on l'ajoute à notre liste
            WeekViewEvent event = new WeekViewEvent(i, title, salle, startTime, endTime);
            event.setColor(Color.parseColor("#" + cours.getColour()));
            events.add(event);
            i++;
        }

        return events;
    }

    /**
     * Fonction appelée lors d'un clic sur un cours
     *
     * @param event l'évènement en question
     * @param eventRect un rectangle
     */
    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        // On récupère l'indice du cours
        int indice = (int)event.getId() - 1;

        // On affiche une boite de dialogue affichant les informations
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Information Cours");

        if(EmploiDuTemps.getEvent().get(indice).getNotes() != null)
            alertDialog.setMessage(event.getName() + "\nSalle: " + event.getLocation() + "\nRemarques: " + EmploiDuTemps.getEvent().get(indice).getNotes());
        else
            alertDialog.setMessage(event.getName() + "\nSalle: " + event.getLocation() + "\nAucune remarque");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * On réécrit les fonctions de l'emploi du temps pour afficher
     * notre propre heure et date
     * @param shortDate la date
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" d/M", Locale.getDefault());

                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour + ":00 -";
            }
        });
    }
}
