package io.linksoft.schedules;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
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

import io.linksoft.schedules.data.Class;
import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.fragments.ClassSectionFragment;
import io.linksoft.schedules.net.DownloadCompleteListener;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DownloadCompleteListener {

    private HashMap<String, Schedule> schedules = new HashMap<>();
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        if (findViewById(R.id.fragment_container) != null && savedInstanceState != null)
            showListFragment(new ArrayList<Class>());

        registerSchedules();
    }

    private void showListFragment(ArrayList<Class> classes) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ClassSectionFragment.newInstance(classes)).commit();
    }

    private void registerSchedules() {
        schedules.put("ICTSE2a", new Schedule("ICTSE2a", false));
        schedules.put("ICTSE2b", new Schedule("ICTSE2b", false));
        schedules.put("ICTSE2c", new Schedule("ICTSE2c", true));

        syncAllScheduleClasses();

        Menu subMenu = ((NavigationView) findViewById(R.id.nav_view)).getMenu().addSubMenu("Current schedules");

        for (Map.Entry<String, Schedule> entry : schedules.entrySet()) {
            MenuItem item = subMenu.add(entry.getValue().getCode());
            item.setIcon(entry.getValue().getToggleIcon());
        }
    }

    private void syncScheduleClasses(final Schedule schedule) {
        if (!hasNetworkConnection()) return;

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
                        try {
                            downloadComplete(schedule, stringToClassArray(result));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void syncAllScheduleClasses() {
        if (!hasNetworkConnection())
            Toast.makeText(getApplicationContext(), "Unable to update schedules, no network connection", Toast.LENGTH_SHORT).show();

        for (Map.Entry<String, Schedule> schedule : schedules.entrySet()) {
            if (!schedule.getValue().isEnabled()) return;

            syncScheduleClasses(schedule.getValue());
        }
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
            Toast.makeText(getApplicationContext(), "Requested action not yet implemented", Toast.LENGTH_SHORT).show();

            shouldClose = false;
        } else if (id == R.id.schedules_refresh) {
            syncAllScheduleClasses();
        } else if (id == 0) {
            if (!handleScheduleClick(item)) return false;
        }

        if (shouldClose)
            ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void downloadComplete(Schedule schedule, ArrayList<Class> classes) {
        schedule.setClasses(classes);

        mToolbar.setTitle(schedule.getCode());
        showListFragment(schedule.getClasses());
    }

    private boolean handleScheduleClick(MenuItem item) {
        String title = item.getTitle().toString();
        if (!schedules.containsKey(title)) return false;

        resetScheduleMenu();
        Schedule schedule = schedules.get(title);

        schedule.setEnabled(!schedule.isEnabled());
        item.setIcon(schedule.getToggleIcon());

        if (schedule.isEnabled() && schedule.getClasses().isEmpty()) {
            syncScheduleClasses(schedule);
        } else if (schedule.isEnabled()) {
            mToolbar.setTitle(schedule.getCode());
            showListFragment(schedule.getClasses());
        }

        refreshMenu();

        return true;
    }

    private void resetScheduleMenu() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu().getItem(2).getSubMenu();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);

            if (item.getItemId() != 0) continue;
            if (!schedules.containsKey(item.getTitle().toString())) continue;

            schedules.get(item.getTitle().toString()).setEnabled(false);
            item.setIcon(schedules.get(item.getTitle().toString()).getToggleIcon());
        }
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

    private ArrayList<Class> stringToClassArray(String json) throws JSONException {
        if (json == null) return new ArrayList<>();

        JSONArray jsonArray = new JSONArray(json);
        ArrayList<Class> classes = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            if (obj != null)
                classes.add(new Class(obj));
        }

        return classes;
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

}
