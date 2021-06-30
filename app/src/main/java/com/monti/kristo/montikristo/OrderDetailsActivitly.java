package com.monti.kristo.montikristo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.circulardialog.CDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.monti.kristo.montikristo.model.AreaModel;
import com.monti.kristo.montikristo.model.AssignedAreaModel;
import com.monti.kristo.montikristo.model.BalanceModel;
import com.monti.kristo.montikristo.model.Head;
import com.monti.kristo.montikristo.model.LoginUserModel;
import com.monti.kristo.montikristo.model.PreviousOrderModel;
import com.monti.kristo.montikristo.model.PuchasedProductModel;
import com.monti.kristo.montikristo.model.StatusModel;
import com.monti.kristo.montikristo.model.ValidatePromoModel;
import com.monti.kristo.montikristo.rest.apiclient;
import com.monti.kristo.montikristo.rest.network.ApiInterface;
import com.monti.kristo.montikristo.utils.Constants;
import com.monti.kristo.montikristo.utils.JWTUtils;
import com.monti.kristo.montikristo.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivitly extends AppCompatActivity {

    Button btn_order;
    ImageView btn_back;
    TextView subTotal, deliveryFee, total, cName, cMobile, validate_textview, discount_amount_tv;
    EditText cAddresss, edittxt_promo;
    TextView label, head1, head2, subTotalHead, deliverFeeHead, totalHead, nameHead, addressHead, mobileHead;
    int price, fee, userID, priceWithFee, netAmount, discount = 100;
    ProgressDialog dialog;
    Typeface myFont, myFontReg;
    BalanceModel balanceModel;
    PuchasedProductModel productModel;
    Spinner assigned_area_spinner;
    CDialog cDialog;
    int cID = 0;
   // double gTotal = 0;
    double sTotal = 0;
    AssignedAreaModel areaListModel;
    ConstraintLayout order_detail_layout;
    int areaId = 0;
    PreviousOrderModel previousOrderModel;
    PuchasedProductModel puchasedProductModel;
    ValidatePromoModel validatePromoModel;
    Head head;
    RelativeLayout layout_promo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_activitly);

        //Crashlytics
        Fabric.with(this, new Crashlytics());

        myFont = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Bold.ttf");
        myFontReg = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Regular.ttf");

        productModel = (PuchasedProductModel) getIntent().getSerializableExtra(Constants.KEY_ORDER_DETAILS);

        areaListModel = new AssignedAreaModel();

        balanceModel = productModel.getBalanceModel();



        sTotal = balanceModel.getSubTotal();

        subTotal = findViewById(R.id.deals_sub_total_amnt);
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
        edittxt_promo = findViewById(R.id.edittxt_promo);
        validate_textview = findViewById(R.id.validate_textview);
        discount_amount_tv = findViewById(R.id.discount_amount_tv);
        layout_promo = findViewById(R.id.layout_promo);

        label.setTypeface(myFontReg);

        order_detail_layout = findViewById(R.id.order_detail_layout);

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

        total.setText(String.valueOf(balanceModel.getGrandTotal()));
        total.setTypeface(myFontReg);

        userID = loginUserModel.getId();


        btn_back = findViewById(R.id.btn_back_odetail);
        btn_back.setOnClickListener(v -> finish());

        for (int i = 0; i< productModel.getCartItemsModels().size(); i++){
            if (productModel.getCartItemsModels().get(i).getType().equals("deal")){
               edittxt_promo.setEnabled(false);
               validate_textview.setEnabled(false);
               validate_textview.setTextColor(getResources().getColor(R.color.md_blue_grey_200));
               break;
            }
        }


        validate_textview.setOnClickListener(v -> apiclient.Companion.getApiClientInstance().getApi().validatePromo(areaId, edittxt_promo.getText().toString()).enqueue(new Callback<ValidatePromoModel>() {
            @Override
            public void onResponse(Call<ValidatePromoModel> call, Response<ValidatePromoModel> response) {
                if (response.isSuccessful()) {
                    validatePromoModel = response.body();
                    if (!validatePromoModel.getStatus()) {
                        Snackbar.make(order_detail_layout, "Promo code you entered in not valid.", Snackbar.LENGTH_SHORT).show();
                    } else {
                        validate_textview.setText("Promo Valid");
                        validate_textview.setEnabled(false);
                        validate_textview.setTextColor(getResources().getColor(R.color.md_green_600));
                        Snackbar.make(order_detail_layout, "Promo code added successfully.", Snackbar.LENGTH_SHORT).show();
                        discount();
                    }
                }
            }

            @Override
            public void onFailure(Call<ValidatePromoModel> call, Throwable t) {

            }
        }));


        btn_order.setOnClickListener(v -> {
            if (assigned_area_spinner.getSelectedItemPosition() == 0) {
                Snackbar.make(order_detail_layout, "Please select location to proceed.", Snackbar.LENGTH_SHORT)
                        .show();
                return;
            }
            if (cAddresss.getText().toString().isEmpty()) {
                cAddresss.setError("Please enter your address to proceed");
                //Snackbar.make(order_detail_layout, "Please enter your address to proceed", Snackbar.LENGTH_LONG).show();
                return;
            }
            if (cName.getText().toString().isEmpty()) {
//                cName.setError("Please enter your name first.");
                Snackbar.make(order_detail_layout, "Please enter your full name in profile section to proceed.", Snackbar.LENGTH_LONG).show();
                return;
            }
            placeOrder(v, cID, balanceModel.getGrandTotal(), cAddresss.getText().toString(), sTotal, cName.getText().toString());
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

        apiclient.Companion.getApiClientInstance().getApi().assignedAreas().enqueue(new Callback<AssignedAreaModel>() {
            @Override
            public void onResponse(Call<AssignedAreaModel> call, Response<AssignedAreaModel> response) {
                if (response.isSuccessful()) {
                    areaListModel = response.body();
                    List<String> AreaName = new ArrayList<>();
                    for (int i = 0; i < areaListModel.getArea().size(); i++) {
                        AreaName.add(areaListModel.getArea().get(i).getName());
                    }
                    AreaName.add(0, "Select Location");
                    assigned_area_spinner.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, AreaName));
                    assigned_area_spinner.setPopupBackgroundResource(R.color.white);

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


            Call<StatusModel> call = apiclient.Companion
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
                   /* if (!edittxt_promo.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Promo code added successfully and is being verified.", Toast.LENGTH_LONG).show();
                    }
*/

                    if (status.equals("true")) {
                        Intent intent = new Intent(OrderDetailsActivitly.this, PizzaAnimation.class);
                        //MyCartActivity.getInstance().finish();
                        startActivity(intent);
                        finish();
                    } else {
                        //  dialog.cancel();
                        finish();

                    }
                }

                @Override
                public void onFailure(Call<StatusModel> call, Throwable t) {
                    dialog.cancel();
                    Snackbar.make(order_detail_layout, "Error reaching server.", Snackbar.LENGTH_SHORT)
                            .show();
                }
            });


        } else {

            Snackbar.make(order_detail_layout, "No internet connection available", Snackbar.LENGTH_SHORT)
                    .show();

        }
    }

    private void discount() {
        Double totalamount = Double.valueOf(validatePromoModel.getData().getDiscount_value());
        Double total_ = Double.parseDouble(total.getText().toString());
        Double total_t = Double.parseDouble(total.getText().toString());
        discount_amount_tv.setTextColor(getResources().getColor(R.color.md_red_700));
        if (validatePromoModel.getData().getDiscount_type().equals("fixedvalue")) {
            discount_amount_tv.setText(String.format("PKR %s",(total_ - totalamount)));
        } else {
            total_ =- (total_ * totalamount) / 100;
            discount_amount_tv.setText(String.format("PKR %s" , total_));
        }
        total.setText(String.format("PKR %s",(total_ + total_t)));
        balanceModel.setGrandTotal(total_ + total_t);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
