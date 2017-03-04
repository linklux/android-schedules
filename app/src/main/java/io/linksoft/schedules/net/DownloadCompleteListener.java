package io.linksoft.schedules.net;

import java.util.ArrayList;

import io.linksoft.schedules.data.Class;
import io.linksoft.schedules.data.Schedule;

public interface DownloadCompleteListener {

    void downloadComplete(Schedule schedule, ArrayList<Class> classes);

}
