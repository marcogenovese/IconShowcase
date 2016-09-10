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

package jahirfiquitiva.iconshowcase.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.events.WallpaperEvent;
import jahirfiquitiva.iconshowcase.tasks.ApplyWallpaper;

/**
 * @author Allan Wang
 */
public class WallpaperDialog extends BaseEventDialog {

    private static final String TAG = "wallpaper_dialog";

    public static void show(final FragmentActivity context, final String url) {
        showBase(context, url, WallpaperEvent.Step.START);
    }

    public static void dismiss(final FragmentActivity context) {
        Fragment frag = context.getSupportFragmentManager().findFragmentByTag(TAG);
        if (frag != null) ((WallpaperDialog) frag).dismiss();
    }

    private static void showBase(final FragmentActivity context, final String url, final WallpaperEvent.Step step) {
        Fragment frag = context.getSupportFragmentManager().findFragmentByTag(TAG);
        if (frag != null) ((WallpaperDialog) frag).dismiss();
        WallpaperDialog.newInstance(url, step).show(context.getSupportFragmentManager(), TAG);
    }

    private static WallpaperDialog newInstance(@NonNull final String url, final WallpaperEvent.Step step) {
        WallpaperDialog f = new WallpaperDialog();
        Bundle args = new Bundle();
        args.putString("wall_url", url);
        args.putSerializable("wall_step", step);
        f.setArguments(args);
        return f;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        WallpaperEvent.Step step = (WallpaperEvent.Step) getArguments().getSerializable("wall_step");
        if (step == null) step = WallpaperEvent.Step.START;

        final MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());

        switch (step) {
            case START:
                builder.title(R.string.apply)
                        .content(R.string.confirm_apply)
                        .positiveText(R.string.apply)
                        .negativeText(android.R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull final DialogAction dialogAction) {
                                showBase(getActivity(), getUrl(), WallpaperEvent.Step.LOADING);
                            }
                        });
                break;
            case LOADING:

                final ApplyWallpaper task = new ApplyWallpaper(getActivity(), getUrl());
                task.execute(); //TODO check if it works multiple times

                builder.content(R.string.downloading_wallpaper)
                        .progress(true, 0)
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() { //TODO set positive text?
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                task.cancel(true);
                                dismiss();
                            }
                        });
                break;

            case APPLYING:

                builder.content(R.string.setting_wall_title)
                        .progress(true, 0)
                        .cancelable(false);

                break;
            default:
                builder.title("Error"); //TODO put to R.string
                break;
        }

        return builder.build();
    }

    private String getUrl() {
        return getArguments().getString("wall_url", "error"); //TODO add default url in case of error?
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void update(WallpaperEvent event) {
        if (event.getNextStep() == WallpaperEvent.Step.FINISH) {
            dismiss();
            return;
        }
        showBase(getActivity(), event.getUrl(), event.getNextStep());
//        switch (event.getStep()) {
//            case LOADING:
//                break;
//            case APPLYING:
//                break;
//        }
    }
}