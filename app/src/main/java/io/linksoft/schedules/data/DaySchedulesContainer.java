package io.linksoft.schedules.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import io.linksoft.schedules.util.DateUtil;

public class DaySchedulesContainer implements Parcelable {

    private Date date;

    private LinkedHashMap<String, ArrayList<Class>> schedules = new LinkedHashMap<>();

    public DaySchedulesContainer(Date date, ArrayList<Schedule> schedules) {
        this.date = date;

        loadSchedules(schedules);
    }

    private DaySchedulesContainer(Parcel in) {
        date = new Date(in.readLong());

        ArrayList<DayScheduleContainer> days = in.readArrayList(DayScheduleContainer.class.getClassLoader());
        for (DayScheduleContainer day : days) {
            schedules.put(day.getCode(), day.getClasses());
        }
    }

    private void loadSchedules(ArrayList<Schedule> schedules) {
        if (schedules == null || schedules.isEmpty()) {
            return;
        }

        for (Schedule schedule : schedules) {
            if (this.schedules.containsKey(schedule.getCode())) {
                continue;
            }

            ArrayList<Class> classes = new ArrayList<>();
            for (Class cls : schedule.getClasses()) {
                if (!DateUtil.areDaysEqual(date, cls.getTimeStart())) {
                    continue;
                }

                classes.add(cls);
            }

            this.schedules.put(schedule.getFullName(), classes);
        }
    }

    public LinkedHashMap<String, ArrayList<Class>> getSchedules() {
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
        ArrayList<DayScheduleContainer> schedules = new ArrayList<>();

        for (Map.Entry<String, ArrayList<Class>> entry : this.schedules.entrySet()) {
            schedules.add(new DayScheduleContainer(entry.getKey(), entry.getValue()));
        }

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
