package com.example.wemood.Fragments;
/**
 * @author Boyuan Dong
 *
 * @version 2.0
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Class name: RequestFragmentDialog
 *
 * Version 2.0
 *
 * Date: November 7, 2019
 *
 * Copyright [2019] [Team10, Fall CMPUT301, University of Alberta]
 */
public class RequestFragmentDialog extends DialogFragment {
    private String requestName;
    private OnFragmentInteractionListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnFragmentInteractionListener)context;
    }
    public interface OnFragmentInteractionListener {
        void AcceptRequest(String message);
        void DeclineRequest(String message);

    }

    public RequestFragmentDialog(String requestMessage) {
        requestName = requestMessage;
    }

    // Constructor
    public RequestFragmentDialog() {
        //Empty constructor
    }

    /**
     * Will Create a view of the RequestFragmentDialog
     * so that user can choose to accept the friend request or decline this request message.
     * @param savedInstanceState
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setTitle("Friend Request")
                .setMessage("Do you accept "+ requestName +" add you to be his/her friend so that he/she will be able to follow and view your most recent mood?")
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.DeclineRequest(requestName);
                    }
                })
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.AcceptRequest(requestName);
                    }
                }).create();
    }
}