package jahirfiquitiva.apps.iconshowcase.dialogs;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.adapters.ChangelogAdapter;
import jahirfiquitiva.apps.iconshowcase.adapters.IconsAdapter;
import jahirfiquitiva.apps.iconshowcase.models.IconsLists;

/**
 * This Class was created by Patrick J
 * on 07.01.16. For more Details and Licensing
 * have a look at the README.md
 */
public final class ISDialogs {

    /*
    Dialogs used in the Showcase Activity
     */

    public static void showChangelogDialog(Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.changelog_dialog_title)
                .adapter(new ChangelogAdapter(context, R.array.fullchangelog), null)
                .positiveText(R.string.great)
                .show();
    }

    public static void showIconsChangelogDialog(Context context) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.changelog)
                .customView(R.layout.icons_changelog, false)
                .positiveText(context.getResources().getString(R.string.close))
                .build();

        RecyclerView iconsGrid = (RecyclerView) dialog.getCustomView().findViewById(R.id.changelogRV);
        iconsGrid.setHasFixedSize(true);
        iconsGrid.setLayoutManager(new GridLayoutManager(context,
                context.getResources().getInteger(R.integer.icon_grid_width)));

        IconsAdapter adapter = new IconsAdapter(context,
                (ArrayList<String>) IconsLists.getNewIconsL(), IconsLists.getNewIconsAL(), true);
        iconsGrid.setAdapter(adapter);

        dialog.show();

    }

    public static void showLicenseSuccessDialog(Context context, MaterialDialog.SingleButtonCallback singleButtonCallback) {
        new MaterialDialog.Builder(context)
                .title(R.string.license_success_title)
                .content(R.string.license_success)
                .positiveText(R.string.close)
                .onPositive(singleButtonCallback)
                .show();
    }

    public static void showLicenseFailedDialog(Context context, MaterialDialog.SingleButtonCallback onPositive, MaterialDialog.SingleButtonCallback onNegative) {
        new MaterialDialog.Builder(context)
                .title(R.string.license_failed_title)
                .content(R.string.license_failed)
                .positiveText(R.string.download)
                .negativeText(R.string.exit)
                .onPositive(onPositive)
                .onNegative(onNegative)
                .cancelable(false)
                .autoDismiss(false)
                .show();
    }

    /*
    ViewerActivity Dialogs
     */

    public static void showApplyWallpaperDialog(Context context, MaterialDialog.SingleButtonCallback onPositive, MaterialDialog.SingleButtonCallback onNeutral) {
        new MaterialDialog.Builder(context)
                .title(R.string.apply)
                .content(R.string.confirm_apply)
                .positiveText(R.string.apply)
                .neutralText(R.string.crop)
                .negativeText(android.R.string.cancel)
                .onPositive(onPositive)
                .onNeutral(onNeutral)
                .show();
    }

    public static MaterialDialog showDownloadDialog(Context context) {
        return new MaterialDialog.Builder(context)
                .content(R.string.downloading_wallpaper)
                .progress(true, 0)
                .cancelable(false)
                .build();
    }


    /*
    Apply Fragment Dialogs
     */

    public static void showOpenInPlayStoreDialog(Context context, String title, String content, MaterialDialog.SingleButtonCallback onPositive) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(onPositive)
                .show();
    }

    public static void showGoogleNowLauncherDialog(Context context, MaterialDialog.SingleButtonCallback onPositive) {
        new MaterialDialog.Builder(context)
                .title(R.string.gnl_title)
                .content(R.string.gnl_content)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(onPositive)
                .show();
    }

    public static void showApplyAdviceDialog(Context context, MaterialDialog.SingleButtonCallback callback) {
        new MaterialDialog.Builder(context)
                .title(R.string.advice)
                .content(R.string.apply_advice)
                .positiveText(R.string.close)
                .neutralText(R.string.dontshow)
                .onAny(callback)
                .show();
    }

    /*
    Request Fragment Dialogs
     */

    public static void showRequestAdviceDialog(Context context, MaterialDialog.SingleButtonCallback callback) {
        new MaterialDialog.Builder(context)
                .title(R.string.advice)
                .content(R.string.request_advice)
                .positiveText(R.string.close)
                .neutralText(R.string.dontshow)
                .onAny(callback)
                .show();
    }

    public static void showPermissionNotGrantedDialog(Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.md_error_label)
                .content(context.getResources().getString(R.string.md_storage_perm_error, R.string.app_name))
                .positiveText(android.R.string.ok)
                .show();
    }

    public static MaterialDialog showBuildingRequestDialog(Context context) {
        return new MaterialDialog.Builder(context)
                .content(R.string.building_request_dialog)
                .progress(true, 0)
                .cancelable(false)
                .build();
    }

}
