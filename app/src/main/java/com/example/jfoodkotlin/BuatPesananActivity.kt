package com.example.jfoodkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.jfoodkotlin.data.PojoCustomer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuatPesananActivity : AppCompatActivity() {
    private val retrofitInterface by lazy{
        RetrofitInterface.create()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_pesanan)
        val customerId: Int = intent.extras!!.getInt("customer")
        val foodId: Int = intent.extras!!.getInt("food")
        var foodHolder: Food? = null

        val foodNameView:       TextView    = findViewById(R.id.food_name)
        val foodCategoryView:   TextView    = findViewById(R.id.food_category)
        val foodPriceView:      TextView    = findViewById(R.id.food_price)
        val totalPriceView:     TextView    = findViewById(R.id.total_price)
        val cashView:           RadioButton = findViewById(R.id.cash)
        val cashlessView:       RadioButton = findViewById(R.id.cashless)
        val promoCodeView:      EditText    = findViewById(R.id.promo_code)
        val btnCountView:       Button      = findViewById(R.id.hitung)
        val btnOrderView:       Button      = findViewById(R.id.pesan)
        val radioView:          RadioGroup  = findViewById(R.id.radioGroup)

        val callFood = retrofitInterface.getFoodById(foodId)
        if (callFood != null) {
            val res = callFood.enqueue(object : Callback<Food> {
                override fun onFailure(call: Call<Food>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<Food>, response: Response<Food>) {
                    if (response.isSuccessful) {
                        foodHolder = Food(  response.body()!!.foodId,
                                                response.body()!!.foodName,
                                                response.body()!!.foodPrice,
                                                response.body()!!.foodCategory,
                                                response.body()!!.foodSeller)

                        foodNameView.text = foodHolder!!.foodName
                        foodCategoryView.text = foodHolder!!.foodCategory
                        foodPriceView.text = foodHolder!!.foodPrice.toString()
                    }
                }
            })
        }

        radioView.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.cash){
                btnCountView.visibility                             = View.INVISIBLE
                findViewById<TextView>(R.id.textCode).visibility    = View.INVISIBLE
                promoCodeView.visibility                            = View.INVISIBLE

                totalPriceView.text = foodHolder!!.foodPrice.toString()
                btnOrderView.visibility                             = View.VISIBLE
            }
            else{
                totalPriceView.text = "0"
                findViewById<TextView>(R.id.textCode).visibility    = View.VISIBLE
                promoCodeView.visibility                            = View.VISIBLE
                btnCountView.visibility                             = View.VISIBLE
                btnOrderView.visibility                             = View.INVISIBLE
            }
        }

        btnCountView.setOnClickListener {
            if (promoCodeView.text.toString() == ""){
                totalPriceView.text = foodPriceView.text
            }
            else if (promoCodeView.text.toString() != ""){
                val inCode: String  = promoCodeView.text.toString()
                var promoHolder: Promo?    = null
                val callPromo = retrofitInterface.getPromoByCode(promoCodeView.text.toString())
                if (callPromo != null) {
                    val res = callPromo.enqueue(object : Callback<Promo> {
                        override fun onFailure(call: Call<Promo>, t: Throwable) {
                            t.printStackTrace()
                        }

                        override fun onResponse(call: Call<Promo>, response: Response<Promo>) {
                            if(response.isSuccessful && response.body() != null){
                                promoHolder = Promo(response.body()!!.promoId,
                                                    response.body()!!.promoCode,
                                                    response.body()!!.promoDiscount,
                                                    response.body()!!.promoMinPrice,
                                                    response.body()!!.promoActive)

                                if (promoHolder!!.promoActive && promoHolder!!.promoMinPrice <= foodHolder!!.foodPrice){
                                    val total: Int = foodHolder!!.foodPrice - promoHolder!!.promoDiscount
                                    totalPriceView.text = total.toString()
                                }
                                else if (!promoHolder!!.promoActive){
                                    Toast.makeText(this@BuatPesananActivity, "Promo not active", Toast.LENGTH_LONG).show()
                                    promoCodeView.text.clear()
                                    totalPriceView.setText(foodHolder!!.foodPrice.toString())
                                }
                                else if (promoHolder!!.promoMinPrice > foodHolder!!.foodPrice){
                                    Toast.makeText(this@BuatPesananActivity, "Minimum order not reached", Toast.LENGTH_LONG).show()
                                    promoCodeView.text.clear()
                                    totalPriceView.setText(foodHolder!!.foodPrice.toString())
                                }
                                else if (!promoHolder!!.promoActive && promoHolder!!.promoMinPrice > foodHolder!!.foodPrice){
                                    Toast.makeText(this@BuatPesananActivity, "Promo not active and minimum order not reached", Toast.LENGTH_LONG).show()
                                    promoCodeView.text.clear()
                                    totalPriceView.setText(foodHolder!!.foodPrice.toString())
                                }
                            }
                            else if (response.body() == null){
                                Toast.makeText(this@BuatPesananActivity, "Promo not found", Toast.LENGTH_LONG).show()
                                promoCodeView.text.clear()
                            }
                        }
                    })
                }
            }
            btnCountView.visibility = View.INVISIBLE
            btnOrderView.visibility = View.VISIBLE
        }
        btnOrderView.setOnClickListener {
            if (cashView.isChecked){
                val callCashInvoice = retrofitInterface.addCashInvoice(foodId, customerId)
                val res = callCashInvoice.enqueue(object : Callback<CashInvoice>{
                    override fun onFailure(call: Call<CashInvoice>, t: Throwable) {
                        t.printStackTrace()
                    }

                    override fun onResponse(call: Call<CashInvoice>, response: Response<CashInvoice>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@BuatPesananActivity, "Invoice number :" + response.body()!!.invoiceId.toString() + " created", Toast.LENGTH_LONG).show()
                        }
                        else if (response.code() == 500){
                            Toast.makeText(this@BuatPesananActivity, "Ongoing Invoice Exist, Complete or Cancel Your Order First", Toast.LENGTH_LONG).show()
                        }
                    }
                })
            }
            else if (cashlessView.isChecked){
                if (promoCodeView.text.toString() == ""){
                    val callCashlessInvoiceNoPromo = retrofitInterface.addCashlessInvoice(foodId, customerId)
                    val res = callCashlessInvoiceNoPromo.enqueue(object : Callback<CashlessInvoice>{
                        override fun onFailure(call: Call<CashlessInvoice>, t: Throwable) {
                            t.printStackTrace()
                        }

                        override fun onResponse(call: Call<CashlessInvoice>, response: Response<CashlessInvoice>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@BuatPesananActivity, "Invoice number :" + response.body()!!.invoiceId.toString() + " created", Toast.LENGTH_LONG).show()
                            }
                            else if (response.code() == 500){
                                Toast.makeText(this@BuatPesananActivity, "Ongoing Invoice Exist, Complete or Cancel Your Order First", Toast.LENGTH_LONG).show()
                            }
                        }
                    })
                }
                else if (promoCodeView.text.toString() != ""){
                    val callCashlessInvoiceNoPromo = retrofitInterface.addCashlessInvoice(foodId, customerId, promoCodeView.text.toString())
                    val res = callCashlessInvoiceNoPromo.enqueue(object : Callback<CashlessInvoice>{
                        override fun onFailure(call: Call<CashlessInvoice>, t: Throwable) {
                            t.printStackTrace()
                        }

                        override fun onResponse(call: Call<CashlessInvoice>, response: Response<CashlessInvoice>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@BuatPesananActivity, "Invoice number :" + response.body()!!.invoiceId.toString() + " created", Toast.LENGTH_LONG).show()
                            }
                            else if (response.code() == 500){
                                Toast.makeText(this@BuatPesananActivity, "Ongoing Invoice Exist, Complete or Cancel Your Order First", Toast.LENGTH_LONG).show()
                            }
                        }
                    })
                }
            }
        }
    }
}