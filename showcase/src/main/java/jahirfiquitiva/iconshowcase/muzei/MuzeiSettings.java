package jahirfiquitiva.iconshowcase.muzei;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.mikepenz.materialize.MaterializeBuilder;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.Utils;

public class MuzeiSettings extends AppCompatActivity implements View.OnClickListener {

    private RadioButton minute, hour;
    private NumberPicker numberpicker;
    private Preferences mPrefs;
    private RelativeLayout muzeiLayout;
    private boolean mLastTheme, mLastNavBar;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeUtils.onActivityCreateSetTheme(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ThemeUtils.onActivityCreateSetNavBar(this);
        }

        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.muzei_settings);

        new MaterializeBuilder()
                .withActivity(this)
                .build();

        muzeiLayout = (RelativeLayout) findViewById(R.id.muzeiLayout);

        mPrefs = new Preferences(MuzeiSettings.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(Utils.getStringFromResources(this, R.string.muzei_settings));
        setSupportActionBar(toolbar);

        numberpicker = (NumberPicker) findViewById(R.id.number_picker);
        numberpicker.setMaxValue(100);
        numberpicker.setMinValue(1);

        setDividerColor(numberpicker);

        minute = (RadioButton) findViewById(R.id.minute);
        hour = (RadioButton) findViewById(R.id.hour);

        minute.setOnClickListener(this);
        hour.setOnClickListener(this);

        if (mPrefs.isRotateMinute()) {
            hour.setChecked(false);
            minute.setChecked(true);
            numberpicker.setValue(convertMillisToMinutes(mPrefs.getRotateTime()));
        } else {
            hour.setChecked(true);
            minute.setChecked(false);
            numberpicker.setValue(convertMillisToMinutes(mPrefs.getRotateTime()) / 60);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.muzei_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.save) {
            int rotate_time;
            if (minute.isChecked()) {
                rotate_time = convertMinutesToMillis(numberpicker.getValue());
                mPrefs.setRotateMinute(true);
                mPrefs.setRotateTime(rotate_time);
            } else {
                rotate_time = convertMinutesToMillis(numberpicker.getValue()) * 60;
                mPrefs.setRotateMinute(false);
                mPrefs.setRotateTime(rotate_time);
            }
            Intent intent = new Intent(MuzeiSettings.this, ArtSource.class);
            intent.putExtra("service", "restarted");
            startService(intent);
            Utils.showSimpleSnackbar(context, muzeiLayout,
                    Utils.getStringFromResources(this, R.string.settings_saved), 1);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mLastTheme = ThemeUtils.darkTheme;
        mLastNavBar = ThemeUtils.coloredNavBar;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mLastTheme != ThemeUtils.darkTheme
                || mLastNavBar != ThemeUtils.coloredNavBar) {
            this.recreate();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.minute) {
            if (minute.isChecked()) {
                hour.setChecked(false);
                minute.setChecked(true);
            }

        } else if (i == R.id.hour) {
            if (hour.isChecked()) {
                minute.setChecked(false);
                hour.setChecked(true);
            }

        }
    }

    private int convertMinutesToMillis(int minute) {
        return minute * 60 * 1000;
    }

    private int convertMillisToMinutes(int millis) {
        return millis / 60 / 1000;
    }

    private void setDividerColor(NumberPicker picker) {
        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(picker, ContextCompat.getDrawable(this, R.drawable.numberpicker));
                } catch (IllegalArgumentException | IllegalAccessException | Resources.NotFoundException e) {
                    //Do nothing
                }
                break;
            }
        }
    }
}