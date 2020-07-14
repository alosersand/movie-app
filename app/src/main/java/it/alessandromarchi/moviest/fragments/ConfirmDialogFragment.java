package it.alessandromarchi.moviest.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import it.alessandromarchi.moviest.R;

public class ConfirmDialogFragment extends DialogFragment {

	private ConfirmDialogFragmentListener listener;

	private String message;
	private String title;

	private long movieID;

	private AlertDialog.Builder alertDialog;

	public ConfirmDialogFragment(String title, String message, long movieID) {
		this.title = title;
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

		if (getActivity() != null) {
			alertDialog = new AlertDialog.Builder(getActivity());
		}

		alertDialog.setTitle(title);
		alertDialog.setMessage(Html.fromHtml(message));

		String positiveButton;
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
