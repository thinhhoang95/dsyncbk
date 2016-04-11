package hu.pe.thinhhoang.aaosync.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by hoang on 2/9/2016.
 */
public class GetTimePicker {
    public static TimePickerDialog getTimePicker(Context c, final VoidInterface vi)
    {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        return new TimePickerDialog(c, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar c = Calendar.getInstance();
                c.set(1995, 9, 27, hourOfDay, minute, 0);
                vi.callback(c);
            }
        }, hour, minute, true);
        // return mTimePicker;
    }
    public static DatePickerDialog getDatePicker(Context c, final VoidInterface vi)
    {
        Calendar mCurrentTime = Calendar.getInstance();
        final int day = mCurrentTime.get(Calendar.DAY_OF_MONTH);
        int month=mCurrentTime.get(Calendar.MONTH);
        int year = mCurrentTime.get(Calendar.YEAR);
        return new DatePickerDialog(c, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year,monthOfYear,dayOfMonth);
                vi.callback(c);
            }
        },year,month,day);
    }
}
