package jahirfiquitiva.apps.iconshowcase.models;

import android.graphics.drawable.Drawable;

public class RequestItem {

    String appName = null;
    String packageName = null;
    String className = null;
    Drawable iconDrawable;
    boolean selected = false;

    public RequestItem(String appName, String packageName, String className, Drawable iconDrawable) {
        super();
        this.appName = appName;
        this.packageName = packageName;
        this.className = className;
        this.iconDrawable = iconDrawable;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String pkgappName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Drawable getIcon() {
        return iconDrawable;
    }

    public void setIcon(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Used to compare object to object
     *
     * @param other
     * @return
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof RequestItem)) {
            return false;
        }

        RequestItem that = (RequestItem) other;

        // Custom equality check here.
        return this.appName.equals(that.appName)
                && this.packageName.equals(that.packageName)
                && this.className.equals(that.className);
    }
}