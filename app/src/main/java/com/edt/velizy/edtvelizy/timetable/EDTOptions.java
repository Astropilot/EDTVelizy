package com.edt.velizy.edtvelizy.timetable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Classe permettant d'obtenir le titre de l'emploi du temps
 */
@Root(strict=false, name="option")
public class EDTOptions {

    /**
     * Le titre de l'emploi du temps
     */
    @Element
    private String subheading;

    public String getSubheading() {
        return subheading;
    }

    public void setSubheading(String subheading) {
        this.subheading = subheading;
    }
}
