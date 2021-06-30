package com.monti.kristo.montikristo.adapters

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.monti.kristo.montikristo.R
import com.monti.kristo.montikristo.model.Detail
import com.monti.kristo.montikristo.model.PreviousOrderModel

class PreviousOrderAdapter(private val mContext: Context, private val cartItemsModelList: PreviousOrderModel) : RecyclerView.Adapter<PreviousOrderAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.previous_order_cards, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val (details, head) = cartItemsModelList.ods[position]
        holder.apply {
            date.text = head.OrderDate
            title.text = "Order Id: " + head.orderID.toString()
            total.text = "Rs. ${head.GrossTotal}"
            status.text = head.OrderStatus
//            quantity.text = mContext.getString(R.string.orderPlaceQty, details[0].ProductQuantity)
        }

        when (head.OrderStatus) {
            "Delivered" -> holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.md_green_500))
            "Pending" -> holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.md_orange_500))
        }

        holder.recyclerView.layoutManager = LinearLayoutManager(mContext)
        holder.recyclerView.adapter = PreOrderAdapter(details)

    }

    override fun getItemCount(): Int {
        return cartItemsModelList.ods.size
    }

    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var thumbnail: ImageView? = null
        var overflow: ImageView? = null
        internal var title: TextView = view.findViewById(R.id.order_number)
        internal var date: TextView = view.findViewById(R.id.ProductQuantityOrderPre)
        internal var price: TextView = view.findViewById(R.id.txt_total_price)
        //      internal var quantity: TextView = view.findViewById(R.id.qty)
        internal var status: TextView = view.findViewById(R.id.status)
        internal var total: TextView = view.findViewById(R.id.txt_total_price)
        internal var recyclerView: RecyclerView = view.findViewById(R.id.recPreOders)

    }
}

class PreOrderAdapter(val od: List<Detail>) : RecyclerView.Adapter<PreOrderAdapter.PreViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PreViewHolder {
        return PreViewHolder(LayoutInflater.from(p0.context)
                .inflate(R.layout.previous_order_item_list, p0, false))
    }

    override fun getItemCount(): Int {
        return od.size
    }

    override fun onBindViewHolder(p0: PreViewHolder, p1: Int) {
        val list = od[p1]
        p0.pName.text = list.ProductName
        p0.pQty.text = list.ProductQuantity.toString()
        p0.pSubT.text = "${list.ProductSubtotal} Rs"
    }

    class PreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var pName: TextView = view.findViewById(R.id.ProductNameOrderPre)
        internal var pQty: TextView = view.findViewById(R.id.ProductQuantityOrder)
        internal var pSubT: TextView = view.findViewById(R.id.ProductSubtotalOrderPre)
    }
}

