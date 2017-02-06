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

package jahirfiquitiva.iconshowcase.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pitchedapps.capsule.library.custom.CapsuleCoordinatorLayout;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.config.Config;
import jahirfiquitiva.iconshowcase.dialogs.ISDialogs;
import jahirfiquitiva.iconshowcase.services.MuzeiArtSourceService;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.color.ToolbarColorizer;
import jahirfiquitiva.iconshowcase.utilities.utils.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.utils.Utils;
import jahirfiquitiva.iconshowcase.views.DebouncedClickListener;
import jahirfiquitiva.iconshowcase.views.FixedElevationAppBarLayout;

@SuppressWarnings("ResourceAsColor")
public class MuzeiSettings extends AppCompatActivity {

    private RadioButton minute, hour;
    private NumberPicker numberpicker;
    private Preferences mPrefs;
    private Context context;
    private CapsuleCoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeUtils.onActivityCreateSetTheme(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ThemeUtils.onActivityCreateSetNavBar(this);
        }

        super.onCreate(savedInstanceState);

        context = this;

        mPrefs = new Preferences(this);

        int iconsColor = ThemeUtils.darkOrLight(this, R.color.toolbar_text_dark, R.color
                .toolbar_text_light);

        setContentView(R.layout.muzei_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CapsuleCoordinatorLayout) findViewById(R.id.muzeiLayout);
        coordinatorLayout.setScrollAllowed(false);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById
                (R.id.collapsingToolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(iconsColor);
        collapsingToolbarLayout.setTitle(Utils.getStringFromResources(this, R.string
                .muzei_settings));

        FixedElevationAppBarLayout appBarLayout = (FixedElevationAppBarLayout) findViewById(R.id
                .appbar);
        appBarLayout.setExpanded(false, false);

        numberpicker = (NumberPicker) findViewById(R.id.number_picker);
        numberpicker.setMaxValue(100);
        numberpicker.setMinValue(1);

        setDividerColor(numberpicker);

        minute = (RadioButton) findViewById(R.id.minute);
        hour = (RadioButton) findViewById(R.id.hour);

        if (mPrefs.isDashboardWorking()) {
            minute.setOnClickListener(new DebouncedClickListener() {
                @Override
                public void onDebouncedClick(View v) {
                    hour.setChecked(false);
                    minute.setChecked(true);
                }
            });

            hour.setOnClickListener(new DebouncedClickListener() {
                @Override
                public void onDebouncedClick(View v) {
                    minute.setChecked(false);
                    hour.setChecked(true);
                }
            });

            if (mPrefs.isRotateMinute()) {
                hour.setChecked(false);
                minute.setChecked(true);
                numberpicker.setValue(Utils.convertMillisToMinutes(mPrefs.getRotateTime()));
            } else {
                hour.setChecked(true);
                minute.setChecked(false);
                numberpicker.setValue(Utils.convertMillisToMinutes(mPrefs.getRotateTime()) / 60);
            }
        } else {
            showShallNotPassDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int iconsColor = ThemeUtils.darkOrLight(this, R.color.toolbar_text_dark, R.color
                .toolbar_text_light);
        ToolbarColorizer.colorizeToolbar(toolbar, iconsColor);
        // TODO: Run license checker
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.muzei, menu);
        MenuItem save = menu.findItem(R.id.save);
        int iconsColor = ThemeUtils.darkOrLight(this, R.color.toolbar_text_dark, R.color
                .toolbar_text_light);
        ToolbarColorizer.tintSaveIcon(save, this, iconsColor);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (mPrefs.isDashboardWorking()) {
            if (i == R.id.save) {
                String timeText;
                int rotate_time;
                if (minute.isChecked()) {
                    rotate_time = Utils.convertMinutesToMillis(numberpicker.getValue());
                    mPrefs.setRotateMinute(true);
                    mPrefs.setRotateTime(rotate_time);
                    timeText = String.valueOf(Utils.convertMillisToMinutes(rotate_time)) + " " +
                            Utils.getStringFromResources(context, R.string.minutes).toLowerCase();
                } else {
                    rotate_time = Utils.convertMinutesToMillis(numberpicker.getValue()) * 60;
                    mPrefs.setRotateMinute(false);
                    mPrefs.setRotateTime(rotate_time);
                    timeText = String.valueOf(Utils.convertMillisToMinutes(rotate_time) / 60) + "" +
                            " " +
                            Utils.getStringFromResources(context, R.string.hours).toLowerCase();
                }
                Intent intent = new Intent(MuzeiSettings.this, MuzeiArtSourceService.class);
                intent.putExtra("service", "restarted");
                startService(intent);
                showSnackBarAndFinish(coordinatorLayout,
                        getResources().getString(R.string.settings_saved, timeText));
                return true;
            }
        } else {
            showShallNotPassDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void setDividerColor(NumberPicker picker) {
        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(picker, ContextCompat.getDrawable(this, R.drawable.numberpicker));
                } catch (IllegalArgumentException | IllegalAccessException | Resources
                        .NotFoundException e) {
                    //Do nothing
                }
                break;
            }
        }
    }

    private void showShallNotPassDialog() {
        ISDialogs.showShallNotPassDialog(this,
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull
                            DialogAction dialogAction) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Config
                                .MARKET_URL + getPackageName()));
                        startActivity(browserIntent);
                    }
                }, new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull
                            DialogAction dialogAction) {
                        finish();
                    }
                }, new MaterialDialog.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                }, new MaterialDialog.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
    }

    private void showSnackBarAndFinish(View location, String text) {
        Utils.snackbar(context, location, text,
                Snackbar.LENGTH_LONG).addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                finish();
            }
        }).show();
    }

}