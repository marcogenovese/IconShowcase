package jahirfiquitiva.iconshowcase.tasks;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.models.WallpaperItem;
import jahirfiquitiva.iconshowcase.holders.WallpapersList;
import jahirfiquitiva.iconshowcase.utilities.JSONParser;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import timber.log.Timber;

/**
 * Created by Allan Wang on 2016-09-06.
 */
public class DownloadJSON extends AsyncTask<Void, Void, Boolean> {

    private final ArrayList<WallpaperItem> walls = new ArrayList<>();
    private final WeakReference<Context> wrContext;

    long startTime, endTime;

    public DownloadJSON(Context context) {
        wrContext = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        Timber.d("Starting DownloadJSON");
        startTime = System.currentTimeMillis();
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

        endTime = System.currentTimeMillis();
        return true;
    }

    @Override
    protected void onPostExecute(Boolean worked) {
        Timber.d("Walls Task completed in: %d milliseconds", (endTime - startTime));
        WallpapersList.createList(walls);
//            if (layout != null) {
//                setupLayout(taskContext.get());
//            } else {
//                Timber.d("Wallpapers layout is null");
//            }
    }
}
