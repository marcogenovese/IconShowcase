package jahirfiquitiva.apps.iconshowcase.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;
import jahirfiquitiva.apps.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.apps.iconshowcase.utilities.Util;

public class MainFragment extends Fragment {

    private static final String MARKET_URL = "https://play.google.com/store/apps/details?id=";

    private String PlayStoreListing;
    private ViewGroup layout;
    private Preferences mPrefs;

    private Drawable iconsDrawable, wallsDrawable, widgetsDrawable, playStoreDrawable;
    private ImageView iconsIV, wallsIV, widgetsIV, playStoreIV;

    private String themedIcons = "600",
            availableWallpapers = "100",
            includedWidgets = "-2";

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        mPrefs = new Preferences(getActivity());

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

        ShowcaseActivity.setupIcons(ShowcaseActivity.icon1, ShowcaseActivity.icon2,
                ShowcaseActivity.icon3, ShowcaseActivity.icon4, getActivity());

        ShowcaseActivity.animateIcons(ShowcaseActivity.icon1, ShowcaseActivity.icon2,
                ShowcaseActivity.icon3, ShowcaseActivity.icon4);

        PlayStoreListing = getActivity().getPackageName();

        TextView iconsT = (TextView) layout.findViewById(R.id.text_themed_icons);
        iconsT.setText(getActivity().getResources().getString(R.string.themed_icons, themedIcons));

        TextView wallsT = (TextView) layout.findViewById(R.id.text_available_wallpapers);
        wallsT.setText(getActivity().getResources().getString(R.string.available_wallpapers, availableWallpapers));

        TextView widgetsT = (TextView) layout.findViewById(R.id.text_included_widgets);
        widgetsT.setText(getActivity().getResources().getString(R.string.included_widgets, includedWidgets));

        iconsIV = (ImageView) layout.findViewById(R.id.icon_themed_icons);
        wallsIV = (ImageView) layout.findViewById(R.id.icon_available_wallpapers);
        widgetsIV = (ImageView) layout.findViewById(R.id.icon_included_widgets);
        playStoreIV = (ImageView) layout.findViewById(R.id.icon_more_apps);

        setupIcons(getActivity());

        LinearLayout moreApps = (LinearLayout) layout.findViewById(R.id.moreApps);
        moreApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.openLinkInChromeCustomTab(getActivity(),
                        Util.getStringFromResources(getActivity(), R.string.iconpack_author_playstore));
            }
        });

        //ObservableScrollView content = (ObservableScrollView) layout.findViewById(R.id.HomeContent);
        AppCompatButton ratebtn = (AppCompatButton) layout.findViewById(R.id.rate_button);
        ratebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rate = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL + PlayStoreListing));
                startActivity(rate);
            }
        });

        AppCompatButton iconsbtn = (AppCompatButton) layout.findViewById(R.id.icons_button);
        iconsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowcaseActivity.drawerItemClick(2);
                ShowcaseActivity.drawer.setSelection(2);
            }
        });

        return layout;
    }

    private void setupIcons(Context context) {
        final int light = context.getResources().getColor(android.R.color.white);
        final int dark = context.getResources().getColor(R.color.grey);

        iconsDrawable = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_android_alt)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        wallsDrawable = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_collection_image)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        widgetsDrawable = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_widgets)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        playStoreDrawable = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_case_play)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        iconsIV.setImageDrawable(iconsDrawable);
        wallsIV.setImageDrawable(wallsDrawable);
        widgetsIV.setImageDrawable(widgetsDrawable);
        playStoreIV.setImageDrawable(playStoreDrawable);

    }

}