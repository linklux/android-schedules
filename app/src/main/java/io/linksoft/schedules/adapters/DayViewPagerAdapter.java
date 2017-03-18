package io.linksoft.schedules.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LongSparseArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import io.linksoft.schedules.data.DaySchedulesContainer;
import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.fragments.DayViewPagerFragment;
import io.linksoft.schedules.util.DateUtil;

public class DayViewPagerAdapter extends ViewPagerAdapter {

    private LongSparseArray<DaySchedulesContainer> days = new LongSparseArray<>();

    public DayViewPagerAdapter(FragmentManager fm, Map<String, Schedule> schedules) {
        super(fm, schedules);

        setActiveSchedules();
        loadDays();
    }

    private void loadDays() {
        Date start = DateUtil.getWeekStart(new Date(), 0);
        Date end = DateUtil.getWeekStart(new Date(), 2);
        ArrayList<Date> dates = DateUtil.getDateRange(start, end, true);

        for (Date date : dates)
            days.put(date.getTime(), new DaySchedulesContainer(date, activeSchedules));
    }

    private void setActiveSchedules() {
        activeSchedules.clear();

        for (Map.Entry<String, Schedule> schedule : schedules.entrySet()) {
            if (!schedule.getValue().isEnabled()) continue;

            activeSchedules.add(schedule.getValue());
        }
    }

    @Override
    public int getCount() {
        return days.size();
    }

    @Override
    public Fragment getItem(int position) {
        return DayViewPagerFragment.newInstance(days.valueAt(position), position);
    }

    @Override
    public int getItemPosition(Object object) {
        long time = object instanceof Long ? ((Long) object) : object instanceof Date ? ((Date) object).getTime() : 0;
        int position = days.indexOfKey(time);

        return position < 0 ? POSITION_NONE : position;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (days == null || position > days.size()) return "Unknown";
        if (days.valueAt(position) == null) return "Unknown";

        return days.valueAt(position).getDay();
    }

}
