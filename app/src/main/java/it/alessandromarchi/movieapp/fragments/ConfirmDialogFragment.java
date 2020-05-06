package it.alessandromarchi.movieapp.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import it.alessandromarchi.movieapp.R;

public class ConfirmDialogFragment extends DialogFragment {

    private String title;
    private String message;
    private long movieID;

    private ConfirmDialogFragmentListener listener;

    public ConfirmDialogFragment(String title, String message, long movieID) {
        this.title = title;
        this.message = message;
        this.movieID = movieID;
    }

    public ConfirmDialogFragment(String message, long movieID) {
        this.message = message;
        this.movieID = movieID;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        if (activity instanceof ConfirmDialogFragmentListener) {
            listener = (ConfirmDialogFragmentListener) activity;
        } else {
            listener = null;
        }

        super.onAttach(activity);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

			String positiveButton;

        alertDialog.setTitle(title);
			alertDialog.setMessage(Html.fromHtml(message));

			if (title.equals(getString(R.string.add_title))) {
				positiveButton = getString(R.string.add);
			} else {
				positiveButton = getString(R.string.remove);
			}

			alertDialog.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onPositivePressed(movieID);
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onNegativePressed();
            }
        });

        return alertDialog.create();
    }
}
