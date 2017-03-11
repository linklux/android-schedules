package io.linksoft.schedules.util;

import java.util.HashMap;
import java.util.Map;

import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.data.Settings;
import io.linksoft.schedules.net.WindesheimApi;

public class SchedulesUtil {

    private HashMap<String, Schedule> schedules = new HashMap<>();

    public SchedulesUtil() {
    }

    public void add(Schedule schedule) {
        schedules.put(schedule.getCode(), schedule);
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

    public void syncAll(WindesheimApi api) {
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

}
