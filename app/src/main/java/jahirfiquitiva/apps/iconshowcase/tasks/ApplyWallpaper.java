package jahirfiquitiva.apps.iconshowcase.tasks;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionMenu;

import java.io.IOException;
import java.lang.ref.WeakReference;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;
import jahirfiquitiva.apps.iconshowcase.utilities.Utils;

public class ApplyWallpaper extends AsyncTask<Void, String, Boolean> {
    private Context context;
    private Activity activity;
    private MaterialDialog dialog;
    private Bitmap resource;
    private View layout;
    private boolean isPicker;
    private Snackbar snackbar;
    private FloatingActionMenu fab;
    private WeakReference<Activity> wrActivity;

    public ApplyWallpaper(Activity activity, MaterialDialog dialog, Bitmap resource, Boolean isPicker,
                          View layout, FloatingActionMenu fab) {
        this.wrActivity = new WeakReference<Activity>(activity);
        this.dialog = dialog;
        this.resource = resource;
        this.isPicker = isPicker;
        this.layout = layout;
        this.fab = fab;
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
        WallpaperManager wm = WallpaperManager.getInstance(context);
        Boolean worked;
        try {
            try {
                wm.setBitmap(scaleToActualAspectRatio(resource));
            } catch (OutOfMemoryError ex) {
                Utils.showLog("OutOfMemoryError: " + ex.getLocalizedMessage());
                showRetrySnackbar();
            }
            worked = true;
        } catch (IOException e2) {
            worked = false;
        }
        return worked;
    }

    @Override
    protected void onPostExecute(Boolean worked) {
        final Preferences mPrefs = new Preferences(context);
        if (worked) {
            dialog.dismiss();
            if (!isPicker) {
                Snackbar longSnackbar = Snackbar.make(layout,
                        context.getString(R.string.set_as_wall_done), Snackbar.LENGTH_LONG);
                longSnackbar.show();
                longSnackbar.setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (fab != null) {
                            fab.showMenuButton(mPrefs.getAnimationsEnabled());
                        }
                    }
                });
            }
        } else {
            showRetrySnackbar();
        }
        if (isPicker) {
            activity.finish();
        }

    }

    public Bitmap scaleToActualAspectRatio(Bitmap bitmap) {
        if (bitmap != null) {
            boolean flag = true;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int deviceWidth = displayMetrics.widthPixels;
            int deviceHeight = displayMetrics.heightPixels;

            int bitmapHeight = bitmap.getHeight();
            int bitmapWidth = bitmap.getWidth();
            if (bitmapWidth > deviceWidth) {
                flag = false;
                int scaledHeight = deviceHeight;
                int scaledWidth = (scaledHeight * bitmapWidth) / bitmapHeight;
                try {
                    if (scaledHeight > deviceHeight)
                        scaledHeight = deviceHeight;
                    bitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth,
                            scaledHeight, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (flag) {
                if (bitmapHeight > deviceHeight) {
                    int scaledWidth = (deviceHeight * bitmapWidth)
                            / bitmapHeight;
                    try {
                        if (scaledWidth > deviceWidth)
                            scaledWidth = deviceWidth;
                        bitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth,
                                deviceHeight, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bitmap;
    }

    private void showRetrySnackbar() {
        String retry = context.getResources().getString(R.string.retry);
        snackbar = Snackbar
                .make(layout, R.string.error, Snackbar.LENGTH_INDEFINITE)
                .setAction(retry.toUpperCase(), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new ApplyWallpaper((Activity) context, dialog, resource, isPicker, layout, fab);
                    }
                });
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.accent));
        snackbar.show();
    }

}
