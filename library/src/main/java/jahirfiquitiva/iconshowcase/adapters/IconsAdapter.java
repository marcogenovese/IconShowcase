/*
 * Copyright (c) 2017 Jahir Fiquitiva
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
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.dialogs.IconDialog;
import jahirfiquitiva.iconshowcase.holders.IconHolder;
import jahirfiquitiva.iconshowcase.models.IconItem;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import timber.log.Timber;

public class IconsAdapter extends RecyclerView.Adapter<IconHolder> {

    private final Activity context;
    private final Preferences mPrefs;
    private ArrayList<IconItem> iconsList;

    public IconsAdapter(Activity context, ArrayList<IconItem> iconsList) {
        this.context = context;
        this.iconsList = iconsList;
        this.mPrefs = new Preferences(context);
    }

    public void setIcons(ArrayList<IconItem> nList) {
        if (iconsList != null) {
            iconsList.clear();
        } else {
            iconsList = new ArrayList<>();
        }
        if (nList != null)
            iconsList.addAll(nList);
        notifyItemRangeChanged(0, iconsList.size());
    }

    public void clearIconsList() {
        this.iconsList.clear();
    }

    @Override
    public IconHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new IconHolder(inflater.inflate(R.layout.item_icon, parent, false), new IconHolder
                .OnIconClickListener() {
            @Override
            public void onIconClick(IconItem item) {
                iconClick(item);
            }
        });
    }

    @Override
    public void onBindViewHolder(final IconHolder holder, int position) {
        if (position < 0) return;
        holder.setItem(iconsList.get(holder.getAdapterPosition()), mPrefs.getAnimationsEnabled());
    }

    @Override
    public int getItemCount() {
        return iconsList == null ? 0 : iconsList.size();
    }

    @Override
    public void onViewDetachedFromWindow(IconHolder holder) {
        holder.clearAnimation();
    }

    private void iconClick(IconItem item) {
        int resId = item.getResId();
        String name = item.getName().toLowerCase();

        if (((ShowcaseActivity) context).isIconsPicker()) {
            Intent intent = new Intent();
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            } catch (Exception e) {
                Timber.e("Icons Picker error:", e.getLocalizedMessage());
            }
            if (bitmap != null) {
                intent.putExtra("icon", bitmap);
                intent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", resId);
                String bmUri = "android.resource://" + context.getPackageName() + "/" + String
                        .valueOf(resId);
                intent.setData(Uri.parse(bmUri));
                context.setResult(Activity.RESULT_OK, intent);
            } else {
                context.setResult(Activity.RESULT_CANCELED, intent);
            }
            context.finish();
        } else {
            IconDialog.show((FragmentActivity) context, name, resId);
        }
    }

}