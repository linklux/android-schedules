package io.linksoft.schedules.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.linksoft.schedules.R;

public class ClassHeaderViewHolder extends RecyclerView.ViewHolder {

    public final TextView title;

    public ClassHeaderViewHolder(View view) {
        super(view);

        title = (TextView) view.findViewById(R.id.class_header_title);
    }

}
