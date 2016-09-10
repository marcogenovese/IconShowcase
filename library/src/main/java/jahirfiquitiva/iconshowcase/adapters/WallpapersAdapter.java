/*
 * Copyright (c) 2016 Jahir Fiquitiva
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
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.iconshowcase.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
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
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.FileOutputStream;
import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.AltWallpaperViewerActivity;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.activities.WallpaperViewerActivity;
import jahirfiquitiva.iconshowcase.dialogs.WallpaperDialog;
import jahirfiquitiva.iconshowcase.models.WallpaperItem;
import jahirfiquitiva.iconshowcase.models.WallpapersList;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import jahirfiquitiva.iconshowcase.utilities.color.ColorUtils;
import timber.log.Timber;

public class WallpapersAdapter extends RecyclerView.Adapter<WallpapersAdapter.WallsHolder> {

    private final FragmentActivity activity;
    private int lastPosition = -1;
    private final ArrayList<WallpaperItem> wallsList;
    private final boolean animationEnabled;

    public WallpapersAdapter (FragmentActivity activity, ArrayList<WallpaperItem> wallsList) {
        this.activity = activity;
        this.wallsList = wallsList;
        animationEnabled = new Preferences(activity).getAnimationsEnabled();
    }

    @Override
    public WallsHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        return new WallsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper, parent, false));
    }

    @Override
    public void onBindViewHolder (final WallsHolder holder, int position) {
        holder.titleBg.setBackgroundColor(
                ColorUtils.changeAlpha(
                        ContextCompat.getColor(activity,
                                ThemeUtils.darkOrLight(R.color.card_light_background, R.color.card_dark_background)),
                        0.65f));
        holder.name.setTextColor(ColorUtils.getMaterialPrimaryTextColor(!ThemeUtils.darkTheme));
        holder.authorName.setTextColor(ColorUtils.getMaterialSecondaryTextColor(!ThemeUtils.darkTheme));

        final WallpaperItem wallItem = wallsList.get(holder.getAdapterPosition());

        ViewCompat.setTransitionName(holder.wall, "transition" + holder.getAdapterPosition());

        holder.name.setText(wallItem.getWallName());
        holder.authorName.setText(wallItem.getWallAuthor());

        final String wallURL = wallItem.getWallURL(), wallThumbURL = wallItem.getWallThumbURL();

        BitmapImageViewTarget target = new BitmapImageViewTarget(holder.wall) {
            @Override
            protected void setResource (Bitmap bitmap) {
                Palette.Swatch wallSwatch = ColorUtils.getPaletteSwatch(bitmap);
                if (animationEnabled && (holder.getAdapterPosition() > lastPosition)) {
                    holder.wall.setAlpha(0f);
                    holder.titleBg.setAlpha(0f);
                    holder.wall.setImageBitmap(bitmap);
                    if (wallSwatch != null) setColors(wallSwatch.getRgb(), holder);
                    holder.wall.animate().setDuration(250).alpha(1f).start();
                    holder.titleBg.animate().setDuration(250).alpha(1f).start();
                    lastPosition = holder.getAdapterPosition();
                } else {
                    holder.wall.setImageBitmap(bitmap);
                    if (wallSwatch != null) setColors(wallSwatch.getRgb(), holder);
                }
            }
        };

        if (!(wallThumbURL.equals("null"))) {
            Glide.with(activity)
                    .load(wallURL)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .priority(Priority.HIGH)
                    .thumbnail(
                            Glide.with(activity)
                                    .load(wallThumbURL)
                                    .asBitmap()
                                    .priority(Priority.IMMEDIATE)
                                    .thumbnail(0.3f))
                    .into(target);
        } else {
            Glide.with(activity)
                    .load(wallURL)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .priority(Priority.HIGH)
                    .thumbnail(0.5f)
                    .into(target);
        }

    }

    private void setColors (int color, WallsHolder holder) {
        if (holder.titleBg != null && color != 0) {
            holder.titleBg.setBackgroundColor(color);
            if (holder.name != null) {
                holder.name.setTextColor(ColorUtils.getMaterialPrimaryTextColor(!ColorUtils.isLightColor(color)));
            }
            if (holder.authorName != null) {
                holder.authorName.setTextColor(ColorUtils.getMaterialPrimaryTextColor(!ColorUtils.isLightColor(color)));
            }
        }
    }

    @Override
    public int getItemCount () {
        return wallsList == null ? 0 : wallsList.size();
    }

    public class WallsHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                                                                        View.OnLongClickListener {

        public final View view;
        public final ImageView wall;
        public final TextView name, authorName;
        public final LinearLayout titleBg;
        private boolean clickable = true;

        WallsHolder (View v) {
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
        public void onClick (View v) {
            if (clickable) {
                clickable = false;
                onWallClick(false);
                reset(); //comment to disable automatic reset //TODO shouldn't all clicks be paused when applying?
            }

        }

        @Override
        public boolean onLongClick (View v) {
            if (clickable) {
                clickable = false;
                onWallClick(true);
                reset(); //comment to disable automatic reset
            }
            return false;
        }

        private void onWallClick (boolean longClick) {
            final WallpaperItem item = wallsList.get(getLayoutPosition());

            if (ShowcaseActivity.wallsPicker || longClick) {
                WallpaperDialog.show(activity, item.getWallURL());
            } else {
                final Intent intent = new Intent(activity,
                        activity.getResources().getBoolean(R.bool.alternative_viewer) ? AltWallpaperViewerActivity.class :
                                WallpaperViewerActivity.class);

                intent.putExtra("item", item);
                intent.putExtra("transitionName", ViewCompat.getTransitionName(wall));

                Bitmap bitmap;

                if (wall.getDrawable() != null) {
                    bitmap = Utils.drawableToBitmap(wall.getDrawable());

                    try {
                        String filename = "temp.png";
                        FileOutputStream stream = activity.openFileOutput(filename, Context.MODE_PRIVATE);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        stream.close();
                        intent.putExtra("image", filename);
                    } catch (Exception e) {
                        Timber.d("Error getting drawable", e.getLocalizedMessage());
                    }

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, wall, ViewCompat.getTransitionName(wall));
                    activity.startActivity(intent, options.toBundle());
                } else {
                    activity.startActivity(intent);
                }
            }
        }

        private void reset () {
            clickable = true;
        }

    }

}