package com.whguardian.android.memo.MemoInfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.whguardian.android.memo.MemoData.MemoInfo;
import com.whguardian.android.memo.MemoData.MemoLab;
import com.whguardian.android.memo.R;

import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/07.
 * comment: 查看单条备忘信息
 */
public class MemoInfoFragment extends DialogFragment {
    private static final String TAG = "MemoInfoFragment";

    private static final String EXTRA_MEMO_ID =
            "com.whguardian.android.memo.memoInfoId";

    public static MemoInfoFragment newInstance(UUID memoId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_MEMO_ID, memoId);

        MemoInfoFragment fragment = new MemoInfoFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_info_meno, null);

        return new AlertDialog.Builder(getActivity())
                .setTitle("")
                .setView(v)
                .setPositiveButton(R.string.memo_edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getActivity(), MemoActivity.class);
                        i.putExtra(MemoActivity.HAS_UUID, true);
                        i.putExtra(MemoFragment.EXTRA_MEMO_ID,
                                getArguments().getSerializable(EXTRA_MEMO_ID));
                        startActivity(i);
                    }
                })
                .create();

    }
}
