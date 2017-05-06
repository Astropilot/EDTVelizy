package com.edt.velizy.edtvelizy.utils;


import android.content.Context;
import android.util.Log;

import com.edt.velizy.edtvelizy.R;
import com.edt.velizy.edtvelizy.historique.Historique;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Classe regroupant des fonctions pour lire/écrire dans des fichiers
 */
public class FileIO {

    /**
     * Fonction qui permet d'écrire du texte dans un fichier stocké en interne, si le fichier n'existe pas il sera
     * créé, sinon il sera réécrit
     *
     * @param context le contexte
     * @param filename le nom du fichier (avec le chemin si besoin)
     * @param content le contenu à écrire dans le fichier
     */
    public static void WriteFile(Context context, String filename, String content) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(content);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", e.toString());
        }
    }

    /**
     * Fonction qui permet de lire du texte dans un fichier stocké en interne.
     *
     * @param context le contexte
     * @param filename le nom du fichier (avec le chemin si besoin)
     * @return Le texte contenu dans le fichier
     */
    public static String ReadFile(Context context, String filename) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "FileIO not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static String ReadFile(File file) {
        String ret = "";

        try {
            FileInputStream inputStream = new FileInputStream(file);

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
        }
        catch (FileNotFoundException e) {
            Log.e("read activity", "FileIO not found: " + e.toString());
        } catch (IOException e) {
            Log.e("read activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static Historique getHistorique(Context context) {
        String historique = ReadFile(context, context.getString(R.string.fichier_histo));

        Historique mHistorique;

        // On sérialise l'historique

        Serializer serializer = new Persister();
        try {
            mHistorique = serializer.read(Historique.class, historique, false);
        } catch (Exception e) {
            return null;
        }

        return mHistorique;
    }

    public static boolean setHistorique(Context context, Historique historique) {
        File mHistorique = new File(context.getFilesDir().getPath().toString(), "/histotemp.xml");
        Serializer serializer = new Persister();
        try {
            serializer.write(historique, mHistorique);
        } catch (Exception e) {
            Log.e("setHistorique", "Erreur dans le parsing: " + e.toString());
            e.printStackTrace();
            return false;
        }
        try {
            WriteFile(context, context.getString(R.string.fichier_histo), ReadFile(mHistorique));
        } catch (Exception e) {
            Log.e("setHistorique", "Erreur dans l'écriture");
            return false;
        }
        mHistorique.delete();
        return true;
    }

    public static void deleteHistorique(Context context) {
        context.deleteFile(context.getString(R.string.fichier_histo));
    }

    public static boolean isHistoriqueExist(Context context) {
        return !ReadFile(context, context.getString(R.string.fichier_histo)).equals("");
    }
}
