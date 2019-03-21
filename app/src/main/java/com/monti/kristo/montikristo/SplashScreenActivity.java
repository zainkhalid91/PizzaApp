package com.monti.kristo.montikristo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.monti.kristo.montikristo.utils.SessionManager;

import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long TIME_LIMIT = 3000;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        sessionManager = new SessionManager(getApplicationContext());

        //Permissions
        getRunTimePermissions();

    }

    private void getRunTimePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    new Handler().postDelayed(() -> {

                        if (sessionManager.isLoggedIn()) {
                            Intent intent;
                            intent = new Intent(getApplicationContext(), MyCartActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (sessionManager.isLoggedOut()) {
                            Intent intent;
                            intent = new Intent(getApplicationContext(), WelcomeScreenActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent;
                            intent = new Intent(getApplicationContext(), WelcomeScreenActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, TIME_LIMIT);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }
}
