package com.powerdata.barcode.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.powerdata.barcode.R;

public class AlertDialogFragment extends DialogFragment {

    private String title;
    private String message;
    private AlertDialogListener listener;

    public AlertDialogFragment() {
    }

    void setTitle(String title) {
        this.title = title;
    }

    void setMessage(String message) {
        this.message = message;
    }

    public void setListener(AlertDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null)
                            listener.onDialogPositiveClick(AlertDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null)
                            listener.onDialogNegativeClick(AlertDialogFragment.this);
                    }
                });
        return builder.create();
    }

    public interface AlertDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }
}
