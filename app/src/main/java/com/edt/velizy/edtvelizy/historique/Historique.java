package com.edt.velizy.edtvelizy.historique;


import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict=false)
public class Historique {

    /**
     * Toutes les semaines ajoutées
     */
    @ElementList(inline=true, entry="evenements")
    private List<Evenement> evenements;

    public Historique() {

    }

    public Historique(List<Evenement> listeEvents) {
        this.evenements = listeEvents;
    }

    // Accesseurs et modifieurs

    public List<Evenement> getEvenements() {
        return evenements;
    }

    public void setEvenements(List<Evenement> evenements) {
        this.evenements = evenements;
    }

    public void addEvent(Evenement event) { this.evenements.add(event); }
}
