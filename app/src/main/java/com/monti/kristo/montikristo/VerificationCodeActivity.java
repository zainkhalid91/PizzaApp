package com.monti.kristo.montikristo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

public class VerificationCodeActivity extends AppCompatActivity implements TextWatcher {

    Button btn_back, btn_verify;
    String email;
    EditText editText_one, editText_two, editText_three, editText_four, editText_five, editText_six;
    ProgressDialog progressDialog;
    TextView head1, head2;
    Typeface myFont, myFontReg;
    String code = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // below code provide full screen mode
       /* this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/
        setContentView(R.layout.activity_verification_code);

        myFont = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Bold.ttf");
        myFontReg = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Regular.ttf");

        head1 = findViewById(R.id.header1);
        head2 = findViewById(R.id.header2);

        head1.setTypeface(myFont);
        head2.setTypeface(myFontReg);

        editText_one = findViewById(R.id.editTextone);
        editText_two = findViewById(R.id.editTexttwo);
        editText_three = findViewById(R.id.editTextthree);
        editText_four = findViewById(R.id.editTextfour);
        editText_five = findViewById(R.id.editTextfive);
        editText_six = findViewById(R.id.editTextsix);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
        }

        btn_verify = findViewById(R.id.btn_verify);
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode(v);
            }
        });
/*
        btn_back = (Button) findViewById(R.id.btn_back_vcode);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });*/

        editText_one.addTextChangedListener(this);
        editText_two.addTextChangedListener(this);
        editText_three.addTextChangedListener(this);
        editText_four.addTextChangedListener(this);
        editText_five.addTextChangedListener(this);
        editText_six.addTextChangedListener(this);
    }

    private void sendVerificationCode(View v) {

        progressDialog = new ProgressDialog(v.getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.progressdialog_processing));

        code = editText_one.getText().toString().trim() + editText_two.getText().toString().trim()
                + editText_three.getText().toString().trim() + editText_four.getText().toString().trim()
                + editText_five.getText().toString().trim() + editText_six.getText().toString().trim();
        String vCode = code;

        if (code.isEmpty()) {
            editText_one.setError("Verification code required");
            editText_one.requestFocus();
            return;
        }

        progressDialog.show();

        Call<StatusModel> call = apiclient.Companion
                .getApiClientInstance()
                .getApi()
                .verifyCode(vCode);

        call.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {
                String resp = null;
                StatusModel statusModel = null;
                progressDialog.cancel();
                try {

                    statusModel = response.body();
                    String status = statusModel.getStatus();

                    if (status.equals("true")) {

                        System.out.println(resp);

                        //Toast.makeText(getApplicationContext(), "Code verified", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), NewPasswordActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Code does not match", Toast.LENGTH_LONG).show();
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "" + statusModel.getMsg(), Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
            }


            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "Error! Verification code does not match.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        if (editable.length() == 1) {
            if (editText_one.length() == 1) {
                editText_two.requestFocus();
            }

            if (editText_two.length() == 1) {
                editText_three.requestFocus();
            }
            if (editText_three.length() == 1) {
                editText_four.requestFocus();
            }
            if (editText_four.length() == 1) {
                editText_five.requestFocus();
            }
            if (editText_five.length() == 1) {
                editText_six.requestFocus();
            }
        } else if (editable.length() == 0) {
            if (editText_six.length() == 0) {
                editText_five.requestFocus();
            }
            if (editText_five.length() == 0) {
                editText_four.requestFocus();
            }
            if (editText_four.length() == 0) {
                editText_three.requestFocus();
            }
            if (editText_three.length() == 0) {
                editText_two.requestFocus();
            }
            if (editText_two.length() == 0) {
                editText_one.requestFocus();
            }
        }

    }

    @Override
    public void onBackPressed() {

    }
}
