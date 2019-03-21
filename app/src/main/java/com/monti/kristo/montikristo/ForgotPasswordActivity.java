package com.monti.kristo.montikristo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    Button sendCode, btn_back;
    EditText vEmail;
    ProgressDialog progressDialog;
    Typeface myFont, myFontReg;
    TextView head1, head2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // below code provide full screen mode
       /* this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/
        setContentView(R.layout.activity_forgot_password);

        myFont = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Bold.ttf");
        myFontReg = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Regular.ttf");

        head1 = findViewById(R.id.header1);
        head2 = findViewById(R.id.header2);

        head1.setTypeface(myFont);
        head2.setTypeface(myFontReg);

        sendCode = findViewById(R.id.btn_send_vcode);
        btn_back = findViewById(R.id.btn_back_fpass);
        vEmail = findViewById(R.id.edittext_Femail);

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode(v);
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

    private void sendVerificationCode(View v) {

        final String email = vEmail.getText().toString().trim();

        progressDialog = new ProgressDialog(v.getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.progressdialog_processing));

        if (email.isEmpty()) {
            vEmail.setError("Email is required");
            vEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            vEmail.setError("Enter a valid email");
            vEmail.requestFocus();
            return;
        }

        progressDialog.show();

        Call<StatusModel> call = apiclient
                .getApiClientInstance()
                .getApi()
                .forgotPassword(email);

        call.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {

                progressDialog.cancel();

                StatusModel statusModel = response.body();
                String status = statusModel.getStatus();


                if (status.equals("true")) {
                    Toast.makeText(getApplicationContext(), "Please check your email", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), EmailCheckActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);

                } else if (status.equals("false")) {
                    Toast.makeText(getApplicationContext(), "Email address does not exist.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "No response", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
