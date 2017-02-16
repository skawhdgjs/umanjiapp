package com.umanji.umanjiapp.ui.auth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.ui.modal.WebViewActivity;

public class SignupOtherActivity extends AppCompatActivity {

    private TextView mEmail;
    private EditText mPhoneNumber;

    private Button mSubmit;

    private CheckBox mCheckbox;
    private TextView mLocationConfirm;
    private TextView mPrivacyConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_other);


        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        mEmail = (TextView) findViewById(R.id.so_email);

        if(email != null) {
            mEmail.setText(email);
            mEmail.setEnabled(false);
        }
        mCheckbox = (CheckBox) findViewById(R.id.so_checkbox1);

        mLocationConfirm = (TextView) findViewById(R.id.so_locationConfirm);
        mLocationConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webIntent = new Intent(SignupOtherActivity.this, WebViewActivity.class);
                webIntent.putExtra("url", "http://blog.naver.com/mothcar/220673352101");
                SignupOtherActivity.this.startActivity(webIntent);
            }
        });



        mPrivacyConfirm = (TextView) findViewById(R.id.so_privacyConfirm);
        mPrivacyConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privacyIntent = new Intent(SignupOtherActivity.this, WebViewActivity.class);
                privacyIntent.putExtra("url", "http://blog.naver.com/mothcar/220673367007");
                SignupOtherActivity.this.startActivity(privacyIntent);
            }
        });

        // mPrivacyConfirm

        mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (buttonView.getId() == R.id.checkbox) {
                    if (isChecked) {
                        Toast.makeText(SignupOtherActivity.this, "눌림", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SignupOtherActivity.this, "안눌림", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mPhoneNumber = (EditText) findViewById(R.id.so_phoneNumber);



        mSubmit = (Button) findViewById(R.id.so_submit);
    }

    
}
