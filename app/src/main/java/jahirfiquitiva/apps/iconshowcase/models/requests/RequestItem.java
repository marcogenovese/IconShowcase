package jahirfiquitiva.apps.iconshowcase.models.requests;

import android.graphics.drawable.Drawable;

public class RequestItem {

    String name = null;
    String packagename = null;
    String classname = null;
    Drawable img;
    int icon;
    boolean selected = false;

    public RequestItem(String name, String packagename, String classname, Drawable icon, boolean selected) {
        super();
        this.name = name;
        this.packagename = packagename;
        this.classname = classname;
        this.img = icon;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packagename;
    }

    public void setPackageName(String pkgname) {
        this.packagename = pkgname;
    }

    public String getClassName() {
        return classname;
    }

    public void setClassName(String classname) {
        this.classname = classname;
    }

    public Drawable getImage() {
        return img;
    }

    public int getIcon() {
        return icon;
    }

    public void setImage(Drawable img) {
        this.img = img;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}