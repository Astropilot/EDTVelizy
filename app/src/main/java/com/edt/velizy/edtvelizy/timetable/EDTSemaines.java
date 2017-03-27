package com.edt.velizy.edtvelizy.timetable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Classe permettant d'obtenir des informations sur les semaines
 * de l'emploi du temps
 */
@Root(strict=false)
public class EDTSemaines {

    /**
     * La description de la semaine, ex: Semaine 11, Semaine commençant le 13/03/2017
     */
    @Element(name="description")
    private String description;

    /**
     * La position de la semaine sur les 52 semaines par an
     */
    @Element(name="alleventweeks")
    private String alleventweeks;

    /**
     * La date du début de la semaine (commencant un lundi) ex: 13/03/2017
     */
    @Attribute(name="date")
    private String date;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAlleventweeks() {
        return alleventweeks;
    }

    public void setAlleventweeks(String alleventweeks) {
        this.alleventweeks = alleventweeks;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
