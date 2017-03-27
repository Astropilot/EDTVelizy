package com.edt.velizy.edtvelizy.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.widget.CompoundButton;

import com.edt.velizy.edtvelizy.R;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Classe regroupant des fonctions pour detecter et prevenir l'utilisateur sur la fonctionnalité
 * "Applications protégées" sur certains appareils qui empechent le service de fonctionner si
 * l'application n'est pas activé
 *
 * @author https://github.com/Aiuspaktyn
 */
public class HuaweiDetection {

    /**
     * Fonction qui detecte si la fonctionnalité "Applications protégées" est présente
     * et si oui affiche une boite de dialogue pour prévenir l'utilisateur
     *
     * @param context le contexte
     */
    public static void ifHuaweiAlert(final Context context) {
        final SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        final String saveIfSkip = "skipProtectedAppsMessage";
        boolean skipMessage = settings.getBoolean(saveIfSkip, false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            Intent intent = new Intent();
            intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
            if (isCallable(intent, context)) {
                final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(context);
                dontShowAgain.setText("Ne plus afficher");
                dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        editor.putBoolean(saveIfSkip, isChecked);
                        editor.apply();
                    }
                });

                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Huawei Applications protégées")
                        .setMessage(String.format("%s a besoin d'être activé dans 'Applications protégées' pour fonctionner proprement.%n", context.getString(R.string.app_name)))
                        .setView(dontShowAgain)
                        .setPositiveButton("Application protégées", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                huaweiProtectedApps(context);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            } else {
                editor.putBoolean(saveIfSkip, true);
                editor.apply();
            }
        }
    }

    /**
     * Permet de vérifier si un Intent est appellable
     *
     * @param intent l'intent à vérifier
     * @param context le contexte
     * @return retourne true si l'intent est appellable, sinon false
     */
    private static boolean isCallable(Intent intent, Context context) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * Permet de rediriger l'utilisateur sur la page des Applications Protégées pour qu'il active
     * notre application
     *
     * @param context
     */
    private static void huaweiProtectedApps(Context context) {
        try {
            String cmd = "am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial(context);
            }
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ignored) {
        }
    }

    /**
     * Permet de récupérer le UserSerial
     *
     * @param context le contexte
     * @return le UserSerial
     */
    private static String getUserSerial(Context context) {
        //noinspection ResourceType
        Object userManager = context.getSystemService("user");
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return "";
    }
}
