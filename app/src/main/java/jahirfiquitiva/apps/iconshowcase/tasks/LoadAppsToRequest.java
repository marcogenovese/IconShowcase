package jahirfiquitiva.apps.iconshowcase.tasks;

import android.content.Context;
import android.os.AsyncTask;

import jahirfiquitiva.apps.iconshowcase.fragments.RequestsFragment;
import jahirfiquitiva.apps.iconshowcase.models.requests.RequestAppsList;
import jahirfiquitiva.apps.iconshowcase.utilities.Util;

public class LoadAppsToRequest extends AsyncTask<Void, String, Boolean> {

    private Context context;

    long startTime, endTime;

    public static Boolean worked;

    public LoadAppsToRequest(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        startTime = System.currentTimeMillis();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            worked = RequestAppsList.createRequestAppsList(context);
        } catch (Exception e) {
            worked = false;
        }
        return worked;
    }

    @Override
    protected void onPostExecute(Boolean worked) {

        if (RequestsFragment.layout != null) {
            RequestsFragment.setupLayout();
        }

        endTime = System.currentTimeMillis();
        Util.showLog("Apps to Request Task completed in: " + String.valueOf((endTime - startTime) / 1000) + " secs.");
    }
}
