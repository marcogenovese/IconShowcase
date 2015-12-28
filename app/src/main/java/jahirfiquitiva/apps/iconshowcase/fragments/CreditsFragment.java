package jahirfiquitiva.apps.iconshowcase.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.utilities.ThemeUtils;

public class CreditsFragment extends Fragment {

    private ViewGroup layout;
    Drawable person, gplus, twitter, website, youtube, community, playstore, github, bugs, donate;
    ImageView iconAuthor, iconDev, authorGPlus, authorCommunity, youtubeIcon, twitterIcon,
            playStoreIcon, authorWebsite, devGPlus, devGitHub, devWebsite, donateIcon,
            bugIcon, devCommunity;
    View jahir, support, thanks;
    boolean withCreditsToDeveloper = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (toolbar != null)
            toolbar.setTitle(R.string.section_six);

        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        try {
            layout = (ViewGroup) inflater.inflate(R.layout.credits_section, container, false);
        } catch (InflateException e) {

        }

        jahir = layout.findViewById(R.id.jahirCredits);
        support = layout.findViewById(R.id.supportDev);
        thanks = layout.findViewById(R.id.specialThanks);

        iconAuthor = (ImageView) layout.findViewById(R.id.icon_author);
        iconDev = (ImageView) layout.findViewById(R.id.icon_dev);
        authorGPlus = (ImageView) layout.findViewById(R.id.icon_google_plus_author);
        authorCommunity = (ImageView) layout.findViewById(R.id.icon_community_author);
        youtubeIcon = (ImageView) layout.findViewById(R.id.icon_youtube);
        twitterIcon = (ImageView) layout.findViewById(R.id.icon_twitter);
        playStoreIcon = (ImageView) layout.findViewById(R.id.icon_play_store);
        authorWebsite = (ImageView) layout.findViewById(R.id.icon_website_author);
        devGPlus = (ImageView) layout.findViewById(R.id.icon_google_plus);
        devGitHub = (ImageView) layout.findViewById(R.id.icon_github);
        devWebsite = (ImageView) layout.findViewById(R.id.icon_website);
        donateIcon = (ImageView) layout.findViewById(R.id.icon_donate);
        bugIcon = (ImageView) layout.findViewById(R.id.icon_bug_report);
        devCommunity = (ImageView) layout.findViewById(R.id.icon_google_plus_community);

        setupLayout(getActivity());

        return layout;
    }

    private void setupLayout(Context mContext) {
        final int light = mContext.getResources().getColor(android.R.color.white);
        final int dark = mContext.getResources().getColor(R.color.grey);

        person = new IconicsDrawable(mContext)
                .icon(GoogleMaterial.Icon.gmd_account)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        gplus = new IconicsDrawable(mContext)
                .icon(GoogleMaterial.Icon.gmd_google_plus)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        community = new IconicsDrawable(mContext)
                .icon(GoogleMaterial.Icon.gmd_group_work)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        twitter = new IconicsDrawable(mContext)
                .icon(GoogleMaterial.Icon.gmd_twitter)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        github = new IconicsDrawable(mContext)
                .icon(GoogleMaterial.Icon.gmd_github)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        youtube = new IconicsDrawable(mContext)
                .icon(GoogleMaterial.Icon.gmd_youtube_play)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        playstore = new IconicsDrawable(mContext)
                .icon(GoogleMaterial.Icon.gmd_case_play)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        website = new IconicsDrawable(mContext)
                .icon(GoogleMaterial.Icon.gmd_globe_alt)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        bugs = new IconicsDrawable(mContext)
                .icon(GoogleMaterial.Icon.gmd_bug)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        donate = new IconicsDrawable(mContext)
                .icon(GoogleMaterial.Icon.gmd_money_box)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        iconAuthor.setImageDrawable(person);
        iconDev.setImageDrawable(person);
        authorGPlus.setImageDrawable(gplus);
        authorCommunity.setImageDrawable(community);
        youtubeIcon.setImageDrawable(youtube);
        twitterIcon.setImageDrawable(twitter);
        playStoreIcon.setImageDrawable(playstore);
        authorWebsite.setImageDrawable(website);
        devGPlus.setImageDrawable(gplus);
        devGitHub.setImageDrawable(github);
        devWebsite.setImageDrawable(website);
        donateIcon.setImageDrawable(donate);
        bugIcon.setImageDrawable(bugs);
        devCommunity.setImageDrawable(community);

        if (!withCreditsToDeveloper) {
            jahir.setVisibility(View.GONE);
            support.setVisibility(View.GONE);
            thanks.setVisibility(View.GONE);
        }

    }

}
