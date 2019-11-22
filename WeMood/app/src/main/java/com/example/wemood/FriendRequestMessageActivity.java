package com.example.wemood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.wemood.Fragments.RequestFragmentDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FriendRequestMessageActivity extends AppCompatActivity implements RequestFragmentDialog.OnFragmentInteractionListener {

    // Declare the variables so that you will be able to reference it later.
    private ListView messageList;
    private ArrayAdapter<String> messageAdapter;
    public ArrayList<String> dataMessageList;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private DocumentReference documentReference;

    private String userName;
    public  ArrayList<String> waitfriendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request_message);

        // Initialize FireBase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Database
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        userName = user.getDisplayName();
        user = mAuth.getCurrentUser();


        messageList = findViewById(R.id.friend_request_message_list);

        // click massage item to jump a Friend Request Dialog
        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String requestMessage = dataMessageList.get(i);
                new RequestFragmentDialog(requestMessage).show(getSupportFragmentManager(), "Friend Request");
            }
        });


    }

    @Override
    public void onResume(){
        super.onResume();
        dataMessageList = new ArrayList<>();
        getWaitList();
    }


    public void getWaitList(){
        collectionReference = db.collection("Users");
        collectionReference.document(userName).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot!=null){
                            User user = documentSnapshot.toObject(User.class);
                            waitfriendList = user.getWaitFriendList();
                            dataMessageList.addAll(waitfriendList);

                        }

                        messageAdapter = new FriendRequestList(getBaseContext(), dataMessageList);
                        messageList.setAdapter(messageAdapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), "Error!", Toast.LENGTH_SHORT).show();
                Log.d(ContentValues.TAG, e.toString());
            }
        });

    }


    // Will implement later
    @Override
    // Decline the friend request
    public void DeclineRequest(String message){
        messageAdapter.remove(message);
        collectionReference = db.collection("Users");
        collectionReference.document(userName)
                .update("waitFriendList", FieldValue.arrayRemove(message));

    }


    @Override
    // Accept the friend request
    public void AcceceptRequest(String message){
        messageAdapter.remove(message);
        collectionReference = db.collection("Users");
        collectionReference.document(userName)
                .update("friendList",FieldValue.arrayUnion(message));
        collectionReference.document(userName)
                .update("waitFriendList",FieldValue.arrayRemove(message));

    }
}
