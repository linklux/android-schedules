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

        if (!fileExists(activity, file, type)) return "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(getTypeDir(activity, type) + "/" + file));
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
        try {
            FileOutputStream fos = new FileOutputStream(getTypeDir(activity, type) + "/" + file, false);

            fos.write(data.getBytes());
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public static boolean deleteFile(Activity activity, String file, byte type) {
        return new File(getTypeDir(activity, type) + "/" + file).delete();
    }

    public static boolean fileExists(Activity activity, String file, byte type) {
        return (new File(getTypeDir(activity, type) + "/" + file).exists());
    }

    private static File getTypeDir(Activity activity, byte type) {
        return type == TYPE_CACHE ? activity.getCacheDir() : activity.getFilesDir();
    }

}
