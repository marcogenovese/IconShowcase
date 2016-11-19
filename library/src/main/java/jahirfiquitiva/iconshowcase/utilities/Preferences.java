/*
 * Copyright (c) 2016 Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.iconshowcase.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import jahirfiquitiva.iconshowcase.R;

public class Preferences {

    private static final String
            PREFERENCES_NAME = "dashboard_preferences",
            FEATURES_ENABLED = "features_enabled",
            VERSION_CODE = "version_code",
            ROTATE_MINUTE = "rotate_time_minute",
            ROTATE_TIME = "muzei_rotate_time",
            LAUNCHER_ICON = "launcher_icon_shown",
            WALLS_DOWNLOAD_FOLDER = "walls_download_folder",
            APPS_TO_REQUEST_LOADED = "apps_to_request_loaded",
            WALLS_LIST_LOADED = "walls_list_loaded",
            SETTINGS_MODIFIED = "settings_modified",
            ANIMATIONS_ENABLED = "animations_enabled",
            WALLPAPER_AS_TOOLBAR_HEADER = "wallpaper_as_toolbar_header",
            APPLY_DIALOG_DISMISSED = "apply_dialog_dismissed",
            WALLS_DIALOG_DISMISSED = "walls_dialog_dismissed",
            WALLS_COLUMNS_NUMBER = "walls_columns_number",
            REQUEST_HOUR = "request_hour",
            REQUEST_DAY = "request_day",
            REQUESTS_CREATED = "requests_created",
            REQUESTS_LEFT = "requests_left",
            NOTIFS_ENABLED = "notifs_enabled",
            NOTIFS_LED_ENABLED = "notifs_led_enabled",
            NOTIFS_VIBRATION_ENABLED = "notifs_vibration_enabled",
            NOTIFS_UPDATE_INTERVAL = "notifs_update_interval";

    private static final String
            DEV_DRAWER_TEXTS = "dev_drawer_texts",
            DEV_LISTS_CARDS = "dev_lists_cards";

    private final Context context;

    public Preferences(Context context) {
        this.context = context;
    }

    private SharedPreferences prefs() {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void setFeaturesEnabled(boolean enable) {
        prefs().edit().putBoolean(FEATURES_ENABLED, enable).apply();
    }

    public boolean areFeaturesEnabled() {
        return prefs().getBoolean(FEATURES_ENABLED, false);
    }

    public int getRotateTime() {
        return prefs().getInt(ROTATE_TIME, 3 * 60 * 60 * 1000);
    }

    public void setRotateTime(int time) {
        prefs().edit().putInt(ROTATE_TIME, time).apply();
    }

    public boolean isRotateMinute() {
        return prefs().getBoolean(ROTATE_MINUTE, false);
    }

    public void setRotateMinute(boolean bool) {
        prefs().edit().putBoolean(ROTATE_MINUTE, bool).apply();
    }

    public void setIconShown(boolean show) {
        prefs().edit().putBoolean(LAUNCHER_ICON, show).apply();
    }

    public boolean getLauncherIconShown() {
        return prefs().getBoolean(LAUNCHER_ICON, true);
    }

    public String getDownloadsFolder() {
        String name = context != null ? context.getResources()
                .getString(R.string.app_name).replaceAll(" ", "")
                : "IconShowcase";
        return prefs().getString(WALLS_DOWNLOAD_FOLDER,
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/" + name + "/Wallpapers");
    }

    public void setDownloadsFolder(String folder) {
        prefs().edit().putString(WALLS_DOWNLOAD_FOLDER, folder).apply();
    }

    public void setIfAppsToRequestLoaded(boolean loaded) {
        prefs().edit().putBoolean(APPS_TO_REQUEST_LOADED, loaded).apply();
    }

    public boolean didAppsToRequestLoad() {
        return prefs().getBoolean(APPS_TO_REQUEST_LOADED, false);
    }

    public boolean getWallsListLoaded() {
        return prefs().getBoolean(WALLS_LIST_LOADED, false);
    }

    public void setWallsListLoaded(boolean loaded) {
        prefs().edit().putBoolean(WALLS_LIST_LOADED, loaded).apply();
    }

    public boolean getSettingsModified() {
        return prefs().getBoolean(SETTINGS_MODIFIED, false);
    }

    public void setSettingsModified(boolean loaded) {
        prefs().edit().putBoolean(SETTINGS_MODIFIED, loaded).apply();
    }

    public boolean getAnimationsEnabled() {
        return prefs().getBoolean(ANIMATIONS_ENABLED, true);
    }

    public void setAnimationsEnabled(boolean animationsEnabled) {
        prefs().edit().putBoolean(ANIMATIONS_ENABLED, animationsEnabled).apply();
    }

    public boolean getWallpaperAsToolbarHeaderEnabled() {
        boolean value = context == null || context.getResources()
                .getBoolean(R.bool.show_user_wallpaper_in_toolbar_by_default);
        return prefs().getBoolean(WALLPAPER_AS_TOOLBAR_HEADER, value);
    }

    public void setWallpaperAsToolbarHeaderEnabled(boolean wallpaperAsToolbarHeader) {
        prefs().edit().putBoolean(WALLPAPER_AS_TOOLBAR_HEADER, wallpaperAsToolbarHeader).apply();
    }

    public boolean getApplyDialogDismissed() {
        return prefs().getBoolean(APPLY_DIALOG_DISMISSED, false);
    }

    public void setApplyDialogDismissed(boolean applyDialogDismissed) {
        prefs().edit().putBoolean(APPLY_DIALOG_DISMISSED, applyDialogDismissed).apply();
    }

    public boolean getWallsDialogDismissed() {
        return prefs().getBoolean(WALLS_DIALOG_DISMISSED, false);
    }

    public void setWallsDialogDismissed(boolean wallsDialogDismissed) {
        prefs().edit().putBoolean(WALLS_DIALOG_DISMISSED, wallsDialogDismissed).apply();
    }

    public int getWallsColumnsNumber() {
        return prefs().getInt(WALLS_COLUMNS_NUMBER,
                context.getResources().getInteger(R.integer.wallpapers_grid_width));
    }

    public void setWallsColumnsNumber(int columnsNumber) {
        prefs().edit().putInt(WALLS_COLUMNS_NUMBER, columnsNumber).apply();
    }

    public String getRequestHour() {
        return prefs().getString(REQUEST_HOUR, "null");
    }

    public void setRequestHour(String hour) {
        prefs().edit().putString(REQUEST_HOUR, hour).apply();
    }

    public int getRequestDay() {
        return prefs().getInt(REQUEST_DAY, 0);
    }

    public void setRequestDay(int day) {
        prefs().edit().putInt(REQUEST_DAY, day).apply();
    }

    public boolean getRequestsCreated() {
        return prefs().getBoolean(REQUESTS_CREATED, false);
    }

    public void setRequestsCreated(boolean requestsCreated) {
        prefs().edit().putBoolean(REQUESTS_CREATED, requestsCreated).apply();
    }

    public int getRequestsLeft() {
        return prefs().getInt(REQUESTS_LEFT, -1);
    }

    public void setRequestsLeft(int requestsLeft) {
        prefs().edit().putInt(REQUESTS_LEFT, requestsLeft).apply();
    }

    public int getRequestsLeft(Context context) {
        return prefs().getInt(REQUESTS_LEFT,
                context.getResources().getInteger(R.integer.max_apps_to_request));
    }

    public void resetRequestsLeft(Context context) {
        prefs().edit().putInt(REQUESTS_LEFT, context.getResources().getInteger(R.integer.max_apps_to_request)).apply();
    }

    //NOTIFICATIONS:

    public boolean getNotifsEnabled() {
        return prefs().getBoolean(NOTIFS_ENABLED, false);
    }

    public void setNotifsEnabled(boolean enabled) {
        prefs().edit().putBoolean(NOTIFS_ENABLED, enabled).apply();
    }

    public boolean getNotifsLedEnabled() {
        return prefs().getBoolean(NOTIFS_LED_ENABLED, true);
    }

    public void setNotifsLedEnabled(boolean enableLed) {
        prefs().edit().putBoolean(NOTIFS_LED_ENABLED, enableLed).apply();
    }

    public boolean getNotifsVibrationEnabled() {
        return prefs().getBoolean(NOTIFS_VIBRATION_ENABLED, true);
    }

    public void setNotifsVibrationEnabled(boolean vibrate) {
        prefs().edit().putBoolean(NOTIFS_VIBRATION_ENABLED, vibrate).apply();
    }

    public int getNotifsUpdateInterval() {
        return prefs().getInt(NOTIFS_UPDATE_INTERVAL, 4);
    }

    public void setNotifsUpdateInterval(int interval) {
        prefs().edit().putInt(NOTIFS_UPDATE_INTERVAL, interval).apply();
    }

    public int getVersionCode() {
        return prefs().getInt(VERSION_CODE, 0);
    }

    public void setVersionCode(int versionCode) {
        prefs().edit().putInt(VERSION_CODE, versionCode).apply();
    }

    public boolean getDevDrawerTexts() {
        return prefs().getBoolean(DEV_DRAWER_TEXTS, true);
    }

    public void setDevDrawerTexts(boolean enable) {
        prefs().edit().putBoolean(DEV_DRAWER_TEXTS, enable).apply();
    }

    public boolean getDevListsCards() {
        return prefs().getBoolean(DEV_LISTS_CARDS, false);
    }

    public void setDevListsCards(boolean enableCards) {
        prefs().edit().putBoolean(DEV_LISTS_CARDS, enableCards).apply();
    }

}