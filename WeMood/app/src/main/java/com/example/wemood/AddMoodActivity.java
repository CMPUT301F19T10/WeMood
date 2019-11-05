package com.example.wemood;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;

public class AddMoodActivity extends AppCompatActivity{
    Mood mood;
    private StorageReference Folder;
    ImageView imageView;
    Uri imageUri;
    private static final int PICK_IMAGE = 100;
    String situationString, emotionString;
    private FirebaseFirestore db;
    String name;
    Date currentTime = Calendar.getInstance().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood);

        db = FirebaseFirestore.getInstance();
        Folder = FirebaseStorage.getInstance().getReference().child("ImageFolder");
        final CollectionReference collectionReference = db.collection("Users");

        imageView = (ImageView) findViewById(R.id.imageView);
        Button choosePhoto = (Button) findViewById(R.id.choosePhoto);
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });


        setSituationSpinner();
        setEmotionSpinner();

        //press add mood button
        Button Add = findViewById(R.id.add);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                EditText exp = findViewById(R.id.explanation);
                String explanation = exp.getText().toString();
                name = user.getDisplayName();
                DocumentReference docRef = db.collection("Users").document(name);
                db = FirebaseFirestore.getInstance();
                mood = new Mood(currentTime, emotionString, explanation, situationString);

                docRef.collection("MoodList").document(currentTime.toString()).set(mood);

                addImageToStorage();
                finish();

            }
        });
    }


    private void addImageToStorage(){
        if (imageUri != null){
            StorageReference Image = Folder.child("image");
            Image.putFile(imageUri);
        }
    }


    //use situation spinner to select a situation
    private void setSituationSpinner() {
        Spinner situation = findViewById(R.id.situations);
        ArrayAdapter<CharSequence> sitAdapter = ArrayAdapter.createFromResource(this, R.array.situations, android.R.layout.simple_spinner_item);
        sitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        situation.setAdapter(sitAdapter);
        situation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                situationString = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    //use emotion spinner to select an emotion
    private void setEmotionSpinner() {
        Spinner emotion = findViewById(R.id.emotionals);
        ArrayAdapter<CharSequence> emoAdapter = ArrayAdapter.createFromResource(this, R.array.emotionals, android.R.layout.simple_spinner_item);
        emoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emotion.setAdapter(emoAdapter);
        emotion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                emotionString = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }


    //get and show the selected image in the imageview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == PICK_IMAGE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);

            }
        }
    }

}