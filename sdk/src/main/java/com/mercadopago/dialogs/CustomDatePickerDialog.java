package com.mercadopago.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.mercadopago.R;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

public class CustomDatePickerDialog extends DialogFragment {

    public static String CALENDAR = "com.mercadopago.fragments.picker";

    NumberPicker mMonth;
    NumberPicker mYear;
    DatePickerDialogListener mListener;
    Calendar mCalendar;

    public static CustomDatePickerDialog newInstance(Calendar selectedExpiryDate) {

        CustomDatePickerDialog fragment = new CustomDatePickerDialog();
        Bundle args = new Bundle();
        args.putSerializable(CALENDAR, selectedExpiryDate);
        fragment.setArguments(args);
        return fragment;
    }

    public interface DatePickerDialogListener {
        public void onDateSet(DialogFragment dialog, int month, int year);
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        try {
            mListener = (DatePickerDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DatePickerDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_date_picker, null);

        // Get date picker text color
        int textColor = getDatePickerTextColor(getActivity().getApplicationContext());

        // Set month control
        mMonth = (NumberPicker) view.findViewById(R.id.dialogMonth);
        mMonth.setMinValue(1);
        mMonth.setMaxValue(12);
        setNumberPickerTextColor(mMonth, textColor);

        // Set year control
        mYear = (NumberPicker) view.findViewById(R.id.dialogYear);
        int minYear = Calendar.getInstance().get(Calendar.YEAR);
        mYear.setMinValue(minYear);
        mYear.setMaxValue(minYear + 20);
        mYear.setWrapSelectorWheel(false);
        setNumberPickerTextColor(mYear, textColor);

        if (getArguments() != null) {
            mCalendar = (Calendar) getArguments().getSerializable(CALENDAR);
        }
        if(mCalendar != null){
            mMonth.setValue(mCalendar.get(Calendar.MONTH) + 1);
            mYear.setValue(mCalendar.get(Calendar.YEAR));
        } else {
            mCalendar = Calendar.getInstance();
            mCalendar.setTime(new Date());
            mMonth.setValue(mCalendar.get(Calendar.MONTH) + 1);
            mYear.setValue(mCalendar.get(Calendar.YEAR));
        }

        builder.setView(view)
            .setTitle(R.string.mpsdk_card_expire_date_label)
            .setPositiveButton(R.string.mpsdk_accept_label, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mListener.onDateSet(CustomDatePickerDialog.this, mMonth.getValue(), mYear.getValue());
                }
            })
            .setNegativeButton(R.string.mpsdk_cancel_label, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

        return builder.create();
    }

    private boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch (Exception ex) {
                    // do nothing
                }
            }
        }
        return false;
    }

    private int getDatePickerTextColor(Context context) {

        // The attributes you want retrieved
        int[] attrs = {android.R.attr.textColor};

        // Parse MyCustomStyle, using Context.obtainStyledAttributes()
        TypedArray ta = context.getTheme().obtainStyledAttributes(R.style.mpsdk_date_picker, attrs);

        // Fetching the text color
        int textColor = ta.getColor(0, Color.BLACK);

        // OH, and don't forget to recycle the TypedArray
        ta.recycle();

        return textColor;
    }
}
