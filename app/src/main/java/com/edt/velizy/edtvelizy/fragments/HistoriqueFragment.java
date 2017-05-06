package com.edt.velizy.edtvelizy.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.edt.velizy.edtvelizy.R;
import com.edt.velizy.edtvelizy.historique.Evenement;
import com.edt.velizy.edtvelizy.historique.Historique;
import com.edt.velizy.edtvelizy.utils.FileIO;
import com.edt.velizy.edtvelizy.utils.HistoArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HistoriqueFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    HistoriqueFragment fragmentContext;

    public HistoriqueFragment() {

    }

    public static HistoriqueFragment newInstance(String param1, String param2) {
        HistoriqueFragment fragment = new HistoriqueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historique, container, false);

        fragmentContext = this;

        final Button clearHistoButton = (Button) view.findViewById(R.id.clear_histo);

        // On récupère l'historique s'il existe
        if(!FileIO.isHistoriqueExist(getActivity())) {
            clearHistoButton.setText("Historique vide");
            clearHistoButton.setEnabled(false);
            return view;
        }

        List<SpannableString> evenements = new ArrayList<>();
        Historique historique = FileIO.getHistorique(getActivity());

        // On inverse la liste des evenements pour avoir le plus récent en haut
        List<Evenement> events = historique.getEvenements();
        Collections.reverse(events);

        for(Evenement evt : events) {
            // Algorithme de coloration des lignes "+" et "-"
            String text = evt.getDate() + ":\n\n" + evt.getDescription().replace("{n}", "\n");
            SpannableString stringWithStyle = new SpannableString(text);
            stringWithStyle = colorLines("+ ", text, stringWithStyle, Color.rgb(204, 255, 153));
            stringWithStyle = colorLines("- ", text, stringWithStyle, Color.rgb(255, 153, 153));
            evenements.add(stringWithStyle);
        }

        // On ajoute notre liste au ListView
        final ListView listHisto = (ListView) view.findViewById(R.id.listview_histo);
        final ArrayAdapter<SpannableString> adapter = new HistoArrayAdapter(this,  evenements);

        listHisto.setAdapter(adapter);

        clearHistoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileIO.deleteHistorique(fragmentContext.getActivity());
                listHisto.setAdapter(null);
                clearHistoButton.setText("Historique vide");
                clearHistoButton.setEnabled(false);
            }
        });

        return view;
    }

    private SpannableString colorLines(String key, String text, SpannableString stringWithStyle, int couleur) {
        int startSpan, endSpan = 0;
        while (true) {
            startSpan = text.indexOf(key, endSpan);
            BackgroundColorSpan backColour = new BackgroundColorSpan(couleur);
            if (startSpan < 0)
                break;
            endSpan = text.indexOf("\n", startSpan);
            stringWithStyle.setSpan(backColour, startSpan, endSpan,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return stringWithStyle;
    }
}
