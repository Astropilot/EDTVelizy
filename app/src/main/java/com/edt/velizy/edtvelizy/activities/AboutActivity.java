package com.edt.velizy.edtvelizy.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.MenuItem;
import android.widget.TextView;

import com.edt.velizy.edtvelizy.BuildConfig;
import com.edt.velizy.edtvelizy.R;

/**
 * Classe permettant de gérer l'affichage et la gestion de la
 * page "A propos"
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // On active la fonction retour
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView textview_version = (TextView) findViewById(R.id.textView_version);
        TextView textview_credits = (TextView)findViewById(R.id.textView_credtis);
        textview_version.setText("Version: " + BuildConfig.VERSION_NAME);

        // On va mettre les liens sur les différents textes

        String credits = getString(R.string.credits);
        Spannable spanCredits = new SpannableString(credits);

        ApplyUrlOnText(credits, spanCredits, "Android Week View", "https://github.com/alamkanak/Android-Week-View/");
        ApplyUrlOnText(credits, spanCredits, "Simple", "http://simple.sourceforge.net/");
        ApplyUrlOnText(credits, spanCredits, "OkHttp", "http://square.github.io/okhttp/");
        ApplyUrlOnText(credits, spanCredits, "Icons8", "https://fr.icons8.com/");

        textview_credits.setText(spanCredits);
        textview_credits.setMovementMethod(LinkMovementMethod.getInstance());

    }

    /**
     * Cette fonction est appelée quand un item
     * du menu est appuyé
     *
     * @param item l'item qui à été selectionné
     * @return un boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Si c'est le bouton retour on ferme cette fenêtre
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Cette fonction permet d'ajouter un URL sur un texte Spannable
     *
     * @param text le texte au format String
     * @param spanText le même texte au format Spannable, c'est celui ci qui sera modifé avec le lien
     * @param keyword le texte sur lequel le lien va être appliqué
     * @param url l'url a vouloir mettre
     */
    private void ApplyUrlOnText(String text, Spannable spanText, String keyword, String url) {
        int startSpan, endSpan = 0;
        startSpan = text.indexOf(keyword, endSpan);
        endSpan = startSpan + keyword.length();
        spanText.setSpan(new URLSpan(url), startSpan, endSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
