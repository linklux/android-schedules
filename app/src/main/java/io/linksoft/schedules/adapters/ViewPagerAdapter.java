package io.linksoft.schedules.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Map;

import io.linksoft.schedules.data.Schedule;

public abstract class ViewPagerAdapter extends FragmentPagerAdapter {

    protected Map<String, Schedule> schedules;

    protected ArrayList<Schedule> activeSchedules = new ArrayList<>();

    protected int displayWeeks;

    public ViewPagerAdapter(FragmentManager fm, Map<String, Schedule> schedules) {
        super(fm);

        this.schedules = schedules;
    }

}
