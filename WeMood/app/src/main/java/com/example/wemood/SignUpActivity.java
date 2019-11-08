package com.example.wemood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * This is an activity that help and guide user to register
 * their account correctly. By being switched by log in activity,
 * this sign-up activity allows user to create a new account by
 * providing their email, password, username and phone. The email
 * format must correspond to correct email address and password
 * must meet its complexity which is at least 6 length long and contain
 * letters and numbers. Username is unique. Phone is consisted of  digits.
 * Also no place can leave empty.
 * */

public class SignUpActivity extends AppCompatActivity implements
        View.OnClickListener{

    private EditText addUserName;
    private EditText addEmail;
    private EditText addPassWord;
    private EditText addPhone;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private boolean valid_name;



    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;

    private Button signUpButton;
    private CollectionReference collectionReference;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize FireBase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Database
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("Users");

        signUpButton = findViewById(R.id.sign_up_activity_button);
        addPassWord = findViewById(R.id.user_password);
        addEmail = findViewById(R.id.email);
        addPhone = findViewById(R.id.phone);
        addUserName = findViewById(R.id.username);


    }

    /**
     * This method is to see whether sign-up button is
     * clicked by its ID. If clicked then pass all parameter
     * including password, email, phone and username to createAccount
     * method.
     * No place can leave empty, otherwise sign-up process can't be completed
     * @param v*/

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_up_activity_button) {
            final String password = addPassWord.getText().toString();
            final String email = addEmail.getText().toString();
            final String phone = addPhone.getText().toString();
            final String userName = addUserName.getText().toString();

            if (email.isEmpty() || userName.isEmpty() || password.isEmpty()|| phone.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Cannot leave empty",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            /*if (isUserNameValid(userName)){


            }else{
                addUserName.setError("User name is already exist");
            }*/

            createAccount(userName, email, password, phone);






        }
    }

    /**
     * This method is to take username, email, password, phone as argument
     * then check their validation and create user by his/her email address
     * to firestore. If email format and password complexity match requirement
     * then show toast message "Success". If not meet, show "Authentication failed."
     * Also it allows user to update their profile by typing new email and other
     * user info.
     * @param username
     * @param email
     * @param password
     * @param phone*/

    public void createAccount(final String username, final String email, final String password, final String phone) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = mAuth.getCurrentUser();
                            setUserFireBase(user.getUid(), username, email, phone);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(SignUpActivity.this, "Success",
                                    Toast.LENGTH_SHORT).show();

                            // set username
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(addUserName.getText().toString())
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });
                            // [END update_profile]
                            Intent intent = new Intent(SignUpActivity.this, LogSignInActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        // [END create_user_with_email]
    }

    /**
     * This method is to create a new User class object and
     * upload it to firebase
     * @param username
     * @param email
     * @param userId
     * @param phone*/

    private void setUserFireBase(String userId, String username, String email, String phone) {
        User user = new User(email, username, phone, userId);
        collectionReference.document(username).set(user);
    }

    /**
     * This method is to check if email format and password are empty and match
     * correct email address and password complexity then show error message
     * accordingly. For username and phone we only check if it's empty and
     * show error message accordingly
     * @return Return boolean value*/

    private boolean validateForm() {
        boolean valid = true;
        String email = addEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            addEmail.setError("Required.");
            valid = false;
        } else if (!addEmail.getText().toString().trim().matches(emailPattern)){
            addEmail.setError("Invalid email address");
            valid = false;
        }

        String password = addPassWord.getText().toString();
        if (TextUtils.isEmpty(password) || !isPasswordValid(addPassWord.getText().toString())) {
            addPassWord.setError("Invalid Password Format! At Least Length of 6 of Digits and Letters");
            valid = false;
        } else {
            addPassWord.setError(null);
        }

        String username = addUserName.getText().toString().trim();
        if (TextUtils.isEmpty(username) ) {
            addUserName.setError("Required");
            valid = false;
        } /*else if (!isUserNameValid(addUserName.getText().toString())) {
            addUserName.setError("Username already exist!");

        }*/else{
            addUserName.setError(null);
        }

        String phone = addPhone.getText().toString();
        if (TextUtils.isEmpty(phone) || !isPhoneValid(addPhone.getText().toString())) {
            addPhone.setError("Invalid Phone!");
            valid = false;
        } else {
            addPhone.setError(null);
        }
        return valid;
    }

    /**
     *  This method is to check only password that input by user
     *  is applied by specific password pattern which is at least
     *  6 length long and must contain both digits and letters
     *  if not meet this requirement then return false else return
     *  true.
     *  @param password
     *  @return Return boolean value*/

    public boolean isPasswordValid(final String password){

        if(password.length()<6){
            return false;
        }else{
            for (int p =0; p < password.length();p++){
                if(Character.isLetter(password.charAt(p))){
                    Log.i("name12",Character.toString(password.charAt(p)));
                    for(int i =0; i< password.length();i++){
                        if (Character.isDigit(password.charAt(i))){
                            Log.i("name23",Character.toString(password.charAt(i)));
                            return true;

                        }
                    }
                }
            }
            return false;
        }
    }
    /**
     * This method is to check length of phone number must
     * less than 10 digits long. Return true and false accordingly
     * @param phone
     * @return Return boolean value*/
    public boolean isPhoneValid(final String phone){
        if (phone.length()<10){
            return false;
        }
        return true;
    }







    /*private boolean isUserNameValid(final String username){
        Log.d("name0",username);

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {


            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){


                    String usernameInDataBase = doc.getId();
                    Log.d("name1",usernameInDataBase);
                    if (usernameInDataBase.equals(username)){
                        myCallback.onCallback(usernameInDataBase);


                        Log.d("name2", "found");
                        break;
                    }else{

                        Log.d("name2", "not found");
                    }

                }
            }
        });
        System.out.println("---------"+valid.isNameValid());
        return valid.isNameValid();
    }

    public String readData(new MyCallback() {
        @Override
        public void onCallback(String value) {
            Log.d("TAG", value);
        }
    });*/


}


