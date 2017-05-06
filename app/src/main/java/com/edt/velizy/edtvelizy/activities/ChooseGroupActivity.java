package com.edt.velizy.edtvelizy.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.edt.velizy.edtvelizy.R;
import com.edt.velizy.edtvelizy.utils.Internet;
import com.edt.velizy.edtvelizy.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ChooseGroupActivity extends AppCompatActivity {

    /**
     * Le contexte de notre activité pour l'utiliser plus loin
     */
    private Context activityContext;

    /**
     * La liste des ID des groupes
     */
    private List<String> ids_groups = new ArrayList<>();

    /**
     * Une boite de dialogue pour patienter pendant le téléchargement de l'emploi du temps
     */
    private ProgressDialog mDialog;

    /**
     * L'accès aux préférences de l'application
     */
    private PrefManager prefs;

    /**
     * Fonction appelée lors de la création de l'activité
     * @param savedInstanceState .
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_group);
        final Intent i = getIntent();

        activityContext = this;

        prefs = new PrefManager(this);

        // On récupère la liste des groupes, puis on l'affiche dans une ListView
        final String hGroups = i.getStringExtra(LoginActivity.DownloadWebpageTask.mGroups);
        final List<String> gList = ParseHtmlGroups(hGroups);
        final ListView listGroups = (ListView) findViewById(R.id.listView_Groups);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, android.R.id.text1, gList);

        listGroups.setAdapter(adapter);

        // On récupère l'éventuelle dernière selection du groupe, et si on en trouve une on séléctionne directement à cette position
        String selectionPreference = prefs.getGroupID();
        listGroups.setItemChecked(Integer.parseInt(selectionPreference), true);
        listGroups.setSelection(Integer.parseInt(selectionPreference));

        // Lors de l'appui du bouton de sélection de groupe
        Button GroupButton = (Button) findViewById(R.id.select_groupe_btn);
        GroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Du coup on sauvegarde ici notre dernier groupe sélectionné
                int selected_group_index = listGroups.getCheckedItemPosition();
                prefs.setGroupID(String.valueOf(selected_group_index));

                // On récupère ensuite l'ID du groupe associé
                String idgroup = ids_groups.get(selected_group_index);

                if(idgroup.equals("g538")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(activityContext).create();
                    alertDialog.setTitle("Attention");
                    alertDialog.setMessage("Le groupe \"IUT\" n'est pas utilisable sur mobile.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return;
                }

                // On affiche une boite de dialogue d'attente
                mDialog = new ProgressDialog(ChooseGroupActivity.this);
                mDialog.setTitle("EDT - Download");
                mDialog.setMessage("Téléchargement en cours...");
                mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mDialog.setCancelable(false);
                mDialog.show();

                // On télécharge ensuite l'emploi du temps au format XML
                new DownloadEDTTask().execute(getString(R.string.edt_url) + idgroup + ".xml", i.getStringExtra(LoginActivity.DownloadWebpageTask.mUser), i.getStringExtra(LoginActivity.DownloadWebpageTask.mPasswd), idgroup);
            }
        });
    }

    /**
     * Une classe permettant de télécharger l'emploi du temps
     * sans passer par le thread de l'UI
     */
    class DownloadEDTTask extends AsyncTask<String, Void, String> {

        public final static String mEDT = "com.edt.velizy.edtvelizy.intent.example.mEDT";
        public final static String mEDTID = "com.edt.velizy.edtvelizy.intent.example.mEDTID";

        private String EdtID = "";

        @Override
        protected String doInBackground(String... urls) {
            // Ici on télécharge la page de l'emploi du temps en XML
            this.EdtID = urls[3];

            return Internet.retrieve(urls[0], urls[1], urls[2]);
        }

        @Override
        protected void onPostExecute(String result) {
            // On ferme la boite de dialogue d'attente
            mDialog.cancel();
            // Si le téléchargement à réussi
            if ( result.contains("<?xml version=") )
            {
                // On passe à l'activité de l'emploi du temps
                Intent EDTActivite = new Intent(getApplicationContext(), NavigateActivity.class);
                //EDTActivite.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                //EDTActivite.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                EDTActivite.putExtra(mEDT, result);
                EDTActivite.putExtra(mEDTID, EdtID);
                overridePendingTransition (0, 0);
                startActivity(EDTActivite);
            }
            // Sinon
            else
            {
                // On affiche que le téléchargement à échoué
                AlertDialog alertDialog = new AlertDialog.Builder(activityContext).create();
                alertDialog.setTitle("Erreur");
                alertDialog.setMessage("Impossible de télécharger l'emploi du temps !");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }
    }

    /**
     * Fonction permettant d'extraire les groupes de la page
     * html des groupes
     *
     * @param htmlGroups la page HTMl des groupes
     * @return la liste des groupes avec leurs ID
     */
    private List<String> ParseHtmlGroups(String htmlGroups)
    {
        List<String> list = new ArrayList<>();
        Pattern mpattern = Pattern.compile("<option value=\"([^\\\"]*).html\">([^\\\"]*)</option>");
        Matcher m = mpattern.matcher(htmlGroups);
        while (m.find())
        {
            list.add(m.group(2));
            ids_groups.add(m.group(1));
        }
        return list;
    }
}
