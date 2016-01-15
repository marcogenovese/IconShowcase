package jahirfiquitiva.apps.iconshowcase.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.adapters.FAQsAdapter;
import jahirfiquitiva.apps.iconshowcase.models.FAQsItem;
import jahirfiquitiva.apps.iconshowcase.views.DividerItemDecoration;
import jahirfiquitiva.apps.iconshowcase.views.FastScroller;

public class FAQsFragment extends Fragment {

    public static String[] questions;
    public static String[] answers;

    List<FAQsItem> faqs;

    private ViewGroup layout;

    int cardsSpacing;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        try {
            layout = (ViewGroup) inflater.inflate(R.layout.faqs_section, container, false);
        } catch (InflateException e) {

        }

        questions = getResources().getStringArray(R.array.questions);
        answers = getResources().getStringArray(R.array.answers);

        faqs = new ArrayList<FAQsItem>();
        for (int i = 0; i < questions.length; i++) {
            FAQsItem item = new FAQsItem(questions[i], answers[i]);
            faqs.add(item);
        }

        cardsSpacing = getResources().getDimensionPixelSize(R.dimen.dividers_height);

        RecyclerView faqsList = (RecyclerView) layout.findViewById(R.id.faqs_list);
        FastScroller fastScroller = (FastScroller) layout.findViewById(R.id.rvFastScroller);
        //fastScroller.setHideDelay(1000);

        FAQsAdapter faqsAdapter = new FAQsAdapter(faqs);
        faqsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        faqsList.setItemAnimator(new DefaultItemAnimator());
        faqsList.addItemDecoration(new DividerItemDecoration(getActivity(), null, false, false));
        faqsList.setHasFixedSize(true);
        faqsList.setAdapter(faqsAdapter);
        fastScroller.setRecyclerView(faqsList);

        return layout;
    }

}