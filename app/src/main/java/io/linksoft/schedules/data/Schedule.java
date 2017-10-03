package io.linksoft.schedules.data;

import java.util.ArrayList;
import java.util.Date;

public class Schedule {

    private String code, label;
    private boolean enabled, synced;

    private Date syncTime;

    private ArrayList<Class> classes = new ArrayList<>();

    public Schedule(String code, String label, boolean enabled, Date syncTime) {
        this.code = code;
        this.label = label;
        this.enabled = enabled;
        this.syncTime = syncTime;

        synced = false;
    }

    public Schedule(String code, String label, boolean enabled) {
        this(code, label, enabled, new Date(0));
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getFullName() {
        return code + (!label.isEmpty() ? " (" + label + ")" : "");
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

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
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
