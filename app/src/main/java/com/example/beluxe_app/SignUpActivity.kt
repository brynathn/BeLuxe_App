package com.example.beluxe_app

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



class SignUpActivity : AppCompatActivity() {
    private lateinit var action             : String
    private lateinit var editFullName       : EditText
    private lateinit var editEmail          : EditText
    private lateinit var editPasswordSignup : EditText
    private lateinit var signupButton       : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        editFullName        = findViewById(R.id.editFullName)
        editEmail           = findViewById(R.id.editEmail)
        editPasswordSignup  = findViewById(R.id.editPasswordSignup)

        signupButton        = findViewById(R.id.signupButton)

        signupButton.setOnClickListener {
            if (isNetworkConnected()) {
                action = "add"
                signup()
            } else {
                Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun signup() {
        Log.d("SignUpActivity", "Mengirim permintaan signup ke server...")

        val url: String = AppConfig().IP_SERVER + "/beluxe/signup.php"
        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Log.d("SignUpActivity", "Respon dari server: $response")

                val jsonObj = JSONObject(response)
                Toast.makeText(this, jsonObj.getString("error_text"), Toast.LENGTH_SHORT).show()
                val accessId: String = jsonObj.getString("access_id")
                val intent = Intent(this, IDActivity::class.java)
                intent.putExtra("ACCESS_ID", accessId)
                startActivity(intent)
                finish()
            },
            Response.ErrorListener { error ->
                Log.e("SignUpActivity", "Gagal terhubung ke server: ${error.message}")
                Toast.makeText(this, "Gagal Terhubung", Toast.LENGTH_SHORT).show()
            }
        ){
            override fun getParams(): HashMap<String,String>{
                val params = HashMap<String,String>()
                params["full_name"]      = editFullName.text.toString()
                params["email"]          = editEmail.text.toString()
                params["user_pass"]      = editPasswordSignup.text.toString()
                params["action"]         = action
                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }
    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}