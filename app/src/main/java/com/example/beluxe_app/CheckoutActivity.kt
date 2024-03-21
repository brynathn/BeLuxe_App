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


// Import statements...

@Suppress("DEPRECATION")
class CheckoutActivity : AppCompatActivity(){
    private lateinit var recyclerView: RecyclerView
    private var checkoutDataList = ArrayList<CheckoutData>()
    private lateinit var recipientName: EditText
    private lateinit var subtotalText: TextView
    private lateinit var taxText: TextView
    private lateinit var grandTotalText: TextView
    private lateinit var changeText: TextView
    private lateinit var cashEditText: EditText
    private lateinit var btnFinish: Button
    private var subtotal = 0.0
    private var tax = 0.0
    private var grandtotal = 0.0
    private var change = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        recipientName = findViewById(R.id.editRecipientName)
        recyclerView = findViewById(R.id.recyclerViewPurchased)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        subtotalText = findViewById(R.id.subtotalText)
        taxText = findViewById(R.id.taxText)
        grandTotalText = findViewById(R.id.grandTotalText)
        changeText = findViewById(R.id.changeText)
        cashEditText = findViewById(R.id.editCash)
        btnFinish = findViewById(R.id.finishOrderPaymentButton)
        recipientName.setText("")
        cashEditText.setText("")

        // Display data if available
        displayUserInfo()
        displayData()

        btnFinish.setOnClickListener {
            saveToDatabase()
            val intent = Intent(this@CheckoutActivity, OrderActivity::class.java)
            startActivity(intent)
        }

        cashEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                calculateChange()
            }
        })
    }

    private fun saveToDatabase() {
        val urlPenjualan: String = AppConfig().IP_SERVER + "/beluxe/insertPenjualan.php"
        val stringRequest = object : StringRequest(Method.POST, urlPenjualan,
            Response.Listener { response ->
                try {
                    val jsonObj = JSONObject(response)
                    if (!jsonObj.getBoolean("error")) {
                        val orderId = jsonObj.getInt("order_id")
                        for (checkoutData in checkoutDataList) {
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
                params["qty"] = checkoutDataList.size.toString()
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
                        Toast.makeText(this@CheckoutActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@CheckoutActivity,
                        "JSON parsing error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    this@CheckoutActivity,
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
        // Retrieve data from the intent
        val previousIntent = intent
        val listBarangFromIntent = previousIntent.getParcelableArrayListExtra<DataCashier>("selectedBarang")

        if (!listBarangFromIntent.isNullOrEmpty()) {
            // Convert DataCashier to CheckoutData
            checkoutDataList.clear()
            subtotal = 0.0
            for (dataCashier in listBarangFromIntent) {
                if (dataCashier.quantityToBuy != 0) {
                    val checkoutData = CheckoutData(
                        dataCashier.id,
                        dataCashier.imageView,
                        dataCashier.productName,
                        dataCashier.productPrice,
                        dataCashier.quantityToBuy,
                        dataCashier.productPrice * dataCashier.quantityToBuy.toDouble()
                    )
                    checkoutDataList.add(checkoutData)
                    subtotal += checkoutData.totalAmount
                }
            }
            tax = subtotal * 0.1
            grandtotal = subtotal + tax

            subtotalText.text = "$subtotal"
            taxText.text = "$tax"
            grandTotalText.text = "$grandtotal"

            // Set adapter for RecyclerView
            val checkoutAdapter = CheckoutAdapter(this@CheckoutActivity, checkoutDataList)
            checkoutAdapter.totalAmountListener = object : CheckoutAdapter.TotalAmountListener {
                override fun onTotalAmountChanged(subtotal: Double) {
                    this@CheckoutActivity.subtotal = subtotal
                    // Format angka desimal sebelum ditampilkan pada TextView
                    subtotalText.text = String.format("%.2f", subtotal)
                    tax = subtotal * 0.1
                    grandtotal = subtotal + tax
                    taxText.text = String.format("%.2f", tax)
                    grandTotalText.text = String.format("%.2f", grandtotal)
                    calculateChange()
                }
            }

            recyclerView.adapter = checkoutAdapter


            calculateChange()
        } else {
            Toast.makeText(this, "Data from intent is null or empty", Toast.LENGTH_SHORT).show()
        }
    }
    private fun calculateChange() {
        val cashInput = cashEditText.text.toString().toDoubleOrNull() ?: 0.0
        change = cashInput - grandtotal
        changeText.text = "$change"
    }
    private fun displayUserInfo() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Mengambil nama pengguna dari SharedPreferences
        val username = sharedPreferences.getString("full_name", "")

        // Menampilkan nama pengguna pada TextView
        val usernameTextView: TextView = findViewById(R.id.cashierNameText)
        usernameTextView.text = "$username"
    }
}

