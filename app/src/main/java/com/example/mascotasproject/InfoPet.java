package com.example.mascotasproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class InfoPet extends DialogFragment {
    public static final String ARGUMENTO_TITTLE = "TITLE";
    public static final String ARGUMENTO_FULL_SNIPPET = "FULL_SNIPPET";

    public  String title;
    public  String fullSnippet;

    public static InfoPet newIntance(String title, String fullSnippet){
        InfoPet fragment = new InfoPet();
        Bundle b = new Bundle();
        b.putString(ARGUMENTO_TITTLE, title);
        b.putString(ARGUMENTO_FULL_SNIPPET, fullSnippet);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        title = args.getString(ARGUMENTO_TITTLE);
        fullSnippet = args.getString(ARGUMENTO_FULL_SNIPPET);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog =  new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(fullSnippet).create();
        return dialog;
    }
}
