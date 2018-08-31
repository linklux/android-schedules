package io.linksoft.schedules.net;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.Date;

import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.util.JSONUtil;
import io.linksoft.schedules.util.NetUtil;
import okhttp3.OkHttpClient;

public class WindesheimApi {

    private static final String BASE_URL = "http://api.windesheim.nl/api/";
    private static final String EP_CLASS = "klas/{code}/les";
    private static final String CLASS_LIST = "klas";

    private Activity activity;

    private OnScheduleCodeValidatedListener validationListener;
    private OnScheduleSyncedListener syncedListener;
    private OnClassListSyncedListener classListListener;

    public WindesheimApi(Activity activity) {
        this.activity = activity;
    }

    /**
     * Make a request to the Windesheim API.
     *
     * @param url      Full URL
     * @param callback Callback when finished
     * @return Request successful
     */
    private boolean makeRequest(String url, okhttp3.Callback callback) {
        if (!NetUtil.hasNetworkConnection(activity)) return false;

        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();

        client.newCall(request).enqueue(callback);

        return true;
    }

    /**
     * Generate the full URL for the given endpoint.
     *
     * @param endPoint Endpoint URL
     * @param code     Class code
     * @return Full URL
     */
    private String getUrl(String endPoint, String code) {
        if (code == null || code.isEmpty() || !endPoint.contains("{code}"))
            return BASE_URL + endPoint;

        return BASE_URL + endPoint.replace("{code}", code);
    }

    /**
     * Set the callback to be used when a schedule is validated.
     *
     * @param validationListener OnScheduleCodeValidatedListener
     */
    public void setOnScheduleCodeValidatedListener(OnScheduleCodeValidatedListener validationListener) {
        this.validationListener = validationListener;
    }

    /**
     * Set the callback to be used when a schedule is synced.
     *
     * @param syncedListener OnScheduleSyncedListener
     */
    public void setOnScheduleSyncedListener(OnScheduleSyncedListener syncedListener) {
        this.syncedListener = syncedListener;
    }

    /**
     * Set the callback to be used when the class list is synced.
     *
     * @param classListListener OnScheduleSyncedListener
     */
    public void setOnClassListSyncedListener(OnClassListSyncedListener classListListener) {
        this.classListListener = classListListener;
    }

    /**
     * Attempt to sync the given schedule. Since this is an async task, notifying
     * is achieved via the callback.
     *
     * @param schedule Schedule
     */
    public void syncSchedule(final Schedule schedule) {
        makeRequest(getUrl(EP_CLASS, schedule.getCode()), new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                final String result = response.body().string();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        schedule.setClasses(JSONUtil.stringToClassList(result));
                        schedule.setSynced(true);
                        schedule.setSyncTime(new Date());

                        syncedListener.onScheduleSynced(schedule);
                    }
                });
            }
        });
    }

    /**
     * Attempt to validate the given schedule. Since this is an async task, notifying
     * is achieved via the callback.
     *
     * @param schedule
     */
    public void validateSchedule(final Schedule schedule) {
        makeRequest(getUrl(EP_CLASS, schedule.getCode()), new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                final String result = response.body().string();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        validationListener.onScheduleCodeValidated(schedule, !result.equals("[]"));
                    }
                });
            }
        });
    }

    /**
     * Fetch a list of available class IDs as a string array. Useful for showing suggestions while
     * adding a schedule for example.
     */
    public void fetchClassList() {
        makeRequest(getUrl(CLASS_LIST, null), new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                try {
                    // Fetch the result and parse the JSON array response.
                    final String result = response.body().string();
                    final JSONArray json = new JSONArray(result);

                    // Create a string array with with the capacity to contain all returned items.
                    final String[] list = new String[json.length()];

                    // Iterate over the JSON response and fetch the class code from the object.
                    for(int i = 0, count = json.length(); i< count; i++) {
                        list[i] = json.getJSONObject(i).getString("code");
                    }

                    // We're done, notify the listener with the result.
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            classListListener.onClassListSynced(list);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();

                    // Just notify the listener with a NULL value in case something went wrong
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            classListListener.onClassListSynced(null);
                        }
                    });
                }
            }
        });
    }

    public interface OnScheduleCodeValidatedListener {

        void onScheduleCodeValidated(Schedule schedule, boolean exists);

    }

    public interface OnScheduleSyncedListener {

        void onScheduleSynced(Schedule schedule);

    }

    public interface OnClassListSyncedListener {

        void onClassListSynced(String[] list);

    }

}
