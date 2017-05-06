package com.edt.velizy.edtvelizy.historique;

import com.edt.velizy.edtvelizy.timetable.EDTJours;
import com.edt.velizy.edtvelizy.timetable.EDTSemaines;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict=false)
public class Evenement {

    /**
     * La date de l'historique
     */
    @Element(name="date")
    private String date;

    /**
     * Tout les cours ajoutés ou supprimés
     */
    @Element(name="description")
    private String description;

    // Accesseurs et modifieurs

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
