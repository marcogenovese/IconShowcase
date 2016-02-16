/*
 *
 */

package jahirfiquitiva.iconshowcase.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mikepenz.materialize.util.UIUtils;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.ArrayList;
import java.util.Locale;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.adapters.IconsAdapter;
import jahirfiquitiva.iconshowcase.models.IconItem;
import jahirfiquitiva.iconshowcase.models.IconsCategory;

public class IconsFragment extends Fragment {

    private IconsAdapter mAdapter;
    private ArrayList<IconItem> iconsList, filteredIconsList;
    private ViewGroup layout;

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }

        layout = (ViewGroup) inflater.inflate(R.layout.icons_grid, container, false);

        RecyclerView iconsGrid = (RecyclerView) layout.findViewById(R.id.iconsGrid);
        RelativeLayout gridParent = (RelativeLayout) layout.findViewById(R.id.gridParent);
        gridParent.setPadding(0, 0, 0, 0);

        switch (getResources().getConfiguration().orientation) {
            case 1:
                gridParent.setPadding(0, 0, 0, UIUtils.getNavigationBarHeight(getActivity()));
                break;
            case 2:
                gridParent.setPadding(0, 0, 0, UIUtils.getNavigationBarHeight(getActivity()) - 16);
                break;
        }

        iconsGrid.setHasFixedSize(true);
        iconsGrid.setLayoutManager(new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.icons_grid_width)));

        iconsList = new ArrayList<>();

        mAdapter = new IconsAdapter(getActivity(), iconsList);

        if (getArguments() != null) {
            IconsCategory category = getArguments().getParcelable("icons");
            if (category != null) {
                iconsList = category.getIconsArray();
            }
            mAdapter.setIcons(iconsList);
        }

        iconsGrid.setAdapter(mAdapter);

        RecyclerFastScroller fastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);
        fastScroller.attachRecyclerView(iconsGrid);

        return layout;
    }

    public static IconsFragment newInstance(IconsCategory icons) {
        IconsFragment fragment = new IconsFragment();
        Bundle args = new Bundle();
        args.putParcelable("icons", icons);
        fragment.setArguments(args);
        return fragment;
    }

    public void performSearch(String query) {
        filter(query, mAdapter);
    }

    private synchronized void filter(CharSequence s, IconsAdapter adapter) {
        if (s == null || s.toString().trim().isEmpty()) {
            if (filteredIconsList != null) {
                filteredIconsList = null;
            }
            adapter.clearIconsList();
            adapter.setIcons(iconsList);
            adapter.notifyDataSetChanged();
        } else {
            if (filteredIconsList != null) {
                filteredIconsList.clear();
            }
            filteredIconsList = new ArrayList<>();
            for (int i = 0; i < iconsList.size(); i++) {
                String name = iconsList.get(i).getName();
                if (name.toLowerCase(Locale.getDefault())
                        .startsWith(s.toString().toLowerCase(Locale.getDefault()))) {
                    filteredIconsList.add(iconsList.get(i));
                }
            }
            adapter.clearIconsList();
            adapter.setIcons(filteredIconsList);
            adapter.notifyDataSetChanged();
        }
    }

}