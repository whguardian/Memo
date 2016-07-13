package com.whguardian.android.memo.Camera;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.whguardian.android.memo.R;

import java.util.UUID;

/**
 * Created by whguardian_control on 16/05/05.
 */
public class PhotoInfoFragment extends DialogFragment {
    private static final String EXTRA_UUID =
            "com.whguardian.android.memo.Camera.PhotoInfoFragment.uuid";

    private AlertDialog dialog;

    public static PhotoInfoFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putString(EXTRA_UUID, uuid.toString());

        PhotoInfoFragment fragment = new PhotoInfoFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_info_photo, null);
        dialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

        return dialog;
    }
}
