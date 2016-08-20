package jahirfiquitiva.iconshowcase.enums;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.utilities.Utils;

/**
 * Created by Allan Wang on 2016-08-18.
 */
public enum DrawerType {

//    MAIN("Main", R.string.app_name),
    HOME("Home", R.string.section_home, R.drawable.ic_home),
    PREVIEWS("Previews", R.string.section_icons, R.drawable.ic_previews),
    WALLPAPERS("Wallpapers", R.string.section_wallpapers, R.drawable.ic_wallpapers),
    REQUESTS("Requests", R.string.section_icon_request, R.drawable.ic_request),
    APPLY("Apply", R.string.section_apply, R.drawable.ic_apply),
    FAQS("Faqs", R.string.faqs_section, R.drawable.ic_questions),
    ZOOPER("Zoopers", R.string.zooper_section_title, R.drawable.ic_zooper_kustom),
    KUSTOM("Kustom", R.string.section_kustom, R.drawable.ic_zooper_kustom),
    CREDITS("Credits", R.string.section_about),
    SETTINGS("Settings", R.string.title_settings),
    DONATE("Donate", R.string.section_donate);

    private String name;
    private int titleID, iconRes;
    private boolean isSecondary = false;

    DrawerType(String name, @StringRes int titleID) {
        this.name = name;
        this.titleID = titleID;
        isSecondary = true;
    }

    DrawerType(String name, @StringRes int titleID, @DrawableRes int iconRes) {
        this.name = name;
        this.titleID = titleID;
        this.iconRes = iconRes;
    }

    public int getTitleID() {
        return titleID;
    }

    public int getIconRes() {
        if (isSecondary) {
            throw new RuntimeException("Secondary DrawerTypes do not have icons");
        }
        return iconRes;
    }

    public String getName() {
        return name;
    }

    public boolean isSecondary() {
        return isSecondary;
    }

    /**
     *
     * @param context for resource retrieval
     * @param dt drawer type
     * @param i identifier for drawer item
     * @return
     */
    public static PrimaryDrawerItem getDrawerItem(final Context context, DrawerType dt, int i) {
        if (!dt.isSecondary()) {
            return new PrimaryDrawerItem().withName(context.getResources().getString(dt.getTitleID()))
                    .withIdentifier(i).withIcon(Utils.getVectorDrawable(context, dt.getIconRes()))
                    .withIconTintingEnabled(true);
        } else {
            return new SecondaryDrawerItem().withName(context.getResources().getString(dt.getTitleID()))
                    .withIdentifier(i);
        }
    }


//    public abstract Fragment getFragment();
}
