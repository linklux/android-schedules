package io.linksoft.schedules.net;

import android.app.Activity;

import java.io.IOException;
import java.util.Date;

import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.util.JSONUtil;
import io.linksoft.schedules.util.NetUtil;
import okhttp3.OkHttpClient;

public class WindesheimApi {

    private static final String BASE_URL = "http://api.windesheim.nl/api/";
    private static final String EP_CLASS = "klas/{code}/les";

    private Activity activity;

    private OnScheduleCodeValidatedListener validationListener;
    private OnScheduleSyncedListener syncedListener;

    public WindesheimApi(Activity activity) {
        this.activity = activity;
    }

    public void setOnScheduleCodeValidatedListener(OnScheduleCodeValidatedListener validationListener) {
        this.validationListener = validationListener;
    }

    public void setOnScheduleSyncedListener(OnScheduleSyncedListener syncedListener) {
        this.syncedListener = syncedListener;
    }

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

    private boolean makeRequest(String url, okhttp3.Callback callback) {
        if (!NetUtil.hasNetworkConnection(activity)) return false;

        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();

        client.newCall(request).enqueue(callback);

        return true;
    }

    private String getUrl(String endPoint, String code) {
        if (code.isEmpty() || !endPoint.contains("{code}"))
            return BASE_URL + endPoint;

        return BASE_URL + endPoint.replace("{code}", code);
    }

    public interface OnScheduleCodeValidatedListener {

        void onScheduleCodeValidated(Schedule schedule, boolean exists);

    }

    public interface OnScheduleSyncedListener {

        void onScheduleSynced(Schedule schedule);

    }

}
