package jahirfiquitiva.apps.iconshowcase.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;

import java.util.ArrayList;
import java.util.Collections;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.apps.iconshowcase.models.IconsLists;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;
import jahirfiquitiva.apps.iconshowcase.utilities.Util;

public class MainFragment extends Fragment {

    private static final String MARKET_URL = "https://play.google.com/store/apps/details?id=";

    private String PlayStoreListing;
    private ArrayList<Integer> icons, finalIconsList = new ArrayList<>();
    private ViewGroup layout;
    private Preferences mPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        mPrefs = new Preferences(getActivity());

        ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (toolbar != null)
            toolbar.setTitle(R.string.app_name);

        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        try {
            layout = (ViewGroup) inflater.inflate(R.layout.main_section, container, false);
        } catch (InflateException e) {

        }

        final ImageView icon1 = (ImageView) layout.findViewById(R.id.iconOne);
        final ImageView icon2 = (ImageView) layout.findViewById(R.id.iconTwo);
        final ImageView icon3 = (ImageView) layout.findViewById(R.id.iconThree);
        final ImageView icon4 = (ImageView) layout.findViewById(R.id.iconFour);

        setupIcons(icon1, icon2, icon3, icon4);

        GridLayout iconsRow = (GridLayout) layout.findViewById(R.id.iconsRow);
        iconsRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupIcons(icon1, icon2, icon3, icon4);
            }
        });

        PlayStoreListing = getActivity().getPackageName();

        ObservableScrollView content = (ObservableScrollView) layout.findViewById(R.id.HomeContent);
        TextView ratebtn = (TextView) layout.findViewById(R.id.rate_button);
        ratebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rate = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL + PlayStoreListing));
                startActivity(rate);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) layout.findViewById(R.id.apply_btn);
        fab.setColorNormal(getResources().getColor(R.color.accent));
        fab.setColorPressed(getResources().getColor(R.color.accent));
        fab.setColorRipple(getResources().getColor(R.color.semitransparent_white));
        fab.show(true);
        fab.attachToScrollView(content);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowcaseActivity.switchFragment(5,
                        Util.getStringFromResources(getActivity(), R.string.section_three),
                        "Apply", (AppCompatActivity) getActivity());
                ShowcaseActivity.drawer.setSelection(5);
            }
        });

        return layout;
    }

    private void setupIcons(final ImageView icon1, final ImageView icon2,
                            final ImageView icon3, final ImageView icon4) {
        icons = IconsLists.getPreviewAL();
        finalIconsList.clear();
        Collections.shuffle(icons);

        int numOfIcons = getResources().getInteger(R.integer.icon_grid_width);
        int i = 0;

        while (i < numOfIcons) {
            finalIconsList.add(icons.get(i));
            i++;
        }

        icon1.setImageResource(finalIconsList.get(0));
        icon2.setImageResource(finalIconsList.get(1));
        icon3.setImageResource(finalIconsList.get(2));
        icon4.setImageResource(finalIconsList.get(3));

        icon1.setVisibility(View.VISIBLE);
        icon2.setVisibility(View.VISIBLE);
        icon3.setVisibility(View.VISIBLE);
        icon4.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPrefs.getAnimationsEnabled()) {
                    YoYo.with(Techniques.Bounce)
                            .duration(700)
                            .playOn(icon1);

                    YoYo.with(Techniques.Bounce)
                            .duration(700)
                            .playOn(icon2);

                    YoYo.with(Techniques.Bounce)
                            .duration(700)
                            .playOn(icon3);

                    YoYo.with(Techniques.Bounce)
                            .duration(700)
                            .playOn(icon4);
                }
            }
        }, 500);

    }

}