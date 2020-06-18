package it.alessandromarchi.moviest.fragments;

public interface ConfirmDialogFragmentListener {
    void onPositivePressed(long movieID);

    void onNegativePressed();
}
