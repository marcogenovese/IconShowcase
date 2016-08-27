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

package jahirfiquitiva.iconshowcase.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pitchedapps.capsule.library.fragments.CapsuleFragment;

import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.adapters.HomeListAdapter;
import jahirfiquitiva.iconshowcase.enums.DrawerType;
import jahirfiquitiva.iconshowcase.models.HomeCard;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import jahirfiquitiva.iconshowcase.views.DebouncedClickListener;
import jahirfiquitiva.iconshowcase.views.DividerItemDecoration;
import timber.log.Timber;

public class MainFragment extends CapsuleFragment {

    private Context context;

    private final ArrayList<HomeCard> homeCards = new ArrayList<>();
    private boolean hasAppsList = false;

    @Override
    public void onFabClick (View v) {
        Intent rate = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" +
                        context.getPackageName()));
        context.startActivity(rate);
    }

    @Override
    public int getTitleId() {
        return DrawerType.HOME.getTitleID();
    }

    @Override
    protected int getFabIcon () {
        return R.drawable.ic_rate;
    }

    @Override
    protected boolean hasFab () {
        return true;
    }

    @Override
    public View onCreateView (LayoutInflater inflater, final ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        String[] listNames = getResources().getStringArray(R.array.home_list_titles);
        String[] listDescriptions = getResources().getStringArray(R.array.home_list_descriptions);
        String[] listIcons = getResources().getStringArray(R.array.home_list_icons);
        String[] listLinks = getResources().getStringArray(R.array.home_list_links);

        int names = listNames.length, descs = listDescriptions.length, icons = listIcons.length,
                packs = listLinks.length;

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
            public void onDebouncedClick (View v) {
                ShowcaseActivity.SHUFFLE = true;
                setupAndAnimateIcons(0);
            }
        });

        if (hasAppsList) {
            for (int i = 0; i < listNames.length; i++) {
                try {
                    homeCards.add(new HomeCard.Builder()
                            .context(getActivity())
                            .title(listNames[i])
                            .description(listDescriptions[i])
                            .icon(ContextCompat.getDrawable(context,
                                    Utils.getIconResId(getResources(),
                                            context.getPackageName(), listIcons[i])))
                            .onClickLink(listLinks[i])
                            .build());
                } catch (IndexOutOfBoundsException e) {
                    hasAppsList = false;
                    showFab();
                    Timber.d("Apps Cards arrays are inconsistent. Fix them.");
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

    private void setupAndAnimateIcons (int delay) {
        ((ShowcaseActivity) getActivity()).setupIcons();
        ((ShowcaseActivity) getActivity()).animateIcons(delay);
    }

}
