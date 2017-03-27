package com.edt.velizy.edtvelizy.timetable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Classe permettant de recevoir les informations de l'emploi du temps
 * après avoir "parser" le fichier XML de l'emploi du temps
 *
 */
@Root(strict=false)
public class timetable {

    /**
     * Le titre de l'emploi du temps
     */
    @Element(name="option")
    private EDTOptions option;

    /**
     * Toutes les semaines de l'emploi du temps
     */
    @ElementList(inline=true, entry="span")
    private List<EDTSemaines> span;

    /**
     * Tout les cours de l'emploi du temps
     */
    @ElementList(inline=true, entry="event")
    private List<EDTJours> event;

    // Accesseurs et modifieurs


    public EDTOptions getOption() {
        return option;
    }

    public void setOption(EDTOptions option) {
        this.option = option;
    }

    public List<EDTSemaines> getSpan() {
        return span;
    }

    public void setSpan(List<EDTSemaines> span) {
        this.span = span;
    }

    public List<EDTJours> getEvent() {
        return event;
    }

    public void setEvent(List<EDTJours> event) {
        this.event = event;
    }
}
