package io.linksoft.schedules.data;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.linksoft.schedules.util.FileUtil;
import io.linksoft.schedules.util.JSONUtil;

public class ScheduleCache {

    public static final String FILE_NAME = "schedules.json";
    public static final int CACHE_DAYS = 1;

    private JSONObject schedules = new JSONObject();

    private Activity activity;

    public ScheduleCache(Activity activity) {
        this.activity = activity;

        load();
    }

    private boolean load() {
        try {
            if (FileUtil.fileExists(activity, FILE_NAME, FileUtil.TYPE_CACHE)) {
                schedules = new JSONObject(FileUtil.readFile(activity, FILE_NAME, FileUtil.TYPE_CACHE));
            } else {
                schedules = new JSONObject();
            }
        } catch (JSONException e) {
            e.printStackTrace();

            schedules = new JSONObject();
        }

        return true;
    }

    public boolean has(String schedule) {
        return schedules.has(schedule);
    }

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

    public boolean write(Schedule schedule) {
        JSONArray json = JSONUtil.classListToJsonArray(schedule.getClasses());
        if (json.length() <= 0) return false;

        try {
            schedules.put(schedule.getCode(), json);
            FileUtil.writeFile(activity, FILE_NAME, schedules.toString(), FileUtil.TYPE_CACHE);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean clear() {
        return FileUtil.deleteFile(activity, FILE_NAME, FileUtil.TYPE_CACHE);
    }

}
