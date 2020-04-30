package it.alessandromarchi.movieapp.fragments;

public interface ConfirmDialogFragmentListener {
    void onPositivePressed(long movieID);

    void onNegativePressed();
}
