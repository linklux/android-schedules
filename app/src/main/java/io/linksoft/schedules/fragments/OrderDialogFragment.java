package io.linksoft.schedules.fragments;

import android.app.DialogFragment;
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
import java.util.LinkedHashMap;

import io.linksoft.schedules.R;
import io.linksoft.schedules.adapters.OrderItemAdapter;
import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.data.Settings;

public class OrderDialogFragment extends DialogFragment {

    private OnScheduleOrderSubmittedListener listener;

    private String scheduleOrder;
    private ArrayList<Pair<Long, String>> schedules;

    public static OrderDialogFragment newInstance() {
        OrderDialogFragment fragment = new OrderDialogFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_dialog, container, false);
        DragListView dragListView = (DragListView) v.findViewById(R.id.drag_list_view);

        dragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        dragListView.setDragListListener(new DragListView.DragListListenerAdapter() {
            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                if (fromPosition == toPosition) return;

                int i = 0;
                scheduleOrder = "";

                for (Pair<Long, String> pair : schedules) {
                    scheduleOrder += pair.second + (i++ < schedules.size() - 1 ? "," : "");
                }
            }
        });

        OrderItemAdapter listAdapter = new OrderItemAdapter(schedules, R.layout.list_order_item, R.id.order_grab_drag_handle);
        dragListView.setAdapter(listAdapter, false);

        dragListView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        dragListView.setCanDragHorizontally(false);
        dragListView.setCustomDragItem(new MyDragItem(v.getContext(), R.layout.list_order_item));

        final Button btnSave = (Button) v.findViewById(R.id.order_dialog_save);
        final Button btnCancel = (Button) v.findViewById(R.id.order_dialog_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Settings settings = new Settings(getActivity());
                settings.writeOption(Settings.PREF_SCHEDULE_ORDER, scheduleOrder);
                settings.save();

                listener.onScheduleOrderSubmitted();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return v;
    }

    public void setOnScheduleOrderSubmittedListener(OnScheduleOrderSubmittedListener listener) {
        this.listener = listener;
    }

    public void setSchedules(LinkedHashMap<String, Schedule> schedules) {
        this.schedules = new ArrayList<>(schedules.size());
        long i = 0;

        for (Schedule schedule : schedules.values()) {
            this.schedules.add(new Pair<>(i++, schedule.getCode()));
        }
    }

    private static class MyDragItem extends DragItem {

        MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.order_list_label)).getText();
            ((TextView) dragView.findViewById(R.id.order_list_label)).setText(text);
            dragView.findViewById(R.id.order_grab_drag_handle).setElevation(5f);
        }

    }

    public interface OnScheduleOrderSubmittedListener {

        void onScheduleOrderSubmitted();

    }

}
