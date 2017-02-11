package com.umanji.umanjiapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.umanji.umanjiapp.R;

public class SignSelectActivity extends AppCompatActivity {

    private Button mEmailButton;
    private Button mNaverButton;
    private Button mFacebookButton;
    private Button mGoogleButton;

    private TextView mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_select);

        mEmailButton = (Button) findViewById(R.id.select_email);
        mNaverButton = (Button) findViewById(R.id.select_naver);
        mFacebookButton = (Button) findViewById(R.id.select_facebook);
        mGoogleButton = (Button) findViewById(R.id.singinG);

        mLogin = (TextView) findViewById(R.id.login);

        mEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSign = new Intent(SignSelectActivity.this, SignupActivity.class);
                startActivity(intentSign);
            }
        });

        mNaverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Sorry Naver is Preparing", Toast.LENGTH_SHORT).show();

            }
        });

        mFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Sorry Facebook is Preparing", Toast.LENGTH_SHORT).show();

            }
        });

        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Sorry Google is Preparing", Toast.LENGTH_SHORT).show();

            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignSelectActivity.this, SigninActivity.class);
                startActivity(intent);
            }
        });

    }
}
