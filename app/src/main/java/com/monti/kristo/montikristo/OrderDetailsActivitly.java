package com.monti.kristo.montikristo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.circulardialog.CDialog;
import com.google.gson.Gson;
import com.monti.kristo.montikristo.model.AreaModel;
import com.monti.kristo.montikristo.model.AssignedAreaModel;
import com.monti.kristo.montikristo.model.BalanceModel;
import com.monti.kristo.montikristo.model.Head;
import com.monti.kristo.montikristo.model.LoginUserModel;
import com.monti.kristo.montikristo.model.PreviousOrderModel;
import com.monti.kristo.montikristo.model.PuchasedProductModel;
import com.monti.kristo.montikristo.model.StatusModel;
import com.monti.kristo.montikristo.rest.apiclient;
import com.monti.kristo.montikristo.utils.Constants;
import com.monti.kristo.montikristo.utils.JWTUtils;
import com.monti.kristo.montikristo.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivitly extends AppCompatActivity {

    Button btn_back, btn_order;
    TextView subTotal, deliveryFee, total, cName, cMobile;
    EditText cAddresss;
    TextView label, head1, head2, subTotalHead, deliverFeeHead, totalHead, nameHead, addressHead, mobileHead;
    int price, fee, userID, priceWithFee, netAmount, discount = 100;
    ProgressDialog dialog;
    Typeface myFont, myFontReg;
    BalanceModel balanceModel;
    PuchasedProductModel productModel;
    Spinner assigned_area_spinner;
    CDialog cDialog;
    int cID = 0;
    double gTotal = 0;
    double sTotal = 0;
    AssignedAreaModel areaListModel;
    int areaId = 0;
    PreviousOrderModel previousOrderModel;
    Head head;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_activitly);

        myFont = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Bold.ttf");
        myFontReg = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Regular.ttf");

        productModel = (PuchasedProductModel) getIntent().getSerializableExtra(Constants.KEY_ORDER_DETAILS);

        areaListModel = new AssignedAreaModel();

        balanceModel = productModel.getBalanceModel();

        gTotal = balanceModel.getGrandTotal();
        sTotal = balanceModel.getSubTotal();

        subTotal = findViewById(R.id.sub_total_amnt);
        deliveryFee = findViewById(R.id.delivery_amnt);
        total = findViewById(R.id.total_amnt);
        cName = findViewById(R.id.txt_name);
        cAddresss = findViewById(R.id.txt_address);
        cMobile = findViewById(R.id.txt_mobile);
        btn_order = findViewById(R.id.btn_order);
        assigned_area_spinner = findViewById(R.id.spinner_location);
        subTotalHead = findViewById(R.id.sub_total);
        subTotalHead.setTypeface(myFontReg);
        deliverFeeHead = findViewById(R.id.delivery_fee);
        deliverFeeHead.setTypeface(myFontReg);
        totalHead = findViewById(R.id.total);
        totalHead.setTypeface(myFontReg);
        nameHead = findViewById(R.id.head_name);
        nameHead.setTypeface(myFontReg);
        addressHead = findViewById(R.id.head_address);
        addressHead.setTypeface(myFontReg);
        mobileHead = findViewById(R.id.head_mobile);
        mobileHead.setTypeface(myFontReg);
        head1 = findViewById(R.id.head_description);
        head1.setTypeface(myFontReg);
        head2 = findViewById(R.id.head_order_details);
        head2.setTypeface(myFontReg);
        label = findViewById(R.id.textview_label);
        label.setTypeface(myFontReg);


        SessionManager sessionManager = new SessionManager(getApplicationContext());
        String decoded = null;
        try {
            decoded = JWTUtils.decoded(sessionManager.getKeyUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        LoginUserModel loginUserModel = gson.fromJson(decoded, LoginUserModel.class);
        cID = loginUserModel.getId();
        final String address = loginUserModel.getAddress();

        cName.setText(loginUserModel.getFname());
        cName.setTypeface(myFontReg);

        cAddresss.setText(loginUserModel.getAddress());
        cAddresss.setTypeface(myFontReg);

        cMobile.setText(loginUserModel.getpNum());
        cMobile.setTypeface(myFontReg);

        subTotal.setText("PKR " + balanceModel.getSubTotal());
        subTotal.setTypeface(myFontReg);

        deliveryFee.setText("PKR " + balanceModel.getDelieveryFee());
        deliveryFee.setTypeface(myFontReg);

        total.setText("PKR " + balanceModel.getGrandTotal());
        total.setTypeface(myFontReg);

        userID = loginUserModel.getId();


        btn_back = findViewById(R.id.btn_back_odetail);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder(v, cID, gTotal, address, sTotal, cName.getText().toString());

            }
        });
        assigned_area_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getAdapter().getItem(position);
                ArrayList<AreaModel> arrayList = new ArrayList<>(areaListModel.getArea());
                for (int i = 0; i < areaListModel.getArea().size(); i++) {
                    if (areaListModel.getArea().get(i).getName().equals(name)) {
                        areaId = arrayList.get(i).getId();
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        apiclient.getApiClientInstance().getApi().assignedAreas().enqueue(new Callback<AssignedAreaModel>() {
            @Override
            public void onResponse(Call<AssignedAreaModel> call, Response<AssignedAreaModel> response) {
                if (response.isSuccessful()) {
                    areaListModel = response.body();
                    List<String> AreaName = new ArrayList<>();
                    for (int i = 0; i < areaListModel.getArea().size(); i++) {
                        AreaName.add(areaListModel.getArea().get(i).getName());
                    }
                    AreaName.add(0, "Select Location");
                    assigned_area_spinner.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, AreaName));

                }
            }

            @Override
            public void onFailure(Call<AssignedAreaModel> call, Throwable t) {

            }
        });
    }

    private void placeOrder(View v, final int id, double gTotal, String address, double sTotal, String name) {

        dialog = new ProgressDialog(v.getContext());
        dialog.setMessage("Processing..");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedDate = df.format(c);

        if (isNetworkAvailable()) {
            dialog.show();

            Gson gson = new Gson();
            String products = gson.toJson(productModel.getCartItemsModels());
            //Toast.makeText(getApplicationContext(), "Order Placed Sucessfully", Toast.LENGTH_LONG).show();
            Call<StatusModel> call = apiclient
                    .getApiClientInstance()
                    .getApi()
                    .placeOrder(id, name, gTotal, address, gTotal, c.getTime(), formattedDate, products, areaId, cMobile.getText().toString());

            call.enqueue(new Callback<StatusModel>() {
                @Override
                public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {

                    dialog.cancel();
                    StatusModel resp = response.body();
                    String status = resp.getStatus();
                    String msg = resp.getMsg();


                    if (status.equals("true")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(OrderDetailsActivitly.this).create(); //Read Update
                        alertDialog.setTitle("Your order has been placed successfully");
                        // alertDialog.setMessage("Order ID: " + head.getOrderID());


                        alertDialog.setButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent;
                                intent = new Intent(getApplicationContext(), MyCartActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });

                        alertDialog.show();  //<-- See This!

                    } else {

                        dialog.cancel();
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
