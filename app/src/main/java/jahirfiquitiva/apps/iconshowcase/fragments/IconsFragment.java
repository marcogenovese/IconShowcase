package jahirfiquitiva.apps.iconshowcase.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mikepenz.materialize.util.UIUtils;

import java.util.ArrayList;
import java.util.Locale;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.adapters.IconsAdapter;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;
import jahirfiquitiva.apps.iconshowcase.views.FastScrollRecyclerView;

public class IconsFragment extends Fragment {

    private IconsAdapter mAdapter;
    private Preferences mPrefs;
    private ArrayList<String> iconsNames, filteredIconsList;
    private ArrayList<Integer> iconsInts, filteredIconsInts;
    private ViewGroup layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mPrefs = new Preferences(getActivity());

        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        try {
            layout = (ViewGroup) inflater.inflate(R.layout.icons_grid, container, false);
        } catch (InflateException e) {

        }

        FastScrollRecyclerView iconsGrid = (FastScrollRecyclerView) layout.findViewById(R.id.iconsGrid);
        RelativeLayout gridParent = (RelativeLayout) layout.findViewById(R.id.gridParent);
        gridParent.setPadding(0, 0, 0, UIUtils.getNavigationBarHeight(getActivity()) - 4);
        iconsGrid.setHasFixedSize(true);
        iconsGrid.setLayoutManager(new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.icon_grid_width)));

        mAdapter = new IconsAdapter(getActivity(), new ArrayList<String>(), new ArrayList<Integer>());

        if (getArguments() != null) {
            iconsNames = getArguments().getStringArrayList("iconsNamesList");
            iconsInts = getArguments().getIntegerArrayList("iconsArray");
            mAdapter.setIcons(iconsNames, iconsInts);
        }

        iconsGrid.setAdapter(mAdapter);

        return layout;
    }

    public static IconsFragment newInstance(ArrayList<String> iconsNames, ArrayList<Integer> iconsArray) {
        IconsFragment fragment = new IconsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("iconsNamesList", iconsNames);
        args.putIntegerArrayList("iconsArray", iconsArray);
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
            if (filteredIconsInts != null) {
                filteredIconsList = null;
            }
            adapter.clearIconsList();
            adapter.setIcons(iconsNames, iconsInts);
            adapter.notifyDataSetChanged();
        } else {
            if (filteredIconsList != null) {
                filteredIconsList.clear();
            }
            if (filteredIconsInts != null) {
                filteredIconsList = null;
            }
            filteredIconsList = new ArrayList<String>();
            filteredIconsInts = new ArrayList<Integer>();
            for (int i = 0; i < iconsNames.size(); i++) {
                String name = iconsNames.get(i);
                if (name.toLowerCase(Locale.getDefault())
                        .startsWith(s.toString().toLowerCase(Locale.getDefault()))) {
                    filteredIconsList.add(iconsNames.get(i));
                    filteredIconsInts.add(iconsInts.get(i));
                }
            }
            adapter.clearIconsList();
            adapter.setIcons(filteredIconsList, filteredIconsInts);
            adapter.notifyDataSetChanged();
        }
    }

}