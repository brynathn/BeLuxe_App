package com.example.beluxe_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class IDActivity : AppCompatActivity() {
    private lateinit var continueBtn       : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idactivity)

        val accessId: String? = intent.getStringExtra("ACCESS_ID")
        continueBtn       = findViewById(R.id.continueButton)


        if (accessId != null) {
            val textViewAccessId: TextView = findViewById(R.id.userIDText)
            textViewAccessId.text = "Access ID: $accessId"
        } else {
            Toast.makeText(this, "Kesalahan: Access ID tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        continueBtn.setOnClickListener {
            val intent = Intent(this@IDActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}