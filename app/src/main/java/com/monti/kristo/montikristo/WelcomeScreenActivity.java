package com.monti.kristo.montikristo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class WelcomeScreenActivity extends AppCompatActivity {

    Button btn_login_email, signup_btn;
    Typeface myFont;
    TextView infoMain, subInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // below code provide full screen mode
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome_screen);
        //Crashlytics
        Fabric.with(this, new Crashlytics());
        myFont = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Bold.ttf");


        infoMain = findViewById(R.id.info);
        subInfo = findViewById(R.id.info_);
        signup_btn = findViewById(R.id.btn_signup);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            infoMain.setTypeface(myFont);
            subInfo.setTypeface(myFont);
        }

        btn_login_email = findViewById(R.id.btn_signup_email);
        btn_login_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), LoginScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterUserActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
