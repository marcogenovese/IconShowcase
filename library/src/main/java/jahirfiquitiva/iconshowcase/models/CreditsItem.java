/*
 * Copyright (c) 2016. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.iconshowcase.models;

import android.graphics.drawable.Drawable;

public class CreditsItem {
    private String text;
    private Drawable icon;

    public CreditsItem(String text, Drawable icon) {
        this.text = text;
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public Drawable getIcon() {
        return icon;
    }
}