package io.linksoft.schedules.data;

import android.app.Activity;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.linksoft.schedules.util.FileUtil;

public class Settings {

    // Preferences stored in Android's SharedPreferences
    public static final String PREF_WIFI_ONLY = "wifi-only-sync";
    public static final String PREF_LOAD_WEEKS = "week-load-size";

    // Self managed preferences
    public static final String PREF_VIEW = "view";
    public static final String PREF_SCHEDULE_LIST = "schedules";
    public static final String PREF_SCHEDULE_ORDER = "schedule_order";

    private static final String FILE_NAME = "settings.json";

    private Activity activity;
    private JSONObject settings;

    public Settings(Activity activity) {
        this.activity = activity;
        this.settings = new JSONObject();

        load();
    }

    /**
     * Read the settings file and store the data in memory. If the file does
     * not exist yet, a new one will be created.
     *
     * @return Whether or not loading was successful.
     */
    private boolean load() {
        try {
            if (FileUtil.fileExists(activity, FILE_NAME, FileUtil.TYPE_NORMAL)) {
                settings = new JSONObject(FileUtil.readFile(activity, FILE_NAME));
            } else {
                settings = new JSONObject();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Write any changes to the settings file.
     *
     * @return Settings successfully written
     */
    public boolean save() {
        return FileUtil.writeFile(activity, FILE_NAME, settings.toString(), FileUtil.TYPE_NORMAL);
    }

    /**
     * Fetch an option from the custom settings file. If the setting is not
     * found, attempt to retrieve it from Android's SharedPreferences.
     *
     * @param name The setting name
     * @return string Settings value
     */
    public String getOption(String name) {
        Object value = getOptionValue(name);

        if (value == null)
            value = PreferenceManager.getDefaultSharedPreferences(activity).getString(name, "");

        return (String) value;
    }

    /**
     * Get a schedule from the settings.
     *
     * @param code The schedule code
     * @return Schedule
     */
    public Schedule getSchedule(String code) {
        JSONArray value = (JSONArray) getOptionValue(PREF_SCHEDULE_LIST);
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

            schedule = new Schedule(
                obj.getString("code"),
                obj.getString("label"),
                obj.getBoolean("enabled"),
                new Date(obj.getLong("syncTime"))
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return schedule;
    }

    /**
     * Fetch all schedules from the stored data as an ordered array if the
     * Settings.PREF_SCHEDULE_ORDER setting exists. If the setting does exist,
     * but a schedule is not added yet, it will be added *after* the existing
     * ones.
     *
     * @return Schedule[]
     */
    public Schedule[] getSchedules() {
        JSONArray value = (JSONArray) getOptionValue(PREF_SCHEDULE_LIST);
        if (value == null) return new Schedule[0];

        String[] sortOrder = getOption(PREF_SCHEDULE_ORDER).split(",");
        Schedule[] schedules = new Schedule[value.length()];

        // TODO Improve sorting
        try {
            for (int i = 0; i < sortOrder.length; i++) {
                String key = sortOrder[i];

                for (int j = 0; j < schedules.length; j++) {
                    if (i >= schedules.length || schedules[i] != null) continue;

                    String code = value.getJSONObject(j).getString("code");
                    if (code.equals(key)) {
                        schedules[i] = getSchedule(code);

                        break;
                    }
                }
            }

            for (int i = 0; i < schedules.length; i++) {
                if (schedules[i] != null) continue;

                String code = value.getJSONObject(i).getString("code");
                schedules[i] = getSchedule(code);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return schedules;
    }

    /**
     * Write an option to the local option data. Be sure to call settings.save()
     * when done writing all options.
     *
     * @param name Option name
     * @param data Option data
     * @return Option successfully written
     */
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

    /**
     * Write a schedule to the local schedule list. Be sure to call settings.save()
     * when done writing all options.
     *
     * @param schedule Schedule
     * @return Schedule successfully written
     */
    public boolean writeSchedule(Schedule schedule) {
        JSONArray value = (JSONArray) getOptionValue(PREF_SCHEDULE_LIST);

        try {
            if (value == null) settings.put(PREF_SCHEDULE_LIST, new JSONArray());
            int index = settings.getJSONArray(PREF_SCHEDULE_LIST).length();

            for (int i = 0; i < settings.getJSONArray(PREF_SCHEDULE_LIST).length(); i++) {
                if (settings.getJSONArray(PREF_SCHEDULE_LIST).getJSONObject(i).get("code").equals(schedule.getCode())) {
                    index = i;
                    break;
                }
            }

            JSONObject json = new JSONObject();
            json.put("code", schedule.getCode());
            json.put("label", schedule.getLabel());
            json.put("enabled", schedule.isEnabled());
            json.put("syncTime", schedule.getSyncTime().getTime());

            settings.getJSONArray(PREF_SCHEDULE_LIST).put(index, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Remove a schedule from the local schedule list. Be sure to call
     * settings.save() when done writing all options.
     *
     * @param schedule Schedule
     * @return Schedule successfully removed
     */
    public boolean removeSchedule(Schedule schedule) {
        JSONArray value = (JSONArray) getOptionValue(PREF_SCHEDULE_LIST);
        if (value == null) return false;

        boolean isRemoved = false;

        try {
            for (int i = 0; i < value.length(); i++) {
                if (value.getJSONObject(i).get("code").equals(schedule.getCode())) {
                    settings.getJSONArray(PREF_SCHEDULE_LIST).remove(i);

                    isRemoved = true;
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isRemoved;
    }

    /**
     * Retrieves the value for the given option as a string. If the option does
     * not exist, null is returned.
     *
     * @param name Option name
     * @return String|null
     */
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
