package com.monti.kristo.montikristo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.monti.kristo.montikristo.model.LoginUserModel;
import com.monti.kristo.montikristo.model.StatusTokenModel;
import com.monti.kristo.montikristo.rest.apiclient;
import com.monti.kristo.montikristo.utils.JWTUtils;
import com.monti.kristo.montikristo.utils.SessionManager;
import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    ImageView btn_back;
    FloatingActionButton editBtn;
    EditText edit_name, edit_address, edit_pNum;
    TextView editDone, emaill, label, done, uName, adderss, mobile, emailAdd;
    ProgressDialog progressDialog;
    Typeface myFont, myFontReg;
    ImageButton uploadPicture;
    CircleImageView circleImageView;
    int cID;
    LoginUserModel loginUserModel;
    private String ts;
    private Long tsLong;
    private String bucket, key, region;
    ScrollView actvitiy_profile_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Crashlytics
        Fabric.with(this, new Crashlytics());
        tsLong = System.currentTimeMillis() / 1000;
        ts = tsLong.toString();

        myFont = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Bold.ttf");
        myFontReg = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Regular.ttf");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        SessionManager sessionManager = new SessionManager(getApplicationContext());
        String decoded = null;
        try {
            decoded = JWTUtils.decoded(sessionManager.getKeyUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        loginUserModel = gson.fromJson(decoded, LoginUserModel.class);

        cID = loginUserModel.getId();

        edit_name = findViewById(R.id.txt_name);
        edit_address = findViewById(R.id.txt_address);
        edit_pNum = findViewById(R.id.txt_mobile);
        actvitiy_profile_layout = findViewById(R.id.actvitiy_profile_layout);


        editDone = findViewById(R.id.textview_done);
        editDone.setTypeface(myFontReg);
        emaill = findViewById(R.id.txt_email);
        emaill.setTypeface(myFontReg);
        label = findViewById(R.id.textview_label_profile);
        label.setTypeface(myFont);
        uName = findViewById(R.id.head_name);
        uName.setTypeface(myFont);
        adderss = findViewById(R.id.head_address);
        adderss.setTypeface(myFont);
        mobile = findViewById(R.id.head_mobile);
        mobile.setTypeface(myFont);
        emailAdd = findViewById(R.id.head_email);
        emailAdd.setTypeface(myFont);


        emaill.setText(loginUserModel.getEmail());
        emaill.setTypeface(myFontReg);
        edit_name.setText(loginUserModel.getFname());
        edit_name.setTypeface(myFontReg);
        edit_address.setText(loginUserModel.getAddress());
        edit_address.setTypeface(myFontReg);
        edit_pNum.setText(loginUserModel.getpNum());
        edit_pNum.setTypeface(myFontReg);
        btn_back = findViewById(R.id.btn_back_prof);
        editBtn = findViewById(R.id.btn_edit);

        btn_back.setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(getApplicationContext(), MyCartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });


        final boolean[] flag = {true};
        editBtn.setOnClickListener(v -> {

            if (flag[0]) {

                editBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_save_black_24dp));
                edit_name.setEnabled(true);
                edit_address.setEnabled(true);
                edit_pNum.setEnabled(true);
                //editDone.setVisibility(View.VISIBLE);
                flag[0] = false;

            } else if (!flag[0]) {

                editBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_edit));
                if(edit_name.getText().toString().isEmpty()){
                    edit_name.setError("Please enter your full name.");
                    return;
                }

                if(edit_address.getText().toString().isEmpty()){
                    edit_address.setError("Please enter your delivery address.");
                    return;
                }
                if(edit_pNum.getText().toString().isEmpty())
                {
                    edit_pNum.setError("Please enter your contact number.");
                    return;
                } if (emaill.getText().toString().isEmpty()){
                    emaill.setError("Please enter a valid email address.");
                    return;
                }

                    UpdateProfile(v);
                    flag[0] = true;

            }

        });

        //editDone.setOnClickListener(this::UpdateProfile);

    }


    private void UpdateProfile(View v) {

        edit_name.setEnabled(false);
        edit_address.setEnabled(false);
        edit_pNum.setEnabled(false);

        //editDone.setVisibility(View.INVISIBLE);

        final String eName = edit_name.getText().toString().trim();
        final String eAddress = edit_address.getText().toString().trim();
        final String ePnum = edit_pNum.getText().toString().trim();
        final String eEmail = emaill.getText().toString().trim();


        progressDialog = new ProgressDialog(v.getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.progressdialog_processing));
        progressDialog.show();

        Call<StatusTokenModel> call = apiclient.Companion
                .getApiClientInstance()
                .getApi()
                .updateProfile(cID, eName, ePnum, eAddress, eEmail);

        call.enqueue(new Callback<StatusTokenModel>() {
            @Override
            public void onResponse(Call<StatusTokenModel> call, Response<StatusTokenModel> response) {


                StatusTokenModel resp = response.body();
                boolean status = resp.getStatus();
                String token = resp.getToken();

                SessionManager sessionManager = new SessionManager(getApplicationContext());
                sessionManager.setKeyUserId(token);
                String decoded = null;
                try {
                    decoded = JWTUtils.decoded(token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                LoginUserModel userModel = gson.fromJson(decoded, LoginUserModel.class);


                if (status) {


                    edit_name.setText(userModel.getFname());
                    edit_address.setText(userModel.getAddress());
                    edit_pNum.setText(userModel.getpNum());
                    emaill.setText(userModel.getEmail());

                    //Toast.makeText(getApplicationContext(), "Profile Updated Successfully", Toast.LENGTH_LONG).show();
                    Snackbar.make(actvitiy_profile_layout, "Profile Updated Successfully", Snackbar.LENGTH_LONG).show();


                    progressDialog.cancel();


                } else {
                   // Toast.makeText(getApplicationContext(), "Error occurred, could not update.", Toast.LENGTH_LONG).show();
                    Snackbar.make(actvitiy_profile_layout, "Error occurred, could not update.", Snackbar.LENGTH_LONG).show();

                    progressDialog.cancel();

                }
            }

            @Override
            public void onFailure(Call<StatusTokenModel> call, Throwable t) {
               // Toast.makeText(getApplicationContext(), "Server time-out", Toast.LENGTH_SHORT).show();
                Snackbar.make(actvitiy_profile_layout, "Server time-out", Snackbar.LENGTH_LONG).show();

                progressDialog.cancel();

            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
