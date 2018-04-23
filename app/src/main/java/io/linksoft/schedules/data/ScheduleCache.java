package io.linksoft.schedules.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.linksoft.schedules.util.FileUtil;
import io.linksoft.schedules.util.JSONUtil;

public class ScheduleCache {

    public static final String FILE_NAME = "schedules.json";
    public static final int CACHE_DAYS = 1;

    private JSONObject schedules = new JSONObject();

    public ScheduleCache() {
        load();
    }

    /**
     * Read the cache file and store the data in memory. If the file does not
     * exist yet, a new one will be created.
     *
     * @return Whether or not loading was successful
     */
    private boolean load() {
        try {
            if (FileUtil.fileExists(FILE_NAME, FileUtil.TYPE_CACHE)) {
                schedules = new JSONObject(FileUtil.readFile(FILE_NAME, FileUtil.TYPE_CACHE));
            } else {
                schedules = new JSONObject();
            }
        } catch (JSONException e) {
            e.printStackTrace();

            schedules = new JSONObject();
        }

        return true;
    }

    /**
     * Check if a cache entry is available for the given schedule.
     *
     * @param schedule Schedule
     * @return boolean
     */
    public boolean has(String schedule) {
        return schedules.has(schedule);
    }

    /**
     * Retrieve the classes for this schedule from the cache and store them in
     * the schedule instance. If the cache entry is non-existent, the schedule
     * will remain unchanged.
     *
     * @param schedule Schedule
     * @return Schedule including classes
     */
    public Schedule get(Schedule schedule) {
        if (!has(schedule.getCode())) return schedule;

        try {
            JSONArray json = schedules.getJSONArray(schedule.getCode());

            schedule.setClasses(JSONUtil.jsonArrToClassList(json));
            schedule.setSynced(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return schedule;
    }

    /**
     * Write the classes for this schedule to the cache. Old data is ignored
     * and will be overwritten without warning.
     *
     * @param schedule Schedule
     * @return Schedule data was successfully written
     */
    public boolean write(Schedule schedule) {
        JSONArray json = JSONUtil.classListToJsonArray(schedule.getClasses());
        if (json.length() <= 0) return false;

        try {
            schedules.put(schedule.getCode(), json);
            FileUtil.writeFile(FILE_NAME, schedules.toString(), FileUtil.TYPE_CACHE);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Clear ALL schedule cache data.
     *
     * @return Cache deleted successfully
     */
    public boolean clear() {
        return FileUtil.deleteFile(FILE_NAME, FileUtil.TYPE_CACHE);
    }

}
