package jahirfiquitiva.iconshowcase.config;

import android.support.annotation.Nullable;

/**
 * Created by Allan Wang on 2016-08-19.
 *
 * With reference to Polar
 * https://github.com/afollestad/polar-dashboard/blob/master/app/src/main/java/com/afollestad/polar/config/IConfig.java
 */
public interface IConfig {

    //Main Configs

    boolean allowDebugging();

    @Nullable
    String[] primaryDrawerItems();

    int appTheme();

    boolean withDrawerText();

    @Nullable
    String[] googleDonationCatalog();

    @Nullable
    String[] googleDonationConsumable();

    @Nullable
    String[] googleDonationNonConsumable();

    @Nullable
    String paypalUser();

    @Nullable
    String paypalCurrency();

    boolean devOptions();

    //FAQ Configs

    boolean faqCards();

    //Home Configs

    boolean shuffleToolbarIcons();

    boolean userWallpaperInToolbar();

    boolean hidePackInfo();

    //Amounts interface?

    @Nullable
    String[] homeListTitles();

    @Nullable
    String[] homeListDescriptions();

    @Nullable
    String[] homeListIcons();

    @Nullable
    String[] homeListLinks();

    //Kustom Configs









    boolean allowThemeSwitching();

    boolean darkTheme();

    void darkTheme(boolean enabled);

    boolean darkThemeDefault();

    boolean navDrawerModeEnabled();

    void navDrawerModeEnabled(boolean enabled);

    boolean navDrawerModeAllowSwitch();

    boolean homepageEnabled();

    @Nullable
    String wallpapersJsonUrl();

    boolean wallpapersAllowDownload();

    boolean wallpapersEnabled();

    boolean zooperEnabled();

    @Nullable
    String iconRequestEmail();

    boolean iconRequestEnabled();

    @Nullable
    String feedbackEmail();

    @Nullable
    String feedbackSubjectLine();

    boolean feedbackEnabled();

    @Nullable
    String donationLicenseKey();

    boolean donationEnabled();

    @Nullable
    String[] donateOptionsNames();

    @Nullable
    String[] donateOptionsIds();

    @Nullable
    String licensingPublicKey();

    boolean persistSelectedPage();

    boolean changelogEnabled();

    int gridWidthWallpaper();

    int gridWidthApply();

    int gridWidthIcons();

    int gridWidthRequests();

    int gridWidthZooper();

    int iconRequestMaxCount();

    String polarBackendHost();

    String polarBackendApiKey();

    boolean polarBackendEmailFallback();
}