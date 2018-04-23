package io.linksoft.schedules.util;

import android.app.Activity;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import io.linksoft.schedules.MainActivity;
import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.data.ScheduleCache;
import io.linksoft.schedules.net.WindesheimApi;

public class SchedulesUtil {

    private LinkedHashMap<String, Schedule> schedules = new LinkedHashMap<>();

    private Activity activity;
    private WindesheimApi api;

    private ScheduleCache cache;

    public SchedulesUtil(Activity activity, WindesheimApi api) {
        this.activity = activity;
        this.api = api;

        cache = new ScheduleCache();
    }

    /**
     * Add a schedule to the list of schedules and sync its classes if
     * required. When there's no network connection, the classes are loaded
     * from the cache.
     *
     * @param schedule Schedule
     */
    public void add(Schedule schedule) {
        schedules.put(schedule.getCode(), schedule);

        if (NetUtil.hasNetworkConnection(activity) && shouldSync(schedule.getCode())) {
            api.syncSchedule(schedule);
        } else {
            cache.get(schedules.get(schedule.getCode()));
        }
    }

    /**
     * Adds a collection of schedules and sync their classes if required.
     * When there's no network connection, the classes are loaded from the
     * cache.
     *
     * @param schedules Schedules[]
     */
    public void add(Schedule[] schedules) {
        for (Schedule schedule : schedules) {
            add(schedule);

            if (this.schedules.size() == schedules.length && isAllSynced()) {
                ((MainActivity) activity).setPagerView();
            }
        }
    }

    /**
     * Get a schedule. When a schedule with the given code does not exist, a
     * new schedule with the code 'Unknown' is returned.
     *
     * @param code Schedule code
     * @return Schedule
     */
    public Schedule get(String code) {
        if (!has(code)) {
            return new Schedule("Unknown: ", "", true);
        }

        return schedules.get(code);
    }

    /**
     * Get the sorted map of schedules.
     *
     * @return LinkedHashMap
     */
    public LinkedHashMap<String, Schedule> get() {
        return schedules;
    }

    /**
     * Check whether or not a schedule exists.
     *
     * @param code Schedule code
     * @return Schedule exists
     */
    public boolean has(String code) {
        return schedules.containsKey(code);
    }

    /**
     * Get the amount of currently added schedules.
     *
     * @return Schedule count
     */
    public int size() {
        return schedules.size();
    }

    /**
     * Determined if a schedule should be synced or not. A schedule 'needs' a
     * sync when the last sync was more than CACHE_DAYS ago.
     *
     * @param schedule Schedule code
     * @return Should sync
     */
    public boolean shouldSync(String schedule) {
        if (!FileUtil.fileExists(ScheduleCache.FILE_NAME, FileUtil.TYPE_CACHE)) {
            return true;
        }

        return new Date().after(DateUtil.getDateByDayOffset(get(schedule).getSyncTime(), ScheduleCache.CACHE_DAYS));
    }

    /**
     * Sync all schedules when there's a network connection regardless of
     * schedule sync state.
     *
     * @return Syncing tasks successful created
     */
    public boolean syncAll() {
        if (!NetUtil.hasNetworkConnection(activity)) {
            return false;
        }

        clearCache();

        for (Map.Entry<String, Schedule> entry : schedules.entrySet()) {
            if (!entry.getValue().isEnabled()) {
                continue;
            }

            entry.getValue().setSynced(false);
            api.syncSchedule(entry.getValue());
        }

        return true;
    }

    /**
     * Checks or all active(enabled) schedules are in sync.
     *
     * @return All schedules synced
     */
    public boolean isAllSynced() {
        boolean isSynced = true;

        for (Map.Entry<String, Schedule> s : schedules.entrySet()) {
            if (!s.getValue().isEnabled()) {
                continue;
            }

            if (!s.getValue().isSynced()) {
                isSynced = false;
                break;
            }
        }

        return isSynced;
    }

    /**
     * Write schedule class list to the cache.
     *
     * @param schedule Schedule code
     * @return Writing successful
     */
    public boolean writeToCache(String schedule) {
        return has(schedule) && cache.write(get(schedule));
    }

    /**
     * Clear schedule class cache.
     *
     * @return Clearing successful
     */
    public boolean clearCache() {
        return cache.clear();
    }

    /**
     * Get the amount of active(enabled) schedules.
     *
     * @return Schedule count
     */
    public int getActiveSchedules() {
        int count = 0;

        for (Map.Entry<String, Schedule> s : schedules.entrySet()) {
            if (s.getValue().isEnabled()) count++;
        }

        return count;
    }

}
