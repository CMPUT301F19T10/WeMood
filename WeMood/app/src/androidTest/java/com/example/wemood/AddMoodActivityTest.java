package com.example.wemood;

/**
 * @author Ziyi Ye
 *
 * @version 2.0
 */

import android.app.Activity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

/**
 * Class name: AddMoodActivityTest
 *
 * Version 2.0
 *
 * Date: November 26, 2019
 *
 * Copyright [2019] [Team10, Fall CMPUT301, University of Alberta]
 */

public class AddMoodActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LogSignInActivity> rule =
            new ActivityTestRule<>(LogSignInActivity.class, true, true);

    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.assertCurrentActivity("Wrong Activity", LogSignInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.add_user_name), "dbytest@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.add_user_password), "dby123");
        solo.clickOnButton("Sign in");
        solo.waitForActivity(MainActivity.class,10000);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

    }

    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void login(){

        ImageView but = (ImageView) solo.getView(R.id.sign_iv);
        solo.clickOnView(but);
        solo.assertCurrentActivity("Wrong Activity", AddMoodActivity.class);

    }

    @Test
    public void testSpinner(){

        ImageView but = (ImageView) solo.getView(R.id.sign_iv);
        solo.clickOnView(but);
        solo.assertCurrentActivity("Wrong Activity", AddMoodActivity.class);
        Spinner s = (Spinner) solo.getView(R.id.situations);
        solo.clickOnView(s);
        Spinner e = (Spinner) solo.getView(R.id.emotionals);
        solo.clickOnView(e);

    }

    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}