package com.umanji.umanjiapp.ui.modal.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import com.umanji.umanjiapp.R;

import java.util.Calendar;

public class AdsCalendarActivity extends AppCompatActivity {

    protected CalendarView mCalendarView;
    protected int mYear;
    protected int mMonth;
    protected int mDay;
    protected Calendar c = Calendar.getInstance();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_calendar);

        mCalendarView       = (CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setShowWeekNumber(false);

        mYear  = c.get(Calendar.YEAR);
        mMonth  = c.get(Calendar.MONTH) + 1;
        mDay  = c.get(Calendar.DAY_OF_MONTH);

        mCalendarView.setOnClickListener(new CalendarView.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Selected Date is\n\n" + mYear + " : " + mMonth + " : " +  mDay, Toast.LENGTH_LONG).show();
            }
        });

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", mYear + " " + mMonth +" "+  mDay );
        setResult(RESULT_OK,returnIntent);
        finish();


        //showCalendar();

    }

    public void showCalendar() {

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {

               /* Toast.makeText(getBaseContext(), "Selected Date is\n\n"
                                + year + " : " + month + " : " +  dayOfMonth,
                        Toast.LENGTH_LONG).show();*/
            }
        });
    }



}
