package com.example.beluxe_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

@Suppress("DEPRECATION")
class ProductActivity : AppCompatActivity(), BarangAdapter.OnItemClickListener{
    private lateinit var recyclerView: RecyclerView
    var listBarang = ArrayList<Barang>()
    private lateinit var cashierButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        displayUserInfo()

        recyclerView = findViewById(R.id.recyclerViewProductList)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        cashierButton = findViewById(R.id.cashierButtonProduct)
        cashierButton.setOnClickListener {
            val intent = Intent(this, CashierOrderActivity::class.java)
            startActivity(intent)
        }
        displayData()

        val plusIconImage: ImageView = findViewById(R.id.plusIconImage)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(HomeActivity.getIntent(this))
                    true
                }
                R.id.order -> {
                    // Implementasi aksi saat item Order dipilih
                    startActivity(OrderActivity.getIntent(this))
                    true
                }
                R.id.buy -> {
                    // Implementasi aksi saat item Buy dipilih
                    startActivity(BuyingActivity.getIntent(this))
                    true
                }
                else -> false
            }
        }

        plusIconImage.setOnClickListener {
            // Menangani klik pada ImageView
            val intent = Intent(this, CreateProductActivity::class.java)
            startActivity(intent)
        }
    }
    companion object {
        // Fungsi utilitas untuk mendapatkan Intent untuk memulai ProductActivity
        fun getIntent(context: Context): Intent {
            return Intent(context, ProductActivity::class.java)
        }
    }

    override fun onItemClick(barang: Barang) {
        // Menangani klik pada item, buka UpdateProductActivity
        val intent = Intent(this, UpdateProductActivity::class.java)
        intent.putExtra("product_id", barang.id)
        startActivity(intent)
    }

    private fun displayData() {
        val url: String = AppConfig().IP_SERVER + "/beluxe/viewProduct.php"
        val stringRequest = object : StringRequest(Method.GET, url, Response.Listener { response ->
            listBarang.clear()
            val jsonObj = JSONObject(response)
            Toast.makeText(this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()

            if (jsonObj.has("data")) {
                val jsonArray = jsonObj.getJSONArray("data")
                var barang: Barang

                if (jsonArray.length() > 0) {
                    Log.d("ProductActivity", "Jumlah item dalam jsonArray: ${jsonArray.length()}")
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        Log.d("ProductActivity", "JSON Object: $item")
                        barang = Barang()
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

            recyclerView.adapter = BarangAdapter(this@ProductActivity, listBarang, this)
        },
            Response.ErrorListener { error ->
                Log.e("ProductActivity", "Error Volley: ${error.message}")
                Toast.makeText(this, "Gagal Terhubung", Toast.LENGTH_SHORT).show()
            }
        ){}
        Volley.newRequestQueue(this).add(stringRequest)
    }
    private fun displayUserInfo() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Mengambil nama pengguna dari SharedPreferences
        val username = sharedPreferences.getString("full_name", "")

        // Menampilkan nama pengguna pada TextView
        val usernameTextView: TextView = findViewById(R.id.usernameText)
        usernameTextView.text = "$username"
    }
}