package jahirfiquitiva.iconshowcase.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.adapters.ChangelogAdapter;
import jahirfiquitiva.iconshowcase.utilities.ChangelogXmlParser;


/**
 * @author Allan Wang
 */
public class ChangelogDialog extends DialogFragment {

    private static final String changelog_items = "changelog_items";
    private static final String changelog_tag = "changelog_dialog";

    public static void show(final AppCompatActivity context) {
        Fragment frag = context.getSupportFragmentManager().findFragmentByTag(changelog_tag);
        if (frag != null) {
            ((ChangelogDialog) frag).dismiss();
            return;
        }
        final Handler mHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<ChangelogXmlParser.ChangelogItem> items = ChangelogXmlParser.parse(context, R.xml.changelog);
                mHandler.post(new TimerTask() {
                    @Override
                    public void run() {
                        ChangelogDialog.newInstance(items).show(context.getSupportFragmentManager(), changelog_tag);
                    }
                });
            }
        }).start();

    }

    public static ChangelogDialog newInstance(final ArrayList<ChangelogXmlParser.ChangelogItem> items) {
        ChangelogDialog f = new ChangelogDialog();
        if (!items.isEmpty()) {
            Bundle args = new Bundle();
            args.putParcelableArrayList(changelog_items, items);
            f.setArguments(args);
        }
        return f;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title(R.string.changelog_dialog_title)
                .positiveText(R.string.great);

        if (getArguments() == null || !getArguments().containsKey(changelog_items)) {
            builder.content(R.string.empty_changelog);
        } else {
            List<ChangelogXmlParser.ChangelogItem> items = getArguments().getParcelableArrayList(changelog_items);
            builder.adapter(new ChangelogAdapter(items), null);
        }

        return builder.build();
    }
}