package com.edt.velizy.edtvelizy.utils;


import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

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
}
