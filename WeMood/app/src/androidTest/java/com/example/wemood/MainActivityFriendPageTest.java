package com.example.wemood;

/**
 * @author Boyuan Dong
 *
 * @version 2.0
 */

import android.widget.EditText;
import android.widget.RadioButton;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

/**
 * Class name: MainActivityFriendPageTest
 *
 * Version 2.0
 *
 * Date: November 26, 2019
 *
 * Copyright [2019] [Team10, Fall CMPUT301, University of Alberta]
 */

/**
 * Will Test the MainActivityFriendPage.
 */

public class MainActivityFriendPageTest {

    private Solo solo;

    /**
     * The First Activity in the app would be LogSignInActivity.
     */
    @Rule
    public ActivityTestRule<LogSignInActivity> rule = new ActivityTestRule<>(LogSignInActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        //initialize the ShowActivity environment before testing
        solo.assertCurrentActivity("Not in LogSignInActivity", LogSignInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.add_user_name), "dbytest@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.add_user_password),"dby123");
        solo.clickOnView(solo.getView(R.id.sign_in_button));
        solo.waitForActivity(MainActivity.class,2000);

    }

    /**
     * Check whether the MainActivity opens by clicking on the Sign in Button.
     */
    @Test
    public void checkoutMainActivity() {
        solo.assertCurrentActivity("Not in MainActivity", MainActivity.class);
    }

    /**
     * Check whether the FriendFragment opens by clicking on the FriendButton RadioButton.
     */
    @Test
    public void checkFriendFragmentChange() {
        RadioButton FriendButton = (RadioButton) solo.getView(R.id.friends_tab);
        solo.clickOnView(FriendButton);
        solo.waitForFragmentById(R.id.friend_fragment);
    }

    /**
     * Check whether the FriendNotExist opens by searching for the username
     */
    @Test
    public void checkFriendNotExistChange() {
        RadioButton FriendButton = (RadioButton) solo.getView(R.id.friends_tab);
        solo.clickOnView(FriendButton);
        solo.waitForFragmentById(R.id.friend_fragment);
        solo.enterText((EditText) solo.getView(R.id.friend_input), "dby123");
        solo.clickOnButton("Search");
        solo.waitForActivity(FriendsNotExist.class,2000);
        solo.assertCurrentActivity("Not this Activity", FriendsNotExist.class);

    }

    /**
     * Check whether the FriendNotExistFollowDialog opens by click on Follow button
     */
    @Test
    public void checkFriendNotExistFollowDialogChange() {
        RadioButton FriendButton = (RadioButton) solo.getView(R.id.friends_tab);
        solo.clickOnView(FriendButton);
        solo.waitForFragmentById(R.id.friend_fragment);
        solo.enterText((EditText) solo.getView(R.id.friend_input), "dby123");
        solo.clickOnButton("Search");
        solo.waitForActivity(FriendsNotExist.class,2000);
        solo.clickOnButton("Follow");
        solo.waitForDialogToOpen();

    }


    /**
     * Check whether the FriendNotExistFollowDialog close by click on No button
     */

    @Test
    public void checkFriendNotExistFollowDialogNoClose() {
        RadioButton FriendButton = (RadioButton) solo.getView(R.id.friends_tab);
        solo.clickOnView(FriendButton);
        solo.waitForFragmentById(R.id.friend_fragment);
        solo.enterText((EditText) solo.getView(R.id.friend_input), "dby123");
        solo.clickOnButton("Search");
        solo.waitForActivity(FriendsNotExist.class,2000);
        solo.clickOnButton("Follow");
        solo.waitForDialogToOpen();
        solo.clickOnButton(1);
        solo.waitForDialogToClose();

    }


    /**
     * Check whether the FriendFragment opens by click on cancel button
     */

    @Test
    public void checkFriendNotExistCancelChange() {
        RadioButton FriendButton = (RadioButton) solo.getView(R.id.friends_tab);
        solo.clickOnView(FriendButton);
        solo.waitForFragmentById(R.id.friend_fragment);
        solo.enterText((EditText) solo.getView(R.id.friend_input), "dby123");
        solo.clickOnButton("Search");
        solo.waitForActivity(FriendsNotExist.class,2000);
        solo.clickOnButton("Cancel");
        solo.waitForFragmentById(R.id.friend_fragment);

    }

    /**
     * Check whether the FriendExist opens by clicking on the friend in the list.
     */

    @Test
    public void checkFriendExistFragmentChange() {
        RadioButton FriendButton = (RadioButton) solo.getView(R.id.friends_tab);
        solo.clickOnView(FriendButton);
        solo.waitForFragmentById(R.id.friend_fragment);
        solo.enterText((EditText) solo.getView(R.id.friend_input), "hubdby");
        solo.clickOnButton("Search");
        solo.waitForActivity(FriendsExist.class,2000);
        solo.assertCurrentActivity("Not FriendsExist Activity", FriendsExist.class);
    }



    /**
     * Check whether the FriendUnfollowFragmentDialog opens by clicking on the Unfollow button.
     */
    @Test
    public void checkFriendUnfollowFragmentDialogChange() {
        RadioButton FriendButton = (RadioButton) solo.getView(R.id.friends_tab);
        solo.clickOnView(FriendButton);
        solo.waitForFragmentById(R.id.friend_fragment);
        solo.enterText((EditText) solo.getView(R.id.friend_input), "hubdby");
        solo.clickOnButton("Search");
        solo.waitForActivity(FriendsExist.class,2000);
        solo.clickOnButton("Unfollow");
        solo.waitForDialogToOpen();
    }


    /**
     * Check whether the FriendUnfollowFragmentDialog close by click on No button
     */
    @Test
    public void checkFriendUnfollowFragmentDialogNoClose() {
        RadioButton FriendButton = (RadioButton) solo.getView(R.id.friends_tab);
        solo.clickOnView(FriendButton);
        solo.waitForFragmentById(R.id.friend_fragment);
        solo.enterText((EditText) solo.getView(R.id.friend_input), "hubdby");
        solo.clickOnButton("Search");
        solo.waitForActivity(FriendsExist.class,2000);
        solo.clickOnButton("Unfollow");
        solo.waitForDialogToOpen();
        solo.clickOnButton(1);
        solo.waitForDialogToClose();
    }


    /**
     * Check whether the FriendExistFragment opens by clicking on the back button.
     */

    @Test
    public void checkFriendBackChange() {
        RadioButton FriendButton = (RadioButton) solo.getView(R.id.friends_tab);
        solo.clickOnView(FriendButton);
        solo.waitForFragmentById(R.id.friend_fragment);
        solo.enterText((EditText) solo.getView(R.id.friend_input), "hubdby");
        solo.clickOnButton("Search");
        solo.waitForActivity(FriendsExist.class,2000);
        solo.clickOnButton("Back");
        solo.waitForActivity(FriendsExist.class,2000);
    }

}