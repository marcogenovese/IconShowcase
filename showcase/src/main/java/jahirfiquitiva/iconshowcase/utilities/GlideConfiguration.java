/*
 *
 */

package jahirfiquitiva.iconshowcase.utilities;

import android.content.Context;
import android.os.Build;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

import jahirfiquitiva.iconshowcase.R;

public class GlideConfiguration implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDecodeFormat(context.getResources().getBoolean(R.bool.high_definition_walls) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ? DecodeFormat.PREFER_ARGB_8888 : DecodeFormat.PREFER_RGB_565);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
    }
}
