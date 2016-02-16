/*
 *
 */

package jahirfiquitiva.iconshowcase.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.Utils;

public class WallpaperToCrop extends AsyncTask<Void, String, Boolean> {

    private Activity activity;
    private MaterialDialog dialog;
    private Bitmap resource;
    private Uri wallUri;
    private Context context;
    private View layout;
    private FloatingActionMenu fab;
    private String wallName;
    private WeakReference<Activity> wrActivity;
    private LinearLayout toHide1, toHide2;

    public WallpaperToCrop(Activity activity, MaterialDialog dialog, Bitmap resource,
                           View layout, FloatingActionMenu fab, String wallName) {
        this.wrActivity = new WeakReference<>(activity);
        this.dialog = dialog;
        this.resource = resource;
        this.layout = layout;
        this.fab = fab;
        this.wallName = wallName;
    }

    public WallpaperToCrop(Activity activity, MaterialDialog dialog, Bitmap resource,
                           View layout, FloatingActionMenu fab, String wallName, LinearLayout toHide1, LinearLayout toHide2) {
        this.wrActivity = new WeakReference<>(activity);
        this.dialog = dialog;
        this.resource = resource;
        this.layout = layout;
        this.fab = fab;
        this.wallName = wallName;
        this.toHide1 = toHide1;
        this.toHide2 = toHide2;
    }

    @Override
    protected void onPreExecute() {
        final Activity a = wrActivity.get();
        if (a != null) {
            this.context = a.getApplicationContext();
            this.activity = a;
        }

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean worked;
        if (wallUri != null) {
            wallUri = null;
        }
        try {
            wallUri = getImageUri(context, resource);
            worked = wallUri != null;
        } catch (Exception e) {
            worked = false;
        }
        return worked;
    }

    @Override
    protected void onPostExecute(Boolean worked) {
        final Preferences mPrefs = new Preferences(context);
        if (toHide1 != null && toHide2 != null) {
            toHide1.setVisibility(View.GONE);
            toHide2.setVisibility(View.GONE);
        }
        if (worked) {
            dialog.dismiss();
            Intent setWall = new Intent(Intent.ACTION_ATTACH_DATA);
            setWall.setDataAndType(wallUri, "image/*");
            setWall.putExtra("png", "image/*");
            activity.startActivityForResult(Intent.createChooser(setWall,
                    context.getResources().getString(R.string.set_as)), 1);
        } else {
            dialog.dismiss();
            Snackbar snackbar = Snackbar.make(layout,
                    context.getResources().getString(R.string.error), Snackbar.LENGTH_SHORT);
            snackbar.show();
            snackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    if (toHide1 != null && toHide2 != null) {
                        toHide1.setVisibility(View.VISIBLE);
                        toHide2.setVisibility(View.VISIBLE);
                    }
                    if (fab != null) {
                        fab.showMenuButton(mPrefs.getAnimationsEnabled());
                    }
                }
            });
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {

        Preferences mPrefs = new Preferences(inContext);
        File downloadsFolder;

        if (inImage.isRecycled()) {
            inImage = inImage.copy(Bitmap.Config.ARGB_8888, false);
        }

        if (mPrefs.getDownloadsFolder() != null) {
            downloadsFolder = new File(mPrefs.getDownloadsFolder());
        } else {
            downloadsFolder = new File(context.getString(R.string.walls_save_location,
                    Environment.getExternalStorageDirectory().getAbsolutePath()));
        }

        downloadsFolder.mkdirs();

        File destFile = new File(downloadsFolder, wallName + ".png");

        if (!destFile.exists()) {
            try {
                inImage.compress(Bitmap.CompressFormat.PNG, 100,
                        new FileOutputStream(destFile));
            } catch (final Exception e) {
                Utils.showLog(context, e.getLocalizedMessage());
            }
        }

        return Uri.fromFile(destFile);
    }

}
