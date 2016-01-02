/*
 * Copyright (c) 2015. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.apps.iconshowcase.tasks;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.models.IconsLists;
import jahirfiquitiva.apps.iconshowcase.utilities.Util;

public class LoadIconsLists extends AsyncTask<Void, String, Boolean> {

    private Context context;
    private ArrayList<Integer> sectionA, sectionB, sectionC, sectionD, sectionE, sectionF, previewAL;
    private List<String> listA, listB, listC, listD, listE, listF, previewL;
    long startTime, endTime;

    public LoadIconsLists(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        startTime = System.currentTimeMillis();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        Resources r = context.getResources();
        String p = context.getPackageName();

        // PREVIEWS
        previewAL = new ArrayList<>();
        String[] prev = r.getStringArray(R.array.preview);
        previewL = sortLists(prev);
        for (String extra : previewL) {
            int res = r.getIdentifier(extra, "drawable", p);
            if (res != 0) {
                final int thumbRes = r.getIdentifier(extra, "drawable", p);
                if (thumbRes != 0) {
                    previewAL.add(thumbRes);
                }
            }
        }
        IconsLists.setPreviewAL(previewAL);
        IconsLists.setPreviewL(previewL);

        // SECTION A
        sectionA = new ArrayList<>();
        String[] arrayA = r.getStringArray(R.array.latest);
        listA = sortLists(arrayA);
        for (String extra : listA) {
            int res = r.getIdentifier(extra, "drawable", p);
            if (res != 0) {
                final int thumbRes = r.getIdentifier(extra, "drawable", p);
                if (thumbRes != 0) {
                    sectionA.add(thumbRes);
                }
            }
        }
        IconsLists.setSectionA(sectionA);
        IconsLists.setListA(listA);

        // SECTION B
        sectionB = new ArrayList<>();
        String[] arrayB = r.getStringArray(R.array.system);
        listB = sortLists(arrayB);
        for (String extra : listB) {
            int res = r.getIdentifier(extra, "drawable", p);
            if (res != 0) {
                final int thumbRes = r.getIdentifier(extra, "drawable", p);
                if (thumbRes != 0) {
                    sectionB.add(thumbRes);
                }
            }
        }
        IconsLists.setSectionB(sectionB);
        IconsLists.setListB(listB);

        // SECTION C
        sectionC = new ArrayList<>();
        String[] arrayC = r.getStringArray(R.array.google);
        listC = sortLists(arrayC);
        for (String extra : listC) {
            int res = r.getIdentifier(extra, "drawable", p);
            if (res != 0) {
                final int thumbRes = r.getIdentifier(extra, "drawable", p);
                if (thumbRes != 0) {
                    sectionC.add(thumbRes);
                }
            }
        }
        IconsLists.setSectionC(sectionC);
        IconsLists.setListC(listC);

        // SECTION D
        sectionD = new ArrayList<>();
        String[] arrayD = r.getStringArray(R.array.games);
        listD = sortLists(arrayD);
        for (String extra : listD) {
            int res = r.getIdentifier(extra, "drawable", p);
            if (res != 0) {
                final int thumbRes = r.getIdentifier(extra, "drawable", p);
                if (thumbRes != 0) {
                    sectionD.add(thumbRes);
                }
            }
        }
        IconsLists.setSectionD(sectionD);
        IconsLists.setListD(listD);

        // SECTION E
        sectionE = new ArrayList<>();
        String[] arrayE = r.getStringArray(R.array.icon_pack);
        listE = sortLists(arrayE);
        for (String extra : listE) {
            int res = r.getIdentifier(extra, "drawable", p);
            if (res != 0) {
                final int thumbRes = r.getIdentifier(extra, "drawable", p);
                if (thumbRes != 0) {
                    sectionE.add(thumbRes);
                }
            }
        }
        IconsLists.setSectionE(sectionE);
        IconsLists.setListE(listE);

        // SECTION F
        sectionF = new ArrayList<>();
        String[] arrayF = r.getStringArray(R.array.latest);
        listF = sortLists(arrayF);
        for (String extra : listF) {
            int res = r.getIdentifier(extra, "drawable", p);
            if (res != 0) {
                final int thumbRes = r.getIdentifier(extra, "drawable", p);
                if (thumbRes != 0) {
                    sectionF.add(thumbRes);
                }
            }
        }
        IconsLists.setSectionF(sectionF);
        IconsLists.setListF(listF);

        return null;
    }

    @Override
    protected void onPostExecute(Boolean worked) {
        endTime = System.currentTimeMillis();
        Util.showLog("Load of icons task completed succesfully in: " + String.valueOf((endTime - startTime)) + " millisecs.");
    }

    private List<String> sortLists(String[] array) {
        List<String> list = new ArrayList<String>(Arrays.asList(array));
        Collections.sort(list);
        return list;
    }

}
