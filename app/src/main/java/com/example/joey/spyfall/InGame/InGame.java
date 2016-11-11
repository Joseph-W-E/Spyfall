package com.example.joey.spyfall.InGame;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joey.spyfall.R;
import com.example.joey.spyfall.Spyfall.Location;
import com.example.joey.spyfall.Spyfall.PlayerInformation;
import com.example.joey.spyfall.Spyfall.Spyfall;
import com.example.joey.spyfall.ViewUtility.ToggleButton;
import com.example.joey.spyfall.ViewUtility.RoleTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joey on 08-Nov-16.
 */

public class InGame extends Activity {

    private PlayerInformation playerInformation;

    private boolean informationHidden = false;

    private String code;

    private int playerNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingame);

        /*** Get the shared preferences ***/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /*** Use shared [references to get player information ***/
        code = prefs.getString(getString(R.string.shared_preferences_code_name), getString(R.string.error_data_not_found));
        playerNumber = prefs.getInt(getString(R.string.shared_preferences_player_number_name), -1);
        playerInformation = Spyfall.getPlayerInformation(code, playerNumber, this);

        /*** Verify that the information is right ***/
        if (!Spyfall.validInformation(code, playerNumber)) {
            finish();
            Toast.makeText(getApplicationContext(), getString(R.string.error_data_not_found), Toast.LENGTH_LONG).show();
        }

        /*** Update the player information text views based on whether the user is a spy or not ***/
        if (playerInformation.isSpy())
            spy();
        else
            detective();

        /*** Setup the countdown timer ***/
        final TextView txtTimer = (TextView) findViewById(R.id.ingame_timer);
        int startTimeInMinutes = 2 + Spyfall.getTotalNumberOfPlayersFromCode(code);
        new CountDownTimer(startTimeInMinutes * 60 * 1000, 1000) {
            @Override
            public void onTick(long l) {
                txtTimer.setText(String.format(
                        Locale.ENGLISH,
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(l),
                        TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))
                ));
            }

            @Override
            public void onFinish() {
                txtTimer.setText(getString(R.string.ingame_times_up));
            }
        }.start();

        /*** Setup the "Leave Game" button ***/
        final Button btnLeaveGame = (Button) findViewById(R.id.ingame_btn_leave_game);
        btnLeaveGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /*** Enable hide information feature and locations feature ***/
        initializePlayers();
        initializeHideFeature();
        initializeLocations();
    }

    /**
     * Initializes the location and role text views (or, should I say, lack-there-of) under
     * the assumption that the user IS the spy.
     */
    private void spy() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ingame_ll_player_information);
        RoleTextView txtSpy = makePlayerInformationTextView(getString(R.string.ingame_spy));
        ll.addView(txtSpy);
    }

    /**
     * Initializes the location and role text views under the assumption that
     * the user is NOT the spy.
     */
    private void detective() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ingame_ll_player_information);

        RoleTextView txtLocation = makePlayerInformationTextView(String.format(
                Locale.ENGLISH,
                "Location: %s",
                playerInformation.getLocation()
        ));

        RoleTextView txtRole     = makePlayerInformationTextView(String.format(
                Locale.ENGLISH,
                "Role: %s",
                playerInformation.getRole()
        ));

        ll.addView(txtLocation);
        ll.addView(txtRole);
    }

    /**
     * Enables the "hide" feature. The goal of this feature is to give the user control over
     * whether or not to display their location and role (or lack-there-of).
     */
    private void initializeHideFeature() {
        final LinearLayout container = (LinearLayout) findViewById(R.id.ingame_ll_player_information);
        final TextView txtHide = (TextView) findViewById(R.id.ingame_txt_hide_or_show_information);
        txtHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (informationHidden) {
                    informationHidden = false;
                    for (int i = 1; i < container.getChildCount(); i++) {
                        container.getChildAt(i).setVisibility(View.VISIBLE);
                    }
                } else {
                    informationHidden = true;
                    for (int i = 1; i < container.getChildCount(); i++) {
                        container.getChildAt(i).setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    /**
     * Creates a tangible list of buttons at the top of the screen so users can cross off
     * players (just like they can with locations, see initializeLocations).
     */
    private void initializePlayers() {
        int totalNumberOfPlayers = Spyfall.getTotalNumberOfPlayersFromCode(code);
        int firstPlayer = Spyfall.getFirstPlayerFromCode(code);

        // Get the parent (linear layout)
        LinearLayout parent = (LinearLayout) findViewById(R.id.ingame_ll_players);
        List<ToggleButton> buffer = new ArrayList<>();

        for (int i = 1; i <= totalNumberOfPlayers; i++) {
            if (buffer.size() < 2) {
                ToggleButton button = makeToggleButton(String.format(Locale.ENGLISH, "Player %d", i));
                if (i == playerNumber) {
                    String text = button.getText().toString();
                    text = text.concat(" (you)");
                    button.setText(text);
                }
                if (i == firstPlayer) {
                    String text = button.getText().toString();
                    text = text.concat(" ***");
                    button.setText(text);
                }
                buffer.add(button);
            } else {
                LinearLayout child = makeChildLinearLayout();
                for (ToggleButton tb : buffer)
                    child.addView(tb);
                buffer.clear();
                parent.addView(child);
                i--;
            }
        }

        if (!buffer.isEmpty()) {
            LinearLayout child = makeChildLinearLayout();
            for (ToggleButton tb : buffer)
                child.addView(tb);
            buffer.clear();
            parent.addView(child);
        }
    }

    /**
     * Gets the list of locations from the Spyfall class, and for each location,
     * a button is added to a linear layout. These buttons can be "enabled" and "disabled".
     */
    private void initializeLocations() {
        // Get the locations
        ArrayList<Location> locations = new ArrayList<>();
        try {
            locations = Spyfall.getLocationsFromFile(Spyfall.LOCATION_FILE_NAME, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the parent (linear layout)
        LinearLayout parent = (LinearLayout) findViewById(R.id.ingame_ll_locations);
        List<ToggleButton> buffer = new ArrayList<>();

        for (int i = 0; i < locations.size(); i++) {
            if (buffer.size() < 2) {
                buffer.add(makeToggleButton(locations.get(i).getLocation()));
            } else {
                LinearLayout child = makeChildLinearLayout();
                for (ToggleButton tb : buffer)
                    child.addView(tb);
                buffer.clear();
                parent.addView(child);
                i--;
            }
        }

        if (!buffer.isEmpty()) {
            LinearLayout child = makeChildLinearLayout();
            for (ToggleButton lb : buffer)
                child.addView(lb);
            buffer.clear();
            parent.addView(child);
        }
    }

    /*** Methods needed to generate views programmatically ***/

    private RoleTextView makePlayerInformationTextView(String text) {
        RoleTextView txt = new RoleTextView(this);
        txt.setText(text);
        return txt;
    }

    private LinearLayout makeChildLinearLayout() {
        LinearLayout child = new LinearLayout(this);
        child.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        child.setOrientation(LinearLayout.HORIZONTAL);
        return child;
    }

    private ToggleButton makeToggleButton(String text) {
        ToggleButton button = new ToggleButton(this);
        button.setText(text);
        return button;
    }
}
