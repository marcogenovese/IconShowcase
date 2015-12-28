package jahirfiquitiva.apps.iconshowcase.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static final String
            PREFERENCES_NAME = "DASHBOARD_PREFERENCES",
            FEATURES_ENABLED = "features_nabled",
            FIRSTRUN = "firstrun",
            ROTATE_MINUTE = "rotate_time_minute",
            ROTATE_TIME = "muzei_rotate_time",
            LAUNCHER_ICON = "laucher_icon_shown",
            WALLS_DOWNLOAD_FOLDER = "walls_download_folder",
            EASTEREGG_ENABLED = "easteregg_enabled",
            HIDDEN_ADVICES = "hidden_advices",
            APPS_TO_REQUEST_LOADED = "apps_to_request_loaded",
            WALLS_LIST_LOADED = "walls_list_loaded",
            SETTINGS_MODIFIED = "settings_modified",
            ANIMATIONS_ENABLED = "animations_enabled";

    private final Context context;

    public Preferences(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public boolean isFirstRun() {
        return getSharedPreferences().getBoolean(FIRSTRUN, true);
    }

    public boolean isFeaturesEnabled() {
        return getSharedPreferences().getBoolean(FEATURES_ENABLED, true);
    }

    public boolean isRotateMinute() {
        return getSharedPreferences().getBoolean(ROTATE_MINUTE, false);
    }

    public int getRotateTime() {
        return getSharedPreferences().getInt(ROTATE_TIME, 21600000);
    }

    public void setFeaturesEnabled(boolean enable) {
        getSharedPreferences().edit().putBoolean(FEATURES_ENABLED, enable).apply();
    }

    public void setNotFirstRun() {
        getSharedPreferences().edit().putBoolean(FIRSTRUN, false).apply();
    }

    public void setRotateTime(int time) {
        getSharedPreferences().edit().putInt(ROTATE_TIME, time).apply();
    }

    public void setRotateMinute(boolean bool) {
        getSharedPreferences().edit().putBoolean(ROTATE_MINUTE, bool).apply();
    }

    public void setEasterEggEnabled(boolean dbenabled) {
        getSharedPreferences().edit().putBoolean(EASTEREGG_ENABLED, dbenabled).apply();
    }

    public boolean getEasterEggEnabled() {
        return getSharedPreferences().getBoolean(EASTEREGG_ENABLED, false);
    }

    public void setIconShown(boolean show) {
        getSharedPreferences().edit().putBoolean(LAUNCHER_ICON, show).apply();
    }

    public boolean getLauncherIconShown() {
        return getSharedPreferences().getBoolean(LAUNCHER_ICON, true);
    }

    public void setHiddenAdvices(boolean dismissed) {
        getSharedPreferences().edit().putBoolean(HIDDEN_ADVICES, dismissed).apply();
    }

    public boolean getHiddenAdvices() {
        return getSharedPreferences().getBoolean(HIDDEN_ADVICES, false);
    }

    public void setDownloadsFolder(String folder) {
        getSharedPreferences().edit().putString(WALLS_DOWNLOAD_FOLDER, folder).apply();
    }

    public String getDownloadsFolder() {
        return getSharedPreferences().getString(WALLS_DOWNLOAD_FOLDER, null);
    }

    public void setAppsToRequestLoaded(boolean loaded) {
        getSharedPreferences().edit().putBoolean(APPS_TO_REQUEST_LOADED, loaded).apply();
    }

    public boolean getAppsToRequestLoaded() {
        return getSharedPreferences().getBoolean(APPS_TO_REQUEST_LOADED, false);
    }

    public void setWallsListLoaded(boolean loaded) {
        getSharedPreferences().edit().putBoolean(WALLS_LIST_LOADED, loaded).apply();
    }

    public boolean getWallsListLoaded() {
        return getSharedPreferences().getBoolean(WALLS_LIST_LOADED, false);
    }

    public void setSettingsModified(boolean loaded) {
        getSharedPreferences().edit().putBoolean(SETTINGS_MODIFIED, loaded).apply();
    }

    public boolean getSettingsModified() {
        return getSharedPreferences().getBoolean(SETTINGS_MODIFIED, false);
    }

    public void setAnimationsEnabled(boolean animationsEnabled) {
        getSharedPreferences().edit().putBoolean(ANIMATIONS_ENABLED, animationsEnabled).apply();
    }

    public boolean getAnimationsEnabled() {
        return getSharedPreferences().getBoolean(ANIMATIONS_ENABLED, true);
    }

}