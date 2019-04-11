package com.monti.kristo.montikristo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.monti.kristo.montikristo.model.LoginUserModel;
import com.monti.kristo.montikristo.model.PushNotificatonsModel;
import com.monti.kristo.montikristo.model.StatusTokenModel;
import com.monti.kristo.montikristo.rest.apiclient;
import com.monti.kristo.montikristo.utils.JWTUtils;
import com.monti.kristo.montikristo.utils.SessionManager;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreenActivity extends AppCompatActivity {

    Button btn_login, btn_back;
    TextView register, forgotPass, head1, head2, headSub;
    TextView txt_email, txt_pass, errortext;
    ProgressDialog dialog;
    SessionManager sessionManager;
    Typeface myFont, myFontReg;
    int cID = 0;
    String decoded = null;
    Boolean status;
    String token;

    public static String getFirebaseToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        //Crashlytics
        Fabric.with(this, new Crashlytics());

        // below code provide full screen mode
        /*this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(R.layout.activity_login_screen);

        myFont = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Bold.ttf");
        myFontReg = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Regular.ttf");


        sessionManager = new SessionManager(getApplicationContext());

        txt_email = findViewById(R.id.edittext_Lemail);
        txt_pass = findViewById(R.id.edittext_Lpass);
        register = findViewById(R.id.textview_register);
        forgotPass = findViewById(R.id.textview_forgot);
        btn_login = findViewById(R.id.btn_login);
        btn_back = findViewById(R.id.btn_back_login);
        errortext = findViewById(R.id.textview_errormsg);

        head1 = findViewById(R.id.header1);
        head2 = findViewById(R.id.header2);

        head1.setTypeface(myFont);
        head2.setTypeface(myFontReg);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), WelcomeScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), RegisterUserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser(v);
            }
        });

    }

    private void LoginUser(View v) {
        if (isNetworkAvailable()) {

            dialog = new ProgressDialog(v.getContext());
            dialog.setMessage("Processing..");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            String Email = txt_email.getText().toString();
            String Password = txt_pass.getText().toString();

            if (Email.isEmpty()) {
                txt_email.setError("Email is required");
                txt_email.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                txt_email.setError("Enter a valid email");
                txt_email.requestFocus();
                return;
            }
            if (Password.isEmpty()) {
                txt_pass.setError("Password required");
                txt_pass.requestFocus();
                return;
            }
            if (Password.length() < 8) {
                txt_pass.setError("Password should be atleast 8 character long");
                txt_pass.requestFocus();
                return;
            }

            dialog.show();

            Call<StatusTokenModel> call = apiclient
                    .getApiClientInstance()
                    .getApi()
                    .loginUser(Email, Password);

            call.enqueue(new Callback<StatusTokenModel>() {
                @Override
                public void onResponse(Call<StatusTokenModel> call, Response<StatusTokenModel> response) {
                    try {


                        dialog.cancel();

                        StatusTokenModel loginUserModel = response.body();
                        if (loginUserModel.getStatus()) {
                            status = loginUserModel.getStatus();
                            token = loginUserModel.getToken();


                            PushNotification();

                          /*  SessionManager sessionManager = new SessionManager(getApplicationContext());
                            sessionManager.setKeyUserId(token);

                                Toast.makeText(getApplicationContext(), ""+status, Toast.LENGTH_LONG).show();


                                sessionManager.setLoggined(true);
                                Intent intent;
                                intent = new Intent(getApplicationContext(), MyCartActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();*/

                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Email/Password", Toast.LENGTH_LONG).show();

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<StatusTokenModel> call, Throwable t) {
                    errortext.setText(getString(R.string.signin__error));
                    errortext.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            });

        } else {

            Toast.makeText(getApplicationContext(), "No internet connection available", Toast.LENGTH_LONG).show();
        }

    }

    private void PushNotification() {
        String decoded = null;
        try {
            decoded = JWTUtils.decoded(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        LoginUserModel userModel = gson.fromJson(decoded, LoginUserModel.class);
        cID = userModel.getId();
        Call<PushNotificatonsModel> call = apiclient
                .getApiClientInstance()
                .getApi()
                .pushNotifications(getFirebaseToken(), String.valueOf(cID));
        Log.d("Token recieved", getFirebaseToken());

        call.enqueue(new Callback<PushNotificatonsModel>() {
            @Override
            public void onResponse(Call<PushNotificatonsModel> call, Response<PushNotificatonsModel> response) {
                try {

                    dialog.cancel();
                    PushNotificatonsModel pushNotificatonsModel = response.body();
                    if (pushNotificatonsModel.isStatus()) {

                        SessionManager sessionManager = new SessionManager(getApplicationContext());
                        sessionManager.setKeyUserId(token);

                        sessionManager.setLoggined(true);
                        Intent intent;
                        intent = new Intent(getApplicationContext(), MyCartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "" + response.message(), Toast.LENGTH_LONG).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<PushNotificatonsModel> call, Throwable t) {

            }


        });


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
