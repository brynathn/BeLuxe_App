package com.example.beluxe_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Context
import android.content.Intent
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

@Suppress("DEPRECATION")
class BuyingActivity : AppCompatActivity() {
    private lateinit var cashierButton: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buying)

        displayUserInfo()

        cashierButton = findViewById(R.id.cashierButtonBuy)

        cashierButton.setOnClickListener {
            val intent = Intent(this, CashierOrderActivity::class.java)
            startActivity(intent)
        }
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Implementasi aksi saat item Home dipilih
                    startActivity(HomeActivity.getIntent(this))
                    true
                }
                R.id.order -> {
                    // Implementasi aksi saat item Order dipilih
                    startActivity(OrderActivity.getIntent(this))
                    true
                }
                R.id.product -> {
                    // Pindah ke ProductActivity saat item Product dipilih
                    startActivity(ProductActivity.getIntent(this))
                    true
                }
                else -> false
            }
        }
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
        // Fungsi utilitas untuk mendapatkan Intent untuk memulai ProductActivity
        fun getIntent(context: Context): Intent {
            return Intent(context, BuyingActivity::class.java)
        }
    }
}