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
        final EditText text = (EditText) v.findViewById(R.id.add_dialog_class);
        final Button button = (Button) v.findViewById(R.id.add_dialog_submit);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (text == null || listener == null) return;
                if (text.getText().toString().isEmpty()) return;

                listener.onScheduleAdded(text.getText().toString());
            }
        });

        return v;
    }

    public void setOnScheduleAddSubmitListener(@NonNull OnScheduleAddListener listener) {
        this.listener = listener;
    }

    public interface OnScheduleAddListener {

        void onScheduleAdded(@NonNull String code);

    }

}
