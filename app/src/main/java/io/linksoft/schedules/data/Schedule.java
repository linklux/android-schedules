package io.linksoft.schedules.data;

import java.util.ArrayList;

import io.linksoft.schedules.R;

public class Schedule {

    private String code;
    private boolean enabled;

    private ArrayList<Class> classes = new ArrayList<>();

    public Schedule(String code, boolean enabled) {
        this.code = code;
        this.enabled = enabled;
    }

    public int getToggleIcon() {
        return enabled ? R.drawable.ic_toggle_active : R.drawable.ic_toggle_inactive;
    }

    public String getCode() {
        return code;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ArrayList<Class> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<Class> classes) {
        this.classes = classes;
    }

}
