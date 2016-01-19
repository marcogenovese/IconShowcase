package jahirfiquitiva.apps.iconshowcase.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.mikepenz.materialize.util.UIUtils;

import java.util.ArrayList;
import java.util.Locale;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.basefragments.FragmentStatePagerAdapter;
import jahirfiquitiva.apps.iconshowcase.models.IconsLists;
import jahirfiquitiva.apps.iconshowcase.views.CustomCoordinatorLayout;

public class PreviewsFragment extends Fragment {

    private MenuItem mSearchItem;
    private int mLastSelected = 0;
    private ViewPager mPager;
    private String[] tabs;
    private ViewGroup layout;
    public TabLayout mTabs;
    private SearchView mSearchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        layout = (ViewGroup) inflater.inflate(R.layout.icons_preview_section, container, false);

        return layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Are you ready for the ugliest fix in the history of the universe?
        // Set custom offset for AppBar. This makes both toolbar and tabs visible

        AppBarLayout appbar = (AppBarLayout) getActivity().findViewById(R.id.appbar);
        CustomCoordinatorLayout.LayoutParams params = (CustomCoordinatorLayout.LayoutParams) appbar.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        CustomCoordinatorLayout coordinatorLayout = (CustomCoordinatorLayout) getActivity().findViewById(R.id.mainCoordinatorLayout);

        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        Integer toolbarCollapsedHeight = 0;
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            toolbarCollapsedHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        Integer toolbarExpandedHeight = getActivity().getResources().getDimensionPixelOffset(R.dimen.toolbar_expanded);
        Integer statusbarHeight = UIUtils.getStatusBarHeight(getActivity());

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float extra = (metrics.densityDpi * 0.05f);

        // Set toolbarCollapsedHeight as offset so tabs are shown
        if (behavior != null) {
            behavior.setTopAndBottomOffset(-toolbarExpandedHeight + statusbarHeight + (toolbarCollapsedHeight * 2) - Math.round(extra));
        }

        // Lock CoordinatorLayout so the toolbar can't be scrolled away
        coordinatorLayout.setScrollAllowed(false);

        if (mPager == null) {
            mPager = (ViewPager) layout.findViewById(R.id.pager);
            mPager.setOffscreenPageLimit(6);
            mPager.setAdapter(new IconsPagerAdapter(getChildFragmentManager()));
            createTabs();
        }

    }

    private void createTabs() {
        mTabs = (TabLayout) getActivity().findViewById(R.id.tabs);
        mTabs.setVisibility(View.VISIBLE);
        mTabs.setupWithViewPager(mPager);
        mTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
                if (mLastSelected > -1) {
                    IconsFragment frag = (IconsFragment) getChildFragmentManager().findFragmentByTag("page:" + mLastSelected);
                    if (frag != null)
                        frag.performSearch(null);
                }
                mLastSelected = tab.getPosition();
                if (mSearchView != null && getActivity() != null)
                    mSearchView.setQueryHint(getString(R.string.search_x, tabs[mLastSelected]));
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        if(mTabs != null) mTabs.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.icons_menu, menu);
        mSearchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        mSearchView.setQueryHint(getString(R.string.search_x, tabs[mLastSelected]));
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save current position
        savedInstanceState.putInt("lastSelected", mLastSelected);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore last selected position
            mLastSelected = savedInstanceState.getInt("lastSelected", 0);
        }
    }
}
