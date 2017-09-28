package io.linksoft.schedules.viewholders;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;

import io.linksoft.schedules.R;

public class OrderItemViewHolder extends DragItemAdapter.ViewHolder  {

    public final TextView textView;

    public OrderItemViewHolder(final View itemView, final int grabHandleID) {
        super(itemView, grabHandleID, true);
        textView = (TextView) itemView.findViewById(R.id.order_list_label);
    }

    @Override
    public void onItemClicked(View view) {
        Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClicked(View view) {
        Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
        return true;
    }

}
