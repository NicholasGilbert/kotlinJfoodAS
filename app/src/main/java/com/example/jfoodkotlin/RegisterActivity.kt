package com.example.jfoodkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.jfoodkotlin.data.PojoCustomer
import retrofit2.Call
import retrofit2.Callback

class RegisterActivity : AppCompatActivity() {
    private val retrofitInterface by lazy{
        RetrofitInterface.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val etName  = findViewById(R.id.name) as EditText
        val etEmail = findViewById(R.id.email) as EditText
        val etPass  = findViewById(R.id.password) as EditText
        val btnRegis = findViewById(R.id.register) as Button

        btnRegis.setOnClickListener {
            val inName:     String = etName.getText().toString()
            val inEmail:    String = etEmail.getText().toString()
            val inPass:     String = etPass.getText().toString()

            if(inName != "" && inEmail != "" && inPass != "") {
                beginRegis(inName, inEmail, inPass)
            }
            else{
                Toast.makeText(this@RegisterActivity, "Please Fill The Form", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun beginRegis(name: String, email: String, pass: String){
        val call = retrofitInterface.regis(name, email, pass)
        if (call != null) {
            val res = call.enqueue(object : Callback<PojoCustomer> {
                override fun onFailure(call: Call<PojoCustomer>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Register Failed", Toast.LENGTH_LONG).show()
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<PojoCustomer>, response: retrofit2.Response<PojoCustomer>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Register Successful", Toast.LENGTH_LONG).show()
                        val intent: Intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                    } else if (response.code() == 500){
                        Toast.makeText(this@RegisterActivity, "Email Already Used", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Register Failed", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }
}