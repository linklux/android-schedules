package io.linksoft.schedules.data;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.linksoft.schedules.util.FileUtil;

public class Settings {

    private static final String FILE_NAME = "settings.json";

    private Activity activity;

    private JSONObject settings;

    public Settings(Activity activity) {
        this.activity = activity;
        this.settings = new JSONObject();

        load();
    }

    private boolean load() {
        try {
            settings = new JSONObject(FileUtil.readFile(activity, FILE_NAME));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean save() {
        return FileUtil.writeFile(activity, FILE_NAME, settings.toString(), FileUtil.TYPE_NORMAL);
    }

    public String getOption(String name) {
        Object value = getOptionValue(name);

        return value == null ? "" : (String) value;
    }

    public Schedule getSchedule(String code) {
        JSONArray value = (JSONArray) getOptionValue("schedules");
        if (value == null) return null;

        Schedule schedule = null;

        try {
            JSONObject obj = null;

            for (int i = 0; i < value.length(); i++) {
                JSONObject o = value.getJSONObject(i);

                if (o.get("code").toString().equals(code)) {
                    obj = o;
                    break;
                }
            }

            schedule = new Schedule(obj.getString("code"), obj.getBoolean("enabled"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return schedule;
    }

    public Schedule[] getSchedules() {
        JSONArray value = (JSONArray) getOptionValue("schedules");
        if (value == null) return new Schedule[0];

        Schedule[] schedules = new Schedule[value.length()];

        try {
            for (int i = 0; i < schedules.length; i++) {
                String code = value.getJSONObject(i).getString("code");
                schedules[i] = getSchedule(code);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return schedules;
    }

    public boolean writeOption(String name, String data) {
        try {
            if (getOptionValue(name) != null)
                settings.remove(name);

            settings.put(name, data);
        } catch (JSONException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public boolean writeSchedule(Schedule schedule) {
        JSONArray value = (JSONArray) getOptionValue("schedules");

        try {
            if (value == null) settings.put("schedules", new JSONArray());
            int index = settings.getJSONArray("schedules").length();

            for (int i = 0; i < settings.getJSONArray("schedules").length(); i++) {
                if (settings.getJSONArray("schedules").getJSONObject(i).get("code").equals(schedule.getCode())) {
                    index = i;
                    break;
                }
            }

            JSONObject json = new JSONObject();
            json.put("code", schedule.getCode());
            json.put("enabled", schedule.isEnabled());

            settings.getJSONArray("schedules").put(index, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean removeSchedule(Schedule schedule) {
        JSONArray value = (JSONArray) getOptionValue("schedules");
        if (value == null) return false;

        boolean isRemoved = false;

        try {
            for (int i = 0; i < value.length(); i++) {
                if (value.getJSONObject(i).get("code").equals(schedule.getCode())) {
                    settings.getJSONArray("schedules").remove(i);

                    isRemoved = true;
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isRemoved;
    }

    private Object getOptionValue(String name) {
        if (!settings.has(name)) return null;
        Object value = "";

        try {
            value = settings.get(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return value;
    }

}
