package io.linksoft.schedules.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Class implements Parcelable {

    private String id;
    private String location;
    private Date timeStart;
    private Date timeEnd;
    private String comments;
    private String groups;
    private String className;
    private String[] tutors;

    public Class(JSONObject json) throws JSONException {
        JSONArray tutorArr = json.getJSONArray("docentnamen");
        String[] tutors = new String[tutorArr.length()];

        for (int j = 0; j < tutors.length; j++)
            tutors[j] = tutorArr.getString(j);

        this.id = json.getString("id");
        this.location = json.getString("lokaal");
        this.timeStart = new Date(json.getLong("starttijd"));
        this.timeEnd = new Date(json.getLong("eindtijd"));
        this.comments = json.getString("commentaar");
        this.groups = json.getString("groepcode");
        this.className = json.getString("vakcode");
        this.tutors = tutors;
    }

    private Class(Parcel in) {
        String[] data = new String[8];
        in.readStringArray(data);

        this.id = data[0];
        this.location = data[1];
        this.timeStart = new Date(Long.parseLong(data[2]));
        this.timeEnd = new Date(Long.parseLong(data[3]));
        this.comments = data[4];
        this.groups = data[5];
        this.className = data[6];
        this.tutors = data[7].split(", ");
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public String getComments() {
        return comments;
    }

    public String getGroups() {
        return groups;
    }

    public String getClassName() {
        return className;
    }

    public String[] getTutors() {
        return tutors;
    }

    public String getTutorString() {
        StringBuilder tutorStrBuilder = new StringBuilder();

        for (String n : tutors)
            tutorStrBuilder.append(n.replace("'", "\\'")).append("|");

        if (tutorStrBuilder.length() > 0)
            tutorStrBuilder.deleteCharAt(tutorStrBuilder.length() - 1);

        return tutorStrBuilder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
            this.id,
            this.location,
            String.valueOf(this.timeStart.getTime()),
            String.valueOf(this.timeEnd.getTime()),
            this.comments,
            this.groups,
            this.className,
            this.getTutorString()
        });
    }

    public static final Creator<Class> CREATOR = new Creator<Class>() {
        @Override
        public Class createFromParcel(Parcel in) {
            return new Class(in);
        }

        @Override
        public Class[] newArray(int size) {
            return new Class[size];
        }
    };

}
