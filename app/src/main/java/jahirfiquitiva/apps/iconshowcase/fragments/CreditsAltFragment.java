package jahirfiquitiva.apps.iconshowcase.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.dialogs.ISDialogs;
import jahirfiquitiva.apps.iconshowcase.utilities.Utils;

public class CreditsAltFragment extends Fragment {

    private boolean WITH_DEVELOPMENT_CREDITS = true;

    private Context context;
    private ViewGroup layout;

    String[] libsLinks, contributorsLinks, uiCollaboratorsLinks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();

        libsLinks = context.getResources().getStringArray(R.array.libs_links);
        contributorsLinks = context.getResources().getStringArray(R.array.contributors_links);
        uiCollaboratorsLinks = context.getResources().getStringArray(R.array.ui_collaborators_links);

        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }

        layout = (ViewGroup) inflater.inflate(R.layout.credits_section_alt, container, false);

        setupViewsIDs(layout);

        return layout;
    }

    private void setupViewsIDs(final ViewGroup layout) {

        if (!WITH_DEVELOPMENT_CREDITS) {
            CardView devCV = (CardView) layout.findViewById(R.id.devCard);
            devCV.setVisibility(View.GONE);
        }

        AppCompatButton emailBtn = (AppCompatButton) layout.findViewById(R.id.send_email_btn);
        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.sendEmailWithDeviceInfo(context);
            }
        });

        AppCompatButton websiteBtn = (AppCompatButton) layout.findViewById(R.id.website_btn);
        websiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openLinkInChromeCustomTab(context,
                        getResources().getString(R.string.iconpack_author_website));
            }
        });

        AppCompatButton forkBtn = (AppCompatButton) layout.findViewById(R.id.fork_btn);
        forkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openLinkInChromeCustomTab(context,
                        getResources().getString(R.string.dashboard_author_github));
            }
        });

        AppCompatButton devWebsiteBtn = (AppCompatButton) layout.findViewById(R.id.dev_website_btn);
        devWebsiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openLinkInChromeCustomTab(context,
                        getResources().getString(R.string.dashboard_author_website));
            }
        });

        final ImageView popUp = (ImageView) layout.findViewById(R.id.designerPopUp);
        popUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popUpMenu = new PopupMenu(context, popUp);
                popUpMenu.getMenuInflater().inflate(R.menu.designer_popup, popUpMenu.getMenu());
                popUpMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.designerFacebook:
                                if (Utils.isAppInstalled(context, "com.facebook.katana")) {
                                    Utils.openLink(context,
                                            getResources().getString(R.string.iconpack_author_fb));
                                } else {
                                    Utils.openLinkInChromeCustomTab(context,
                                            getResources().getString(R.string.iconpack_author_fb_alt));
                                }
                                break;
                            case R.id.designerGooglePlus:
                                Utils.openLinkInChromeCustomTab(context,
                                        getResources().getString(R.string.iconpack_author_gplus));
                                break;
                            case R.id.designerCommunity:
                                Utils.openLinkInChromeCustomTab(context,
                                        getResources().getString(R.string.iconpack_author_gplus_community));
                                break;
                            case R.id.designerYouTube:
                                Utils.openLinkInChromeCustomTab(context,
                                        getResources().getString(R.string.iconpack_author_youtube));
                                break;
                            case R.id.designerTwitter:
                                try {
                                    Utils.openLink(context,
                                            getResources().getString(R.string.iconpack_author_twitter));
                                } catch (Exception e) {
                                    Utils.openLink(context,
                                            getResources().getString(R.string.iconpack_author_twitter_alt));
                                }
                                break;
                            case R.id.designerPlayStore:
                                Utils.openLinkInChromeCustomTab(context,
                                        getResources().getString(R.string.iconpack_author_playstore));
                                break;
                        }
                        return false;
                    }
                });
                popUpMenu.show();
            }
        });

        final ImageView devPopUp = (ImageView) layout.findViewById(R.id.developerPopUp);
        devPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu devPopUpMenu = new PopupMenu(context, devPopUp);
                devPopUpMenu.getMenuInflater().inflate(R.menu.dev_popup, devPopUpMenu.getMenu());
                devPopUpMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.developmentSherry:
                                ISDialogs.showSherryDialog(context);
                                break;
                            case R.id.developmentContributors:
                                ISDialogs.showContributorsDialog(context, contributorsLinks);
                                break;
                            case R.id.developmentUIDesign:
                                ISDialogs.showUICollaboratorsDialog(context, uiCollaboratorsLinks);
                                break;
                            case R.id.developmentLibraries:
                                ISDialogs.showLibrariesDialog(context, libsLinks);
                                break;
                            case R.id.developmentBugs:
                                Utils.openLinkInChromeCustomTab(context,
                                        getResources().getString(R.string.dashboard_bugs_report));
                                break;
                            case R.id.developmentCommunity:
                                Utils.openLinkInChromeCustomTab(context,
                                        getResources().getString(R.string.dashboard_author_gplus_community));
                                break;
                        }
                        return false;
                    }
                });
                devPopUpMenu.show();
            }
        });
    }

}