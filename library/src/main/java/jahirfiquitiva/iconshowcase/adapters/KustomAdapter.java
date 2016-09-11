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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;

import java.io.File;
import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.models.KustomKomponent;
import jahirfiquitiva.iconshowcase.models.KustomWallpaper;
import jahirfiquitiva.iconshowcase.models.KustomWidget;
import jahirfiquitiva.iconshowcase.tasks.LoadKustomFiles;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import jahirfiquitiva.iconshowcase.views.DebouncedClickListener;

public class KustomAdapter extends SectionedRecyclerViewAdapter<KustomAdapter.KustomHolder> {

    private ArrayList<KustomWidget> widgets;
    private ArrayList<KustomKomponent> komponents;
    private ArrayList<KustomWallpaper> kustomWalls;
    private final Context context;
    private final Drawable wallpaper;

    public KustomAdapter (Context context, Drawable wallpaper) {
        this.context = context;

        this.komponents = LoadKustomFiles.komponents;
        this.kustomWalls = LoadKustomFiles.wallpapers;
        this.widgets = LoadKustomFiles.widgets;

        this.wallpaper = wallpaper;
    }

    public void setLists (ArrayList<KustomWidget> widgets,
                          ArrayList<KustomKomponent> komponents,
                          ArrayList<KustomWallpaper> kustomWalls) {
        if (widgets != null) {
            this.widgets.addAll(widgets);
            this.notifyItemRangeInserted(0, widgets.size() - 1);
        } else {
            this.widgets = new ArrayList<>();
            this.notifyItemRangeInserted(0, 0);
        }
        if (komponents != null) {
            this.komponents.addAll(komponents);
            this.notifyItemRangeInserted(0, komponents.size() - 1);
        } else {
            this.komponents = new ArrayList<>();
            this.notifyItemRangeInserted(0, 0);
        }
        if (kustomWalls != null) {
            this.kustomWalls.addAll(kustomWalls);
            this.notifyItemRangeInserted(0, kustomWalls.size() - 1);
        } else {
            this.kustomWalls = new ArrayList<>();
            this.notifyItemRangeInserted(0, 0);
        }
    }

    @Override
    public KustomHolder onCreateViewHolder (ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(i == VIEW_TYPE_HEADER ?
                R.layout.kustom_section_header : R.layout.item_widget_preview, parent, false);
        return new KustomHolder(view);
    }

    @Override
    public int getSectionCount () {
        return 3;
    }

    @Override
    public int getItemCount (int section) {
        switch (section) {
            case 0:
                return komponents != null ? komponents.size() : 0;
            case 1:
                return kustomWalls != null ? kustomWalls.size() : 0;
            case 2:
                return widgets != null ? widgets.size() : 0;
            default:
                return 0;
        }
    }

    public int getHeadersBeforePosition (int position) {
        int headers = 0;

        for (int i = 0; i < position; i++) {
            if (isHeader(i)) {
                headers += 1;
            }
        }

        return headers;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder (KustomHolder holder, int section) {
        switch (section) {
            case 0:
                holder.sectionTitle.setText("Komponents");
                break;
            case 1:
                holder.sectionTitle.setText("Wallpapers");
                break;
            case 2:
                holder.sectionTitle.setText("Widgets");
                break;
            default:
                holder.sectionTitle.setText("Empty Assets");
                break;
        }
    }

    @Override
    public void onBindViewHolder (KustomHolder holder, int section, final int relativePosition, int absolutePosition) {
        holder.background.setImageDrawable(wallpaper);
        String filePath;
        switch (section) {
            case 0:
                filePath = komponents.get(relativePosition).getPreviewPath();
                break;
            case 1:
                holder.itemView.setOnClickListener(new DebouncedClickListener() {
                    @Override
                    public void onDebouncedClick (View v) {
                        if (Utils.isAppInstalled(context, "org.kustom.wallpaper")) {
                            context.startActivity(kustomWalls.get(relativePosition).getKLWPIntent(context));
                        }
                    }
                });

                switch (context.getResources().getConfiguration().orientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        filePath = kustomWalls.get(relativePosition).getPreviewPath();
                        break;
                    case Configuration.ORIENTATION_LANDSCAPE:
                        filePath = kustomWalls.get(relativePosition).getPreviewPathLand();
                        break;
                    default:
                        filePath = kustomWalls.get(relativePosition).getPreviewPath();
                        break;
                }
                break;
            case 2:
                holder.itemView.setOnClickListener(new DebouncedClickListener() {
                    @Override
                    public void onDebouncedClick (View v) {
                        if (Utils.isAppInstalled(context, "org.kustom.widget")) {
                            context.startActivity(widgets.get(relativePosition).getKWGTIntent(context));
                        }
                    }
                });

                switch (context.getResources().getConfiguration().orientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        filePath = widgets.get(relativePosition).getPreviewPath();
                        break;
                    case Configuration.ORIENTATION_LANDSCAPE:
                        filePath = widgets.get(relativePosition).getPreviewPathLand();
                        break;
                    default:
                        filePath = widgets.get(relativePosition).getPreviewPath();
                        break;
                }
                break;
            default:
                filePath = null;
                break;
        }

        if (filePath != null) {
            Glide.with(context)
                    .load(new File(filePath))
                    .priority(Priority.HIGH)
                    .into(holder.widget);
        }
    }

    class KustomHolder extends RecyclerView.ViewHolder {

        final ImageView background;
        final ImageView widget;
        final TextView sectionTitle;

        public KustomHolder (View itemView) {
            super(itemView);
            background = (ImageView) itemView.findViewById(R.id.wall);
            widget = (ImageView) itemView.findViewById(R.id.preview);
            sectionTitle = (TextView) itemView.findViewById(R.id.kustom_section_title);
        }

    }

}