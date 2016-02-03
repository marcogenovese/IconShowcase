package jahirfiquitiva.apps.iconshowcase.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Environment;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyFilesToStorage extends AsyncTask<Void, String, Boolean> {
    private Context context;
    private Activity activity;
    private MaterialDialog dialog;
    private String folder;

    public CopyFilesToStorage(Context context, MaterialDialog dialog, String folder) {
        this.activity = (Activity) context;
        this.context = context;
        this.dialog = dialog;
        this.folder = folder;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean worked;
        try {
            AssetManager assetManager = context.getAssets();
            String[] files = null;
            try {
                files = assetManager.list(folder);
            } catch (IOException e) {
            }

            for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(folder + "/" + filename);
                    out = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/ZooperWidget/" + folder + "/" + filename);
                    copyFiles(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (Exception e) {
                }
            }
            worked = true;
        } catch (Exception e2) {
            worked = false;
        }
        return worked;
    }

    @Override
    protected void onPostExecute(Boolean worked) {
        dialog.dismiss();
    }

    private void copyFiles(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

}
