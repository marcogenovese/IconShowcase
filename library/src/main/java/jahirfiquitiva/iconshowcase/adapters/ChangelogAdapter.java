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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.utilities.ChangelogXmlParser;

/**
 * @author Allan Wang
 */
public class ChangelogAdapter extends RecyclerView.Adapter<ChangelogAdapter.ChangelogVH> {

    private final List<ChangelogXmlParser.ChangelogItem> mItems;

    public ChangelogAdapter (List<ChangelogXmlParser.ChangelogItem> items) {
        mItems = items;
    }

    @Override
    public ChangelogVH onCreateViewHolder (ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.changelog_content, parent, false);
        return new ChangelogVH(view);
    }

    @Override
    public void onBindViewHolder (ChangelogVH holder, int position) {
        ChangelogXmlParser.ChangelogItem item = mItems.get(position);

        String contentStr = "";
        List<String> points = item.getItems();
        for (int i = 0; i < points.size(); i++) {
            if (i > 0) {
                // No need for new line on the first item
                contentStr += "\n";
            }
            contentStr += "\u2022 ";
            contentStr += points.get(i);
        }
        holder.title.setText(item.getTitle());
        holder.content.setText(contentStr);
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public int getItemCount () {
        return mItems != null ? mItems.size() : 0;
    }

    public static class ChangelogVH extends RecyclerView.ViewHolder {

        final TextView title, content;

        public ChangelogVH (View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.changelog_title);
            content = (TextView) itemView.findViewById(R.id.changelog_content);
        }
    }
}