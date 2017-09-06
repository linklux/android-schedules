package io.linksoft.schedules.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import io.linksoft.schedules.R;
import io.linksoft.schedules.data.Schedule;

public class AddDialogFragment extends DialogFragment {

    OnScheduleAddListener listener;

    public static AddDialogFragment newInstance() {
        AddDialogFragment f = new AddDialogFragment();
        Bundle args = new Bundle();

        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_dialog, container, false);

        final EditText code = (EditText) v.findViewById(R.id.add_dialog_class);
        final EditText label = (EditText) v.findViewById(R.id.add_dialog_label);
        final Button button = (Button) v.findViewById(R.id.add_dialog_submit);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (code == null || listener == null) return;
                if (code.getText().toString().isEmpty()) return;

                Schedule schedule = new Schedule(
                    code.getText().toString(),
                    label.getText().toString(),
                    true
                );

                listener.onScheduleAdded(schedule);
            }
        });

        return v;
    }

    public void setOnScheduleAddSubmitListener(@NonNull OnScheduleAddListener listener) {
        this.listener = listener;
    }

    public interface OnScheduleAddListener {

        void onScheduleAdded(@NonNull Schedule schedule);

    }

}
