package com.edt.velizy.edtvelizy.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.edt.velizy.edtvelizy.R;
import com.edt.velizy.edtvelizy.utils.FileIO;
import com.edt.velizy.edtvelizy.utils.HuaweiDetection;
import com.edt.velizy.edtvelizy.utils.Internet;
import com.edt.velizy.edtvelizy.utils.PrefManager;


public class LoginActivity extends AppCompatActivity {

    /**
     * Le contexte de notre activité pour l'utiliser plus loin
     */
    Context activityContext;

    /**
     * Une boite de dialogue pour patienter pendant le téléchargement de l'emploi du temps
     */
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activityContext = this;

        // On récupère l'utilisateur et le mot de passe (si existant) et l'ID de l'emploi du temps qui n'est pas nul si
        // l'emploi du temps à déjà été chargé une fois
        final PrefManager prefs = new PrefManager(this);
        String Username = prefs.getUsername();
        String Password = prefs.getPassword();
        final String eID = prefs.getEdtID();

        Button horsligneButton = (Button) findViewById(R.id.button_horsligne);

        // Si aucun ID de l'emploi du temps n'a été sauvegarder, on ne peut pas activer le mode hors-ligne
        if(eID.equals("")) {
            horsligneButton.setEnabled(false);
        }

        // Si l'utilisateur et le password avait été sauvegardé, on les met dans les TextBox
        if(!Username.equals("") && !Password.equals("")) {
            EditText m_username = (EditText) findViewById(R.id.text_ID);
            EditText m_password = (EditText) findViewById(R.id.text_Pass);
            CheckBox saveID = (CheckBox) findViewById(R.id.check_SaveID);
            m_username.setText(Username);
            m_password.setText(Password);
            saveID.setChecked(true);
        }

        // Lors de l'appui sur le bouton de connection
        Button loginButton = (Button) findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText username = (EditText) findViewById(R.id.text_ID);
                EditText password = (EditText) findViewById(R.id.text_Pass);
                CheckBox saveID = (CheckBox) findViewById(R.id.check_SaveID);

                // Si l'utilisateur veux sauvegarder les identifiant on le fait
                if(saveID.isChecked()) {
                    prefs.setUsername(username.getText().toString());
                    prefs.setPassword(password.getText().toString());
                }
                else {
                    prefs.setUsername("");
                    prefs.setPassword("");
                }

                // On affiche une boite de dialogue d'attente
                mDialog = new ProgressDialog(LoginActivity.this);
                mDialog.setTitle("EDT - Connexion");
                mDialog.setMessage("Connexion en cours...");
                mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mDialog.setCancelable(false);
                mDialog.show();
                // On commence le téléchargement des groupes
                new DownloadWebpageTask().execute(getString(R.string.login_group_url), username.getText().toString() ,password.getText().toString());
            }
        });

        // Lors de l'appui sur le bouton "hors-ligne"
        horsligneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // On lis le fichier de sauvegarde de l'emploi du temps
                String edt = FileIO.ReadFile(activityContext, "oldedt.edt");

                // Puis on lance notre activité qui va afficher notre emploi du temps
                Intent EDTActivite = new Intent(getApplicationContext(), NavigateActivity.class);
                //EDTActivite.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                //EDTActivite.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                EDTActivite.putExtra(ChooseGroupActivity.DownloadEDTTask.mEDT, edt);
                EDTActivite.putExtra(ChooseGroupActivity.DownloadEDTTask.mEDTID, eID);
                //overridePendingTransition (0, 0);
                startActivity(EDTActivite);
            }
        });

        // Lors de l'appuis sur le bouton "A propos"
        TextView aboutButton = (TextView) findViewById(R.id.textView6);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On ouvre l'activité "A propos"
                Intent RegisterActivite = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(RegisterActivite);
            }
        });

        // On lance la détection du système "Applications Protégées"
        HuaweiDetection.ifHuaweiAlert(this);
    }

    /**
     * Une classe permettant de télécharger la page des groupes
     * sans passer par le thread de l'UI
     */
    class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        public final static String mGroups = "com.edt.velizy.edtvelizy.intent.example.Groups";
        public final static String mUser = "com.edt.velizy.edtvelizy.intent.example.User";
        public final static String mPasswd = "com.edt.velizy.edtvelizy.intent.example.Passwd";

        private String user_u;
        private String passwd_u;

        @Override
        protected String doInBackground(String... urls) {
            // Ici on télécharge la page des groupes
            user_u = urls[1];
            passwd_u = urls[2];
            return Internet.retrieve(urls[0], urls[1], urls[2]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // On ferme la boite de dialogue d'attente
            mDialog.cancel();

            // Si le téléchargement à réussi
            if ( result.contains("Groupe index") ) {
                // On récupère la partie de la page qui nous intéresse et on passe à l'activité de sélection des groupes
                String GroupsHtml = getGroupsHtml(result);
                Intent ChooseGroupActivite = new Intent(getApplicationContext(), ChooseGroupActivity.class);
                ChooseGroupActivite.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                ChooseGroupActivite.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                ChooseGroupActivite.putExtra(mGroups, GroupsHtml);
                ChooseGroupActivite.putExtra(mUser, user_u);
                ChooseGroupActivite.putExtra(mPasswd, passwd_u);
                overridePendingTransition(0, 0);
                startActivity(ChooseGroupActivite);
            }
            // Sinon
            else {
                // On affiche que le téléchargement à échoué
                AlertDialog alertDialog = new AlertDialog.Builder(activityContext).create();
                alertDialog.setTitle("Erreur");
                alertDialog.setMessage("Connexion impossible, vérifier votre connexion internet ou réessayez plus tard !");
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
     * Permet d'extraire de la page HTML la zone des groupes
     * de l'IUT
     *
     * @param srcHtml la page HTML complète
     * @return le code HTML ne contenant que les groupes
     */
    private String getGroupsHtml(String srcHtml) {
        int selectIndex = srcHtml.indexOf("<select name=\"menu2\"");
        int selectIndex2 = srcHtml.indexOf("</select>");
        return srcHtml.substring(selectIndex, selectIndex2);

    }

}
