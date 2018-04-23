package io.linksoft.schedules.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.linksoft.schedules.MainActivity;
import io.linksoft.schedules.R;
import io.linksoft.schedules.data.Class;
import io.linksoft.schedules.data.DaySchedulesContainer;
import io.linksoft.schedules.sections.ClassSection;

public class DayViewPagerFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final String ARG_DAY = "day";

    protected DaySchedulesContainer day;

    public static DayViewPagerFragment newInstance(DaySchedulesContainer day, int position) {
        DayViewPagerFragment fragment = new DayViewPagerFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_POSITION, position);
        args.putParcelable(ARG_DAY, day);
        fragment.setArguments(args);

        fragment.day = day;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            try {
                // When a user resumes the app after it was suspended, attempt to
                // read the data from the session storage and start off where the
                // user left.
                day = getArguments().getParcelable(ARG_DAY);
            } catch (Exception e) {
                // If reading failed for some reason, don't bother to properly
                // solve this issue. Just start the main activity again and let
                // it reload everything from cache or whatever it likes to get
                // the data from.
                ((MainActivity) getActivity()).reload();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager_schedules, container, false);
        if (day.getSchedules().isEmpty()) {
            return view;
        }

        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
        for (Map.Entry<String, ArrayList<Class>> entry : day.getSchedules().entrySet()) {
            sectionAdapter.addSection(new ClassSection(entry.getKey(), new ArrayList<>(entry.getValue())));
        }

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);

        return view;
    }

    public DaySchedulesContainer getDay() {
        return day;
    }

}
