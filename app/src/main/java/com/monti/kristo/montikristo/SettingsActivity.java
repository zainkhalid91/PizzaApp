package com.monti.kristo.montikristo;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.monti.kristo.montikristo.model.LoginUserModel;
import com.monti.kristo.montikristo.model.StatusModel;
import com.monti.kristo.montikristo.rest.apiclient;
import com.monti.kristo.montikristo.utils.JWTUtils;
import com.monti.kristo.montikristo.utils.SessionManager;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Crashlytics
        Fabric.with(this, new Crashlytics());
        btn_back = findViewById(R.id.btn_back_settings);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), MyCartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


        Fragment fragment = new SettingsFragment();
        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            fragmentTransaction.add(R.id.content_frame, fragment, "Settings Fragment");
            fragmentTransaction.commit();
        } else {
            fragment = getFragmentManager().findFragmentByTag("Settings Fragment");
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                             Preference preference) {
            String key = preference.getKey();
            if (key.equals("password")) {

                sendVerificationCode();
                return true;
            }
            return false;
        }

        private void sendVerificationCode() {
            SessionManager sessionManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                sessionManager = new SessionManager(getContext());
            }
            String decoded = null;
            try {
                decoded = JWTUtils.decoded(sessionManager.getKeyUserId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            LoginUserModel loginUserModel = gson.fromJson(decoded, LoginUserModel.class);

            final String email = loginUserModel.getEmail();

          /*  progressDialog=new ProgressDialog(v.getContext());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.progressdialog_processing));
*/

            //          progressDialog.show();

            Call<StatusModel> call = apiclient
                    .getApiClientInstance()
                    .getApi()
                    .forgotPassword(email);

            call.enqueue(new Callback<StatusModel>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {

                    // progressDialog.cancel();

                    StatusModel statusModel = response.body();
                    String status = statusModel.getStatus();


                    if (status.equals("true")) {
                        Toast.makeText(getContext(), "Verification code sent to your email address", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getContext(), VerificationCodeActivity.class);
                        startActivity(intent);

                    } else if (status.equals("false")) {
                        Toast.makeText(getContext(), "Email address does not exist.", Toast.LENGTH_LONG).show();
                    }
                }

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onFailure(Call<StatusModel> call, Throwable t) {
                    Toast.makeText(getContext(), "No response", Toast.LENGTH_SHORT).show();
                    // progressDialog.dismiss();
                }
            });
        }
    }
}
