package jahirfiquitiva.iconshowcase.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.fragments.WallpapersFragment;
import jahirfiquitiva.iconshowcase.holders.FullListHolder;
import jahirfiquitiva.iconshowcase.models.WallpaperItem;
import jahirfiquitiva.iconshowcase.utilities.JSONParser;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import timber.log.Timber;

/**
 * Created by Allan Wang on 2016-09-06.
 */
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
        FullListHolder.get().walls().createList(walls);
        if (fragment != null) {
            if (layout != null) {
                fragment.setupContent();
            } else {
                Timber.d("Wallpapers layout is null");
            }
        } else {
            Timber.d("Wallpapers fragment is null");
        }
    }

    public void setFragmentAndLayout(WallpapersFragment fragment, View layout) {
        this.fragment = fragment;
        this.layout = layout;
    }

}
