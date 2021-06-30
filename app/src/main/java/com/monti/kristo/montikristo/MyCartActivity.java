package com.monti.kristo.montikristo;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.Transition;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.monti.kristo.montikristo.adapters.MyCartAdapter;
import com.monti.kristo.montikristo.interfaces.BalanceChangeListner;
import com.monti.kristo.montikristo.model.BalanceModel;
import com.monti.kristo.montikristo.model.CartItemsModel;
import com.monti.kristo.montikristo.model.GrandTotalModel;
import com.monti.kristo.montikristo.model.ItemsModel;
import com.monti.kristo.montikristo.model.LoginUserModel;
import com.monti.kristo.montikristo.model.PuchasedProductModel;
import com.monti.kristo.montikristo.model.StatusItemModel;
import com.monti.kristo.montikristo.rest.apiclient;
import com.monti.kristo.montikristo.utils.DealsActvitiy;
import com.monti.kristo.montikristo.utils.JWTUtils;
import com.monti.kristo.montikristo.utils.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monti.kristo.montikristo.utils.Constants.KEY_ORDER_DETAILS;

public class MyCartActivity extends AppCompatActivity implements BalanceChangeListner {

    static ArrayList<ItemsModel> itemsModelList;
    static PuchasedProductModel puchasedProductModel;
    private static MyCartActivity sSoleInstance;
    public Button confirmOrder, testbtn;
    public TextView subTotal, delivery, pizzaTitle, pizzaPrice, pizzaQty, stapperQty, pizzaIngredientsHearder, pizzaNameHeaderTitle;
    public TextView grandTotal;
    public int selectedPostion = 0;
    ImageView btn_back;
    ImageView btnAdd, btnSub;
    GrandTotalModel grandTotalModel;
    private RecyclerView recyclerView;
    private MyCartAdapter adapter;
    private ArrayList<ItemsModel> itemModelsServerData;
    private ArrayList<CartItemsModel> cartItemsModel;
    private int price, fee, cid, pID;
    private int itemPrice = 0, itemQty = 0, deliverFee = 0;
    private KenBurnsView imageViewHeader;
    private SessionManager sessionManager;
    private ConstraintLayout constraintLayout;

    public static MyCartActivity getInstance() {
        if (sSoleInstance == null) { //if there is no instance available... create new one
            sSoleInstance = new MyCartActivity();
        }

        return sSoleInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);
        toolbar();
        setUpNavigationDrawer();
        //Crashlytics
        Fabric.with(this, new Crashlytics());


       /* final Calendar c = Calendar.getInstance();
        int  hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String am_pm;
        if (c.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM";
        else if (c.get(Calendar.AM_PM) == Calendar.PM)
            am_pm = "PM";

        // Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss a");
        String formattedDate = df.format(c.getTime());
        Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();

        int cTime = Integer.parseInt(formattedDate);
        int specTime = Integer.parseInt("11:00:00 AM");
        int specTime1 = Integer.parseInt("24:00:00 AM");
        if (Date.parse(formattedDate) <  Date.parse(formattedDate) || cTime < specTime1){
            showAlertDialogButtonClicked(getCurrentFocus());

        }
*/
       /* //dateTime check
        Calendar currentTime = Calendar.getInstance();
        int time = 24-1;
        currentTime.setTimeZone(TimeZone.getTimeZone("GMT+5"));
        int currentHourin24Format = currentTime.get(Calendar.HOUR_OF_DAY);  //24 hour format
        if (currentHourin24Format > 11 ) {
           return;
        }
        else {
            showAlertDialogButtonClicked(getCurrentFocus());
        }
        if (currentHourin24Format < 24 ) {
            return;
        }
        else {
            showAlertDialogButtonClicked(getCurrentFocus());
        }*/
        recyclerView = findViewById(R.id.recycler_view_prev_order);
        btn_back = findViewById(R.id.btn_back_prevOrder);
        confirmOrder = findViewById(R.id.btn_cOrder);
        imageViewHeader = findViewById(R.id.header);
        grandTotalModel = new GrandTotalModel();

        constraintLayout = findViewById(R.id.constraint_layout_mycart);
        testbtn = findViewById(R.id.btn_test);
        subTotal = findViewById(R.id.deals_sub_total_amnt);
        delivery = findViewById(R.id.delivery_amnt);
        grandTotal = findViewById(R.id.total_amnt);
        pizzaTitle = findViewById(R.id.dealName);
        pizzaPrice = findViewById(R.id.textPizzaPrice);
        pizzaQty = findViewById(R.id.textPizzaQty);
        stapperQty = findViewById(R.id.pizza_qty);
        pizzaIngredientsHearder = findViewById(R.id.pizza_ingredients);
        pizzaNameHeaderTitle = findViewById(R.id.pizzaNameTitle);

        btnAdd = findViewById(R.id.btnDealQtyAdd);
        btnSub = findViewById(R.id.btnDealQtySub);


        itemModelsServerData = new ArrayList<>();

        cartItemsModel = new ArrayList<>();

        puchasedProductModel = new PuchasedProductModel();

        sessionManager = new SessionManager(getApplicationContext());


        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            cid = extras.getInt("custID");

        }


        confirmOrder.setOnClickListener(v -> sendToOrderDetails());


        itemsModelList = new ArrayList<>();
        adapter = new MyCartAdapter(this, itemsModelList, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        imageViewHeader.setTransitionListener(new KenBurnsView.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {

            }
        });

        testbtn.setOnClickListener(v -> {
            Intent testintent = new Intent(MyCartActivity.this, PizzaAnimation.class);
            startActivity(testintent);
        });


        btnAdd.setOnClickListener(v -> {
            try {
                for (int i = 0 ; i < itemsModelList.size(); i++){
                    if (itemsModelList.get(selectedPostion).getType().equals("deal")){
                        Snackbar.make(constraintLayout, "Note: Promo code cannot be applied with deals.", Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    if (itemsModelList.get(selectedPostion).getType() .equals("product")){
                        showAlertDialogButtonClicked(getCurrentFocus());
                        break;
                    }
                }
                int count = itemsModelList.get(selectedPostion).getQuantity();
                if (count >= 0) {
                    count += 1;
                    itemsModelList.get(selectedPostion).setQuantity(count);
                    adapter.notifyDataSetChanged();
                    stapperQty.setText("" + count);
                    pizzaQty.setText(count + "(Qty)");

                    calculateTotalBalance();

                }
            } catch (Exception ex) {
                Snackbar.make(constraintLayout, "Server not responding, try again later.", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        btnSub.setOnClickListener(v -> {
            try {

                if (stapperQty.getText() == "0") {
                    itemsModelList.remove(selectedPostion);
                } else {
                    int count = itemsModelList.get(selectedPostion).getQuantity();
                    if (count > 0) {
                        count -= 1;
                        itemsModelList.get(selectedPostion).setQuantity(count);
                        adapter.notifyDataSetChanged();
                        stapperQty.setText("" + count);
                        pizzaQty.setText(count + "(Qty)");

                        calculateTotalBalance();
                    }
                    if (stapperQty.getText() == "0") {
                        grandTotal.setText("0.0");

                    }
                }
            } catch (Exception ex) {
                Snackbar.make(constraintLayout, "Server not responding, try again later.", Snackbar.LENGTH_SHORT)
                        .show();

            }
        });

        prepareList();
    }

    private void setUpNavigationDrawer() {
//          ------------------------- Material Drawer ---------------------------------
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        String decoded = null;
        try {
            decoded = JWTUtils.decoded(sessionManager.getKeyUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        LoginUserModel userModel = gson.fromJson(decoded, LoginUserModel.class);
        String name = userModel.getFname();
        String email = userModel.getEmail();
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.welcome_screen)
                .addProfiles(
                        new ProfileDrawerItem().withName(name).withEmail(email).withIcon(getResources().getDrawable(R.drawable.launch_icon))
                )
                .withOnAccountHeaderItemLongClickListener((view, profile, current) -> {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                    return false;
                })


                .build();

        DrawerBuilder wip = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withHeaderDivider(true)
                .withToolbar(toolbar())
                .withSelectedItem(-1)

                .addDrawerItems(
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_item_cart)).withIcon(R.drawable.shopping_bag).withIdentifier(0),
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_item_profile)).withIcon(R.drawable.profile).withIdentifier(1),
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_item_contact)).withIcon(R.drawable.contact_us).withIdentifier(2),
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.Deals)).withIcon(R.drawable.settings).withIdentifier(3),

                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_item_rateus)).withIcon(R.drawable.rate_us).withIdentifier(4),
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_item_share)).withIcon(R.drawable.share).withIdentifier(5),

                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Terms and Conditions").withIcon(R.drawable.terms).withIdentifier(6),
                        new SecondaryDrawerItem().withName("Logout").withIcon(R.drawable.logout).withTextColor(Color.RED).withIdentifier(7))
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (drawerItem instanceof Nameable) {

                        if (drawerItem.getIdentifier() == 0) {
                            Intent intent = new Intent(MyCartActivity.this, PreviousOrdersActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        if (drawerItem.getIdentifier() == 1) {
                            Intent profile;
                            profile = new Intent(MyCartActivity.this, ProfileActivity.class);
                            profile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(profile);

                        }
                        if (drawerItem.getIdentifier() == 2) {
                            Intent chat;
                            chat = new Intent(MyCartActivity.this, ChatActivity.class);
                            chat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(chat);

                        }
                        if (drawerItem.getIdentifier() == 3) {
                            Intent deals;
                            deals = new Intent(MyCartActivity.this, DealsActvitiy.class);
                            startActivity(deals);
                        }
                        if (drawerItem.getIdentifier() == 4) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.monti.kristo")));
                        }
                        if (drawerItem.getIdentifier() == 5) {
                            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.monti.kristo");
                            startActivity(Intent.createChooser(intent, "Share"));
                        }

                        if (drawerItem.getIdentifier() == 7) {
                            sessionManager.setLoggined(false);
                            sessionManager.setLogOut(true);
                            Intent logout;
                            logout = new Intent(MyCartActivity.this, WelcomeScreenActivity.class);
                            logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(logout);
                            finish();
                        }
                    }
                    return false;
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {

                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        //changeStatusBarColor();
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        //changeStatusBarTranslucent();
                    }
                });

        Drawer result = wip.build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
    }


    protected Toolbar toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle("");
        }
        return toolbar;
    }

    public void updateView() {
        pizzaTitle.setText(itemsModelList.get(selectedPostion).getName());
        pizzaNameHeaderTitle.setText(itemsModelList.get(selectedPostion).getName());
        pizzaIngredientsHearder.setText(itemsModelList.get(selectedPostion).getDescription());
        pizzaPrice.setText("PKR" + String.valueOf(itemsModelList.get(selectedPostion).getPrice()) + " x");
        pizzaQty.setText(String.valueOf(itemsModelList.get(selectedPostion).getQuantity()) + "(Qty)");
        stapperQty.setText("" + itemsModelList.get(selectedPostion).getQuantity());

        calculateTotalBalance();

    }

    public void calculateTotalBalance() {
        double totalBalance = 0;//grandTotalModel.getTotal();
        double subTotal = 0;
        double delieveryFee = 0;
        boolean isFound = false;

        for (ItemsModel itemsModel : itemsModelList) {

            if (itemsModelList.get(selectedPostion).getQuantity() > 0) {
                delieveryFee += (itemsModel.getDeliverfee());
                totalBalance += ((itemsModel.getPrice() * itemsModel.getQuantity()) + itemsModel.getDeliverfee());
                grandTotalModel.setTotal(totalBalance);
                subTotal += (itemsModel.getPrice() * itemsModel.getQuantity());
                itemsModel.setSubTotal(subTotal);

            }
        }

        if (itemModelsServerData.size() <= 0) {
            //here too
            if (itemsModelList.get(selectedPostion).getQuantity() == 0) {
                itemModelsServerData.add(itemsModelList.get(selectedPostion));
            }
        }
        for (int i = 0; i < itemModelsServerData.size(); i++) {
            if (itemModelsServerData.get(i).getId() == itemsModelList.get(selectedPostion).getId()) {
                itemModelsServerData.get(i).setQuantity(itemsModelList.get(selectedPostion).getQuantity());
                isFound = true;
                break;
            } else {
                isFound = false;
            }
        }

        if (!isFound) {
            //quantity zero
            if (itemsModelList.get(selectedPostion).getQuantity() == 0) {
                itemModelsServerData.add(itemsModelList.get(selectedPostion));
            }
        }
        BalanceModel balanceModel = new BalanceModel();
        balanceModel.setGrandTotal(totalBalance);
        balanceModel.setSubTotal(subTotal);
        balanceModel.setDelieveryFee(delieveryFee);

        upDateBalance(balanceModel);

        puchasedProductModel.setBalanceModel(balanceModel);
        puchasedProductModel.setCartItemsModels(itemModelsServerData);


    }


    public void calculateTotalBalance(int toppings) {
        double totalBalance = 0;//grandTotalModel.getTotal();
        if (toppings > 0) {

            totalBalance += 100;
        }
        double subTotal = 0;
        double delieveryFee = 0;
        boolean isFound = false;

        for (ItemsModel itemsModel : itemsModelList) {

            if (itemsModelList.get(selectedPostion).getQuantity() > 0) {
                delieveryFee += (itemsModel.getDeliverfee());
                totalBalance += ((itemsModel.getPrice() * itemsModel.getQuantity()) + itemsModel.getDeliverfee());
                grandTotalModel.setTotal(totalBalance);
                subTotal += (itemsModel.getPrice() * itemsModel.getQuantity());
                itemsModel.setSubTotal(subTotal);

            }
        }

        if (itemModelsServerData.size() <= 0) {
            //here too
            if (itemsModelList.get(selectedPostion).getQuantity() == 0) {
                itemModelsServerData.add(itemsModelList.get(selectedPostion));
            }
        }
        for (int i = 0; i < itemModelsServerData.size(); i++) {
            if (itemModelsServerData.get(i).getId() == itemsModelList.get(selectedPostion).getId()) {
                itemModelsServerData.get(i).setQuantity(itemsModelList.get(selectedPostion).getQuantity());
                isFound = true;
                break;
            } else {
                isFound = false;
            }
        }

        if (!isFound) {
            //quantity zero
            if (itemsModelList.get(selectedPostion).getQuantity() == 0) {
                itemModelsServerData.add(itemsModelList.get(selectedPostion));
            }
        }
        BalanceModel balanceModel = new BalanceModel();
        balanceModel.setGrandTotal(totalBalance);
        balanceModel.setSubTotal(subTotal);
        balanceModel.setDelieveryFee(delieveryFee);

        upDateBalance(balanceModel);

        puchasedProductModel.setBalanceModel(balanceModel);
        puchasedProductModel.setCartItemsModels(itemModelsServerData);


    }

    public void sendToOrderDetails() {
        try {
            for (int i = 0; i < puchasedProductModel.getCartItemsModels().size(); i++) {
                if (puchasedProductModel.getCartItemsModels().get(i).getQuantity() == 0) {
                    puchasedProductModel.getCartItemsModels().remove(i);
                }
            }
            if (puchasedProductModel.getBalanceModel().getGrandTotal() > 0) {
                Intent intent = new Intent(getApplicationContext(), OrderDetailsActivitly.class);
                intent.putExtra(KEY_ORDER_DETAILS, puchasedProductModel);
                startActivity(intent);
            } else {
                Snackbar.make(constraintLayout, "Please select your cart item before proceeding.", Snackbar.LENGTH_SHORT)
                        .show();

            }
        } catch (Exception ex) {
            Snackbar.make(constraintLayout, "Sever not responding, try again later.", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Adding few items for testing
     */
    private void prepareList() {
        Call<StatusItemModel> call = apiclient.Companion
                .getApiClientInstance()
                .getApi()
                .allProducts();

        call.enqueue(new Callback<StatusItemModel>() {
            @Override
            public void onResponse(Call<StatusItemModel> call, Response<StatusItemModel> response) {


                //    mSwipeRefreshLayout.setRefreshing(false);

                // progressDialog.cancel();
                if (response.isSuccessful()) {
                    StatusItemModel resp = response.body();
                    String status = resp.getStatus();


                    if (status.equals("true")) {

                        itemsModelList.clear();
                        itemsModelList.addAll(Arrays.asList(resp.getGetAllItems()));
                        if (itemsModelList.size() > 0) {
                            selectedPostion = 0;
                            updateView();
                        }
                        adapter.notifyDataSetChanged();

                    } else {
                        //   mSwipeRefreshLayout.setRefreshing(false);
                        // progressDialog.cancel();

                        Snackbar.make(constraintLayout, "Your cart is empty.", Snackbar.LENGTH_SHORT)
                                .show();

                    }
                } else {
                    Snackbar.make(constraintLayout, response.message(), Snackbar.LENGTH_SHORT)
                            .show();

                }
            }

            @Override
            public void onFailure(Call<StatusItemModel> call, Throwable t) {
                //     mSwipeRefreshLayout.setRefreshing(false);
                // progressDialog.cancel();
                Snackbar.make(constraintLayout, "Please check your internet connection.", Snackbar.LENGTH_LONG)
                        .show();


            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void upDateBalance(BalanceModel balanceModel) {

        subTotal.setText("PKR " + balanceModel.getSubTotal());
        delivery.setText("PKR " + balanceModel.getDelieveryFee());
        grandTotal.setText("PKR " + balanceModel.getGrandTotal());
    }

    public void showAlertDialogButtonClicked(View view) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.topping_custom_dialog, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button btnYes = customLayout.findViewById(R.id.yes_add_topping);
        btnYes.setOnClickListener(v -> {

           // calculateTotalBalance(Integer.valueOf(itemsModelList.get(0).getToppingPrice()));
            calculateTotalBalance(Integer.valueOf(itemsModelList.get(selectedPostion).getToppingPrice()));
            dialog.dismiss();
        });
        Button btnNo = customLayout.findViewById(R.id.no_add_topping);
        btnNo.setOnClickListener(v -> dialog.dismiss());
    }
}

