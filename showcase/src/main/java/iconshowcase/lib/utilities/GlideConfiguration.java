package iconshowcase.lib.utilities;

import android.content.Context;
import android.os.Build;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

public class GlideConfiguration implements GlideModule {

    // High quality will increase loading time and memory usage
    private boolean HIGH_QUALITY = true;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDecodeFormat(HIGH_QUALITY && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ? DecodeFormat.PREFER_ARGB_8888 : DecodeFormat.PREFER_RGB_565);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
    }
}
