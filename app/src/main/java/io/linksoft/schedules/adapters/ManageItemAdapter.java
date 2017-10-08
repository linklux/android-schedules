package io.linksoft.schedules.adapters;

import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.viewholders.ManageItemViewHolder;

public class ManageItemAdapter extends DragItemAdapter<Pair<Long, Schedule>, ManageItemViewHolder> {

    private int layoutID;
    private int grabHandleID;

    private OnScheduleDeleteListener listener;

    public ManageItemAdapter(ArrayList<Pair<Long, Schedule>> list, int layoutID, int grabHandleID) {
        this.layoutID = layoutID;
        this.grabHandleID = grabHandleID;

        setHasStableIds(true);
        setItemList(list);
    }

    /**
     * Set the callback instance for schedule delete actions.
     *
     * @param listener OnScheduleDeleteListener
     */
    public void setOnScheduleDeleteListener(OnScheduleDeleteListener listener) {
        this.listener = listener;
    }

    @Override
    public ManageItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutID, parent, false);

        return new ManageItemViewHolder(view, grabHandleID);
    }

    @Override
    public void onBindViewHolder(final ManageItemViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        final Schedule schedule = mItemList.get(position).second;

        holder.txtLabel.setText(schedule.getCode());
        holder.cbxEnabled.setChecked(schedule.isEnabled());
        holder.itemView.setTag(mItemList.get(position));

        holder.cbxEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                schedule.setEnabled(b);
                mItemList.set(position, new Pair<>((long) position, schedule));
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onScheduleDelete(holder, schedule);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

    public interface OnScheduleDeleteListener {
        void onScheduleDelete(ManageItemViewHolder holder, Schedule schedule);
    }

}
