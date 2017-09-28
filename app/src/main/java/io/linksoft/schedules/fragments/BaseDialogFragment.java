package io.linksoft.schedules.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class BaseDialogFragment extends DialogFragment {

    protected OnDialogActionListener listener;

    /**
     * Default constructor is used to setup the required fragment base data
     */
    public BaseDialogFragment() {
        setupFragment();
    }

    /**
     * Setup a base dialog fragment transaction. Handles the removing of a
     * previous fragment if it exists and adds itself to the backstack.
     *
     * @return FragmentTransaction
     */
    private FragmentTransaction setupTransaction(Activity activity) {
        FragmentManager manager = activity.getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment prev = manager.findFragmentByTag("dialog");

        if (prev != null)
            ft.remove(prev);

        ft.addToBackStack(null);

        return ft;
    }

    /**
     * Set required base data for the dialog fragment.
     */
    protected void setupFragment() {
        Bundle args = new Bundle();

        this.setArguments(args);
    }

    /**
     * Sets up the transaction and shows itself.
     */
    public void show(Activity activity) {
        show(setupTransaction(activity), "dialog");
    }

    /**
     * Set the callback instance for this dialog.
     *
     * @param listener OnDialogActionListener
     */
    public void setOnDialogActionListener(OnDialogActionListener listener) {
        this.listener = listener;
    }

    public interface OnDialogActionListener {

        void onDialogActionSubmit(boolean reload);

    }

}
