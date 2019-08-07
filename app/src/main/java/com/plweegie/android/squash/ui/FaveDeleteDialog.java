package com.plweegie.android.squash.ui;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import com.plweegie.android.squash.R;
import com.plweegie.android.squash.adapters.FaveAdapter;

public class FaveDeleteDialog extends DialogFragment {

    private FaveAdapter.FaveAdapterOnClickHandler mClickHandler;
    private long mRepoId;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.fave_delete)
                .setPositiveButton(R.string.fave_delete_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mClickHandler.onItemClick(mRepoId);
                    }
                })
                .setNegativeButton(R.string.fave_delete_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

        return builder.create();
    }

    public void setClickHandler(FaveAdapter.FaveAdapterOnClickHandler handler, long repoId) {
        mClickHandler = handler;
        mRepoId = repoId;
    }
}
