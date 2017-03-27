package com.edt.velizy.edtvelizy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.edt.velizy.edtvelizy.fragments.EDTFragment;
import com.edt.velizy.edtvelizy.R;

public class NavigateActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent i = getIntent();

        String xmlEDT = i.getStringExtra(ChooseGroupActivity.DownloadEDTTask.mEDT);
        if(!xmlEDT.startsWith("<timetable>")) {
            xmlEDT = ChangeXml(xmlEDT);
        }
        String EdtID = i.getStringExtra(ChooseGroupActivity.DownloadEDTTask.mEDTID);

        EDTFragment fragment = EDTFragment.newInstance(xmlEDT, EdtID);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Pour gérer la navigation selon les éléments du menu
        int id = item.getItemId();

        if (id == R.id.nav_edt) {
            // Afficher ici l'emploi du temps
        } else if (id == R.id.nav_suivi) {
            // TODO: afficher l'historique du suivi
        } else if (id == R.id.nav_retour) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Fonction permettant de supprimer le header du XML
     * qui fais bugger le parser XML
     *
     * @param xmlEDT le xml au format texte
     * @return le xml sans le header
     */
    private String ChangeXml(String xmlEDT)
    {
        return xmlEDT.substring(xmlEDT.indexOf("<timetable>"), xmlEDT.length());
    }
}
