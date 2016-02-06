/*
 * Copyright (c) 2016. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.iconshowcase.models;

public class IconItem {

    private String name;
    private int resId;

    public IconItem(String name, int resId){
        this.name = name;
        this.resId = resId;
    }

    public String getName(){
        return this.name;
    }

    public int getResId(){
        return this.resId;
    }

}
