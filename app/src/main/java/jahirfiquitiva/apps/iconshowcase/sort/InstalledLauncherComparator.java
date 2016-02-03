package jahirfiquitiva.apps.iconshowcase.sort;

import android.content.Context;

import java.util.Comparator;

import jahirfiquitiva.apps.iconshowcase.fragments.ApplyFragment;

/**
 * @author Aidan Follestad (afollestad)
 */
public class InstalledLauncherComparator implements Comparator<ApplyFragment.Launcher> {

    private final Context context;

    public InstalledLauncherComparator(Context context) {
        this.context = context;
    }

    @Override
    public int compare(ApplyFragment.Launcher lhs, ApplyFragment.Launcher rhs) {
        if (!lhs.isInstalled(context) && rhs.isInstalled(context)) {
            // Left is not installed, right is, push left down towards the bottom.
            return 1;
        } else if (lhs.isInstalled(context) && !rhs.isInstalled(context)) {
            // Left is installed, right isn't, pull left up towards the top.
            return -1;
        } else {
            // Sort alphabetically if they're at the same position.
            return lhs.name.compareTo(rhs.name);
        }
    }
}
