package com.monti.kristo.montikristo.rest.network

import com.monti.kristo.montikristo.model.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {

    companion object {
        private const val BASE_URL = "https://montikristo.com/api/"
        operator fun invoke(): ApiInterface {
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiInterface::class.java)
        }

    }

    @POST("product/getAllproducts")
    fun allProducts(): Call<StatusItemModel>

    @FormUrlEncoded
    @POST("auth/register")
    fun createUser(
            @Field("name") name: String,
            @Field("PhoneNum") p_num: String,
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("address") address: String,
            @Field("source") source: String,
            @Field("roleId") roleId: Int
    ): Call<StatusModel>

    @FormUrlEncoded
    @POST("auth/login")
    fun loginUser(
            @Field("email") email: String,
            @Field("password") password: String
    ): Call<StatusTokenModel>

    @POST("area/assignedAreas")
    fun assignedAreas(
    ): Call<AssignedAreaModel>

    @FormUrlEncoded
    @POST("validatePromo")
    fun validatePromo( @Field ("areaId") areaId: Int?,
                       @Field ("code") code: String?
    ): Call<ValidatePromoModel>

    @FormUrlEncoded
    @POST("auth/forgotPassword")
    fun forgotPassword(
            @Field("email") email: String
    ): Call<StatusModel>

    @FormUrlEncoded
    @POST("auth/verifypasswordCode")
    fun verifyCode(
            @Field("vcode") code: String

    ): Call<StatusModel>

    @FormUrlEncoded
    @POST("auth/changePassword")
    fun resetForgotPassword(

            @Field("newPassword") npass: String,
            @Field("email") vemail: String
    ): Call<StatusModel>

    @FormUrlEncoded
    @POST("user/updateProfile")
    fun updateProfile(

            @Field("id") id: Int,
            @Field("name") name: String,
            @Field("phone") phone: String,
            @Field("address") address: String,
            @Field("email") email: String

    ): Call<StatusTokenModel>


    /* @FormUrlEncoded
     @POST ("product/getCartItems")
     Call<StatusCartModel> getAllCartItems(

             @Field("cId") int custID

     );
 */
    @FormUrlEncoded
    @POST("order/CustomerOrders")
    fun getAllOrders(
            @Field("customerId") custID: Int
    ): Call<PreviousOrderModel>

    @FormUrlEncoded
    @POST("vendor/sendToken")
    fun pushNotifications(
            @Field("dToken") dToken: String,
            @Field("vendorId") vendorId: String
    ): Call<PushNotificatonsModel>

    @FormUrlEncoded
    @POST("product/Read")
    fun productDetails(

            @Field("productId") pID: Int

    ): Call<ProductDescriptionModel>

    @FormUrlEncoded
    @POST("order")
    fun placeOrder(

            @Field("customerId") id: Int,
            @Field("customerName") name: String,
            @Field("total") total: Double,
            @Field("address") address: String?,
            @Field("netAmount") netAmount: Double,
            @Field("ordertime") orderTime: Long?,
            @Field("deliveryDate") date: String?,
            @Field("products") products: String?,
            @Field("areaId") areaId: Int?,
            @Field("orderPhone") orderPhone: String?
//            @Field("promo") promo: String?

    ): Call<StatusModel>

    @FormUrlEncoded
    @POST("products/addToCart")
    fun addToCart(

            @Field("cId") id: Int,
            @Field("subTotal") subTotal: Int,
            @Field("time") dateTime: String,
            @Field("productName") pName: String,
            @Field("quantity") qty: Int

    ): Call<StatusModel>

    @FormUrlEncoded
    @POST("products/removeFromCart")
    fun removeFromCart(

            @Field("pName") pName: String,
            @Field("cId") cID: Int

    ): Call<StatusModel>


}