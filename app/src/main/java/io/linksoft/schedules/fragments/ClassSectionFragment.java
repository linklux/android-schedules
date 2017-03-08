package io.linksoft.schedules.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.linksoft.schedules.R;
import io.linksoft.schedules.data.Class;
import io.linksoft.schedules.sections.ClassSection;
import io.linksoft.schedules.util.DateUtil;

public class ClassSectionFragment extends Fragment {

    protected static final String ARG_CLASSES = "classes";

    protected List<Class> mClasses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            mClasses = getArguments().getParcelableArrayList(ARG_CLASSES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager_schedules, container, false);
        if (mClasses.isEmpty()) return view;

        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
        List<Class> classes = new ArrayList<>();

        Date dateLimit = DateUtil.getWeekStart(mClasses.get(0).getTimeStart(), 2);
        String curDay = DateUtil.getScheduleDay(mClasses.get(0).getTimeStart());

        int i = 0;
        while (i < mClasses.size() - 1 && mClasses.get(i).getTimeStart().before(dateLimit)) {
            Class cls = mClasses.get(i);
            String day = DateUtil.getScheduleDay(cls.getTimeStart());

            if (!day.equals(curDay)) {
                sectionAdapter.addSection(new ClassSection(curDay, new ArrayList<>(classes)));
                classes.clear();

                curDay = day;
            }

            classes.add(cls);
            i++;
        }

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);

        return view;
    }

}
