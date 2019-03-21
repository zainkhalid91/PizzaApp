package com.monti.kristo.montikristo.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.monti.kristo.montikristo.PreviousOrdersActivity;
import com.monti.kristo.montikristo.R;
import com.monti.kristo.montikristo.model.ItemsModel;
import com.monti.kristo.montikristo.model.ProductDescriptionModel;
import com.monti.kristo.montikristo.rest.apiclient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder> {

    private Context mContext;
    private List<ItemsModel> itemsModels;

    public ItemsAdapter(Context mContext, List<ItemsModel> itemsModels) {
        this.mContext = mContext;
        this.itemsModels = itemsModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cards, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final ItemsModel itemsModel = itemsModels.get(position);
        holder.title.setText(itemsModel.getName());
        //holder.size.setText("Size : "+itemsModel.getSize());
        holder.price.setText("RS" + itemsModel.getPrice());
        final int id = itemsModel.getId();

        holder.view.setOnClickListener(v -> {

            if (isNetworkAvailable()) {
                holder.progressDialog.show();

                Call<ProductDescriptionModel> call = apiclient
                        .getApiClientInstance()
                        .getApi()
                        .productDetails(id);

                call.enqueue(new Callback<ProductDescriptionModel>() {
                    @Override
                    public void onResponse(Call<ProductDescriptionModel> call, Response<ProductDescriptionModel> response) {


                        holder.progressDialog.cancel();

                        ProductDescriptionModel resp = response.body();
                        String status = resp.getStatus();

                        String name = resp.getName();
                        String description = resp.getDesc();
                        int price = resp.getPrice();
                        String dTime = resp.getdTime();
                        String image = resp.getPicture();
                        int dFee = resp.getdFee();


                        if (status.equals("true")) {


                            Intent intent = new Intent(mContext, PreviousOrdersActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("itemName", name);
                            intent.putExtra("itemDescription", description);
                            intent.putExtra("itemPrice", price);
                            intent.putExtra("itemDtime", dTime);
                            intent.putExtra("itemImage", image);
                            intent.putExtra("itemDFee", dFee);
                            mContext.startActivity(intent);

                        } else {
                            holder.progressDialog.cancel();
                            Toast.makeText(mContext, "Error occured could not update.", Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ProductDescriptionModel> call, Throwable t) {
                        holder.progressDialog.cancel();
                        Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();

                    }
                });
            } else {

                Toast.makeText(mContext, "No internet connection available", Toast.LENGTH_LONG).show();

            }
        });


        // loading album cover using Glide library
        Glide.with(mContext).load(itemsModel.getThumbnail()).into(holder.thumbnail);

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public int getItemCount() {
        return itemsModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, size, price;
        public ImageView thumbnail, overflow;
        public View view;
        ProgressDialog progressDialog;


        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            title = view.findViewById(R.id.title);
            price = view.findViewById(R.id.price);
            thumbnail = view.findViewById(R.id.thumbnail);
            overflow = view.findViewById(R.id.overflow);
            progressDialog = new ProgressDialog(view.getContext());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(mContext.getString(R.string.progressdialog_processing));

        }
    }
}

