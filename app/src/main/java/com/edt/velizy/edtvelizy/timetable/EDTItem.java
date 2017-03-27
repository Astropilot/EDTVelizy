package com.edt.velizy.edtvelizy.timetable;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict=false)
public class EDTItem {

    @ElementList(inline=true, entry="item")
    private List<String> item;

    public List<String> getItem() {
        return item;
    }

    public void setItem(List<String> item) {
        this.item = item;
    }

    public String toString()
    {
        String message = "";
        if(item.size() == 1)
            return item.get(0);
        for(String mitem : item)
        {
            message += mitem + "; ";
        }
        // On retire le dernier ";" inutile ici
        message = message.substring(0, message.length() - 2);
        return message;
    }
}
