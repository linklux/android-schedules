package io.linksoft.schedules.util;

import android.app.Activity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.linksoft.schedules.MainActivity;
import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.data.ScheduleCache;
import io.linksoft.schedules.data.Settings;
import io.linksoft.schedules.net.WindesheimApi;

public class SchedulesUtil {

    private HashMap<String, Schedule> schedules = new HashMap<>();

    private Activity activity;
    private WindesheimApi api;

    private ScheduleCache cache;

    public SchedulesUtil(Activity activity, WindesheimApi api) {
        this.activity = activity;
        this.api = api;

        cache = new ScheduleCache(activity);
    }

    public void add(Schedule schedule) {
        schedules.put(schedule.getCode(), schedule);

        if (NetUtil.hasNetworkConnection(activity) && shouldSync(schedule.getCode())) {
            api.syncSchedule(schedule);
        } else {
            cache.get(schedules.get(schedule.getCode()));
        }
    }

    public void set(Schedule[] schedules) {
        for (Schedule schedule : schedules) {
            add(schedule);

            if (this.schedules.size() == schedules.length && isAllSynced())
                ((MainActivity) activity).setPagerView();
        }
    }

    public Schedule get(String code) {
        if (!has(code))
            return new Schedule("Unknown: " + code, true);

        return schedules.get(code);
    }

    public HashMap<String, Schedule> get() {
        return schedules;
    }

    public boolean has(String code) {
        return schedules.containsKey(code);
    }

    public int size() {
        return schedules.size();
    }

    public void removeInactive(Settings settings) {
        for (Map.Entry<String, Schedule> entry : schedules.entrySet())
            if (!entry.getValue().isEnabled())
                settings.removeSchedule(entry.getValue());

        settings.save();
    }

    public boolean shouldSync(String schedule) {
        if (!FileUtil.fileExists(activity, ScheduleCache.FILE_NAME, FileUtil.TYPE_CACHE)) return true;

        return new Date().after(DateUtil.getDateByDayOffset(get(schedule).getSyncTime(), ScheduleCache.CACHE_DAYS));
    }

    public void syncAll() {
        if (!NetUtil.hasNetworkConnection(activity)) return;
        clearCache();

        for (Map.Entry<String, Schedule> entry : schedules.entrySet()) {
            if (!entry.getValue().isEnabled()) continue;

            entry.getValue().setSynced(false);
            api.syncSchedule(entry.getValue());
        }
    }

    public boolean isAllSynced() {
        boolean isSynced = true;

        for (Map.Entry<String, Schedule> s : schedules.entrySet()) {
            if (!s.getValue().isEnabled()) continue;

            if (!s.getValue().isSynced()) {
                isSynced = false;
                break;
            }
        }

        return isSynced;
    }

    public boolean writeToCache(String schedule) {
        return has(schedule) && cache.write(get(schedule));
    }

    public boolean clearCache() {
        return cache.clear();
    }

    public int getActiveSchedules() {
        int count = 0;

        for (Map.Entry<String, Schedule> s : schedules.entrySet())
            if (s.getValue().isEnabled()) count++;

        return count;
    }

}
