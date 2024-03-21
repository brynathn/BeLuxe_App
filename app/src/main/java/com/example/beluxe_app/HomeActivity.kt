package com.example.beluxe_app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.bottomnavigation.BottomNavigationView


@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity() {
    private lateinit var cashierButton: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        displayUserInfo()
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        cashierButton = findViewById(R.id.cashierButton)

        cashierButton.setOnClickListener {
            val intent = Intent(this, CashierOrderActivity::class.java)
            startActivity(intent)
        }
            bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
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
                R.id.product -> {
                    // Pindah ke ProductActivity saat item Product dipilih
                    startActivity(ProductActivity.getIntent(this))
                    true
                }
                else -> false
            }
        }
    }
    companion object {
        // Fungsi utilitas untuk mendapatkan Intent untuk memulai ProductActivity
        fun getIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
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

}