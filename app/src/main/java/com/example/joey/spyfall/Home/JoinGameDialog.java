package com.example.joey.spyfall.Home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.joey.spyfall.R;
import com.example.joey.spyfall.Spyfall.Spyfall;

/**
 * Created by Joey on 08-Nov-16.
 */

public class JoinGameDialog extends DialogFragment {

    private EditText etCode, etPlayerNumber;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_join_game, null);

        etCode         = (EditText) view.findViewById(R.id.dialog_join_game_et_code);
        etPlayerNumber = (EditText) view.findViewById(R.id.dialog_join_game_et_player_number);

        builder.setView(view)
                .setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .setNeutralButton(getString(R.string.dialog_join_game_use_recent_code), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });

        /*** This is my wonderful hack to stop the stupid dialog from closing after hitting
         *** the neutral button.
         ***/
        AlertDialog dialog = builder.create();
        dialog.show();
        setPositiveButton(dialog);
        setNegativeButton(dialog);
        setNeutralButton(dialog);

        return dialog;
    }

    private void setPositiveButton(final AlertDialog dialog) {
        if (dialog != null) {
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String code = etCode.getText().toString().toLowerCase();
                    String playerNumberPreParse = etPlayerNumber.getText().toString();

                    if (!code.isEmpty() && !playerNumberPreParse.isEmpty()) {
                        int playerNumber = Integer.parseInt(playerNumberPreParse);

                        if (Spyfall.validInformation(code, playerNumber)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            editor.putString(getString(R.string.shared_preferences_code_name), code);
                            editor.putInt(getString(R.string.shared_preferences_player_number_name), playerNumber);
                            editor.apply();

                            ((Home) getActivity()).launchPreGame();

                            dialog.dismiss();
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                }
            });
        }
    }

    private void setNegativeButton(final AlertDialog dialog) {
        if (dialog != null) {
            Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });
        }
    }

    private void setNeutralButton(final AlertDialog dialog) {
        if (dialog != null) {
            Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    etCode.setText(prefs.getString(getString(R.string.shared_preferences_code_name), ""));
                }
            });
        }
    }

    private void showErrorMessage() {
        Toast.makeText(getActivity(), getString(R.string.error_bad_data), Toast.LENGTH_LONG).show();
        etCode.setText("");
        etPlayerNumber.setText("");
    }

}
