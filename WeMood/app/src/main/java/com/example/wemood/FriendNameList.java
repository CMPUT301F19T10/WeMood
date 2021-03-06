package com.example.wemood;
/**
 * @author Alpha Hou
 *
 * @version 2.0
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Class name: FriendNameList
 *
 * Version 2.0
 *
 * Date: November 7, 2019
 *
 * Copyright [2019] [Team10, Fall CMPUT301, University of Alberta]
 */

public class FriendNameList extends ArrayAdapter<String> {
    private List<String> friends;
    private Context context;

    /**
     * Constructor
     * @param context
     * @param friends
     */
    public FriendNameList(Context context, ArrayList<String> friends) {
        super(context, 0, friends);
        this.friends = friends;
        this.context = context;
    }

    /**
     * Create the view of FriendNameList. Will display a list of user's names.
     * @param position
     * @param convertView
     * @param parent
     * @return view
     */
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.friend_list, parent, false);
        }

        String friendName = friends.get(position);

        TextView FriendName = view.findViewById(R.id.friend_view);

        FriendName.setText(friendName);

        return view;
    }
}