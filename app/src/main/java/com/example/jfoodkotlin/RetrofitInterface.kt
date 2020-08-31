package com.example.jfoodkotlin

import com.example.jfoodkotlin.data.PojoCustomer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitInterface {
    companion object{
        fun create(): RetrofitInterface{
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
//                .baseUrl("http://192.168.0.8:8080/")
                .baseUrl("http://192.168.43.99:8080/")
                .build()

            return retrofit.create(RetrofitInterface::class.java)
        }
    }

    @POST("customer/register")
    fun regis(@Query("name")        name    : String,
              @Query("email")       email   : String,
              @Query("password")    pass    : String): Call<PojoCustomer>?

    @POST("customer/login")
    fun login(@Query("email")       email   : String,
              @Query("password")    pass    : String): Call<PojoCustomer>?

    @GET("food/getfoods")
    fun getFoods(): Call<ArrayList<Food>>?

    @GET("food/{id}")
    fun getFoodById(@Path("id")     id      : Int): Call<Food>?

    @GET("promo/{code}")
    fun getPromoByCode(@Path("code")code    : String): Call<Promo>?

    @POST("invoice/cashorder")
    fun addCashInvoice( @Query("food")      food    : Int,
                        @Query("customer")  customer: Int,
                        @Query("delivery")  delivery: Int = 0): Call<CashInvoice>

    @POST("invoice/cashlessorder")
    fun addCashlessInvoice( @Query("food")      food    : Int,
                            @Query("customer")  customer: Int,
                            @Query("promo")     promo   : String = ""): Call<CashlessInvoice>

    @GET("invoice/customer/{id}")
    fun getCustomerInvoice(@Path("id")     id      : Int): Call<ArrayList<Invoice>>?
}