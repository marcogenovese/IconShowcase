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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;
import java.util.Locale;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.models.IconItem;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import jahirfiquitiva.iconshowcase.utilities.color.ColorUtils;
import jahirfiquitiva.iconshowcase.views.DebouncedClickListener;
import timber.log.Timber;

public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.IconsHolder> {

    private final Activity context;
    private boolean inChangelog = false;
    private ArrayList<IconItem> iconsList = new ArrayList<>();
    private final Preferences mPrefs;
    private int lastPosition = -1;

    public IconsAdapter (Activity context, ArrayList<IconItem> iconsList) {
        this.context = context;
        this.iconsList = iconsList;
        this.inChangelog = false;
        this.mPrefs = new Preferences(context);
    }

    public IconsAdapter (Activity context, ArrayList<IconItem> iconsList, boolean inChangelog) {
        this.context = context;
        this.iconsList = iconsList;
        this.inChangelog = inChangelog;
        this.mPrefs = new Preferences(context);
    }

    public void setIcons (ArrayList<IconItem> iconsList) {
        if (iconsList != null) {
            this.iconsList.addAll(iconsList);
            this.notifyItemRangeInserted(0, iconsList.size() - 1);
        } else {
            this.iconsList = new ArrayList<>();
            this.notifyItemRangeInserted(0, 0);
        }
    }

    public void clearIconsList () {
        this.iconsList.clear();
    }

    @Override
    public IconsHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new IconsHolder(inflater.inflate(R.layout.item_icon, parent, false));
    }

    @Override
    public void onBindViewHolder (final IconsHolder holder, int position) {

        if (position < 0) return;

        int iconResource = iconsList.get(holder.getAdapterPosition()).getResId();

        if (iconResource == 0) return;

        Glide.with(context)
                .load(iconResource)
                .asBitmap()
                .into(new BitmapImageViewTarget(holder.icon) {
                    @Override
                    protected void setResource (Bitmap resource) {
                        if ((!inChangelog && mPrefs.getAnimationsEnabled()) &&
                                (holder.getAdapterPosition() > lastPosition)) {
                            holder.icon.setAlpha(0f);
                            holder.icon.setImageBitmap(resource);
                            holder.icon.animate().setDuration(250).alpha(1f).start();
                            lastPosition = holder.getAdapterPosition();
                        } else {
                            holder.icon.setImageBitmap(resource);
                        }
                    }
                });

        holder.view.setOnClickListener(new DebouncedClickListener() {
            @Override
            public void onDebouncedClick (View v) {
                iconClick(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount () {
        return iconsList == null ? 0 : iconsList.size();
    }

    @Override
    public void onViewDetachedFromWindow (IconsHolder holder) {
        holder.clearAnimation();
    }

    private void iconClick (int position) {
        int resId = iconsList.get(position).getResId();
        String name = iconsList.get(position).getName().toLowerCase(Locale.getDefault());

        if (ShowcaseActivity.iconsPicker) {
            Intent intent = new Intent();
            Bitmap bitmap = null;

            try {
                bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            } catch (Exception e) {
                Timber.d("Icons Picker error:", e.getLocalizedMessage());
            }

            if (bitmap != null) {
                intent.putExtra("icon", bitmap);
                intent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", resId);
                String bmUri = "android.resource://" + context.getPackageName() + "/" + String.valueOf(resId);
                intent.setData(Uri.parse(bmUri));
                context.setResult(Activity.RESULT_OK, intent);
            } else {
                context.setResult(Activity.RESULT_CANCELED, intent);
            }

            context.finish();

        } else {
            if (!inChangelog) {
                Drawable iconDrawable = ContextCompat.getDrawable(context, resId);
                MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .customView(R.layout.dialog_icon, false)
                        .title(Utils.makeTextReadable(name))
                        .positiveText(R.string.close)
                        .positiveColor(ColorUtils.getColorFromIcon(iconDrawable, context))
                        .show();
                if (dialog.getCustomView() != null) {
                    ImageView dialogIcon = (ImageView) dialog.getCustomView().findViewById(R.id.dialogicon);
                    dialogIcon.setImageResource(resId);
                }
            }
        }
    }

    class IconsHolder extends RecyclerView.ViewHolder {

        final View view;
        final ImageView icon;

        IconsHolder (View v) {
            super(v);
            view = v;
            icon = (ImageView) v.findViewById(R.id.icon_img);
        }

        private void clearAnimation () {
            if (view != null) view.clearAnimation();
            if (icon != null) icon.clearAnimation();
        }

    }

}