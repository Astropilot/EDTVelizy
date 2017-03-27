package com.edt.velizy.edtvelizy.timetable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Classe permettant d'avoir les ressources du cours
 */
@Root(strict=false, name="resources")
public class EDTJourInfos {

    /**
     * Le nom de la matière/module
     */
    @Element(name = "module", required = false)
    private EDTItem module;

    /**
     * Le/Les professeur(s) de ce cours
     */
    @Element(name = "staff", required = false)
    private EDTItem staff;

    /**
     * La/Les salle(s) du cours
     */
    @Element(name = "room", required = false)
    private EDTItem room;

    public EDTItem getModule() {
        return module;
    }

    public void setModule(EDTItem module) {
        this.module = module;
    }

    public EDTItem getStaff() {
        return staff;
    }

    public void setStaff(EDTItem staff) {
        this.staff = staff;
    }

    public EDTItem getRoom() {
        return room;
    }

    public void setRoom(EDTItem room) {
        this.room = room;
    }
}
