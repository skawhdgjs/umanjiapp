package com.umanji.umanjiapp.ui.modal.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CalendarView;
import android.widget.Toast;

import com.umanji.umanjiapp.R;

public class AdsCalendarActivity extends AppCompatActivity {

    protected CalendarView mCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_calendar);

        mCalendarView       = (CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setShowWeekNumber(false);






        showCalendar();

    }

    public void showCalendar() {

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {

                Toast.makeText(getBaseContext(), "Selected Date is\n\n"
                                + year + " : " + month + " : " +  dayOfMonth,
                        Toast.LENGTH_LONG).show();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", year + " " + month +" "+  dayOfMonth );
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}
