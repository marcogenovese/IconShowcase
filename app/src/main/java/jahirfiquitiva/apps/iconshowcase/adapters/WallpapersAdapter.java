package jahirfiquitiva.apps.iconshowcase.adapters;

import android.content.Context;
import android.support.v4.view.ViewCompat;
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

import java.util.ArrayList;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.models.WallpaperItem;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;
import jahirfiquitiva.apps.iconshowcase.utilities.Utils;

public class WallpapersAdapter extends RecyclerView.Adapter<WallpapersAdapter.WallsHolder> {

    public interface ClickListener {
        void onClick(WallsHolder view, int index, boolean longClick);
    }

    private final Context context;
    private Preferences mPrefs;

    private ArrayList<WallpaperItem> wallsList;

    private boolean USE_OF_PALETTE = true, MODIFY_TEXT_COLORS_WITH_PALETTE = false;

    /*
    * Palette styles: (use this format only)
    * VIBRANT
    * VIBRANT_LIGHT
    * VIBRANT_DARK
    * MUTED
    * MUTED_LIGHT
    * MUTED_DARK
     */
    private String PALETTE_STYLE = "VIBRANT";

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

            if (USE_OF_PALETTE) {
                Glide.with(context)
                        .load(wallUrl)
                        .centerCrop()
                        .listener(Utils.getGlidePalette(PALETTE_STYLE, MODIFY_TEXT_COLORS_WITH_PALETTE,
                                mPrefs, wallUrl, holder))
                        .into(holder.wall);
            } else {
                Glide.with(context)
                        .load(wallUrl)
                        .centerCrop()
                        .into(holder.wall);
            }

        } else {

            if (USE_OF_PALETTE) {
                Glide.with(context)
                        .load(wallUrl)
                        .centerCrop()
                        .dontAnimate()
                        .listener(Utils.getGlidePalette(PALETTE_STYLE, MODIFY_TEXT_COLORS_WITH_PALETTE,
                                mPrefs, wallUrl, holder))
                        .into(holder.wall);
            } else {
                Glide.with(context)
                        .load(wallUrl)
                        .centerCrop()
                        .dontAnimate()
                        .into(holder.wall);
            }

        }

    }

    @Override
    public int getItemCount() {
        return wallsList == null ? 0 : wallsList.size();
    }

    public class WallsHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

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
            titleBg = (LinearLayout) view.findViewById(R.id.titleBg);
            layout = (FrameLayout) view.findViewById(R.id.wall_frame_layout);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int index = getLayoutPosition();
            if (mCallback != null)
                mCallback.onClick(this, index, false);
        }

        @Override
        public boolean onLongClick(View v) {
            int index = getLayoutPosition();
            if (mCallback != null)
                mCallback.onClick(this, index, true);
            return false;
        }
    }

}