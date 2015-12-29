package jahirfiquitiva.apps.iconshowcase.adapters;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.models.WallpaperItem;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;

public class WallpapersAdapter extends RecyclerView.Adapter<WallpapersAdapter.WallsHolder> {

    public interface ClickListener {
        void onClick(WallsHolder view, int index);
    }

    private final Context context;
    private Preferences mPrefs;

    private ArrayList<WallpaperItem> wallsList;

    private boolean usePalette = true;
    private boolean modifyTextsColor = false;

    private final ClickListener mCallback;

    public WallpapersAdapter(Context context, ClickListener callback) {
        this.context = context;
        this.mCallback = callback;
        this.mPrefs = new Preferences(context);
    }

    public void setData(ArrayList<WallpaperItem> wallsList) {
        this.wallsList = wallsList;
        notifyDataSetChanged();
    }

    @Override
    public WallsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new WallsHolder(inflater.inflate(R.layout.item_wallpaper, parent, false));
    }

    @Override
    public void onBindViewHolder(final WallsHolder holder, int position) {

        WallpaperItem wallItem = wallsList.get(position);

        ViewCompat.setTransitionName(holder.wall, "transition" + position);

        holder.name.setText(wallItem.getWallName());
        holder.authorName.setText(wallItem.getWallAuthor());
        final String wallUrl = wallItem.getWallURL();

        if (mPrefs.getAnimationsEnabled()) {
            Glide.with(context)
                    .load(wallUrl)
                    .placeholder(R.drawable.placeholder)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new GlideDrawableImageViewTarget(holder.wall) {
                        @Override
                        public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                            holder.progressBar.setVisibility(View.GONE);
                            if (usePalette) {
                                Palette p = new Palette.Builder(((GlideBitmapDrawable) drawable).getBitmap()).generate();
                                if (p != null) {
                                    Palette.Swatch wallSwatch = p.getVibrantSwatch();
                                    if (wallSwatch != null) {
                                        holder.titleBg.setBackgroundColor(wallSwatch.getRgb());
                                        if (modifyTextsColor) {
                                            holder.name.setTextColor(wallSwatch.getTitleTextColor());
                                            holder.authorName.setTextColor(wallSwatch.getTitleTextColor());
                                        }
                                    }
                                }
                            }
                            super.onResourceReady(drawable, anim);
                        }
                    });
        } else {
            Glide.with(context)
                    .load(wallUrl)
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new GlideDrawableImageViewTarget(holder.wall) {
                        @Override
                        public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                            holder.progressBar.setVisibility(View.GONE);
                            if (usePalette) {
                                Palette p = new Palette.Builder(((GlideBitmapDrawable) drawable).getBitmap()).generate();
                                if (p != null) {
                                    Palette.Swatch wallSwatch = p.getVibrantSwatch();
                                    if (wallSwatch != null) {
                                        holder.titleBg.setBackgroundColor(wallSwatch.getRgb());
                                        if (modifyTextsColor) {
                                            holder.name.setTextColor(wallSwatch.getTitleTextColor());
                                            holder.authorName.setTextColor(wallSwatch.getTitleTextColor());
                                        }
                                    }
                                }
                            }
                            super.onResourceReady(drawable, anim);
                        }
                    });
        }

    }

    @Override
    public int getItemCount() {
        return wallsList == null ? 0 : wallsList.size();
    }

    public class WallsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final View view;
        public final ImageView wall;
        public final TextView name, authorName;
        public final ProgressBar progressBar;
        public final LinearLayout titleBg;
        public FrameLayout layout;

        WallsHolder(View v) {
            super(v);
            view = v;
            wall = (ImageView) view.findViewById(R.id.wall);
            name = (TextView) view.findViewById(R.id.name);
            authorName = (TextView) view.findViewById(R.id.author);
            progressBar = (ProgressBar) view.findViewById(R.id.progress);
            titleBg = (LinearLayout) view.findViewById(R.id.titlebg);
            layout = (FrameLayout) view.findViewById(R.id.wall_frame_layout);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int index = getLayoutPosition();
            if (mCallback != null)
                mCallback.onClick(this, index);
        }
    }
}