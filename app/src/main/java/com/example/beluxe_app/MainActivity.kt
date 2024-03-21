package com.example.beluxe_app

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonGoSignup : Button = findViewById(R.id.goSignUpButton)
        val haveAccountText: TextView = findViewById(R.id.haveAccountButton)

        val spannableString = SpannableString(getString(R.string.have_account_btn))

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                // Tindakan yang diambil saat teks diklik
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        spannableString.setSpan(clickableSpan, spannableString.indexOf("Login"), spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        haveAccountText.text = spannableString
        haveAccountText.movementMethod = LinkMovementMethod.getInstance()

        buttonGoSignup.setOnClickListener {
            val intent = Intent(this@MainActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    fun onLoginClick(view: View) {
        // Tindakan yang diambil saat teks diklik
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}