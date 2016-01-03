package jahirfiquitiva.apps.iconshowcase.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import java.util.ArrayList;
import java.util.Locale;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.apps.iconshowcase.basefragments.FragmentStatePagerAdapter;
import jahirfiquitiva.apps.iconshowcase.models.IconsLists;
import jahirfiquitiva.apps.iconshowcase.utilities.Util;

public class PreviewsFragment extends Fragment {

    private MenuItem mSearchItem;
    private int mLastSelected = -1;
    private ViewPager mPager;
    private String[] tabs;
    private ViewGroup layout;
    public TabLayout mTabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        try {
            layout = (ViewGroup) inflater.inflate(R.layout.icons_preview_section, container, false);
        } catch (InflateException e) {

        }

        if (ShowcaseActivity.toolbar != null) {
            if (ShowcaseActivity.toolbar.getTitle() != null &&
                    !ShowcaseActivity.toolbar.getTitle().equals(
                            Util.getStringFromResources(getActivity(), R.string.section_two))) {
                ShowcaseActivity.toolbar.setTitle(R.string.section_two);
            }
        }

        mPager = (ViewPager) layout.findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(6);
        mPager.setAdapter(new IconsPagerAdapter(getChildFragmentManager()));
        mLastSelected = 0;

        mTabs = (TabLayout) layout.findViewById(R.id.tabs);
        mTabs.setupWithViewPager(mPager);
        mTabs.setTabTextColors(getResources().getColor(R.color.semitransparent_white),
                getResources().getColor(android.R.color.white));
        mTabs.setSelectedTabIndicatorColor(getResources().getColor(android.R.color.white));
        mTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
                if (mSearchItem != null && mSearchItem.isActionViewExpanded())
                    mSearchItem.collapseActionView();
                if (mLastSelected > -1) {
                    IconsFragment frag = (IconsFragment) getChildFragmentManager().findFragmentByTag("page:" + mLastSelected);
                    if (frag != null)
                        frag.performSearch(null);
                }
                mLastSelected = tab.getPosition();
                if (getActivity() != null)
                    getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.icons_menu, menu);
        mSearchItem = menu.findItem(R.id.search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        mSearchView.setQueryHint(mLastSelected > -1 ?
                getString(R.string.search_x, tabs[mLastSelected]) : getString(R.string.search_icons));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                search(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return false;
            }

            private void search(String s) {
                IconsFragment frag = (IconsFragment) getChildFragmentManager().findFragmentByTag("page:" + mPager.getCurrentItem());
                if (frag != null)
                    frag.performSearch(s);
            }

        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                IconsFragment frag = (IconsFragment) getChildFragmentManager().findFragmentByTag("page:" + mPager.getCurrentItem());
                if (frag != null)
                    frag.performSearch(null);
                mSearchItem.collapseActionView();
                return false;
            }
        });
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            toolbar.setElevation(0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            toolbar.setElevation((int) getResources().getDimension(R.dimen.toolbar_elevation));
        }
    }

    class IconsPagerAdapter extends FragmentStatePagerAdapter {

        public IconsPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = getResources().getStringArray(R.array.tabs);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = new Fragment();
            switch (position) {
                case 0:
                    f = IconsFragment.newInstance((ArrayList<String>) IconsLists.getListA(), IconsLists.getSectionA());
                    break;
                case 1:
                    f = IconsFragment.newInstance((ArrayList<String>) IconsLists.getListB(), IconsLists.getSectionB());
                    break;
                case 2:
                    f = IconsFragment.newInstance((ArrayList<String>) IconsLists.getListC(), IconsLists.getSectionC());
                    break;
                case 3:
                    f = IconsFragment.newInstance((ArrayList<String>) IconsLists.getListD(), IconsLists.getSectionD());
                    break;
                case 4:
                    f = IconsFragment.newInstance((ArrayList<String>) IconsLists.getListE(), IconsLists.getSectionE());
                    break;
                case 5:
                    f = IconsFragment.newInstance((ArrayList<String>) IconsLists.getListF(), IconsLists.getSectionF());
                    break;
            }
            return f;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position].toUpperCase(Locale.getDefault());
        }

        @Override
        public int getCount() {
            return tabs.length;
        }
    }
}
