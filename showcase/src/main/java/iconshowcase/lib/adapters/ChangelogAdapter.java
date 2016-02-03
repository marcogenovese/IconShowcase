package iconshowcase.lib.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import iconshowcase.lib.R;

public class ChangelogAdapter extends BaseAdapter {

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
    public int getCount() {
        return mChangelog == null ? 0 : mChangelog.length;
    }

    @Override
    public String[] getItem(int position) {
        return mChangelog[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.changelog_content, parent, false);
            convertView.setClickable(false);
            convertView.setLongClickable(false);
            convertView.setFocusable(false);
            convertView.setFocusableInTouchMode(false);
            convertView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }

        TextView title = (TextView) convertView.findViewById(R.id.changelog_title);
        TextView content = (TextView) convertView.findViewById(R.id.changelog_content);
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

        title.setText(nameStr);
        title.setClickable(false);
        title.setLongClickable(false);
        title.setFocusable(false);
        title.setFocusableInTouchMode(false);
        title.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

        content.setText(contentStr);
        content.setClickable(false);
        content.setLongClickable(false);
        content.setFocusable(false);
        content.setFocusableInTouchMode(false);
        content.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

        return convertView;
    }
}
