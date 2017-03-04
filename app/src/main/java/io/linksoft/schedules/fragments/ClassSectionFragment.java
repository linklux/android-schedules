package io.linksoft.schedules.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import io.linksoft.schedules.util.DateUtil;
import io.linksoft.schedules.R;
import io.linksoft.schedules.data.Class;

public class ClassSectionFragment extends Fragment {

    private static final String ARG_CLASSES = "classes";

    private List<Class> mClasses;

    public static ClassSectionFragment newInstance(ArrayList<Class> classes) {
        ClassSectionFragment fragment = new ClassSectionFragment();
        Bundle args = new Bundle();

        args.putParcelableArrayList(ARG_CLASSES, classes);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            mClasses = getArguments().getParcelableArrayList(ARG_CLASSES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_section, container, false);
        if (mClasses.isEmpty()) return view;

        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
        List<Class> classes = new ArrayList<>();

        Date dateLimit = DateUtil.getWeekStart(mClasses.get(0).getTimeStart(), 2);
        String curDay = DateUtil.getScheduleDay(mClasses.get(0).getTimeStart());

        int i = 0;
        while (mClasses.get(i).getTimeStart().before(dateLimit)) {
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

    @Override
    public void onResume() {
        super.onResume();
    }

    private class ClassSection extends StatelessSection {

        private String title;
        private List<Class> classes;

        ClassSection(String title, List<Class> list) {
            super(R.layout.section_class_header, R.layout.section_class_item);

            this.title = title;
            this.classes = list;
        }

        @Override
        public int getContentItemsTotal() {
            return classes.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;

            Class cls = classes.get(position);

            itemHolder.timeFrame.setText(DateUtil.getClassTimeframe(cls.getTimeStart(), cls.getTimeEnd()));
            itemHolder.location.setText(cls.getLocation());
            itemHolder.name.setText(String.format("%s - %s", cls.getClassName(), cls.getComments()));
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

            headerHolder.title.setText(title);
        }

    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;

        HeaderViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.class_header_title);
        }

    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView timeFrame;
        private final TextView location;
        private final TextView name;

        ItemViewHolder(View view) {
            super(view);

            timeFrame = (TextView) view.findViewById(R.id.class_item_timeframe);
            location = (TextView) view.findViewById(R.id.class_item_location);
            name = (TextView) view.findViewById(R.id.class_item_name);
        }

    }

}
