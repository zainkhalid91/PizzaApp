package com.monti.kristo.montikristo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.monti.kristo.montikristo.R;
import com.monti.kristo.montikristo.interfaces.BalanceChangeListner;
import com.monti.kristo.montikristo.model.BalanceModel;
import com.monti.kristo.montikristo.model.ItemsModel;
import com.monti.kristo.montikristo.model.StatusModel;
import com.monti.kristo.montikristo.rest.apiclient;
import com.nex3z.notificationbadge.NotificationBadge;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {

    int count;
    int grandTotal = 0;
    int deliveryFee = 0;
    private Context mContext;
    private ArrayList<ItemsModel> itemsModelList;
    private BalanceChangeListner balanceChangeListner;
    private BalanceModel balanceModel;


    public MyCartAdapter(Context mContext, ArrayList<ItemsModel> itemsModelList, BalanceChangeListner balanceChangeListner) {
        this.mContext = mContext;
        this.itemsModelList = itemsModelList;
        this.balanceChangeListner = balanceChangeListner;
    }

    @Override
    public MyCartAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item_cards, parent, false);

        return new MyCartAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyCartAdapter.MyViewHolder holder, final int position) {
        final ItemsModel itemsModel = itemsModelList.get(position);
        holder.title.setText(itemsModel.getName());
        holder.price.setText("PKR" + itemsModel.getPrice() + ".00");
        holder.pid = String.valueOf(itemsModel.getProductId());
        holder.badge.setNumber(itemsModel.getQuantity());
        Glide.with(mContext).load(itemsModel.getThumbnail()).into(holder.thumbnail);

        /*holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                itemsModel.setQuantity(1);
                Intent intent = new Intent("custom-message");
                intent.putExtra("name",itemsModel.getName());
                intent.putExtra("price",itemsModel.getPrice());
                intent.putExtra("qty", itemsModel.getQuantity());
                intent.putExtra("deliveryFee", itemsModel.getDeliverfee());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        });*/



/*
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               int count = cartItemsModel.getQty();
                int price = cartItemsModel.getProductPrice();
                String pizzaName = cartItemsModel.getProductName();
                count+=1;
                int subTotalPrice = price * count;
                cartItemsModel.setQty(count);
                cartItemsModel.setSubTotal(subTotalPrice);
                notifyDataSetChanged();
                setBalanceUpdated();
                updateQuantity(cartItemsModel.getCustomerID(),count, pizzaName, subTotalPrice);
              }

        });

        holder.btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = cartItemsModel.getQty();
                int productPrice = cartItemsModel.getProductPrice();
                int subTotalPrice = cartItemsModel.getSubTotal();
                String pizzaName = cartItemsModel.getProductName();
                count-=1;
                int subTotal = subTotalPrice - productPrice;
                cartItemsModel.setQty(count>=1?count:1);
                if(subTotal > 0) {
                    cartItemsModel.setSubTotal(subTotal);
                    notifyDataSetChanged();
                    setBalanceUpdated();
                }
                updateQuantity(cartItemsModel.getCustomerID(),count, pizzaName, subTotal);
            }
        });*/

    }

    /* public void sendToOrderDetails(){
         PuchasedProductModel puchasedProductModel = new PuchasedProductModel();
         puchasedProductModel.setBalanceModel(balanceModel);
         puchasedProductModel.setCartItemsModels(itemsModelList);
         Intent intent = new Intent(mContext, OrderDetailsActivitly.class);
         intent.putExtra(KEY_ORDER_DETAILS , puchasedProductModel);
         mContext.startActivity(intent);
     }*/
    public void updateQuantity(int cID, int qty, String pizzaName, int subtotal) {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedDate = df.format(c);

        Call<StatusModel> call = apiclient
                .getApiClientInstance()
                .getApi()
                .addToCart(cID, subtotal, formattedDate, pizzaName, qty);

        call.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {

                //   Toast.makeText(getApplicationContext(), ""+formattedDate, Toast.LENGTH_LONG).show();


                StatusModel resp = response.body();
                String status = resp.getStatus();
                String msg = resp.getMsg();

                if (status.equals("true")) {

                    Toast.makeText(mContext, "" + msg, Toast.LENGTH_LONG).show();


                } else {
                    // progressDialog.cancel();
                    Toast.makeText(mContext, "" + msg, Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {
                //  progressDialog.cancel();
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();

            }
        });

    }

   /* private void setBalanceUpdated() {
        balanceModel = new BalanceModel();
        int subTotal = 0;
       // int size = cartItemsModelList.size();
           //  for (int i = 0; i < size; i++) {
           //     subTotal += (cartItemsModelList.get(i).getProductPrice()*cartItemsModelList.get(i).getQty());
          //  }

        deliveryFee = 12;
        grandTotal = subTotal + deliveryFee;
        balanceModel.setGrandTotal(grandTotal+"");
        balanceModel.setDelieveryFee(deliveryFee+"");
        balanceModel.setSubTotal(subTotal+"");
        balanceChangeListner.upDateBalance(balanceModel);
    }*/

    @Override
    public int getItemCount() {
        return itemsModelList.size();
    }

    public void removeItem(int position) {
        itemsModelList.remove(position);
        //setBalanceUpdated();
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(ItemsModel item, int position) {
        itemsModelList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, price, quantity;
        public ImageView thumbnail, overflow;
        public Button btnAdd, btnSub;
        public RelativeLayout viewBackground, viewForeground;
        String pid = "";
        NotificationBadge badge;


        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.pTitle);
            price = view.findViewById(R.id.pPrice);
            thumbnail = view.findViewById(R.id.pThumbnail);
            quantity = view.findViewById(R.id.pizza_qty);
            badge = view.findViewById(R.id.badge);
            // viewBackground = view.findViewById(R.id.view_background);
            // viewForeground = view.findViewById(R.id.view_foreground);
            // setBalanceUpdated();


        }
    }
}



