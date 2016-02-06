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
import jahirfiquitiva.iconshowcase.utilities.Utils;

public class IconsFragment extends Fragment {

    private IconsAdapter mAdapter;
    private ArrayList<IconItem> iconsList, filteredIconsList;
    private ViewGroup layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        try {
            layout = (ViewGroup) inflater.inflate(R.layout.icons_grid, container, false);
        } catch (InflateException e) {
            //Do nothing
        }

        RecyclerView iconsGrid = (RecyclerView) layout.findViewById(R.id.iconsGrid);
        RelativeLayout gridParent = (RelativeLayout) layout.findViewById(R.id.gridParent);

        gridParent.setPadding(0, 0, 0,
                getResources().getConfiguration().orientation == 1 ||
                        getResources().getConfiguration().orientation == 2 ?
                        UIUtils.getNavigationBarHeight(getActivity()) - 4 : 0);

        iconsGrid.setHasFixedSize(true);
        iconsGrid.setLayoutManager(new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.icons_grid_width)));

        iconsList = new ArrayList<>();

        mAdapter = new IconsAdapter(getActivity(), iconsList);

        if (getArguments() != null) {
            ArrayList<String> names = getArguments().getStringArrayList("iconsNames");
            ArrayList<Integer> ints = getArguments().getIntegerArrayList("iconsInts");
            if (names != null) {
                if (ints != null) {
                    if (names.size() == ints.size()) {
                        for (int i = 0; i < names.size(); i++) {
                            iconsList.add(new IconItem(names.get(i), ints.get(i)));
                        }
                    } else {
                        Utils.showLog(getActivity(), "Inconsistent arrays");
                    }
                }
            }
            mAdapter.setIcons(iconsList);
        }

        iconsGrid.setAdapter(mAdapter);

        RecyclerFastScroller fastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);
        fastScroller.attachRecyclerView(iconsGrid);

        return layout;
    }

    public static IconsFragment newInstance(ArrayList<String> iconsNames, ArrayList<Integer> iconsInts) {
        IconsFragment fragment = new IconsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("iconsNames", iconsNames);
        args.putIntegerArrayList("iconsInts", iconsInts);
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
            filteredIconsList = new ArrayList<IconItem>();
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