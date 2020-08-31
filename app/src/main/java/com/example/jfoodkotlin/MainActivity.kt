package com.example.jfoodkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val retrofitInterface by lazy{
        RetrofitInterface.create()
    }
    val listSeller: ArrayList<Seller> = ArrayList<Seller>()
    val listFood: ArrayList<Food> = ArrayList<Food>()
    val childMapping: HashMap<Seller, ArrayList<Food>> = HashMap<Seller, ArrayList<Food>>()
    var expListView: ExpandableListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        expListView = findViewById<ExpandableListView>(R.id.lvExp) as ExpandableListView
        val btnOrders = findViewById<Button>(R.id.pesanan) as Button
        val customerId: Int = intent.extras!!.getInt("customer")
        refreshList()
        expListView!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val selected: Food = childMapping.get(listSeller.get(groupPosition))!!.get(childPosition)
            val intent: Intent = Intent(this@MainActivity, BuatPesananActivity::class.java)
            intent.putExtra("customer", customerId)
            intent.putExtra("food",selected.foodId)
            startActivity(intent)
            false
        }
        btnOrders.setOnClickListener {
            val intent: Intent = Intent(this@MainActivity, ListPesanan::class.java)
            intent.putExtra("customer", customerId)
            startActivity(intent)
        }
    }

    fun refreshList(){
        val call = retrofitInterface.getFoods()
        if (call != null) {
            val res = call.enqueue(object : Callback<ArrayList<Food>> {
                override fun onFailure(call: Call<ArrayList<Food>>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<ArrayList<Food>>, responses: Response<ArrayList<Food>>) {
                    if (responses.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Please enter your order", Toast.LENGTH_LONG).show()
                        for (response in responses.body()!!){
                            val foodHolder: Food = Food(response.foodId,
                                                        response.foodName,
                                                        response.foodPrice,
                                                        response.foodCategory,
                                                        response.foodSeller)
                            val locationHolder: Location = Location(response.foodSeller.sellerLocation.locationProvince,
                                                                    response.foodSeller.sellerLocation.locationDescription,
                                                                    response.foodSeller.sellerLocation.locationCity)
                            val sellerHolder: Seller = Seller(  response.foodSeller.sellerId,
                                                                response.foodSeller.sellerName,
                                                                response.foodSeller.sellerEmail,
                                                                response.foodSeller.sellerPhoneNumber,
                                                                locationHolder)

                            var checker: Boolean = true
                            for (seller in listSeller){
                                if (sellerHolder.sellerName == seller.sellerName){
                                    checker = false
                                }
                            }
                            if (checker){
                                listSeller.add(sellerHolder)
                            }
                            listFood.add(foodHolder)
                        }
                        for (seller in listSeller) {
                            val temp: ArrayList<Food> = ArrayList<Food>()
                            for (food in listFood){
                                if (food.foodSeller.sellerName.equals(seller.sellerName)){
                                    temp.add(food)
                                }
                            }
                            childMapping.put(seller, temp)
                        }
                        val listAdapter: MainListAdapter = MainListAdapter(this@MainActivity, listSeller, childMapping)
                        expListView!!.setAdapter(listAdapter)
                    }
                }
            })
        }
    }
}