package io.linksoft.schedules.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.linksoft.schedules.data.Class;

public class JSONUtil {

    public static ArrayList<Class> stringToClassList(String json) {
        if (json == null) return new ArrayList<>();

        JSONArray jsonArray;
        ArrayList<Class> classes = new ArrayList<>();

        try {
            jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                if (obj != null)
                    classes.add(new Class(obj));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return classes;
    }

}
