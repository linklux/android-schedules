package io.linksoft.schedules.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.linksoft.schedules.R;

public class ClassItemViewHolder extends RecyclerView.ViewHolder {

    public final TextView timeFrame;
    public final TextView location;
    public final TextView name;

    public ClassItemViewHolder(View view) {
        super(view);

        timeFrame = (TextView) view.findViewById(R.id.class_item_timeframe);
        location = (TextView) view.findViewById(R.id.class_item_location);
        name = (TextView) view.findViewById(R.id.class_item_name);
    }

}
