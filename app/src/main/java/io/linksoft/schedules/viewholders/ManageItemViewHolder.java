package io.linksoft.schedules.viewholders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.swipe.ListSwipeItem;

import io.linksoft.schedules.R;

public class ManageItemViewHolder extends DragItemAdapter.ViewHolder  {

    public final ListSwipeItem lsiContainer;
    public final TextView txtLabel;
    public final CheckBox cbxEnabled;
    public final ImageButton btnDelete;

    public ManageItemViewHolder(final View itemView, final int grabHandleID) {
        super(itemView, grabHandleID, false);

        lsiContainer = (ListSwipeItem) itemView.findViewById(R.id.manage_grab_drag_handle);
        txtLabel = (TextView) itemView.findViewById(R.id.manage_list_label);
        cbxEnabled = (CheckBox) itemView.findViewById(R.id.manage_list_enabled);
        btnDelete = (ImageButton) itemView.findViewById(R.id.manage_list_delete);
    }

}
