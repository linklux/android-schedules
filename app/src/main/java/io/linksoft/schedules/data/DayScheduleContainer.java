package io.linksoft.schedules.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class DayScheduleContainer implements Parcelable {

    private String code;
    private List<Class> classes;

    public DayScheduleContainer(String code, List<Class> classes) {
        this.code = code;
        this.classes = classes;
    }

    private DayScheduleContainer(Parcel in) {
        code = in.readString();

        this.classes = in.readArrayList(new ArrayList<Class>().getClass().getClassLoader());
    }

    public String getCode() {
        return code;
    }

    public List<Class> getClasses() {
        return classes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeTypedList(classes);
    }

    public static final Creator<DayScheduleContainer> CREATOR = new Creator<DayScheduleContainer>() {
        @Override
        public DayScheduleContainer createFromParcel(Parcel in) {
            return new DayScheduleContainer(in);
        }

        @Override
        public DayScheduleContainer[] newArray(int size) {
            return new DayScheduleContainer[size];
        }
    };

}