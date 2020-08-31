package com.example.jfoodkotlin.data

import com.google.gson.annotations.SerializedName

data class PojoCustomer(

	@field:SerializedName("customerEmail")
	val customerEmail: String? = null,

	@field:SerializedName("customerPassword")
	val customerPassword: String? = null,

	@field:SerializedName("customerId")
	val customerId: Int? = null,

	@field:SerializedName("customerJoindate")
	val customerJoindate: String? = null,

	@field:SerializedName("customerName")
	val customerName: String? = null
)
