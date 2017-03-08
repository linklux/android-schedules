package io.linksoft.schedules;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.linksoft.schedules.adapters.SchedulePagerAdapter;
import io.linksoft.schedules.data.Class;
import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.data.Settings;
import io.linksoft.schedules.fragments.AddDialogFragment;
import io.linksoft.schedules.net.DownloadCompleteListener;
import io.linksoft.schedules.util.NetUtil;
import okhttp3.OkHttpClient;

// TODO Clean up this class, it's a mess...
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AddDialogFragment.OnScheduleAddListener, DownloadCompleteListener {

    private HashMap<String, Schedule> schedules = new HashMap<>();

    private Toolbar mToolbar;
    private ViewPager mPager;

    private Settings settings;

    private static final int ID_APPLY = 1;
    private static final int ID_REMOVE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = new Settings(this);

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        registerSchedules();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        settings.save();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        boolean shouldClose = true;

        if (id == R.id.schedule_add) {
            showDialog();

            shouldClose = true;
        } else if (id == R.id.schedules_refresh) {
            syncAllScheduleClasses();
        } else if (id == ID_APPLY) {
            reload();
        } else if (id == ID_REMOVE) {
            removeInactiveSchedules();
        } else if (id == 0) {
            shouldClose = false;
            if (!handleScheduleClick(item)) return false;
        }

        if (shouldClose)
            ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onScheduleAdded(@NonNull String code) {
        addScheduleIfExists(code);
    }

    @Override
    public void downloadComplete(Schedule schedule, ArrayList<Class> classes) {
        schedule.setClasses(classes);
        schedule.setSynced(true);
        mToolbar.setTitle(schedule.getCode());

        if (allSchedulesSynced()) setPagerView();
    }

    private void setPagerView() {
        if (mPager == null) {
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setOffscreenPageLimit(schedules.size() / 2 + 1);
        }

        if (mPager.getAdapter() == null)
            mPager.setAdapter(new SchedulePagerAdapter(getSupportFragmentManager(), schedules));
    }

    private void registerSchedules() {
        for (Schedule schedule : settings.getSchedules())
            schedules.put(schedule.getCode(), schedule);

        Menu subMenu = ((NavigationView) findViewById(R.id.nav_view)).getMenu().addSubMenu("Current schedules");
        int itemID = schedules.size() + 1;

        MenuItem itemRemove = subMenu.add(0, ID_REMOVE, itemID--, "Remove inactive");
        itemRemove.setIcon(R.drawable.ic_delete);

        MenuItem itemApply = subMenu.add(0, ID_APPLY, itemID--, "Save changes");
        itemApply.setIcon(R.drawable.ic_save);

        for (Map.Entry<String, Schedule> entry : schedules.entrySet()) {
            MenuItem item = subMenu.add(0, Menu.NONE, itemID--, entry.getValue().getCode());
            item.setIcon(entry.getValue().getToggleIcon());
        }

        syncAllScheduleClasses();
    }

    private void removeInactiveSchedules() {
        for (Map.Entry<String, Schedule> entry : schedules.entrySet())
            if (!entry.getValue().isEnabled())
                settings.removeSchedule(entry.getValue());

        settings.save();

        reload();
    }

    private boolean syncScheduleClasses(final Schedule schedule) {
        if (!NetUtil.hasNetworkConnection(this)) return false;
        if (schedule.isSynced()) return false;

        String url = "http://api.windesheim.nl/api/klas/" + schedule.getCode() + "/les";
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                final String result = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        downloadComplete(schedule, stringToClassArray(result));
                    }
                });
            }
        });

        return true;
    }

    private boolean addScheduleIfExists(final String code) {
        if (!NetUtil.hasNetworkConnection(this)) return false;

        String url = "http://api.windesheim.nl/api/klas/" + code + "/les";
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                final String result = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (schedules.containsKey(code)) {
                            Toast.makeText(getApplicationContext(), "Schedule already exists.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (result.equals("[]")) {
                            Toast.makeText(getApplicationContext(), "Couldn't find a schedule by that name.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Schedule schedule = new Schedule(code, true);

                        settings.writeSchedule(schedule);
                        settings.save();

                        reload();
                    }
                });
            }
        });

        return true;
    }

    private void syncAllScheduleClasses() {
        if (!NetUtil.hasNetworkConnection(this))
            Toast.makeText(getApplicationContext(), "Unable to update schedules, no network connection", Toast.LENGTH_SHORT).show();

        for (Map.Entry<String, Schedule> schedule : schedules.entrySet()) {
            if (!schedule.getValue().isEnabled()) continue;

            schedule.getValue().setSynced(false);
            syncScheduleClasses(schedule.getValue());
        }
    }

    private boolean allSchedulesSynced() {
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

    private boolean handleScheduleClick(MenuItem item) {
        String title = item.getTitle().toString();
        if (!schedules.containsKey(title)) return false;

        Schedule schedule = schedules.get(title);

        schedule.setEnabled(!schedule.isEnabled());
        item.setIcon(schedule.getToggleIcon());

        settings.writeSchedule(schedule);
        settings.save();

        refreshMenu();

        return true;
    }

    private void reload() {
        Intent intent = getIntent();

        finish();
        startActivity(intent);
    }

    private void refreshMenu() {
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        for (int i = 0, count = mNavigationView.getChildCount(); i < count; i++) {
            final View child = mNavigationView.getChildAt(i);
            if (child == null || !(child instanceof ListView)) continue;

            final ListView menuView = (ListView) child;
            final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
            final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();

            wrapped.notifyDataSetChanged();
        }
    }

    private ArrayList<Class> stringToClassArray(String json) {
        if (json == null) return new ArrayList<>();

        JSONArray jsonArray;
        ArrayList<Class> classes = new ArrayList<>();
        try {
            jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                if (obj != null)
                    classes.add(new Class(obj));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return classes;
    }

    private void showDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");

        if (prev != null)
            ft.remove(prev);

        ft.addToBackStack(null);

        AddDialogFragment addDialog = AddDialogFragment.newInstance();
        addDialog.setOnScheduleAddSubmitListener(this);
        addDialog.show(ft, "dialog");
    }
}
