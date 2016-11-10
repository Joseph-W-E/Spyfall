package com.example.joey.spyfall.Home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joey.spyfall.PreGame.PreGame;
import com.example.joey.spyfall.R;
import com.example.joey.spyfall.Spyfall.Spyfall;

import java.util.Locale;

public class Home extends Activity {

    /*** Shared Preferences ***/
    private SharedPreferences prefs;

    /*** Views ***/
    private TextView txtCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*** Get shared preference information ***/
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /*** Get views ***/
        txtCode = (TextView) findViewById(R.id.home_txt_code);
        updateTxtCode();

        /*** Initialize button logic ***/
        final Button btnGenerateCode = (Button) findViewById(R.id.home_btn_generate_new_code);
        btnGenerateCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GenerateCodeDialog generateCodeDialog = new GenerateCodeDialog();
                generateCodeDialog.show(getFragmentManager(), "Generate Code");
            }
        });

        final Button btnJoinGame     = (Button) findViewById(R.id.home_btn_join_game);
        btnJoinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JoinGameDialog dialog = new JoinGameDialog();
                dialog.show(getFragmentManager(), "Join Game");
            }
        });
    }

    /**
     * Updates the text view that holds the user's most recently used code.
     */
    public void updateTxtCode() {
        if (txtCode != null) {
            txtCode.setText(String.format(
                    Locale.ENGLISH,
                    "Recent code: %s",
                    prefs.getString(getString(R.string.shared_preferences_code_name), getString(R.string.generate_a_new_code_hint))
            ));
        }
    }

    /**
     * Launches the PreGame activity.
     * Fails to launch if the user entered data incorrectly.
     */
    public void launchPreGame() {
        String code = prefs.getString(getString(R.string.shared_preferences_code_name), null);
        int playerNumber = prefs.getInt(getString(R.string.shared_preferences_player_number_name), -1);

        if (Spyfall.validInformation(code, playerNumber)) {
            updateTxtCode();
            startActivity(new Intent(this, PreGame.class));
        } else {
            Toast.makeText(this, getString(R.string.error_data_not_found), Toast.LENGTH_LONG).show();
        }
    }

}
