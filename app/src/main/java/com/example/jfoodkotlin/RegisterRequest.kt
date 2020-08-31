package com.example.jfoodkotlin

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class RegisterRequest(
    name: String,
    email: String,
    pass: String,
    listener: Response.Listener<String>) : StringRequest( Method.POST, "http://192.168.0.8/customer/register", listener, null) {
    val URL: String = "http://192.168.0.8/customer/register"
    var paramReq: Map<String, String>

        init {
         paramReq = HashMap()
        (paramReq as HashMap<String, String>).put("name",name)
        (paramReq as HashMap<String, String>).put("email",email)
        (paramReq as HashMap<String, String>).put("password",pass)
    }

    @Override
    override fun getParams() : Map<String, String>{
        return paramReq
    }
}