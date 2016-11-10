package com.example.joey.spyfall.Home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.example.joey.spyfall.R;
import com.example.joey.spyfall.Spyfall.Spyfall;

/**
 * Created by Joey on 08-Nov-16.
 */

public class GenerateCodeDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_generate_code, null);

        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.dialog_generate_code_number_picker);
        numberPicker.setMinValue(Spyfall.MINIMUM_NUMBER_OF_PLAYERS);
        numberPicker.setMaxValue(Spyfall.MAXIMUM_NUMBER_OF_PLAYERS);

        builder.setView(view)
                .setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int totalNumberOfPlayers = numberPicker.getValue();
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                        editor.putString(getString(R.string.shared_preferences_code_name), Spyfall.generateCode(totalNumberOfPlayers));
                        editor.apply();

                        ((Home) getActivity()).updateTxtCode();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getDialog().cancel();
                    }
                });

        return builder.create();
    }

}
