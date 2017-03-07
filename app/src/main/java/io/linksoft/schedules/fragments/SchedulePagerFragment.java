package io.linksoft.schedules.fragments;

import android.os.Bundle;

import io.linksoft.schedules.data.Schedule;

public class SchedulePagerFragment extends ClassSectionFragment {

    private static final String ARG_SCHEDULE = "schedule";
    private static final String ARG_POSITION = "position";

    private String schedule;

    public static SchedulePagerFragment newInstance(Schedule schedule, int position) {
        SchedulePagerFragment fragment = new SchedulePagerFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_POSITION, position);
        args.putString(ARG_SCHEDULE, schedule.getCode());
        args.putParcelableArrayList(ARG_CLASSES, schedule.getClasses());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            schedule = getArguments().getString(ARG_SCHEDULE);
    }

    public String getScheduleCode() {
        return schedule;
    }

}
