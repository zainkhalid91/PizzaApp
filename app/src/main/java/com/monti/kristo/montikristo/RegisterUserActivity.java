package com.monti.kristo.montikristo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.monti.kristo.montikristo.model.StatusModel;
import com.monti.kristo.montikristo.rest.apiclient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterUserActivity extends AppCompatActivity {

    Button btn_back, reg_btn;
    TextView login, head1, head2, headSub;
    EditText txt_name, txt_email, txt_contact, txt_address, txt_pass;
    TextView txt_error;
    ProgressDialog dialog;
    Typeface myFont, myFontReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // below code provide full screen mode
    /*    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(R.layout.activity_register_user);

        myFont = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Bold.ttf");
        myFontReg = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Regular.ttf");


        txt_name = findViewById(R.id.edittext_name);
        txt_email = findViewById(R.id.edittext_email);
        txt_contact = findViewById(R.id.edittext_phone_no);
        txt_address = findViewById(R.id.edittext_address);
        txt_pass = findViewById(R.id.edittext_pass);
        txt_error = findViewById(R.id.textview_errormsg);

        head1 = findViewById(R.id.header1);
        head2 = findViewById(R.id.header_);
        headSub = findViewById(R.id.header2);

        head1.setTypeface(myFont);
        head2.setTypeface(myFont);
        headSub.setTypeface(myFontReg);

        login = findViewById(R.id.textview_login);
        reg_btn = findViewById(R.id.btn_register);

        reg_btn.setOnClickListener(v -> registerUser(v));

        btn_back = findViewById(R.id.btn_back_reg);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), LoginScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), LoginScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerUser(View v) {

        String name = txt_name.getText().toString().trim();
        String email = txt_email.getText().toString().trim();
        String contact = txt_contact.getText().toString().trim();
        String address = txt_address.getText().toString().trim();
        String password = txt_pass.getText().toString().trim();
        int roleId = 1;
        String source = "email";

        dialog = new ProgressDialog(v.getContext());
        dialog.setMessage("Processing..");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        if (name.isEmpty()) {
            txt_name.setError("Name required");
            txt_name.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            txt_email.setError("Email address required");
            txt_email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txt_email.setError("Enter a valid email");
            txt_email.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            txt_pass.setError("Password required");
            txt_pass.requestFocus();
            return;
        }
        if (password.length() < 8) {
            txt_pass.setError("Password should be atleast 8 character long");
            txt_pass.requestFocus();
            return;
        }
        if (contact.isEmpty()) {
            txt_contact.setError("Contact no. required");
            txt_contact.requestFocus();
            return;
        }
        if (address.isEmpty()) {
            txt_address.setError("Address required");
            txt_address.requestFocus();
            return;
        }

        if (isNetworkAvailable()) {

            dialog.show();

            Call<StatusModel> call = apiclient
                    .getApiClientInstance()
                    .getApi()
                    .createUser(name, contact, email, password, address, source, roleId);

            call.enqueue(new Callback<StatusModel>() {
                @Override
                public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {

                    dialog.cancel();
                    StatusModel resp = response.body();
                    String status = resp.getStatus();
                    // Toast.makeText(getApplicationContext(), resp.getMsg(), Toast.LENGTH_SHORT).show();

                    if (status.equals("true")) {

                        Intent intent;
                        intent = new Intent(getApplicationContext(), LoginScreenActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        Toast.makeText(getApplicationContext(), "Registered Sucessfully, Please Sign In", Toast.LENGTH_SHORT).show();


                        //clear all fields
//                        txt_name.setText("");
//                        txt_email.setText("");
//                        txt_address.setText("");
//                        txt_contact.setText("");
//                        txt_pass.setText("");

                    } else {

                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "Try again.", Toast.LENGTH_LONG).show();
                        Intent intent;
                        intent = new Intent(getApplicationContext(), RegisterUserActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                }

                @Override
                public void onFailure(Call<StatusModel> call, Throwable t) {
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });


        } else {

            Toast.makeText(getApplicationContext(), "No internet connection available", Toast.LENGTH_LONG).show();

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
