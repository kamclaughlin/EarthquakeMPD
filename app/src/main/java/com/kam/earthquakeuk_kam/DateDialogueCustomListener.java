/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam;


import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.EditText;

public class DateDialogueCustomListener implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "CustomDateDialogue";
    private final EditText mEditText;

    public DateDialogueCustomListener(EditText textBox) {
        this.mEditText = textBox;
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        String dateString = String.format("%d/%d/%d", dayOfMonth, month+1, year);
        mEditText.setText(dateString);

    }


}
