/*
 * Copyright (c) 2016.  Jahir Fiquitiva
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
 * Big thanks to the project contributors. Check them in the repository.
 *
 */

/*
 *
 */

package jahirfiquitiva.iconshowcase.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.adapters.HomeListAdapter;
import jahirfiquitiva.iconshowcase.models.HomeCard;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import jahirfiquitiva.iconshowcase.views.DebouncedClickListener;
import jahirfiquitiva.iconshowcase.views.DividerItemDecoration;


public class MainFragment extends BaseFragment {

    private Context context;

    private final ArrayList<HomeCard> homeCards = new ArrayList<>();
    private boolean hasAppsList = false;

    @Override
    public void onFabClick(View v) {
        Intent rate = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" +
                        context.getPackageName()));
        context.startActivity(rate);
    }

    @Override
    int getFabIcon() {
        return R.drawable.ic_rate;
    }

    @Override
    boolean hasFab() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        String[] appsNames = getResources().getStringArray(R.array.apps_titles);
        String[] appsDescriptions = getResources().getStringArray(R.array.apps_descriptions);
        String[] appsIcons = getResources().getStringArray(R.array.apps_icons);
        String[] appsPackages = getResources().getStringArray(R.array.apps_packages);

        int names = appsNames.length, descs = appsDescriptions.length, icons = appsIcons.length,
                packs = appsPackages.length;

        if (names > 0 && names == descs && names == icons && names == packs) {
            hasAppsList = true;
            hideFab();
        }

        context = getActivity();

        View layout = inflater.inflate(R.layout.main_section, container, false);

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.home_rv);

        setupAndAnimateIcons(600);

        GridLayout iconsRow = (GridLayout) getActivity().findViewById(R.id.iconsRow);

        iconsRow.setOnClickListener(new DebouncedClickListener() {

            @Override
            public void onDebouncedClick(View v) {
                ShowcaseActivity.SHUFFLE = true;
                setupAndAnimateIcons(0);
            }
        });

        if (hasAppsList) {
            for (int i = 0; i < appsNames.length; i++) {
                try {
                    if (appsPackages[i].contains("http")) { //checks if package is a site
                        homeCards.add(new HomeCard.Builder()
                                .context(getActivity())
                                .title(appsNames[i])
                                .description(appsDescriptions[i])
                                .icon(ContextCompat.getDrawable(context,
                                        Utils.getIconResId(getResources(),
                                                context.getPackageName(), appsIcons[i])))
                                .onClickLink(appsPackages[i], false, false, null)
                                .build());
                        continue;
                    }
                    Intent intent;
                    boolean isInstalled = Utils.isAppInstalled(context, appsPackages[i]);
                    if (isInstalled) {
                        PackageManager pm = context.getPackageManager();
                        intent = pm.getLaunchIntentForPackage(appsPackages[i]);
                        if (intent != null) {
                            try {
                                homeCards.add(new HomeCard.Builder()
                                        .context(getActivity())
                                        .title(appsNames[i])
                                        .description(appsDescriptions[i])
                                        .icon(ContextCompat.getDrawable(context,
                                                Utils.getIconResId(getResources(),
                                                        context.getPackageName(), appsIcons[i])))
                                        .onClickLink(appsPackages[i], true, true, intent)
                                        .build());
                            } catch (Resources.NotFoundException e) {
                                Utils.showLog(context, "There's no icon that matches name: "
                                        + appsIcons[i]);
                                homeCards.add(new HomeCard.Builder()
                                        .context(getActivity())
                                        .title(appsNames[i])
                                        .description(appsDescriptions[i])
                                        .icon(ContextCompat.getDrawable(context,
                                                Utils.getIconResId(getResources(),
                                                        context.getPackageName(), "ic_na_launcher")))
                                        .onClickLink(appsPackages[i], true, true, intent)
                                        .build());
                            }
                        }
                    } else {
                        try {
                            homeCards.add(new HomeCard.Builder()
                                    .context(getActivity())
                                    .title(appsNames[i])
                                    .description(appsDescriptions[i])
                                    .icon(ContextCompat.getDrawable(context,
                                            Utils.getIconResId(getResources(),
                                                    context.getPackageName(), appsIcons[i])))
                                    .onClickLink(appsPackages[i], true, false, null)
                                    .build());
                        } catch (Resources.NotFoundException e) {
                            Utils.showLog(context, "There's no icon that matches name: " + appsIcons[i]);
                            homeCards.add(new HomeCard.Builder()
                                    .context(getActivity())
                                    .title(appsNames[i])
                                    .description(appsDescriptions[i])
                                    .icon(ContextCompat.getDrawable(context,
                                            Utils.getIconResId(getResources(),
                                                    context.getPackageName(), "ic_na_launcher")))
                                    .onClickLink(appsPackages[i], true, false, null)
                                    .build());
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    hasAppsList = false;
                    showFab();
                    Utils.showLog(context, "Apps Cards arrays are inconsistent. Fix them.");
                }
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                null, getResources().getDimensionPixelSize(R.dimen.dividers_height), false, true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        HomeListAdapter mAdapter = new HomeListAdapter(homeCards, context, hasAppsList);
        mRecyclerView.setAdapter(mAdapter);

        return layout;
    }

    private void setupAndAnimateIcons(int delay) {
        ((ShowcaseActivity) getActivity()).setupIcons();
        ((ShowcaseActivity) getActivity()).animateIcons(delay);
    }

}
