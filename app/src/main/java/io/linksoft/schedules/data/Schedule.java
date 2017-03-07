package io.linksoft.schedules.data;

import java.util.ArrayList;

import io.linksoft.schedules.R;

public class Schedule {

    private String code;
    private boolean enabled, synced;

    private ArrayList<Class> classes = new ArrayList<>();

    public Schedule(String code, boolean enabled) {
        this.code = code;
        this.enabled = enabled;
        this.synced = false;
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

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public ArrayList<Class> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<Class> classes) {
        this.classes = classes;
    }

    @Override
    public String toString() {
        return String.format("%s, classes: %s, enabled: %b, synced: %b", code, classes.size(), enabled, synced);
    }

}
