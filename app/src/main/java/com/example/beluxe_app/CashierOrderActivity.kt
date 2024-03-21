package com.example.beluxe_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

class CashierOrderActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    var listBarang = ArrayList<DataCashier>()
    private lateinit var cashierCheckoutBtn: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashier_order)

        recyclerView = findViewById(R.id.recyclerViewCashier)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        displayData()

        cashierCheckoutBtn = findViewById(R.id.checkoutCashierButton)

        cashierCheckoutBtn.setOnClickListener {
            // Filter listBarang sesuai dengan quantityToBuy yang tidak nol
            val selectedBarang = listBarang.filter { it.quantityToBuy > 0 }

            if (selectedBarang.isNotEmpty()) {
                // Membuat intent untuk pindah ke CheckoutActivity
                val intent = Intent(this, CheckoutActivity::class.java)
                intent.putParcelableArrayListExtra("selectedBarang", ArrayList(listBarang))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Pilih setidaknya satu barang", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun displayData() {
        val url: String = AppConfig().IP_SERVER + "/beluxe/viewProduct.php"
        val stringRequest = object : StringRequest(
            Method.GET, url, Response.Listener { response ->
            listBarang.clear()
            val jsonObj = JSONObject(response)
            Toast.makeText(this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()

            if (jsonObj.has("data")) {
                val jsonArray = jsonObj.getJSONArray("data")
                var barang: DataCashier

                if (jsonArray.length() > 0) {
                    Log.d("ProductActivity", "Jumlah item dalam jsonArray: ${jsonArray.length()}")
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        Log.d("ProductActivity", "JSON Object: $item")
                        barang = DataCashier()
                        barang.id = item.getString("product_id")
                        barang.imageView = AppConfig().IP_SERVER + "/beluxe/" + item.getString("product_image")
                        barang.productName = item.getString("product_name")
                        barang.productPrice = item.getString("product_price").toDoubleOrNull() ?: 0.0
                        listBarang.add(barang)
                    }
                    Log.d("ProductActivity", "Jumlah item dalam listBarang: ${listBarang.size}")
                } else {
                    Toast.makeText(this, "Data kosong", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Data kosong", Toast.LENGTH_SHORT).show()
            }

            recyclerView.adapter = CashierAdapter(this@CashierOrderActivity, listBarang)
        },
            Response.ErrorListener { error ->
                Log.e("ProductActivity", "Error Volley: ${error.message}")
                Toast.makeText(this, "Gagal Terhubung", Toast.LENGTH_SHORT).show()
            }
        ){}
        Volley.newRequestQueue(this).add(stringRequest)
    }
}