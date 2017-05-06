package com.edt.velizy.edtvelizy.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Classe servant a regrouper des fonctions utiles à la gestion
 * des préférences de l'application
 */
public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;
    Context context;

    private static final String NOM_PREF = "Prefs";

    private static final String EDT_ID = "EDT_ID";

    private static final String USERNAME_LOGIN = "USERNAME_LOGIN";

    private static final String PASSWORD_LOGIN = "PASSWORD_LOGIN";

    private static final String EDT_GID = "EDT_GID";

    private static final String FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String SUIVI_ACTIVE = "SUIVI";

    private static final String SUIVI_ON_BOOT = "SUIVI_DEMARRAGE";

    public PrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(NOM_PREF, Context.MODE_PRIVATE);
        prefEditor = pref.edit();
    }

    public void setValeur(String cle, String valeur) {
        prefEditor.putString(cle, valeur);
        prefEditor.commit();
    }

    public void setValeur(String cle, Boolean valeur) {
        prefEditor.putBoolean(cle, valeur);
        prefEditor.commit();
    }

    public String getValeur(String cle, String defaut) {
        return pref.getString(cle, defaut);
    }

    public void setEdtID(String edtID) {
        setValeur(EDT_ID, edtID);
    }

    public String getEdtID() {
        return getValeur(EDT_ID, "No");
    }

    public void setUsername(String username) {
        setValeur(USERNAME_LOGIN, username);
    }

    public String getUsername() {
        return getValeur(USERNAME_LOGIN, "");
    }

    public void setPassword(String password) {
        setValeur(PASSWORD_LOGIN, password);
    }

    public String getPassword() {
        return getValeur(PASSWORD_LOGIN, "");
    }

    public void setGroupID(String gID) {
        setValeur(EDT_GID, gID);
    }

    public String getGroupID() {
        return getValeur(EDT_GID, "0");
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        setValeur(FIRST_TIME_LAUNCH, isFirstTime);
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(FIRST_TIME_LAUNCH, true);
    }

    public void setSuiviActive(boolean SuiviActive) {
        setValeur(SUIVI_ACTIVE, SuiviActive);
    }

    public boolean getSuiviActive() {
        return pref.getBoolean(SUIVI_ACTIVE, false);
    }

    public void setSuiviOnBoot(boolean SuiviBoot) {
        setValeur(SUIVI_ON_BOOT, SuiviBoot);
    }

    public boolean getSuiviOnBoot() {
        return pref.getBoolean(SUIVI_ON_BOOT, true);
    }
}
