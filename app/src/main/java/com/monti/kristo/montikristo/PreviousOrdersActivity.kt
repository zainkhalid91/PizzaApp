package com.monti.kristo.montikristo

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.google.gson.Gson
import com.monti.kristo.montikristo.adapters.PreviousOrderAdapter
import com.monti.kristo.montikristo.model.CartItemsModel
import com.monti.kristo.montikristo.model.LoginUserModel
import com.monti.kristo.montikristo.model.PreviousOrderModel
import com.monti.kristo.montikristo.rest.network.ApiInterface
import com.monti.kristo.montikristo.utils.JWTUtils
import com.monti.kristo.montikristo.utils.SessionManager
import io.fabric.sdk.android.Fabric
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PreviousOrdersActivity : AppCompatActivity() {

    internal lateinit var btn_back: ImageView
    internal var price: Int = 0
    internal var fee: Int = 0
    internal var cid: Int = 0
    internal lateinit var progressDialog: ProgressBar
    private var recyclerView: RecyclerView? = null
    private var adapter: PreviousOrderAdapter? = null
    private var cartItemsModelList: List<CartItemsModel>? = null
    private var cartItemsModels: ArrayList<CartItemsModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_orders)

        //Crashlytics
        Fabric.with(this, Crashlytics())

        recyclerView = findViewById<View>(R.id.recycler_view_prev_order) as RecyclerView
        recyclerView!!.isNestedScrollingEnabled = false
        btn_back = findViewById<View>(R.id.btn_back_prevOrder) as ImageView
        progressDialog = findViewById(R.id.progressBar)

        val extras = intent.extras
        if (extras != null) {

            /* price = extras.getInt("getPrice");
            fee = extras.getInt("getDFee");*/
            cid = extras.getInt("cid")

        }

        btn_back.setOnClickListener {
            val intent = Intent(applicationContext, MyCartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        cartItemsModelList = ArrayList()
//        adapter = PreviousOrderAdapter(this, cartItemsModelList)

        val mLayoutManager = GridLayoutManager(this, 1)
        recyclerView!!.layoutManager = mLayoutManager
        //   recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView!!.itemAnimator = DefaultItemAnimator()

        prepareList()
    }

    /**
     * Adding few items for testing
     */
    private fun prepareList() {

        progressDialog.visibility = View.VISIBLE
        val sessionManager = SessionManager(applicationContext)
        var decoded: String? = null
        try {
            decoded = JWTUtils.decoded(sessionManager.keyUserId)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val gson = Gson()
        val loginUserModel = gson.fromJson<LoginUserModel>(decoded, LoginUserModel::class.java)
        val cID = loginUserModel.id!!
        ApiInterface.invoke().getAllOrders(cID).enqueue(object : Callback<PreviousOrderModel> {
            override fun onResponse(call: Call<PreviousOrderModel>, response: Response<PreviousOrderModel>) {
                if (response.isSuccessful) {
                    val res = response.body()
                    if (res!!.status) {
                        recyclerView!!.adapter = PreviousOrderAdapter(applicationContext, res)
                    }
                }
                progressDialog.visibility = View.GONE
            }

            override fun onFailure(call: Call<PreviousOrderModel>, t: Throwable) {
                Toast.makeText(applicationContext, "Error " + t.message, Toast.LENGTH_SHORT).show()
                progressDialog.visibility = View.GONE
            }
        })


    }

    /**
     * Converting dp to pixel
     */
    private fun dpToPx(dp: Int): Int {
        val r = resources
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics))
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    inner class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }
}
