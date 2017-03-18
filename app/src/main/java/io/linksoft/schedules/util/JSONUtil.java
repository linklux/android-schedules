package io.linksoft.schedules.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.linksoft.schedules.data.Class;

public class JSONUtil {

    public static ArrayList<Class> jsonArrToClassList(JSONArray array) {
        if (array == null) return new ArrayList<>();
        ArrayList<Class> classes = new ArrayList<>();

        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                if (obj != null)
                    classes.add(new Class(obj));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return classes;
    }

    public static ArrayList<Class> stringToClassList(String json) {
        if (json == null) return new ArrayList<>();

        try {
            return jsonArrToClassList(new JSONArray(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static JSONArray classListToJsonArray(ArrayList<Class> classes) {
        if (classes == null || classes.isEmpty()) return new JSONArray();
        JSONArray json = new JSONArray();

        try {
            for (Class cls : classes) {
                JSONObject obj = new JSONObject();
                JSONArray tutors = new JSONArray();

                for (String tutor : cls.getTutors())
                    tutors.put(tutor);

                obj.put("id", cls.getId());
                obj.put("lokaal", cls.getLocation());
                obj.put("starttijd", cls.getTimeStart().getTime());
                obj.put("eindtijd", cls.getTimeEnd().getTime());
                obj.put("commentaar", cls.getComments());
                obj.put("groepcode", cls.getGroups());
                obj.put("vakcode", cls.getClassName());
                obj.put("docentnamen", tutors);

                json.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

}
