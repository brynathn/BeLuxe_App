package com.example.beluxe_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.android.volley.Request.Method
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

class DataOrder(
    var orderId: String,
    var date: String,
    var qty: String,
    var grandTotal: String
)

@Suppress("DEPRECATION")
class OrderActivity : AppCompatActivity() {
    private lateinit var orderHistoryTable: TableLayout
    private lateinit var orderApi: OrderApi
    private lateinit var cashierButton: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        displayUserInfo()
        orderHistoryTable = findViewById(R.id.orderHistoryTable)
        orderApi = OrderApi(this)  // Inisialisasi orderApi
        cashierButton = findViewById(R.id.cashierButton)

        cashierButton.setOnClickListener {
            val intent = Intent(this, CashierOrderActivity::class.java)
            startActivity(intent)
        }

        orderApi.getOrders(
            onSuccess = { response: JSONObject ->
                val orders = parseJsonObject(response)
                displayOrders(orders)
            },
            onError = { errorMessage: String ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(HomeActivity.getIntent(this))
                    true
                }
                R.id.buy -> {
                    startActivity(BuyingActivity.getIntent(this))
                    true
                }
                R.id.product -> {
                    startActivity(ProductActivity.getIntent(this))
                    true
                }
                else -> false
            }
        }
    }


    private fun parseJsonObject(jsonObject: JSONObject): List<DataOrder> {
        val orders = mutableListOf<DataOrder>()
        val dataArray = jsonObject.getJSONArray("data")

        for (i in 0 until dataArray.length()) {
            val orderObject = dataArray.getJSONObject(i)

            val orderId = orderObject.getString("order_id")
            val date = orderObject.getString("date")
            val qty = orderObject.getString("qty")
            val grandTotal = orderObject.getString("grand_total")

            val order = DataOrder(orderId, date, qty, grandTotal)
            orders.add(order)
        }

        return orders
    }


    private fun displayOrders(orders: List<DataOrder>) {
        // Bersihkan konten tabel sebelum menambahkan data baru
        orderHistoryTable.removeAllViews()

        // Tambahkan header tabel
        val headerRow = TableRow(this)
        val orderNumberHeader = getHeaderTextView("Order Number")
        val dateHeader = getHeaderTextView("Date")
        val qytHeader = getHeaderTextView("Quantity")
        val grandTotalHeader = getHeaderTextView("Grand Total")
        // Tambahkan header lainnya sesuai kebutuhan
        headerRow.addView(orderNumberHeader)
        headerRow.addView(dateHeader)
        headerRow.addView(qytHeader)
        headerRow.addView(grandTotalHeader)
        // Tambahkan header lainnya sesuai kebutuhan
        orderHistoryTable.addView(headerRow)

        // Tambahkan data ke tabel
        for (order in orders) {
            val dataRow = TableRow(this)
            val orderNumberText = getDataTextView(order.orderId)
            val date = getDataTextView(order.date)
            val qty = getDataTextView(order.qty)
            val grandTotal = getDataTextView(order.grandTotal)

            // Tambahkan pendengar klik ke setiap baris
            dataRow.setOnClickListener {
                // Implementasi tindakan yang ingin Anda lakukan saat baris diklik
                // Contoh: Intent ke DetailOrderActivity dengan mengirim data order
                val intent = Intent(this, UpdateOrderDetailsActivity::class.java)
                intent.putExtra("orderId", order.orderId)
                intent.putExtra("date", order.date)
                intent.putExtra("qty", order.qty)
                intent.putExtra("grandTotal", order.grandTotal)
                orderApi.updateOrder(order.orderId, mapOf(), {
                    // Handle berhasil
                    startActivity(intent)
                }, { errorMessage ->
                    // Handle error
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                })
            }

            // Tambahkan TextView lainnya sesuai kebutuhan
            dataRow.addView(orderNumberText)
            dataRow.addView(date)
            dataRow.addView(qty)
            dataRow.addView(grandTotal)

            // Tambahkan TextView lainnya sesuai kebutuhan
            orderHistoryTable.addView(dataRow)
        }
    }

    private fun getHeaderTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.layoutParams = getTableRowLayoutParams()
        textView.setBackgroundResource(R.drawable.table_cell_border)
        textView.gravity = Gravity.CENTER
        textView.setPadding(16, 16, 16, 16) // Ubah padding sesuai kebutuhan
        textView.setTextColor(ContextCompat.getColor(this, R.color.black))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f) // Ubah ukuran teks sesuai kebutuhan
        textView.setTypeface(null, Typeface.BOLD)
        return textView
    }

    private fun getDataTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.layoutParams = getTableRowLayoutParams()
        textView.setBackgroundResource(R.drawable.table_cell_border)
        textView.gravity = Gravity.CENTER
        textView.setPadding(16, 16, 16, 16) // Ubah padding sesuai kebutuhan
        textView.setTextColor(ContextCompat.getColor(this, R.color.black))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f) // Ubah ukuran teks sesuai kebutuhan
        return textView
    }

    private fun getTableRowLayoutParams(): TableRow.LayoutParams {
        return TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT // Ubah menjadi WRAP_CONTENT untuk tinggi
        )
    }

    private fun displayUserInfo() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Mengambil nama pengguna dari SharedPreferences
        val username = sharedPreferences.getString("full_name", "")

        // Menampilkan nama pengguna pada TextView
        val usernameTextView: TextView = findViewById(R.id.usernameText)
        usernameTextView.text = "$username"
    }


    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, OrderActivity::class.java)
        }
    }
}

class OrderApi(context: Context) {
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)
    private val url: String = AppConfig().IP_SERVER + "/beluxe/showTableOrder.php"

    fun getOrders(onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val request = JsonObjectRequest(
            Method.GET, url, null,
            { response ->
                onSuccess(response)
            },
            { error ->
                onError(error.message ?: "An error occurred")
            }
        )

        requestQueue.add(request)
    }

    fun updateOrder(orderId: String, newData: Map<String, String>, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val url: String = AppConfig().IP_SERVER + "/beluxe/getDataOrder.php"
        Log.d("OrderActivity", "Sending updateOrder request with orderId: $orderId")
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { _ ->
                onSuccess()
            },
            Response.ErrorListener { error ->
                onError(error.message ?: "An error occurred")
            }) {
            override fun getParams(): Map<String, String> {
                // Menyiapkan data yang akan dikirim
                val params = HashMap<String, String>()
                params["order_id"] = orderId
                params.putAll(newData)
                return params
            }
        }
        Log.d("OrderActivity", "Params for updateOrder request: $request")
        // Tambahkan request ke queue
        requestQueue.add(request)
    }
}

