package io.linksoft.schedules.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {

    public static final byte TYPE_NORMAL = 0x01;
    public static final byte TYPE_CACHE = 0x02;

    private static File fileCache;
    private static File fileNormal;

    /**
     * Get the base directory for storing the given file type.
     *
     * @param type     File type
     * @return Base directory as File
     */
    private static File getTypeDir(byte type) {
        return type == TYPE_CACHE ? fileCache : fileNormal;
    }

    /**
     * Attempt to read the given file. Support for regular files and for the
     * custom cache files.
     *
     * @param file     Filename
     * @param type     The file type, this can be either of TYPE_NORMAL or TYPE_CACHE
     * @return File contents as string
     */
    public static String readFile(String file, byte type) {
        StringBuilder text = new StringBuilder();

        if (!fileExists(file, type)) {
            return "";
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(getTypeDir(type) + "/" + file));
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

    /**
     * Attempt to read the given file as a regular file.
     *
     * @param file     Filename
     * @return File contents as string
     */
    public static String readFile(String file) {
        return readFile(file, TYPE_NORMAL);
    }

    /**
     * Attempt to write the data to the given file. Support for regular files
     * and for the custom cache files.
     *
     * @param file     File name
     * @param data     File data
     * @param type     File type
     * @return Writing successful
     */
    public static boolean writeFile(String file, String data, byte type) {
        try {
            FileOutputStream fos = new FileOutputStream(getTypeDir(type) + "/" + file, false);

            fos.write(data.getBytes());
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    /**
     * Attempt to delete the given file. Support for regular files and for the
     * custom cache files.
     *
     * @param file     File name
     * @param type     File type
     * @return Deleting successful
     */
    public static boolean deleteFile(String file, byte type) {
        return new File(getTypeDir(type) + "/" + file).delete();
    }

    /**
     * Validate whether or not the given file exists.
     *
     * @param file     File name
     * @param type     File type
     * @return File exists
     */
    public static boolean fileExists(String file, byte type) {
        return (new File(getTypeDir(type) + "/" + file).exists());
    }

    /**
     * Set the file location for the cache file. MUST be set before using the
     * cache file related utility methods.
     *
     * @param file file location
     */
    public static void setCacheStorageFile(File file) {
        fileCache = file;
    }

    /**
     * Set the file location for the normal storage file. MUST be set before using the
     * normal file related utility methods.
     *
     * @param file file location
     */
    public static void setNormalStorageFile(File file) {
        fileNormal = file;
    }

}
