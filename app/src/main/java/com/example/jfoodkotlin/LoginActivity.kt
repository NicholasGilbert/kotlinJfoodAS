package com.example.jfoodkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.jfoodkotlin.data.PojoCustomer
import retrofit2.Call
import retrofit2.Callback

class LoginActivity : AppCompatActivity() {
    private val retrofitInterface by lazy{
        RetrofitInterface.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val etEmail = findViewById(R.id.email) as EditText
        val etPassword = findViewById(R.id.password) as EditText
        val btnLogin = findViewById(R.id.login) as Button
        val tvRegister = findViewById(R.id.register) as TextView

        btnLogin.setOnClickListener{
            val email: String = etEmail.getText().toString()
            val pass: String = etPassword.getText().toString()

            login(email,pass)
        }
5
        tvRegister.setOnClickListener{
            val intent: Intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    fun login(email: String, pass: String){
        val call = retrofitInterface.login(email, pass)
        if (call != null) {
            val res = call.enqueue(object : Callback<PojoCustomer>{
                override fun onFailure(call: Call<PojoCustomer>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_LONG).show()
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<PojoCustomer>, response: retrofit2.Response<PojoCustomer>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "Login Successful, Welcome " + response.body()!!.customerName, Toast.LENGTH_LONG).show()
                        val intent: Intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("customer", response.body()!!.customerId)
                        startActivity(intent)
                    }else {
                        Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }
}