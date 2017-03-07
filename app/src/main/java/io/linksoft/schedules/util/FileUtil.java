package io.linksoft.schedules.util;

import android.app.Activity;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {

    public static final byte TYPE_NORMAL = 0x01;
    public static final byte TYPE_CACHE = 0x02;

    public static String readFile(Activity activity, String file, byte type) {
        StringBuilder text = new StringBuilder();
        File dir = type == TYPE_CACHE ? activity.getCacheDir() : activity.getFilesDir();

        try {
            BufferedReader br = new BufferedReader(new FileReader(dir + "/" + file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();

            return "";
        }

        return text.toString();
    }

    public static String readFile(Activity activity, String file) {
        return readFile(activity, file, TYPE_NORMAL);
    }

    public static boolean writeFile(Activity activity, String file, String data, byte type) {
        FileOutputStream fos;

        try {
            fos = activity.openFileOutput(file, Context.MODE_PRIVATE);

            fos.write(data.getBytes());
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

}
