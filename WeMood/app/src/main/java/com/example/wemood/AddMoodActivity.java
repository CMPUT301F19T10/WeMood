package com.example.wemood;
/**
 * @author Ziyi Ye
 *
 * @version 1.0
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Class name: AddMoodActivity
 *
 * Version 1.0
 *
 * Date: November 4, 2019
 *
 * Copyright [2019] [Team10, Fall CMPUT301, University of Alberta]
 */

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
    private Switch locationSwitch;
    private TextView locationMessage;
    private LocationManager lm;
    private double longitude;
    private double latitude;
    private LocationListener mLocationListener;

    /**
     * Initialize
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood);

        db = FirebaseFirestore.getInstance();
        Folder = FirebaseStorage.getInstance().getReference().child("ImageFolder");
        final CollectionReference collectionReference = db.collection("Users");

        imageView = (ImageView) findViewById(R.id.imageView);
        Button choosePhoto = (Button) findViewById(R.id.choosePhoto);
        locationSwitch = findViewById(R.id.gpsSwitch);
        locationMessage = findViewById(R.id.locationMessage);
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });


        setSituationSpinner();
        setEmotionSpinner();

        /**
         * press the Add Mood button
         * put the mood to fireBase
         */
        Button Add = findViewById(R.id.add);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                EditText exp = findViewById(R.id.reason);
                String explanation = exp.getText().toString();
                EditText titl = findViewById(R.id.title);
                String title = titl.getText().toString();

                if (containsSpace(title)){
                    Toast.makeText(AddMoodActivity.this, "title has no more than 3 words", Toast.LENGTH_SHORT).show();
                }else{
                    name = user.getDisplayName();
                    DocumentReference docRef = db.collection("Users").document(name);
                    db = FirebaseFirestore.getInstance();
                    if (explanation.length() == 0){
                        explanation = "No explanation";
                    }
                    addMood(currentTime, emotionString, explanation, situationString, title, docRef);
                    //add image to storage if it is not null
                    if (imageUri != null){
                        StorageReference Image = Folder.child("image");
                        Image.putFile(imageUri);
                    }

                    finish();
                }


            }
        });


        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (isChecked){
                    mLocationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            // update location if it is changed
                            setLongitude(location.getLongitude());
                            setLatitude(location.getLatitude());
                        }

                        // need override this method
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        // update location if GPS is allowed
                        @Override
                        public void onProviderEnabled(String provider) {
                            if (checkCallingOrSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(AddMoodActivity.this, "Need GPS Permission!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            setLongitude(lm.getLastKnownLocation(provider).getLongitude());
                            setLatitude(lm.getLastKnownLocation(provider).getLatitude());
                        }

                        // need override this method
                        @Override
                        public void onProviderDisabled(String provider) {
                        }

                    };
                    locationUpdate();
                    try {
                        Geocoder geocoder = new Geocoder(getApplicationContext(),Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(getLatitude(), getLongitude(), 1);
                        String city = addresses.get(0).getLocality();
                        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                        if (knownName != null){
                            locationMessage.setText(city + " - " + knownName);
                        }else{
                            locationMessage.setText(city);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                        Log.w("Current location", "Cannot get Address!");
                        System.out.println(getLatitude());
                    }
                }else {
                    locationMessage.setText("None");
                }
            }
        });
    }

    /**
     * add a mood to fireBase
     * @param currentTime
     * @param emotionString
     * @param explanation
     * @param situationString
     * @param title
     * @param docRef
     */
    public void addMood(Date currentTime, String emotionString, String explanation, String situationString, String title, DocumentReference docRef){
        mood = new Mood(currentTime, emotionString, explanation, situationString, title, longitude,latitude,locationMessage.getText().toString());
        //put the mood to fireBase
        docRef.collection("MoodList").document(currentTime.toString()).set(mood);

    }
    /**
     * check if title has more than 3 words
     * @param comment
     * @return whether comment has more than 3 words
     */
    public boolean containsSpace(String comment){
        String Comment = comment.trim();
        int numSpace = 0;
        for(int i =0;i< Comment.length(); i++){
            if (Character.isWhitespace(Comment.charAt(i))){
                numSpace++;
            }
        }
        if (numSpace > 2){
            return true;
        }else{
            return false;
        }
    }

    /**
     * initialize the situation spinner
     * use situation spinner to select an emotion
     */
    private void setSituationSpinner() {
        Spinner situation = findViewById(R.id.situations);
        ArrayAdapter<CharSequence> sitAdapter = ArrayAdapter.createFromResource(this, R.array.situations, android.R.layout.simple_spinner_item);
        sitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        situation.setAdapter(sitAdapter);
        situation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("choose a situation")){
                    situationString = "";
                }else{
                    situationString = parent.getItemAtPosition(position).toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }


    /**
     * initialize the emotion spinner
     * use emotion spinner to select an emotion
     */
    private void setEmotionSpinner() {
        Spinner emotion = findViewById(R.id.emotionals);
        ArrayAdapter<CharSequence> emoAdapter = ArrayAdapter.createFromResource(this, R.array.emotionals, android.R.layout.simple_spinner_item);
        emoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emotion.setAdapter(emoAdapter);
        emotion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("choose an emotion")){
                    emotionString = "";
                }
                else{
                    emotionString = parent.getItemAtPosition(position).toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    /**
     * get and show the selected image in the imageView
     */

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

    /**
     * get current longitude
     *
     * @return current longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * set current longitude
     *
     * @param longitude current location information
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * get current latitude
     *
     * @return current latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * set current latitude
     *
     * @param latitude current location information
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * update location
     */
    public void locationUpdate() {

        // if no permission
        if (checkCallingOrSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(AddMoodActivity.this, "Need GPS Permission!", Toast.LENGTH_SHORT).show();
            return;
        }

        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        setLongitude(location.getLongitude());
        setLatitude(location.getLatitude());

        // get the location every 2 seconds
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 8, mLocationListener);
    }

}