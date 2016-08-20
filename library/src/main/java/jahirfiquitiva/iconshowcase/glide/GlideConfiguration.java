/*
 * Copyright (c) 2016. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

/*
 *
 */

package jahirfiquitiva.iconshowcase.glide;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

public class GlideConfiguration implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        boolean runsMinSDK = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        boolean lowRAMDevice;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            lowRAMDevice = activityManager.isLowRamDevice();
        } else {
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memInfo);
            lowRAMDevice = memInfo.lowMemory;
        }

        builder.setDecodeFormat(runsMinSDK ?
                lowRAMDevice ? DecodeFormat.PREFER_RGB_565 : DecodeFormat.PREFER_ARGB_8888 :
                DecodeFormat.PREFER_RGB_565);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
    }
}
