package jahirfiquitiva.iconshowcase.tasks;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jahirfiquitiva.iconshowcase.models.ZooperWidget;
import jahirfiquitiva.iconshowcase.utilities.Utils;

public class LoadZooperWidgets extends AsyncTask<Void, String, Boolean> {

    private Context context;
    public static ArrayList<ZooperWidget> widgets = new ArrayList<>();
    long startTime, endTime;

    public LoadZooperWidgets(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        startTime = System.currentTimeMillis();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        boolean worked = false;

        try {
            AssetManager assetManager = context.getAssets();
            String[] templates = assetManager.list("templates");
            File previewsFolder = new File(context.getExternalCacheDir(), "WidgetsPreviews");

            if (templates != null && templates.length > 0) {
                if (previewsFolder.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    previewsFolder.delete();
                }
                previewsFolder.mkdirs();
                for (String template : templates) {
                    File widgetFile = new File(previewsFolder, template);
                    String widgetName = getFilenameWithoutExtension(template);
                    Bitmap preview = getWidgetPreviewFromZip(widgetName,
                            assetManager.open("templates/" + template), previewsFolder, widgetFile);
                    if (preview != null) {
                        widgets.add(new ZooperWidget(widgetName, preview));
                    }
                }
                if (widgets.size() == templates.length) {
                    worked = true;
                }
            }
        } catch (Exception e) {
            //Do nothing
            worked = false;
        }

        return worked;
    }

    @Override
    protected void onPostExecute(Boolean worked) {
        endTime = System.currentTimeMillis();
        if (worked) {
            Utils.showLog(context, "Load of widgets task completed successfully in: " + String.valueOf((endTime - startTime)) + " millisecs.");
        }
    }

    private void copyFiles(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[2048];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.flush();
    }

    private String getFilenameWithoutExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * This code was created by Aidan Follestad. Complete credits to him.
     */
    private Bitmap getWidgetPreviewFromZip(String name, InputStream in, File previewsFolder, File widgetFile) {
        OutputStream out = null;
        File preview = new File(previewsFolder, name + ".png");

        try {
            out = new FileOutputStream(widgetFile);

            try {
                copyFiles(in, out);
                in.close();
                out.close();

                if (widgetFile.exists()) {
                    ZipFile zipFile = new ZipFile(widgetFile);
                    Enumeration<? extends ZipEntry> entryEnum = zipFile.entries();
                    ZipEntry entry;
                    while ((entry = entryEnum.nextElement()) != null) {
                        if (entry.getName().endsWith("screen.png")) {
                            InputStream zipIn = null;
                            OutputStream zipOut = null;
                            zipIn = zipFile.getInputStream(entry);
                            zipOut = new FileOutputStream(preview);
                            copyFiles(zipIn, zipOut);
                            zipIn.close();
                            zipOut.close();
                            break;
                        }
                    }
                }

            } catch (IOException e) {
                //Do nothing
            }
        } catch (FileNotFoundException e) {
            //Do nothing
        }

        return BitmapFactory.decodeFile(preview.getAbsolutePath());
    }

}