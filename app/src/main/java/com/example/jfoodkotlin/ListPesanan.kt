package com.example.jfoodkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ExpandableListView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListPesanan : AppCompatActivity() {
    private val retrofitInterface by lazy{
        RetrofitInterface.create()
    }
    val listDate: ArrayList<String> = ArrayList<String>()
    val listInvoice: ArrayList<Invoice> = ArrayList<Invoice>()
    val childMapping: HashMap<String, ArrayList<Invoice>> = HashMap<String, ArrayList<Invoice>>()
    var expListView: ExpandableListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_pesanan)
        expListView = findViewById<ExpandableListView>(R.id.lvExp)
        val customerId: Int = intent.extras!!.getInt("customer")
        refreshList(customerId)
    }

    fun refreshList(id: Int){
        val call = retrofitInterface.getCustomerInvoice(id)
        val res = call!!.enqueue(object : Callback<ArrayList<Invoice>> {
            override fun onFailure(call: Call<ArrayList<Invoice>>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<ArrayList<Invoice>>, responses: Response<ArrayList<Invoice>>) {
                if(responses.isSuccessful){
                    for (response in responses.body()!!){
//                        if (response.invoicePaymentType.toString() == "Cash"){
                            val invoiceHolder: Invoice = Invoice(response.invoiceId,
                                                                    response.invoicefood,
                                                                    response.invoiceDate,
                                                                    response.invoiceTotalPrice,
                                                                    response.invoiceCustomer,
                                                                    response.invoicePaymentType,
                                                                    response.invoiceStatus)
                            var checker: Boolean = true
                            for (date in listDate){
                                if (date == invoiceHolder.invoiceDate){
                                    checker = false
                                }
                            }
                            if (checker){
                                listDate.add(invoiceHolder.invoiceDate)
                            }
                            listInvoice.add(invoiceHolder)
//                        }
                    }
                    for (date in listDate) {
                        val temp: ArrayList<Invoice> = ArrayList<Invoice>()
                        for (invoice in listInvoice){
                            if (invoice.invoiceDate.equals(date)){
                                temp.add(invoice)
                            }
                        }
                        childMapping.put(date, temp)
                    }
                    val listAdapter: ListPesananAdapter = ListPesananAdapter(this@ListPesanan, listDate, childMapping)
                    expListView!!.setAdapter(listAdapter)
                }
            }
        })
    }
}