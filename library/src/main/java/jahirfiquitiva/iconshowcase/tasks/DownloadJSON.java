package jahirfiquitiva.iconshowcase.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.fragments.MainFragment;
import jahirfiquitiva.iconshowcase.fragments.WallpapersFragment;
import jahirfiquitiva.iconshowcase.holders.lists.FullListHolder;
import jahirfiquitiva.iconshowcase.models.WallpaperItem;
import jahirfiquitiva.iconshowcase.utilities.JSONParser;
import jahirfiquitiva.iconshowcase.utilities.utils.Utils;
import timber.log.Timber;

public class DownloadJSON extends AsyncTask<Void, Void, Boolean> {

    private final ArrayList<WallpaperItem> walls = new ArrayList<>();
    private final WeakReference<Context> wrContext;
    private WallpapersFragment fragment;
    private View layout;

    public DownloadJSON(Context context) {
        wrContext = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        if (fragment != null) {
            fragment.refreshContent(wrContext.get());
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        boolean worked;

        JSONObject json = JSONParser.getJSONFromURL(wrContext.get(),
                Utils.getStringFromResources(wrContext.get(),
                        R.string.wallpapers_json_link));

        if (json != null) {
            try {
                // Locate the array name in JSON
                JSONArray jsonarray = json.getJSONArray("wallpapers");

                for (int i = 0; i < jsonarray.length(); i++) {
                    json = jsonarray.getJSONObject(i);
                    // Retrieve JSON Objects

                    String thumbLink, dimens, copyright;
                    boolean downloadable;

                    try {
                        thumbLink = json.getString("thumbnail");
                    } catch (JSONException e) {
                        thumbLink = "null";
                    }

                    try {
                        dimens = json.getString("dimensions");
                    } catch (JSONException e1) {
                        dimens = "null";
                    }

                    try {
                        copyright = json.getString("copyright");
                    } catch (JSONException e2) {
                        copyright = "null";
                    }

                    try {
                        downloadable = json.getString("downloadable").equals("true");
                    } catch (JSONException e3) {
                        downloadable = true;
                    }

                    walls.add(new WallpaperItem(
                            json.getString("name"),
                            json.getString("author"),
                            json.getString("url"),
                            thumbLink,
                            dimens,
                            copyright,
                            downloadable));
                }
            } catch (JSONException e) { //TODO log
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean worked) {
        if (worked) {
            FullListHolder.get().walls().createList(walls);
            if (fragment != null) {
                fragment.setupContent();
            }
            if (wrContext.get() instanceof ShowcaseActivity) {
                if (((ShowcaseActivity) wrContext.get()).getCurrentFragment() instanceof
                        MainFragment) {
                    ((MainFragment) ((ShowcaseActivity) wrContext.get()).getCurrentFragment())
                            .updateAppInfoData();
                }
            }
        } else {
            Timber.d("Something went really wrong while loading wallpapers.");
        }
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment instanceof WallpapersFragment ? (WallpapersFragment) fragment :
                null;
    }

}
