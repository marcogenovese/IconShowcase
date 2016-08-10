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

package jahirfiquitiva.iconshowcase.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jahirfiquitiva.iconshowcase.R;


public class ChangelogAdapter extends RecyclerView.Adapter<ChangelogAdapter.ChangelogHolder> {

    private final Context context;
    private final String[][] mChangelog;

    public ChangelogAdapter(Context context, int changelogArray) {
        // Save the context
        this.context = context;
        // Populate the two-dimensional array
        TypedArray typedArray = context.getResources().obtainTypedArray(changelogArray);
        mChangelog = new String[typedArray.length()][];
        for (int i = 0; i < typedArray.length(); i++) {
            int id = typedArray.getResourceId(i, 0);
            if (id > 0) {
                mChangelog[i] = context.getResources().getStringArray(id);
            }
        }
        typedArray.recycle();
    }

    @Override
    public ChangelogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ChangelogHolder(inflater.inflate(R.layout.changelog_content, parent, false));
    }

    @Override
    public void onBindViewHolder(ChangelogHolder holder, int position) {
        String nameStr = mChangelog[position][0];
        String contentStr = "";

        for (int i = 1; i < mChangelog[position].length; i++) {
            if (i > 1) {
                // No need for new line on the first item
                contentStr += "\n";
            }
            contentStr += "\u2022 ";
            contentStr += mChangelog[position][i];
        }

        holder.title.setText(nameStr);
        holder.content.setText(contentStr);
    }

    @Override
    public int getItemCount() {
        return mChangelog == null ? 0 : mChangelog.length;
    }

    class ChangelogHolder extends RecyclerView.ViewHolder {

        View view;
        TextView title, content;

        ChangelogHolder(View v) {
            super(v);
            view = v;

            title = (TextView) view.findViewById(R.id.changelog_title);
            content = (TextView) view.findViewById(R.id.changelog_content);
        }
    }
}
