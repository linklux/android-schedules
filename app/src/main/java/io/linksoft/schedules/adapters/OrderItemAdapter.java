package io.linksoft.schedules.adapters;

import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

import io.linksoft.schedules.viewholders.OrderItemViewHolder;

public class OrderItemAdapter extends DragItemAdapter<Pair<Long, String>, OrderItemViewHolder> {

    private int layoutID;
    private int grabHandleID;

    public OrderItemAdapter(ArrayList<Pair<Long, String>> list, int layoutID, int grabHandleID) {
        this.layoutID = layoutID;
        this.grabHandleID = grabHandleID;
        setHasStableIds(true);
        setItemList(list);
    }

    @Override
    public OrderItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutID, parent, false);

        return new OrderItemViewHolder(view, grabHandleID);
    }

    @Override
    public void onBindViewHolder(OrderItemViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        String text = mItemList.get(position).second;
        holder.textView.setText(text);
        holder.itemView.setTag(mItemList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

}
