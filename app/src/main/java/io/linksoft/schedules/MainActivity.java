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
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import io.linksoft.schedules.adapters.DayViewPagerAdapter;
import io.linksoft.schedules.adapters.ScheduleViewPagerAdapter;
import io.linksoft.schedules.adapters.ViewPagerAdapter;
import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.data.Settings;
import io.linksoft.schedules.fragments.AddDialogFragment;
import io.linksoft.schedules.fragments.DayViewPagerFragment;
import io.linksoft.schedules.layouts.CustomSwipeRefreshLayout;
import io.linksoft.schedules.net.WindesheimApi;
import io.linksoft.schedules.util.DateUtil;
import io.linksoft.schedules.util.SchedulesUtil;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AddDialogFragment.OnScheduleAddListener, SwipeRefreshLayout.OnRefreshListener, WindesheimApi.WindesheimApiListener, ViewPager.OnPageChangeListener {

    // TODO Not hardcode views, reflection might be an option
    private static final int VIEW_DAY = 1;
    private static final int VIEW_SCHEDULE = 2;

    private int activeView;

    private ViewPager mPager;
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private ViewPagerAdapter pagerAdapter;

    private Settings settings;
    private WindesheimApi api;

    private SchedulesUtil schedules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = new Settings(this);
        api = new WindesheimApi(this, this);
        schedules = new SchedulesUtil(this, api);

        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);

        mSwipeRefreshLayout = (CustomSwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        String viewSetting = settings.getOption("view");
        activeView = viewSetting.isEmpty() ? VIEW_DAY : Integer.parseInt(viewSetting);

        registerSchedules();
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
        } else if (id == R.id.schedule_save) {
            reload();
        } else if (id == R.id.schedules_remove) {
            schedules.removeInactive(settings);
            reload();
        } else if (id == R.id.toggle_view) {
            settings.writeOption("view", String.valueOf((activeView == VIEW_DAY ? VIEW_SCHEDULE : VIEW_DAY)));
            settings.save();

            reload();
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
        api.validateScheduleCode(code);
    }

    @Override
    public void onRefresh() {
        if (schedules.getActiveSchedules() == 0)
            mSwipeRefreshLayout.setRefreshing(false);

        schedules.syncAll();
    }

    @Override
    public void onScheduleCodeValidated(String code, boolean exists) {
        if (!exists) {
            Toast.makeText(getApplicationContext(), "Unable to add schedule, it either doesn't exists or is already added.", Toast.LENGTH_SHORT).show();
            return;
        }

        Schedule schedule = new Schedule(code, true);

        settings.writeSchedule(schedule);
        settings.save();

        reload();
    }

    @Override
    public void onScheduleSynced(Schedule schedule) {
        schedules.writeToCache(schedule.getCode());
        settings.writeSchedule(schedule);
        settings.save();

        if (schedules.isAllSynced()) {
            if (mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);

            setPagerView();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (activeView != VIEW_DAY) return;
        Date date = ((DayViewPagerFragment) pagerAdapter.getItem(position)).getDay().getDate();

        getSupportActionBar().setTitle(DateUtil.getFormattedTime(date, DateFormat.LONG));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void setPagerView() {
        if (mPager == null) {
            mPager = (ViewPager) findViewById(R.id.pager);

            mPager.addOnPageChangeListener(this);
        }

        if (mPager.getAdapter() == null) {
            if (activeView == VIEW_DAY) {
                pagerAdapter = new DayViewPagerAdapter(getSupportFragmentManager(), schedules.get());
            } else if (activeView == VIEW_SCHEDULE) {
                pagerAdapter = new ScheduleViewPagerAdapter(getSupportFragmentManager(), schedules.get());
            }

            mPager.setAdapter(pagerAdapter);

            if (activeView != VIEW_DAY) return;
            Date curDate = DateUtil.getStartOfDay(new Date());
            curDate = DateUtil.isWeekend(curDate) ? DateUtil.getWeekStart(curDate, 1) : curDate;

            mPager.setCurrentItem(pagerAdapter.getItemPosition(curDate));
            getSupportActionBar().setTitle(DateUtil.getFormattedTime(curDate, DateFormat.LONG));
        } else {
            pagerAdapter.notifyDataSetChanged();
        }
    }

    private void registerSchedules() {
        schedules.set(settings.getSchedules());

        Menu subMenu = ((NavigationView) findViewById(R.id.nav_view)).getMenu().addSubMenu("Current schedules");
        int itemID = schedules.size() - 1;

        for (Map.Entry<String, Schedule> entry : schedules.get().entrySet()) {
            MenuItem item = subMenu.add(0, Menu.NONE, itemID--, entry.getValue().getCode());
            item.setIcon(entry.getValue().getToggleIcon());
        }
    }

    private boolean handleScheduleClick(MenuItem item) {
        String title = item.getTitle().toString();
        if (!schedules.has(title)) return false;

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
