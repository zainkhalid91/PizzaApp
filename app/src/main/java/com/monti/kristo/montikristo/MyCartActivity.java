package com.monti.kristo.montikristo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.Transition;
import com.google.gson.Gson;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.monti.kristo.montikristo.adapters.MyCartAdapter;
import com.monti.kristo.montikristo.helpermethod.RecyclerItemTouchHelper;
import com.monti.kristo.montikristo.interfaces.BalanceChangeListner;
import com.monti.kristo.montikristo.model.BalanceModel;
import com.monti.kristo.montikristo.model.CartItemsModel;
import com.monti.kristo.montikristo.model.GrandTotalModel;
import com.monti.kristo.montikristo.model.ItemsModel;
import com.monti.kristo.montikristo.model.LoginUserModel;
import com.monti.kristo.montikristo.model.PuchasedProductModel;
import com.monti.kristo.montikristo.model.StatusItemModel;
import com.monti.kristo.montikristo.model.StatusModel;
import com.monti.kristo.montikristo.rest.apiclient;
import com.monti.kristo.montikristo.utils.JWTUtils;
import com.monti.kristo.montikristo.utils.RecyclerItemClickListener;
import com.monti.kristo.montikristo.utils.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monti.kristo.montikristo.utils.Constants.KEY_ORDER_DETAILS;

public class MyCartActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, BalanceChangeListner {

    GrandTotalModel grandTotalModel;
    private RecyclerView recyclerView;
    private MyCartAdapter adapter;
    private ArrayList<ItemsModel> itemsModelList;
    private ArrayList<ItemsModel> itemModelsServerData;
    private ArrayList<CartItemsModel> cartItemsModel;
    private Button btn_back, confirmOrder, btnAdd, btnSub;
    private int price, fee, cid, pID;
    private ProgressDialog progressDialog;
    private int itemPrice = 0, itemQty = 0, deliverFee = 0;
    private TextView subTotal, delivery, grandTotal, pizzaTitle, pizzaPrice, pizzaQty, stapperQty;
    private KenBurnsView imageViewHeader;
    private int selectedPostion = 0;
    private PuchasedProductModel puchasedProductModel;
    private SessionManager sessionManager;
    private ScrollView recylerScrollView, layoutScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);
        toolbar();
        setUpNavigationDrawer();

        recyclerView = findViewById(R.id.recycler_view_prev_order);
        btn_back = findViewById(R.id.btn_back_prevOrder);
        confirmOrder = findViewById(R.id.btn_cOrder);
        imageViewHeader = findViewById(R.id.header);
        grandTotalModel = new GrandTotalModel();

        /*Scroll view touch objects*//*
        layoutScrollView = (ScrollView) findViewById(R.id.layoutScroll);
        recylerScrollView = (ScrollView) findViewById(R.id.recyclerLayer);*/


        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);

        subTotal = findViewById(R.id.sub_total_amnt);
        delivery = findViewById(R.id.delivery_amnt);
        grandTotal = findViewById(R.id.total_amnt);
        pizzaTitle = findViewById(R.id.pizzaName);
        pizzaPrice = findViewById(R.id.textPizzaPrice);
        pizzaQty = findViewById(R.id.textPizzaQty);
        stapperQty = findViewById(R.id.pizza_qty);

        btnAdd = findViewById(R.id.btnQtyAdd);
        btnSub = findViewById(R.id.btnQtySub);

        BalanceModel balanceModel = new BalanceModel();

        itemModelsServerData = new ArrayList<>();

        cartItemsModel = new ArrayList<>();

        puchasedProductModel = new PuchasedProductModel();

        sessionManager = new SessionManager(getApplicationContext());

        /*layoutScrollView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                findViewById(R.id.recyclerLayer).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });


        recylerScrollView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event)
            {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/


        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            cid = extras.getInt("custID");

        }

       /* btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });*/

        confirmOrder.setOnClickListener(v -> sendToOrderDetails());


        itemsModelList = new ArrayList<>();
        adapter = new MyCartAdapter(this, itemsModelList, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectedPostion = position;
                updateView();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        imageViewHeader.setTransitionListener(new KenBurnsView.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {

            }
        });


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        btnAdd.setOnClickListener(v -> {
            int count = itemsModelList.get(selectedPostion).getQuantity();
            if (count >= 0) {
                count += 1;
                itemsModelList.get(selectedPostion).setQuantity(count);
                adapter.notifyDataSetChanged();
                stapperQty.setText("" + count);
                pizzaQty.setText(count + "(Qty)");

                calculateTotalBalance();

            }
        });

        btnSub.setOnClickListener(v -> {
            int count = itemsModelList.get(selectedPostion).getQuantity();
            if (count > 0) {
                count -= 1;
                itemsModelList.get(selectedPostion).setQuantity(count);
                adapter.notifyDataSetChanged();
                stapperQty.setText("" + count);
                pizzaQty.setText(count + "(Qty)");

                calculateTotalBalance();

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
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_item_cart)).withIcon(R.drawable.shopping_bag),
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_item_profile)).withIcon(R.drawable.profile),
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_item_contact)).withIcon(R.drawable.contact_us),
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_item_settings)).withIcon(R.drawable.settings),

                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_item_rateus)).withIcon(R.drawable.rate_us),
                        new PrimaryDrawerItem().withName(getResources().getString(R.string.nav_item_share)).withIcon(R.drawable.share),

                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Terms and Conditions").withIcon(R.drawable.terms),
                        new SecondaryDrawerItem().withName("Logout").withIcon(R.drawable.logout).withTextColor(Color.RED)


                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    selectItem(position);
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

    private void selectItem(int position) {
        final String[] url = {""};
        final CharSequence[] items;
        AlertDialog.Builder builder;
        AlertDialog alert;
        switch (position) {
            case 2:
                Intent intent;
                intent = new Intent(MyCartActivity.this, PreviousOrdersActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
//            case 2:
//                manageWalletsRouter.open(this, false);
//                break;
            case 3:

                Intent profile;
                profile = new Intent(MyCartActivity.this, ProfileActivity.class);
                profile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(profile);
                break;


            case 4:

                Intent chat;
                chat = new Intent(MyCartActivity.this, ChatActivity.class);
                chat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(chat);
                break;

                /*Intent mailto = new Intent(Intent.ACTION_SENDTO);
                mailto.setType("message/rfc822"); // use from live device
                mailto.setData(Uri.parse("mailto:answer@speroinfo.io")
                        .buildUpon()
                        .appendQueryParameter("cc", "answer@speroinfo.io")
                        .appendQueryParameter("subject", "Etherin Cash support question")
                        .appendQueryParameter("body", "Dear Etherin cash support,")
                        .build());
                startActivity(Intent.createChooser(mailto, "Select email application."));
                break;*/

            case 5:

                Intent settings;
                settings = new Intent(MyCartActivity.this, SettingsActivity.class);
                settings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settings);
                break;


            case 11:
                sessionManager.setLoggined(false);
                sessionManager.setLogOut(true);
                Intent logout;
                logout = new Intent(MyCartActivity.this, WelcomeScreenActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logout);
                finish();
                break;

            case 7:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.monti.kristo.montikristo")));
                break;

        }

    }

    protected Toolbar toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle("");
        }
        return toolbar;
    }

    private void updateView() {
        pizzaTitle.setText(itemsModelList.get(selectedPostion).getName());
        pizzaPrice.setText("PKR" + String.valueOf(itemsModelList.get(selectedPostion).getPrice()) + " x");
        pizzaQty.setText(String.valueOf(itemsModelList.get(selectedPostion).getQuantity()) + "(Qty)");
        stapperQty.setText("" + itemsModelList.get(selectedPostion).getQuantity());

        calculateTotalBalance();

    }

    private void calculateTotalBalance() {
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

    public void sendToOrderDetails() {
        if (puchasedProductModel.getBalanceModel().getGrandTotal() > 0) {
            Intent intent = new Intent(getApplicationContext(), OrderDetailsActivitly.class);
            intent.putExtra(KEY_ORDER_DETAILS, puchasedProductModel);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Your cart is empty.", Toast.LENGTH_LONG).show();

        }
    }

    /**
     * Adding few items for testing
     */
    private void prepareList() {

    /*    progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.progressdialog_processing));
        progressDialog.show();*/

        Call<StatusItemModel> call = apiclient
                .getApiClientInstance()
                .getApi()
                .getAllProducts();

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

                        Toast.makeText(getApplicationContext(), "Your cart is empty.", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<StatusItemModel> call, Throwable t) {
                //     mSwipeRefreshLayout.setRefreshing(false);
                // progressDialog.cancel();

                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MyCartAdapter.MyViewHolder) {

            String title = (String) ((MyCartAdapter.MyViewHolder) viewHolder).title.getText();

            // remove the item from recycler view
            adapter.removeItem(viewHolder.getAdapterPosition());

            Call<StatusModel> call = apiclient
                    .getApiClientInstance()
                    .getApi()
                    .removeFromCart(title, cid);

            call.enqueue(new Callback<StatusModel>() {
                @Override
                public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {


                    //    mSwipeRefreshLayout.setRefreshing(false);

                    // progressDialog.cancel();
                    StatusModel resp = response.body();
                    String status = resp.getStatus();


                    if (status.equals(true)) {

                        Toast.makeText(getApplicationContext(), "" + resp.getMsg(), Toast.LENGTH_LONG).show();


                    } else {
                        //   mSwipeRefreshLayout.setRefreshing(false);
                        // progressDialog.cancel();

                        Toast.makeText(getApplicationContext(), "" + resp.getMsg(), Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(Call<StatusModel> call, Throwable t) {
                    //     mSwipeRefreshLayout.setRefreshing(false);
                    //   progressDialog.cancel();

                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

                }
            });


            //deleteItem();

            // get the removed item name to display it in snack bar
            //String name = cartItemsModelList.get(viewHolder.getAdapterPosition()).getProductName();

            // backup of removed item for undo purpose
            // final CartItemsModel deletedItem = cartItemsModelList.get(viewHolder.getAdapterPosition());
            // final int deletedIndex = viewHolder.getAdapterPosition();


            // showing snack bar with Undo option
            /*Snackbar snackbar = Snackbar
                    .make(constraintLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    adapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();*/
        }
    }

    @Override
    public void upDateBalance(BalanceModel balanceModel) {

        subTotal.setText("PKR " + balanceModel.getSubTotal());
        delivery.setText("PKR " + balanceModel.getDelieveryFee());
        grandTotal.setText("PKR " + grandTotalModel.getTotal());
    }
}
