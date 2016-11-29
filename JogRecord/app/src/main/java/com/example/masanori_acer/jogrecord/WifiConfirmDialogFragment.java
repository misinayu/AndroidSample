package com.example.masanori_acer.jogrecord;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by MASANORI on 2016/11/22.
 */

public class WifiConfirmDialogFragment extends DialogFragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";

    private int mTitle;
    private int mMessage;

    public static DialogFragment newInstance(int title, int message) {
        WifiConfirmDialogFragment fragment = new WifiConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, title);
        args.putInt(ARG_MESSAGE, message);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null){
            mTitle = getArguments().getInt(ARG_TITLE);
            mMessage = getArguments().getInt(ARG_MESSAGE);
        }
        return new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setMessage(mMessage)
                .setNegativeButton(R.string.alert_dialog_no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        }
                )
                .setPositiveButton(R.string.alert_dialog_yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((MapsActivity) getActivity()).wifiOff();
                            }
                        }
                )
                .create();
    }
}
