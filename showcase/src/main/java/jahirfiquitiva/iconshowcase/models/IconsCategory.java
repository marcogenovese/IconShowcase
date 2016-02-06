/*
 * Copyright (c) 2016. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.iconshowcase.models;

import java.util.ArrayList;

public class IconsCategory {

    private String name;
    private ArrayList<IconItem> iconsArray = new ArrayList<>();

    public IconsCategory(String name) {
        this.name = name;
    }

    public IconsCategory(String name, ArrayList<IconItem> iconsArray) {
        this.name = name;
        this.iconsArray = iconsArray;
    }

    public String getCategoryName() {
        return this.name;
    }

    public void setCategoryName(String name) {
        this.name = name;
    }

    public ArrayList<IconItem> getIconsArray() {
        return iconsArray.size() > 0 ? this.iconsArray : null;
    }

}
