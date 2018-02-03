package io.linksoft.schedules.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.cachapa.expandablelayout.ExpandableLayout;

import io.linksoft.schedules.R;

public class ClassItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final TextView timeFrame;
    public final TextView location;
    public final TextView name;

    public final TextView tutors;
    public final TextView comments;

    public boolean extraInfoEnabled;
    public final ExpandableLayout expandableLayout;

    public ClassItemViewHolder(View view) {
        super(view);

        timeFrame = view.findViewById(R.id.class_item_timeframe);
        location = view.findViewById(R.id.class_item_location);
        name = view.findViewById(R.id.class_item_name);

        tutors = view.findViewById(R.id.class_item_tutors);
        comments = view.findViewById(R.id.class_item_comments);

        expandableLayout = view.findViewById(R.id.expandable_layout);
        extraInfoEnabled = true;

        (view.findViewById(R.id.root_view)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (extraInfoEnabled) {
            expandableLayout.toggle();
        }
    }

}
