package com.drjing.xibao.common.view.calendarview;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.DatePicker;

/**
 * Created by kristain on 16/1/28.
 */
public class CustomerDatePickerDialog extends DatePickerDialog {


    public CustomerDatePickerDialog(Context context,
                                    OnDateSetListener callBack, int year, int monthOfYear,
                                    int dayOfMonth) {
        super(context, 0, callBack, year, monthOfYear, dayOfMonth);
        setTitle(year + "年" + (monthOfYear+1) + "月");
    }


    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
        setTitle(year+"年"+(month + 1) + "月" );
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
