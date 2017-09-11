package io.linksoft.schedules.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.fragments.ScheduleViewPagerFragment;

public class ScheduleViewPagerAdapter extends ViewPagerAdapter {

    private List<String> positionMap = new ArrayList<>();

    public ScheduleViewPagerAdapter(FragmentManager fm, int displayWeeks, Map<String, Schedule> schedules) {
        super(fm, schedules);

        this.displayWeeks = displayWeeks;

        setPositionMap();
    }

    private Schedule getScheduleById(int position) {
        if (positionMap.get(position) == null)
            return new Schedule("Unknown", "", false);

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

    @Override
    public int getCount() {
        return activeSchedules.size();
    }

    @Override
    public Fragment getItem(int position) {
        return ScheduleViewPagerFragment.newInstance(getScheduleById(position), position, displayWeeks);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (activeSchedules != null && position < activeSchedules.size())
            return getScheduleById(position).getFullName();

        return "Unknown";
    }

}
