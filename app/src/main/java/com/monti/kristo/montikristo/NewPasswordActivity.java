package com.monti.kristo.montikristo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.monti.kristo.montikristo.model.StatusModel;
import com.monti.kristo.montikristo.rest.apiclient;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPasswordActivity extends AppCompatActivity {

    Button submit;
    EditText newPass, confirmPass;
    ProgressDialog progressDialog;
    String email;
    TextView head1;
    Typeface myFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        //Crashlytics
        Fabric.with(this, new Crashlytics());

        myFont = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Bold.ttf");
        head1 = findViewById(R.id.header1);
        head1.setTypeface(myFont);

        newPass = findViewById(R.id.edittext_NPass);
        confirmPass = findViewById(R.id.edittext_CPass);
        submit = findViewById(R.id.btn_submit);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newPass.getText().toString().equals(confirmPass.getText().toString())) {

                    if (newPass.getText().toString().length() >= 8) {

                        submitNewPassword(v);

                    } else {

                        Toast.makeText(getApplicationContext(), "Password must be eight characters long", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    private void submitNewPassword(View v) {

        progressDialog = new ProgressDialog(v.getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.progressdialog_processing));
        progressDialog.show();

        String nPassword = newPass.getText().toString().trim();


        Call<StatusModel> call = apiclient
                .getApiClientInstance()
                .getApi()
                .resetForgotPassword(nPassword, email);

        call.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {

                progressDialog.dismiss();
                StatusModel resp = response.body();
                String status = resp.getStatus();
                System.out.print(resp);

                if (status.equals("true")) {
                    Intent intent = new Intent(getApplicationContext(), LoginScreenActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "" + resp.getMsg(), Toast.LENGTH_LONG).show();
                    finish();
                }

            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed() {

    }
}
