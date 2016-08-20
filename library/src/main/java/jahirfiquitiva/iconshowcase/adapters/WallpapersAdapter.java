/*
 * Copyright (c) 2016.  Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Big thanks to the project contributors. Check them in the repository.
 *
 */

/*
 *
 */

package jahirfiquitiva.iconshowcase.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.models.WallpaperItem;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.color.ColorUtils;

public class WallpapersAdapter extends RecyclerView.Adapter<WallpapersAdapter.WallsHolder> {

    public interface ClickListener {
        void onClick(WallsHolder view, int index, boolean longClick);
    }

    private final Context context;
    private final Preferences mPrefs;
    private int lastPosition = -1;
    private ArrayList<WallpaperItem> wallsList;
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
        final WallpaperItem wallItem = wallsList.get(holder.getAdapterPosition());

        ViewCompat.setTransitionName(holder.wall, "transition" + holder.getAdapterPosition());

        holder.name.setText(wallItem.getWallName());
        holder.authorName.setText(wallItem.getWallAuthor());

        final String wallUrl = wallItem.getWallURL();
        String wallThumb = wallItem.getWallThumbUrl();

        BitmapImageViewTarget target = new BitmapImageViewTarget(holder.wall) {
            @Override
            protected void setResource(Bitmap bitmap) {
                Palette.Swatch wallSwatch = ColorUtils.getPaletteSwatch(bitmap);
                if (mPrefs.getAnimationsEnabled() && (holder.getAdapterPosition() > lastPosition)) {
                    holder.wall.setAlpha(0f);
                    holder.titleBg.setAlpha(0f);
                    holder.wall.setImageBitmap(bitmap);
                    setWallInfoColors(wallSwatch, holder);
                    holder.wall.animate().setDuration(250).alpha(1f).start();
                    holder.titleBg.animate().setDuration(250).alpha(1f).start();
                    lastPosition = holder.getAdapterPosition();
                } else {
                    holder.wall.setImageBitmap(bitmap);
                    setWallInfoColors(wallSwatch, holder);
                }
            }
        };

        if (!(wallThumb.equals("null"))) {
            Glide.with(context)
                    .load(wallUrl)
                    .asBitmap()
                    .thumbnail(
                            Glide.with(context)
                                    .load(wallThumb)
                                    .asBitmap()
                                    .thumbnail(0.3f))
                    .into(target);
        } else {
            Glide.with(context)
                    .load(wallUrl)
                    .asBitmap()
                    .thumbnail(0.4f)
                    .into(target);
        }
    }

    @Override
    public int getItemCount() {
        return wallsList == null ? 0 : wallsList.size();
    }

    public class WallsHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        public final View view;
        public final ImageView wall;
        public final TextView name, authorName;
        public final LinearLayout titleBg;
        private boolean clickable = true;

        WallsHolder(View v) {
            super(v);
            view = v;
            wall = (ImageView) view.findViewById(R.id.wall);
            name = (TextView) view.findViewById(R.id.name);
            authorName = (TextView) view.findViewById(R.id.author);
            titleBg = (LinearLayout) view.findViewById(R.id.titleBg);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickable) {
                clickable = false;
                onWallClick(false);
                reset(); //comment to disable automatic reset
            }

        }

        @Override
        public boolean onLongClick(View v) {
            if (clickable) {
                clickable = false;
                onWallClick(true);
                reset(); //comment to disable automatic reset
            }
            return false;
        }

        private void onWallClick(boolean longClick) {
            int index = getLayoutPosition();
            if (mCallback != null)
                mCallback.onClick(this, index, longClick);
        }

        private void reset() {
            clickable = true;
        }

    }

    private void setWallInfoColors(Palette.Swatch swatch, WallsHolder holder) {
        if (swatch != null) {
            holder.titleBg.setBackgroundColor(swatch.getRgb());
            holder.name.setTextColor(swatch.getBodyTextColor());
            holder.authorName.setTextColor(swatch.getTitleTextColor());
        }
    }

}