package com.umanji.umanjiapp.ui.search;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.umanji.umanjiapp.R;

import java.io.IOException;
import java.util.List;

import static java.util.Locale.getDefault;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final EditText edittext = (EditText) findViewById(R.id.searchBtn);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press

                    TextView mName = (TextView) findViewById(R.id.name);

                    Geocoder geoCoder = new Geocoder(getBaseContext(), getDefault());

                    Barcode.GeoPoint p;

                    List<Address> addresses;
                    String latString ="";
                    try {
                        addresses = geoCoder.getFromLocationName(edittext.getText().toString(), 1);

                        if (addresses.size() > 0) {
                            Double lat = (double) (addresses.get(0).getLatitude());
                            Double lon = (double) (addresses.get(0).getLongitude());

                            String fullName = addresses.get(0).getAddressLine(0);
                            String name = addresses.get(0).getFeatureName();


                            mName.setText("Locality : "+ fullName + "\n 장소명 : " + name);

                            latString = lat.toString();


                            /*
                            controller.animateTo(p);
                            controller.setZoom(12);

                            MapOverlay mapOverlay = new MapOverlay();
                            List<Overlay> listOfOverlays = mapView.getOverlays();
                            listOfOverlays.clear();
                            listOfOverlays.add(mapOverlay);

                            mapView.invalidate();*/
                            edittext.setText("");
                        } else {
                            AlertDialog.Builder adb = new AlertDialog.Builder(SearchActivity.this);
                            adb.setTitle("Google Map");
                            adb.setMessage("Please Provide the Proper Place");
                            adb.setPositiveButton("Close", null);
                            adb.show();
                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    Toast.makeText(getApplicationContext(), latString, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }
}
