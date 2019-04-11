package com.monti.kristo.montikristo.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
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
import com.monti.kristo.montikristo.MyCartActivity;
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
    View itemView;
    private Context mContext;
    private ArrayList<ItemsModel> itemsModelList;
    private BalanceChangeListner balanceChangeListner;
    private BalanceModel balanceModel;
    int selectedPosition = -1;
    private int currentpos = 0;
    private ArrayList<Boolean> tickArray = new ArrayList<>();


    public MyCartAdapter(Context mContext, ArrayList<ItemsModel> itemsModelList, BalanceChangeListner balanceChangeListner) {
        this.mContext = mContext;
        this.itemsModelList = itemsModelList;
        this.balanceChangeListner = balanceChangeListner;
    }

    @Override
    public MyCartAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item_cards, parent, false);

        return new MyCartAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyCartAdapter.MyViewHolder holder, final int position) {
        if (selectedPosition == position)
            holder.tick.setVisibility(View.VISIBLE);
        else
            holder.tick.setVisibility(View.INVISIBLE);
        final ItemsModel itemsModel = itemsModelList.get(position);
        holder.title.setText(itemsModel.getName());
        holder.price.setText("PKR" + itemsModel.getPrice() + ".00");
        holder.pid = String.valueOf(itemsModel.getProductId());
        holder.badge.setNumber(itemsModel.getQuantity());
        Glide.with(mContext).load(itemsModel.getThumbnail()).into(holder.thumbnail);

        currentpos = position;

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentpos = position;
                if (itemsModelList.get(position).getQuantity() == 0) {
                    ((MyCartActivity) mContext).grandTotal.setText("PKR 0.0");
                }
                ((MyCartActivity) mContext).selectedPostion = position;
                if (mContext instanceof MyCartActivity) {
                    ((MyCartActivity) mContext).updateView();
                }
                selectedPosition = position;
                notifyDataSetChanged();
            }
        });

    }
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
        public ImageView thumbnail, overflow, tick;
        public Button btnAdd, btnSub;
        public RelativeLayout viewBackground, viewForeground;
        String pid = "";
        CardView cardView;
        NotificationBadge badge;


        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.pTitle);
            price = view.findViewById(R.id.pPrice);
            thumbnail = view.findViewById(R.id.pThumbnail);
            quantity = view.findViewById(R.id.pizza_qty);
            badge = view.findViewById(R.id.badge);
            cardView = view.findViewById(R.id.card_view);
            tick = view.findViewById(R.id.tick);

        }
    }
}



