package com.edt.velizy.edtvelizy.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        textview_version.setText("Version: " + BuildConfig.VERSION_NAME);
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
}
