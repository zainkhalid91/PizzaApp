package com.monti.kristo.montikristo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.monti.kristo.montikristo.model.LoginUserModel;
import com.monti.kristo.montikristo.model.StatusTokenModel;
import com.monti.kristo.montikristo.rest.apiclient;
import com.monti.kristo.montikristo.utils.JWTUtils;
import com.monti.kristo.montikristo.utils.SessionManager;
import com.onecode.s3.callback.S3Callback;
import com.onecode.s3.model.S3BucketData;
import com.onecode.s3.model.S3Credentials;
import com.onecode.s3.service.S3UploadService;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    // accessKeyId: 'AKIAJL5T4LYEDFD2334A',
//secretAccessKey: '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',
//region: 'ap-southeast-1'

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 0;
    private final String ACCESS_KEY = "AKIAJL5T4LYEDFD2334A";
    private final String SECRET_KEY = "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";
    Button btn_back;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
        uploadPicture = findViewById(R.id.imageButton_uploadPhoto);
        circleImageView = findViewById(R.id.profile_picture);


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
        String finalUrl = "https://s3.ap-southeast-1.amazonaws.com/montikrist/profileImages/" + cID;
        getImage(finalUrl);

        btn_back = findViewById(R.id.btn_back_prof);
        editBtn = findViewById(R.id.btn_edit);

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

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit_name.setEnabled(true);
                edit_address.setEnabled(true);
                edit_pNum.setEnabled(true);

                editDone.setVisibility(View.VISIBLE);
            }
        });

        editDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfile(v);
            }
        });

        uploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
            }
        });
    }

    private void requestCameraPermission() {

        Dexter.withActivity(this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    imagepicker();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }

    public void imagepicker() {
        final CharSequence[] items;

        items = new CharSequence[2];
        items[1] = "Camera";
        items[0] = "Gallery";


        android.app.AlertDialog.Builder alertdialog = new android.app.AlertDialog.Builder(ProfileActivity.this);
        alertdialog.setTitle("Add Image");
        alertdialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Camera")) {
                    openCamera();
                } else if (items[item].equals("Gallery")) {
                    openGallery();
                }
            }
        });
        alertdialog.show();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }


    private void openGallery() {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        cameraIntent.setType("image/*");
        startActivityForResult(cameraIntent, REQUEST_GALLERY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            //URI FROM THE BITMAP

            Uri tempUri = getImageUri(getApplicationContext(), photo);

            //ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));
            Glide.with(getApplicationContext()).load(finalFile).into(circleImageView);
            String bucket = "montikrist", key = loginUserModel.getId() + ts, region = "ap-southeast-1";
            //imgUrl = "https://s3.us-east-2.amazonaws.com/locuswallet/"+key;

            S3Credentials s3Credentials = new S3Credentials(ACCESS_KEY, SECRET_KEY, null);
            S3Callback s3Callback = new S3Callback("com.example.android.S3_UPLOAD_COMPLETED", "Uploaded Successfully");

            S3BucketData s3BucketData = new S3BucketData.Builder()
                    .setCredentials(s3Credentials)
                    .setBucket(bucket)
                    .setKey(key)
                    .setRegion(region)
                    .build();

            S3UploadService.upload(getApplicationContext(), s3BucketData, finalFile, true, s3Callback);
            Toast.makeText(getApplicationContext(), "Image Updated", Toast.LENGTH_LONG).show();


        }
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {

            Uri contentURI = data.getData();

            //ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(contentURI));
            Glide.with(getApplicationContext()).load(finalFile).into(circleImageView);

            bucket = "montikrist";
            key = "profileImages/" + loginUserModel.getId();
            region = "ap-southeast-1";
            //  imgUrl = "https://s3.us-east-2.amazonaws.com/locuswallet/"+key;

            S3Credentials s3Credentials = new S3Credentials(ACCESS_KEY, SECRET_KEY, null);
            S3Callback s3Callback = new S3Callback("com.example.android.S3_UPLOAD_COMPLETED", "Uploaded Successfully");

            S3BucketData s3BucketData = new S3BucketData.Builder()
                    .setCredentials(s3Credentials)
                    .setBucket(bucket)
                    .setKey(key)
                    .setRegion(region)
                    .build();

            S3UploadService.upload(getApplicationContext(), s3BucketData, finalFile, true, s3Callback);
            Toast.makeText(getApplicationContext(), "Image Updated", Toast.LENGTH_LONG).show();
        }
    }
   /* protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            //URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), photo);

            //ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));

            SessionManager sessionManager = new SessionManager(getApplicationContext());
            String decoded = null;
            try {
                decoded = JWTUtils.decoded(sessionManager.getKeyUserId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            LoginUserModel userModel = gson.fromJson(decoded, LoginUserModel.class);

            bucket = "montikristo"; key = "profileImages/"+userModel.getId(); region = "ap-southeast-1";


            S3Credentials s3Credentials = new S3Credentials(ACCESS_KEY, SECRET_KEY, null);
            S3Callback s3Callback = new S3Callback("com.example.android.S3_UPLOAD_COMPLETED", "Uploaded Successfully");


            S3BucketData s3BucketData = new S3BucketData.Builder()
                    .setCredentials(s3Credentials)
                    .setBucket(bucket)
                    .setKey(key)
                    .setRegion(region)
                    .build();

            S3UploadService.upload(getApplicationContext(), s3BucketData, finalFile , true, s3Callback);

        }
    }*/

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    private void UpdateProfile(View v) {

        edit_name.setEnabled(false);
        edit_address.setEnabled(false);
        edit_pNum.setEnabled(false);

        editDone.setVisibility(View.INVISIBLE);

        final String eName = edit_name.getText().toString().trim();
        final String eAddress = edit_address.getText().toString().trim();
        final String ePnum = edit_pNum.getText().toString().trim();
        final String eEmail = emaill.getText().toString().trim();


        progressDialog = new ProgressDialog(v.getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.progressdialog_processing));
        progressDialog.show();

        Call<StatusTokenModel> call = apiclient
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


                //String finalUrl = "https://s3.us-east-2.amazonaws.com/etherincash-bucket/profileImages/"+userModel.getId();

                if (status) {

                    edit_name.setText(userModel.getFname());
                    edit_address.setText(userModel.getAddress());
                    edit_pNum.setText(userModel.getpNum());
                    emaill.setText(userModel.getEmail());
                    String finalUrl = "https://ap-southeast-1.amazonaws.com/montikrist/" + userModel.getId();
                    getImage(finalUrl);

                    Toast.makeText(getApplicationContext(), "Profile Updated Successfully", Toast.LENGTH_LONG).show();

                    progressDialog.cancel();

                   /* Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);*/

                } else {
                    Toast.makeText(getApplicationContext(), "Error occured could not update.", Toast.LENGTH_LONG).show();
                    progressDialog.cancel();

                }
            }

            @Override
            public void onFailure(Call<StatusTokenModel> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();

            }
        });


    }

    private void getImage(String imageurl) {
        Picasso.get().load(imageurl).transform(new CropCircleTransformation()).
                into(circleImageView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
