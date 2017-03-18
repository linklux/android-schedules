package io.linksoft.schedules.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.linksoft.schedules.util.DateUtil;

public class DaySchedulesContainer implements Parcelable {

    private Date date;

    private HashMap<String, List<Class>> schedules = new HashMap<>();

    public DaySchedulesContainer(Date date, List<Schedule> schedules) {
        this.date = date;

        loadSchedules(schedules);
    }

    private DaySchedulesContainer(Parcel in) {
        date = new Date(in.readLong());

        List<DayScheduleContainer> days = new ArrayList<>();
        days = in.readArrayList(days.getClass().getClassLoader());

        for (DayScheduleContainer day : days)
            schedules.put(day.getCode(), day.getClasses());
    }

    private void loadSchedules(List<Schedule> schedules) {
        if (schedules == null || schedules.isEmpty()) return;

        for (Schedule schedule : schedules) {
            if (this.schedules.containsKey(schedule.getCode())) continue;
            List<Class> classes = new ArrayList<>();

            for (Class cls : schedule.getClasses()) {
                if (!DateUtil.areDaysEqual(date, cls.getTimeStart())) continue;

                classes.add(cls);
            }

            this.schedules.put(schedule.getCode(), classes);
        }
    }

    public HashMap<String, List<Class>> getSchedules() {
        return schedules;
    }

    public Date getDate() {
        return date;
    }

    public String getDay() {
        return new SimpleDateFormat("EEEE").format(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        List<DayScheduleContainer> schedules = new ArrayList<>();

        for (Map.Entry<String, List<Class>> entry : this.schedules.entrySet())
            schedules.add(new DayScheduleContainer(entry.getKey(), entry.getValue()));

        dest.writeLong(date.getTime());
        dest.writeList(schedules);
    }

    public static final Creator<DaySchedulesContainer> CREATOR = new Creator<DaySchedulesContainer>() {
        @Override
        public DaySchedulesContainer createFromParcel(Parcel in) {
            return new DaySchedulesContainer(in);
        }

        @Override
        public DaySchedulesContainer[] newArray(int size) {
            return new DaySchedulesContainer[size];
        }
    };

}
