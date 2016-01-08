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
            APPS_TO_REQUEST_LOADED = "apps_to_request_loaded",
            WALLS_LIST_LOADED = "walls_list_loaded",
            SETTINGS_MODIFIED = "settings_modified",
            ANIMATIONS_ENABLED = "animations_enabled",
            WALLPAPER_AS_TOOLBAR_HEADER = "wallpaper_as_toolbar_header",
            REQUESTS_DIALOG_DISMISSED = "requests_dialog_dismissed",
            APPLY_DIALOG_DISMISSED = "apply_dialog_dismissed",
            WALLS_DIALOG_DISMISSED = "walls_dialog_dismissed";

    private final Context context;

    public Preferences(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void setFirstRun(boolean firstRun) {
        getSharedPreferences().edit().putBoolean(FIRSTRUN, firstRun).apply();
    }

    public boolean isFirstRun() {
        return getSharedPreferences().getBoolean(FIRSTRUN, true);
    }

    public void setFeaturesEnabled(boolean enable) {
        getSharedPreferences().edit().putBoolean(FEATURES_ENABLED, enable).apply();
    }

    public boolean areFeaturesEnabled() {
        return getSharedPreferences().getBoolean(FEATURES_ENABLED, true);
    }

    public void setRotateTime(int time) {
        getSharedPreferences().edit().putInt(ROTATE_TIME, time).apply();
    }

    public int getRotateTime() {
        return getSharedPreferences().getInt(ROTATE_TIME, 21600000);
    }

    public void setRotateMinute(boolean bool) {
        getSharedPreferences().edit().putBoolean(ROTATE_MINUTE, bool).apply();
    }

    public boolean isRotateMinute() {
        return getSharedPreferences().getBoolean(ROTATE_MINUTE, false);
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

    public void setWallpaperAsToolbarHeaderEnabled(boolean wallpaperAsToolbarHeader) {
        getSharedPreferences().edit().putBoolean(WALLPAPER_AS_TOOLBAR_HEADER, wallpaperAsToolbarHeader).apply();
    }

    public boolean getWallpaperAsToolbarHeaderEnabled() {
        return getSharedPreferences().getBoolean(WALLPAPER_AS_TOOLBAR_HEADER, true);
    }

    public void setRequestsDialogDismissed(boolean requestsDialogDismissed) {
        getSharedPreferences().edit().putBoolean(REQUESTS_DIALOG_DISMISSED, requestsDialogDismissed).apply();
    }

    public boolean getRequestsDialogDismissed() {
        return getSharedPreferences().getBoolean(REQUESTS_DIALOG_DISMISSED, false);
    }

    public void setApplyDialogDismissed(boolean applyDialogDismissed) {
        getSharedPreferences().edit().putBoolean(APPLY_DIALOG_DISMISSED, applyDialogDismissed).apply();
    }

    public boolean getApplyDialogDismissed() {
        return getSharedPreferences().getBoolean(APPLY_DIALOG_DISMISSED, false);
    }

    public void setWallsDialogDismissed(boolean wallsDialogDismissed) {
        getSharedPreferences().edit().putBoolean(WALLS_DIALOG_DISMISSED, wallsDialogDismissed).apply();
    }

    public boolean getWallsDialogDismissed() {
        return getSharedPreferences().getBoolean(WALLS_DIALOG_DISMISSED, false);
    }

}