package io.linksoft.schedules.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.linksoft.schedules.R;
import io.linksoft.schedules.data.Schedule;
import io.linksoft.schedules.data.Settings;
import io.linksoft.schedules.net.WindesheimApi;

public class AddDialogFragment extends BaseDialogFragment implements WindesheimApi.OnScheduleCodeValidatedListener{

    private WindesheimApi api;

    /**
     * Validates a schedule.
     *
     * @param schedule Schedule
     */
    private void validateSchedule(@NonNull Schedule schedule) {
        api.validateSchedule(schedule);
    }

    @Override
    public void onScheduleCodeValidated(Schedule schedule, boolean exists) {
        if (!exists) {
            Toast.makeText(getActivity().getApplicationContext(), "Unable to add schedule, it either doesn't exists or is already added.", Toast.LENGTH_SHORT).show();
            return;
        }

        Settings settings = new Settings(getActivity());
        settings.writeSchedule(schedule);
        settings.save();

        listener.onDialogActionSubmit(true);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        api = new WindesheimApi(getActivity());
        api.setOnScheduleCodeValidatedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_dialog, container, false);

        final EditText code = (EditText) v.findViewById(R.id.add_dialog_class);
        final EditText label = (EditText) v.findViewById(R.id.add_dialog_label);
        final Button btnSubmit = (Button) v.findViewById(R.id.add_dialog_submit);
        final Button btnCancel = (Button) v.findViewById(R.id.add_dialog_cancel);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code == null || listener == null) return;
                if (code.getText().toString().isEmpty()) return;

                Schedule schedule = new Schedule(
                    code.getText().toString(),
                    label.getText().toString(),
                    true
                );

                validateSchedule(schedule);
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

}
