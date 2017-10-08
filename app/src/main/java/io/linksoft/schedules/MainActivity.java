package io.linksoft.schedules;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

import io.linksoft.schedules.adapters.DayViewPagerAdapter;
import io.linksoft.schedules.adapters.ScheduleViewPagerAdapter;
import io.linksoft.schedules.adapters.ViewPagerAdapter;
import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.data.Settings;
import io.linksoft.schedules.fragments.AddDialogFragment;
import io.linksoft.schedules.fragments.BaseDialogFragment;
import io.linksoft.schedules.fragments.DayViewPagerFragment;
import io.linksoft.schedules.fragments.ManageDialogFragment;
import io.linksoft.schedules.layouts.CustomSwipeRefreshLayout;
import io.linksoft.schedules.net.WindesheimApi;
import io.linksoft.schedules.util.DateUtil;
import io.linksoft.schedules.util.SchedulesUtil;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity
    implements
        NavigationView.OnNavigationItemSelectedListener,
        BaseDialogFragment.OnDialogActionListener,
        SwipeRefreshLayout.OnRefreshListener,
        WindesheimApi.OnScheduleSyncedListener,
        ViewPager.OnPageChangeListener {

    private static final int VIEW_DAY = 1;
    private static final int VIEW_SCHEDULE = 2;

    private int activeView;

    private ViewPager pager;
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private ViewPagerAdapter pagerAdapter;

    private Settings settings;
    private SchedulesUtil schedules;

    /**
     * Simple activity reload.
     */
    private void reload() {
        Intent intent = getIntent();

        finish();
        startActivity(intent);
    }

    /**
     * Handles a menu item click from the navigation drawer and from the
     * options menu.
     *
     * @param item The clicked menu item
     *
     * @return Whether or not the navigation drawer should be closed
     */
    private boolean handleMenuAction(MenuItem item) {
        int id = item.getItemId();
        boolean shouldClose = true;

        // TODO Might be worth looking into the use of the command pattern here
        if (id == R.id.schedule_add) {
            AddDialogFragment dialog = new AddDialogFragment();

            dialog.setOnDialogActionListener(this);
            dialog.show(this);
        } else if (id == R.id.schedule_manage) {
            ManageDialogFragment dialog = new ManageDialogFragment();

            dialog.setSchedules(schedules.get());
            dialog.setOnDialogActionListener(this);
            dialog.show(this);
        } else if (id == R.id.toggle_view) {
            settings.writeOption("view", String.valueOf((activeView == VIEW_DAY ? VIEW_SCHEDULE : VIEW_DAY)));
            settings.save();

            reload();
        } else if (id == R.id.settings_view) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return shouldClose;
    }

    /**
     * Initialize the ViewPager and / or its adapter if it hasn't been already.
     * If both are already properly initialized, notify the adapter of a data
     * change.
     */
    public void setPagerView() {
        // TODO Check if this can be handled by the different pager fragments
        if (pager == null) {
            pager = (ViewPager) findViewById(R.id.pager);

            pager.addOnPageChangeListener(this);
        }

        if (pager.getAdapter() == null) {
            int displayWeeks = Integer.parseInt(settings.getOption(Settings.PREF_LOAD_WEEKS));

            if (activeView == VIEW_DAY) {
                pagerAdapter = new DayViewPagerAdapter(getSupportFragmentManager(), displayWeeks, schedules.get());
            } else if (activeView == VIEW_SCHEDULE) {
                pagerAdapter = new ScheduleViewPagerAdapter(getSupportFragmentManager(), displayWeeks, schedules.get());
            }

            pager.setAdapter(pagerAdapter);

            if (activeView != VIEW_DAY) return;
            Date curDate = DateUtil.getStartOfDay(new Date());
            curDate = DateUtil.isWeekend(curDate) ? DateUtil.getWeekStart(curDate, 1) : curDate;

            pager.setCurrentItem(pagerAdapter.getItemPosition(curDate));
            getSupportActionBar().setTitle(DateUtil.getFormattedTime(curDate, DateFormat.LONG));
        } else {
            pagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize all settings with their default value as defined by the 'android:defaultValue' property
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        WindesheimApi api = new WindesheimApi(this);
        api.setOnScheduleSyncedListener(this);

        settings = new Settings(this);
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

        String viewSetting = settings.getOption(Settings.PREF_VIEW);
        activeView = viewSetting.isEmpty() ? VIEW_DAY : Integer.parseInt(viewSetting);

        schedules.add(settings.getSchedules());
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
        handleMenuAction(item);

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (handleMenuAction(item))
            ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onRefresh() {
        if (schedules.getActiveSchedules() == 0)
            mSwipeRefreshLayout.setRefreshing(false);

        if (!schedules.syncAll()) {
            if (mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);

            Toast.makeText(getApplicationContext(), "No internet connection detected!", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onDialogActionSubmit(boolean reload) {
        if (reload) {
            reload();
        }
    }

}
