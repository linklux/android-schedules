package io.linksoft.schedules.viewholders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItemAdapter;

import io.linksoft.schedules.R;

public class ManageItemViewHolder extends DragItemAdapter.ViewHolder  {

    public final TextView txtLabel;
    public final CheckBox cbxEnabled;

    public ManageItemViewHolder(final View itemView, final int grabHandleID) {
        super(itemView, grabHandleID, false);

        txtLabel = (TextView) itemView.findViewById(R.id.manage_list_label);
        cbxEnabled = (CheckBox) itemView.findViewById(R.id.manage_list_enabled);
    }

}
