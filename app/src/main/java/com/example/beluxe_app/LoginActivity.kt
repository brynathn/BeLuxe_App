package com.example.beluxe_app

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.util.Log

class LoginActivity : AppCompatActivity() {
    private lateinit var editAccessId       : EditText
    private lateinit var editPassword       : EditText

    private lateinit var loginButton       : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editAccessId = findViewById(R.id.editAccessID)
        editPassword = findViewById(R.id.editLoginPassword)

        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            if (isNetworkConnected()) {
                login()
            } else {
                Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login() {
        Log.d("LoginActivity", "Mengirim permintaan login ke server...")

        val url: String = AppConfig().IP_SERVER + "/beluxe/login.php"
        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Log.d("LoginActivity", "Respon dari server: $response")

                val jsonObj = JSONObject(response)

                if (!jsonObj.getBoolean("error")) {
                    // Login berhasil
                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()

                    // Simpan informasi login ke SharedPreferences
                    saveLoginInfo(jsonObj.getString("full_name"))

                    // Pindah ke halaman HomeActivity
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Login gagal
                    Toast.makeText(this, jsonObj.getString("error_text"), Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.e("LoginActivity", "Gagal terhubung ke server: ${error.message}")
                Toast.makeText(this, "Gagal Terhubung", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): HashMap<String, String> {
                val params = HashMap<String, String>()
                params["access_id"] = editAccessId.text.toString()
                params["user_pass"] = editPassword.text.toString()
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun saveLoginInfo(username: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Simpan nama pengguna atau informasi login lainnya
        editor.putString("full_name", username)

        // Commit perubahan
        editor.apply()
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}