package io.linksoft.schedules.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.fragments.SchedulePagerFragment;

public class SchedulePagerAdapter extends FragmentPagerAdapter {

    private Map<String, Schedule> schedules;

    private List<Schedule> activeSchedules = new ArrayList<>();
    private List<String> positionMap = new ArrayList<>();

    public SchedulePagerAdapter(FragmentManager fm, Map<String, Schedule> schedules) {
        super(fm);

        this.schedules = schedules;

        setPositionMap();
    }

    @Override
    public int getCount() {
        return activeSchedules.size();
    }

    @Override
    public Fragment getItem(int position) {
        return SchedulePagerFragment.newInstance(getScheduleById(position), position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (activeSchedules != null && position < activeSchedules.size())
            return getScheduleById(position).getCode();

        return "Unknown";
    }

    private Schedule getScheduleById(int position) {
        if (positionMap.get(position) == null)
            return new Schedule("Unknown", false);

        return activeSchedules.get(position);
    }

    private void setPositionMap() {
        activeSchedules.clear();
        positionMap.clear();

        int i = 0;
        for (Map.Entry<String, Schedule> schedule : schedules.entrySet()) {
            if (!schedule.getValue().isEnabled()) continue;

            activeSchedules.add(i, schedule.getValue());
            positionMap.add(i++, schedule.getKey());
        }
    }

}
