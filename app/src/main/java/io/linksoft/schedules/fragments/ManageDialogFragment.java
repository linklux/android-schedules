package io.linksoft.schedules.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import io.linksoft.schedules.R;
import io.linksoft.schedules.adapters.ManageItemAdapter;
import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.data.Settings;
import io.linksoft.schedules.viewholders.ManageItemViewHolder;

public class ManageDialogFragment extends BaseDialogFragment implements ManageItemAdapter.OnScheduleDeleteListener {

    private String scheduleOrder;
    private ArrayList<Pair<Long, Schedule>> schedules;
    private HashMap<String, Schedule> schedulesDelete;

    /**
     * Updates the schedule order string.
     */
    private void setScheduleOrder() {
        int i = 0;
        scheduleOrder = "";

        for (Pair<Long, Schedule> pair : schedules) {
            // Skip the schedule if it will be deleted
            if (schedulesDelete.containsKey(pair.second.getCode())) continue;

            scheduleOrder += pair.second.getCode() + (i++ < schedules.size() - 1 ? "," : "");
        }
    }

    /**
     * Write the schedule data to the settings file.
     */
    private void save() {
        Settings settings = new Settings(getActivity());

        // Write all schedules to handle property updates
        for (Pair<Long, Schedule> pair : schedules)
            settings.writeSchedule(pair.second);

        // Delete inactive schedules if present
        for (Schedule schedule : schedulesDelete.values())
            settings.removeSchedule(schedule);

        // Write the schedule order string
        setScheduleOrder();
        settings.writeOption(Settings.PREF_SCHEDULE_ORDER, scheduleOrder);

        settings.save();
    }

    /**
     * Set the schedules to be displayed in the reorder list.
     *
     * @param schedules SchedulesUtil
     */
    public void setSchedules(LinkedHashMap<String, Schedule> schedules) {
        this.schedules = new ArrayList<>(schedules.size());
        this.schedulesDelete = new HashMap<>(schedules.size());

        long i = 0;

        for (Schedule schedule : schedules.values()) {
            this.schedules.add(new Pair<>(i++, schedule));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manage_dialog, container, false);
        DragListView dragListView = (DragListView) v.findViewById(R.id.drag_list_view);

        dragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        dragListView.setDragListListener(new DragListView.DragListListenerAdapter() {
            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                if (fromPosition == toPosition) return;

                setScheduleOrder();
            }
        });

        ManageItemAdapter listAdapter = new ManageItemAdapter(schedules, R.layout.list_manage_item, R.id.manage_grab_drag_handle);
        listAdapter.setOnScheduleDeleteListener(this);
        dragListView.setAdapter(listAdapter, false);

        dragListView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        dragListView.setCanDragHorizontally(false);
        dragListView.setCustomDragItem(new MyDragItem(v.getContext(), R.layout.list_manage_item));

        final Button btnSave = (Button) v.findViewById(R.id.manage_dialog_save);
        final Button btnCancel = (Button) v.findViewById(R.id.manage_dialog_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();

                listener.onDialogActionSubmit(true);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        setScheduleOrder();

        return v;
    }

    @Override
    public void onScheduleDelete(ManageItemViewHolder holder, Schedule schedule) {
        boolean isRemoved = !schedulesDelete.containsKey(schedule.getCode());
        holder.lsiContainer.setBackgroundColor(isRemoved ? 0xBCF44242 : 0xFFFFFFFF);

        if (isRemoved) {
            schedulesDelete.put(schedule.getCode(), schedule);
        } else {
            schedulesDelete.remove(schedule.getCode());
        }
    }

    private static class MyDragItem extends DragItem {

        MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.manage_list_label)).getText();
            ((TextView) dragView.findViewById(R.id.manage_list_label)).setText(text);
            dragView.findViewById(R.id.manage_grab_drag_handle).setElevation(5f);
        }

    }

}
