package com.edt.velizy.edtvelizy.utils;


import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Classe regroupant des fonctions d'utilisation de requêtes web
 *
 */
public class Internet {

    /**
     * Permet de faire une requête sur une URL spécifique avec un couple (ID, MDP)
     * et de récupérer le contenu de la page
     *
     * @param url l'URL de la page
     * @param cID l'identifiant de sécurité
     * @param cPass le mot de passe de sécurité
     * @return le contenu de la page web
     */
    public static String retrieve(String url, String cID, String cPass) {

        // On démarre une requete sur la page donnée avec les identifiants de sécurité
        // donnés, puis on retourne la réponse ou "" si la requête à échouée
        OkHttpClient client = new OkHttpClient();
        String credential = Credentials.basic(cID, cPass);
        Request request = new Request.Builder()
                .header("Authorization", credential)
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        catch (IOException e) {
            return "";
        }
    }
}
