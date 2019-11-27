package com.example.wemood;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wemood.Fragments.FriendUnfollowFragmentDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.ContentValues.TAG;


public class FriendsExist extends AppCompatActivity implements FriendUnfollowFragmentDialog.OnFragmentInteractionListener{
    ListView friendmoodList;
    ArrayAdapter<Mood> moodAdapter;
    ArrayList<Mood> moodDataList;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userName;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private DocumentReference documentReference;
    private FirebaseStorage storage;
    private StorageReference image;

    private TextView userNameView;
    private String searchName;

    private ImageView figureView;
    private TextView moodsView;
    private TextView followingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_exist_page);
        // Initialize FireBase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Database
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        userName = user.getDisplayName();
        user = mAuth.getCurrentUser();

        userNameView = findViewById(R.id.friend_view);
        friendmoodList = findViewById(R.id.mood_list);

        moodDataList = new ArrayList<>();
        Intent intent=getIntent();
        searchName=intent.getStringExtra("searchName");

        figureView = findViewById(R.id.friend_photo);
        moodsView = findViewById(R.id.mood_num);
        followingView = findViewById(R.id.following_num);

        Button backButton = findViewById(R.id.back_button);
        Button unfollowButton = findViewById(R.id.unfollow_button);
        setBackButton(backButton);
        setUnfollowButton(unfollowButton);

        getPhoto();
        updateMoods();
        updateFollowing();
        setSearchName();
        getMostRecentMood();


    }

    public void getPhoto(){
//        // Get and display figure
//        // Get storage and image
//        storage = getStorage();
//        image = storage.getReference().child("ImageFolder/" + mood.getUsername() + "/" + mood.getDatetime().toString());
//        image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(final Uri uri) {
//                Picasso.get().load(uri).into(FriendMoodPhoto);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//                FriendMoodPhoto.setImageResource(R.drawable.default_photo);
//            }
//        });
    }

    public void updateMoods() {
        collectionReference = db.collection("Users")
                .document(searchName)
                .collection("MoodList");
        collectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Count the number of moods
                            int numMoods = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Increment by 1 per iteration
                                numMoods += 1;
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            String moodsDisplay = "Moods\n%d";
                            moodsDisplay = String.format(moodsDisplay, numMoods);
                            moodsView.setText(moodsDisplay);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getMostRecentMood(){
        collectionReference = db.collection("Users")
                .document(searchName)
                .collection("MoodList");
        collectionReference
                .orderBy("datetime", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Mood mood = document.toObject(Mood.class);
                            mood.setUsername(searchName);
                            moodDataList.add(mood);
                        }
                        Collections.sort(moodDataList, Collections.reverseOrder());
                        moodAdapter = new FriendMoodList(getBaseContext(), moodDataList);
                        friendmoodList.setAdapter(moodAdapter);
                    }
                });
    }

    public void updateFollowing() {
        // Get collection reference
        documentReference = db.collection("Users")
                .document(searchName);
        documentReference
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        ArrayList<String> friendList = user.getFriendList();
                        int numFollowing = friendList.size();
//                        if (friendList.size() = null){
//                        }
                        String followingDisplay = "Following\n%d";
                        followingDisplay = String.format(followingDisplay, numFollowing);
                        followingView.setText(followingDisplay);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FriendsExist.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        });
    }

    public void setSearchName(){
        // Get and display username
        userNameView.setText(searchName);
    }

    public void setBackButton(Button backButton){
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setUnfollowButton(Button unfollowButton){
        unfollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FriendUnfollowFragmentDialog().show(getSupportFragmentManager(), "Unfollow Pressed");
            }
        });
    }

    @Override
    public void UnfollowRequest(){
        collectionReference = db.collection("Users");
        collectionReference.document(userName)
                .update("friendList", FieldValue.arrayRemove(searchName));
    }
}