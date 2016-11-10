package com.example.joey.spyfall.PreGame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joey.spyfall.InGame.InGame;
import com.example.joey.spyfall.R;
import com.example.joey.spyfall.Spyfall.Spyfall;

import java.util.Locale;

/**
 * Created by Joey on 08-Nov-16.
 */

public class PreGame extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregame);

        /*** Get needed information ***/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String code = prefs.getString(getString(R.string.shared_preferences_code_name), getString(R.string.error_data_not_found));
        int playerNumber = prefs.getInt(getString(R.string.shared_preferences_player_number_name), -1);
        int totalNumberOfPlayers = Spyfall.getTotalNumberOfPlayersFromCode(code);

        /*** Make sure no information was lost in translation ***/
        if (!Spyfall.validInformation(code, playerNumber)) {
            finish();
            Toast.makeText(getApplicationContext(), getString(R.string.error_data_not_found), Toast.LENGTH_LONG).show();
        }

        /*** Update the views ***/
        final TextView txtCode = (TextView) findViewById(R.id.pregame_txt_code);
        txtCode.setText(code);

        final TextView txtPlayerNumber = (TextView) findViewById(R.id.pregame_txt_player_number);
        txtPlayerNumber.setText(String.format(
                Locale.ENGLISH,
                "Player: %d (out of %d)",
                playerNumber,
                totalNumberOfPlayers
        ));

        final Button btnStart = (Button) findViewById(R.id.pregame_btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
    }

    /**
     * Starts the InGame activity.
     * Note that finish() is called so the user cannot go back to this screen.
     */
    private void start() {
        startActivity(new Intent(this, InGame.class));
        finish();
    }

}