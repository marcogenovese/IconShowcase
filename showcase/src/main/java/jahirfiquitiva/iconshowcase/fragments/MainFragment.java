package jahirfiquitiva.iconshowcase.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.utilities.LauncherIntents;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.ToolbarColorizer;
import jahirfiquitiva.iconshowcase.utilities.Utils;

public class MainFragment extends Fragment {
    private Context context;

    private static final String MARKET_URL = "https://play.google.com/store/apps/details?id=";
    private String PlayStoreListing;
    private ViewGroup layout;
    private ImageView iconsIV, wallsIV, widgetsIV, playStoreIV;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        context = this.getContext().getApplicationContext(); //TODO see if this is good

        String themedIcons = String.valueOf(getActivity().getResources().getInteger(R.integer.icons_amount));
        String availableWallpapers = String.valueOf(getActivity().getResources().getInteger(R.integer.walls_amount));
        String includedWidgets = String.valueOf(getActivity().getResources().getInteger(R.integer.zooper_widgets));

        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        try {
            layout = (ViewGroup) inflater.inflate(R.layout.main_section, container, false);
        } catch (InflateException e) {
            //Do nothing
        }

        showFAB();

        ShowcaseActivity.setupToolbarHeader(getActivity());

        ShowcaseActivity.setupIcons(ShowcaseActivity.icon1, ShowcaseActivity.icon2,
                ShowcaseActivity.icon3, ShowcaseActivity.icon4, ShowcaseActivity.icon5,
                ShowcaseActivity.icon6, ShowcaseActivity.icon7, ShowcaseActivity.icon8,
                ShowcaseActivity.numOfIcons);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                ShowcaseActivity.animateIcons(ShowcaseActivity.icon1, ShowcaseActivity.icon2,
                        ShowcaseActivity.icon3, ShowcaseActivity.icon4, ShowcaseActivity.icon5,
                        ShowcaseActivity.icon6, ShowcaseActivity.icon7, ShowcaseActivity.icon8,
                        ShowcaseActivity.numOfIcons);
            }

        }, 600);

        PlayStoreListing = getActivity().getPackageName();

        LinearLayout packInfo = (LinearLayout) layout.findViewById(R.id.appDetails);
        View divider = layout.findViewById(R.id.divider);
        packInfo.setVisibility(getActivity().getResources().getBoolean(R.bool.hide_pack_info) ?
                View.GONE :
                View.VISIBLE);
        divider.setVisibility(getActivity().getResources().getBoolean(R.bool.hide_pack_info) ?
                View.GONE :
                View.VISIBLE);

        if (!ShowcaseActivity.WITH_ZOOPER_SECTION) {
            LinearLayout widgets = (LinearLayout) layout.findViewById(R.id.widgets);
            widgets.setVisibility(View.GONE);
        }

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
                Utils.openLinkInChromeCustomTab(getActivity(),
                        Utils.getStringFromResources(getActivity(), R.string.iconpack_author_playstore));
            }
        });

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
                ShowcaseActivity.drawerItemClick(ShowcaseActivity.iconPickerIdentifier);
                ShowcaseActivity.drawer.setSelection(ShowcaseActivity.iconPickerIdentifier);
            }
        });

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        showFAB();
        int iconsColor = ThemeUtils.darkTheme ?
                ContextCompat.getColor(getActivity(), R.color.toolbar_text_dark) :
                ContextCompat.getColor(getActivity(), R.color.toolbar_text_light);
        ToolbarColorizer.colorizeToolbar(
                ShowcaseActivity.toolbar,
                iconsColor);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ShowcaseActivity.fab.setVisibility(View.GONE);
        ShowcaseActivity.fab.hide();
    }

    private void showFAB() {
        ShowcaseActivity.fab.setVisibility(View.VISIBLE);
        ShowcaseActivity.fab.show();
        ShowcaseActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ShowcaseActivity.drawerItemClick(ShowcaseActivity.applyIdentifier);
//                ShowcaseActivity.drawer.setSelection(ShowcaseActivity.applyIdentifier);

                if (Utils.isAppInstalled(getContext().getApplicationContext(), "org.cyanogenmod.theme.chooser") || Utils.isAppInstalled(context, "com.cyngn.theme.chooser")) {
                    new LauncherIntents(getActivity(), "Cmthemeengine");
                } else if (Utils.isAppInstalled(context, "com.lovejoy777.rroandlayersmanager")) {
                    new LauncherIntents(getActivity(), "Layers");
                }
            }
        });
    }

    private void setupIcons(Context context) {
        final int light = ContextCompat.getColor(context, R.color.drawable_tint_dark);
        final int dark = ContextCompat.getColor(context, R.color.drawable_tint_light);

        Drawable iconsDrawable = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_android_alt)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        Drawable wallsDrawable = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_collection_image)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        Drawable widgetsDrawable = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_widgets)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        Drawable playStoreDrawable = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_case_play)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        iconsIV.setImageDrawable(iconsDrawable);
        wallsIV.setImageDrawable(wallsDrawable);
        widgetsIV.setImageDrawable(widgetsDrawable);
        playStoreIV.setImageDrawable(playStoreDrawable);

    }

}