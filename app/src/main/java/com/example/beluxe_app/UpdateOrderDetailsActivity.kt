package com.example.beluxe_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class UpdateOrderDetailsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private var OrderDataList = ArrayList<OrderList>()
    private lateinit var recipientName: EditText
    private lateinit var subtotalText: TextView
    private lateinit var taxText: TextView
    private lateinit var grandTotalText: TextView
    private lateinit var changeText: TextView
    private lateinit var cashEditText: EditText
    private lateinit var btnFinish: Button
    private lateinit var dateTxt : TextView
    private lateinit var orderIdText : TextView
    private var subtotal = 0.0
    private var tax = 0.0
    private var grandtotal = 0.0
    private var change = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_order_details)

        recipientName = findViewById(R.id.editRecipientName)
        recyclerView = findViewById(R.id.recycleEditOrder)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        subtotalText = findViewById(R.id.subtotalText)
        taxText = findViewById(R.id.taxText)
        grandTotalText = findViewById(R.id.grandTotalText)
        changeText = findViewById(R.id.changeText)
        cashEditText = findViewById(R.id.editCash)
        btnFinish = findViewById(R.id.saveUpdateButton)
        dateTxt = findViewById(R.id.dateText)
        orderIdText = findViewById(R.id.orderNumberText)

        recipientName.setText("")
        cashEditText.setText("")

        var orderId = intent.getStringExtra("orderId")
        val date = intent.getStringExtra("date")
        val qty = intent.getStringExtra("qty")
        val grandTotal = intent.getStringExtra("grandTotal")

        // Display data if available
        displayUserInfo()
        Log.d("UpdateOrderDetailsActivity", "orderId: $orderId, date: $date, qty: $qty, grandTotal: $grandTotal")

        btnFinish.setOnClickListener {
            saveToDatabase()
            val intent = Intent(this@UpdateOrderDetailsActivity, OrderActivity::class.java)
            startActivity(intent)
        }

        cashEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                calculateChange()
            }
        })

        displayData()
    }

    private fun saveToDatabase() {
        val urlPenjualan: String = AppConfig().IP_SERVER + "/beluxe/insertPenjualan.php"
        val stringRequest = object : StringRequest(Method.POST, urlPenjualan,
            Response.Listener { response ->
                try {
                    val jsonObj = JSONObject(response)
                    if (!jsonObj.getBoolean("error")) {
                        val orderId = jsonObj.getInt("order_id")
                        for (checkoutData in OrderDataList) {
                            val productId = checkoutData.id
                            val qty = checkoutData.quantityToBuy

                            insertPenjualanDetail(orderId, productId, qty)
                        }
                        Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "JSON parsing error", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { _ ->
                Toast.makeText(this, "Failed to connect to server", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["reciepent_name"] = recipientName.text.toString()
                params["qty"] = OrderDataList.size.toString()
                params["grand_total"] = grandtotal.toString()
                params["cash"] = cashEditText.text.toString()
                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun insertPenjualanDetail(
        orderId: Int,
        productId: String,
        qty: Int,
    ) {
        val urlPenjualanDetail: String = AppConfig().IP_SERVER + "/beluxe/insertPenjualanDetail.php"
        val stringRequest = object : StringRequest(
            Method.POST, urlPenjualanDetail,
            Response.Listener { response ->
                Log.d("Response", response)
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean("error")) {
                        Toast.makeText(this@UpdateOrderDetailsActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@UpdateOrderDetailsActivity,
                        "JSON parsing error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    this@UpdateOrderDetailsActivity,
                    "Error connecting to server",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                // Send relevant parameters, adjust accordingly
                params["order_id"] = orderId.toString()
                params["product_id"] = productId
                params["qty"] = qty.toString()
                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun displayData() {
        val orderIds = intent.getStringExtra("orderId")
        Log.d("UpdateOrderDetailsActivity", "order_id: $orderIds")
        val url: String = AppConfig().IP_SERVER + "/beluxe/getDataOrder.php?order_id=$orderIds"
        val stringRequest = object : StringRequest(Method.GET, url, Response.Listener { response ->
            try {
                Log.d("UpdateOrderDetailsActivity", "Server Response: $response")
                val jsonObj = JSONObject(response)
                Toast.makeText(this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()

                if (jsonObj.has("data")) {
                    val jsonArray = jsonObj.getJSONArray("data")

                    if (jsonArray.length() > 0) {
                        OrderDataList.clear()
                        subtotal = 0.0

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val productId = jsonObject.getString("product_id")
                            val riciepentName = jsonObject.getString("reciepent_name")
                            val cash =  jsonObject.getString("cash")
                            val date = jsonObject.getString("date")
                            val productImage =  AppConfig().IP_SERVER + "/beluxe/" + jsonObject.getString("product_image")
                            val productName = jsonObject.getString("product_name")
                            val productPrice = jsonObject.getDouble("product_price")
                            val qty = jsonObject.getInt("qty")

                            if (qty != 0) {
                                val orderData = OrderList(
                                    productId,
                                    productImage,
                                    productName,
                                    productPrice,
                                    qty,
                                    productPrice * qty.toDouble()
                                )
                                OrderDataList.add(orderData)
                                subtotal += orderData.totalAmount
                            }
                            recipientName.setText(riciepentName)
                            cashEditText.setText(cash)
                            dateTxt.text = date.toString()
                            orderIdText.text = orderIds
                        }

                        tax = subtotal * 0.1
                        grandtotal = subtotal + tax

                        subtotalText.text = String.format("%.2f", subtotal)
                        taxText.text = String.format("%.2f", tax)
                        grandTotalText.text = String.format("%.2f", grandtotal)

                        // Set adapter for RecyclerView
                        val OrderAdapter =
                            UpdateOrderAdapter(this@UpdateOrderDetailsActivity, OrderDataList)
                        OrderAdapter.totalAmountListener =
                            object : UpdateOrderAdapter.TotalAmountListener {
                                override fun onTotalAmountChanged(subtotal: Double) {
                                    this@UpdateOrderDetailsActivity.subtotal = subtotal
                                    subtotalText.text = String.format("%.2f", subtotal)
                                    tax = subtotal * 0.1
                                    grandtotal = subtotal + tax
                                    taxText.text = String.format("%.2f", tax)
                                    grandTotalText.text = String.format("%.2f", grandtotal)
                                    calculateChange()
                                }
                            }
                        recyclerView.adapter = OrderAdapter
                        calculateChange()
                    } else {
                        Toast.makeText(this, "Data kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Data kosong", Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(this, "JSON parsing error", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener {
            Toast.makeText(this, "Error connecting to server", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                // Send any relevant parameters if needed
                return HashMap()
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun calculateChange() {
        val cashInput = cashEditText.text.toString().toDoubleOrNull() ?: 0.0
        change = cashInput - grandtotal
        changeText.text = String.format("%.2f", change)
    }

    private fun displayUserInfo() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("full_name", "")
        val usernameTextView: TextView = findViewById(R.id.cashierNameText)
        usernameTextView.text = "$username"
    }
}
