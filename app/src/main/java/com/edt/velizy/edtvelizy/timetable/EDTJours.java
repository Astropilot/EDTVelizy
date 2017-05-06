package com.edt.velizy.edtvelizy.timetable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Classe permettant d'obtenir des informations sur un cours
 */
@Root(strict=false)
public class EDTJours {

    /**
     * L'identifiant du cours
     */
    @Attribute(name="id")
    private String eID;

    /**
     * La couleur du cours
     */
    @Attribute(name="colour")
    private String colour;

    /**
     * Le jour du cours 0=Lundi, 1=Mardi, 2=Mercredi, etc
     */
    @Element(name="day")
    private String day;

    /**
     * C'est la plage horaire du cours ex: 11:00-12:00
     */
    @Element(name="prettytimes")
    private String prettytimes;

    /**
     * L'heure de début du cours
     */
    @Element(name="starttime")
    private String starttime;

    /**
     * L'heure de fin du cours
     */
    @Element(name="endtime")
    private String endtime;

    /**
     * La semaine dans laquelle le cours est placé
     */
    @Element(name="rawweeks")
    private String rawweeks;

    /**
     * Des informations complémentaires
     */
    @Element(name="resources")
    private EDTJourInfos resources;

    /**
     * Les éventuelles remarques du cours
     */
    @Element(name="notes", required = false)
    private String notes;

    /**
     * Cet attribut n'est utilisé que pour l'historique de suivi,
     * il sert a savoir si l'evenement à été ajouté ou supprimé
     */
    @Attribute(name="ajout", required = false)
    private boolean ajout;

    // Accesseurs et modifieurs


    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getRawweeks() {
        return rawweeks;
    }

    public void setRawweeks(String rawweeks) {
        this.rawweeks = rawweeks;
    }

    public EDTJourInfos getResources() {
        return resources;
    }

    public void setResources(EDTJourInfos resources) {
        this.resources = resources;
    }

    public String getPrettytimes() {
        return prettytimes;
    }

    public void setPrettytimes(String prettytimes) {
        this.prettytimes = prettytimes;
    }

    public String geteID() { return eID; }

    public void seteID(String eID) { this.eID = eID; }

    public String getNotes() { return notes; }

    public void setNotes(String notes) { this.notes = notes; }

    public boolean isAjout() {
        return ajout;
    }

    public void setAjout(boolean ajout) {
        this.ajout = ajout;
    }
}
