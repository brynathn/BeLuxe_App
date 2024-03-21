package com.example.beluxe_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.io.IOException
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream

@Suppress("DEPRECATION")
class UpdateProductActivity : AppCompatActivity() {
    private var id: String? = null
    private var resId     = 0
    private lateinit var imageView       : ImageView
    private lateinit var productName     : EditText
    private lateinit var productSupplier : EditText
    private lateinit var supplierPrice   : EditText
    private lateinit var productPrice    : EditText
    private lateinit var save            : Button
    private lateinit var delete          : Button
    private var bitmap                   : Bitmap
    init {
        bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_product)

        id = intent.getStringExtra("product_id")

        imageView           = findViewById(R.id.editProductImage)
        productName         = findViewById(R.id.editProductName)
        productSupplier     = findViewById(R.id.editProductSupplier)
        supplierPrice       = findViewById(R.id.editSupplierPrice)
        productPrice        = findViewById(R.id.editProductPrice)
        save                = findViewById(R.id.saveProductButton)
        delete              = findViewById(R.id.deleteProductButton)

        clearEditTextFields()
        imagePick()
        fetchData()
        save.setOnClickListener {
            if (resId == 1) {
                // Jika resId == 1, berarti user ingin mengganti gambar
                saveDataWithImage()
            } else {
                // Jika resId != 1, berarti user tidak ingin mengganti gambar
                saveDataWithoutImage()
            }
        }
        delete.setOnClickListener {
            // Menghapus data
            deleteData()
        }
    }

    private fun clearEditTextFields() {
        productName.text.clear()
        productSupplier.text.clear()
        supplierPrice.text.clear()
        productPrice.text.clear()
    }

    private fun imagePick() {
        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data!!
                val uri = data.data
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    imageView.setImageBitmap(bitmap)
                    resId = 1
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            activityResultLauncher.launch(intent)
        }
    }

    private fun fetchData() {
        val url: String = AppConfig().IP_SERVER + "/beluxe/getDataProduct.php"

        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonObj = JSONObject(response)
                    Toast.makeText(this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()
                    Log.d("UpdateProductActivity", "Response: $jsonObj")
                    // Ambil data dari JSON
                    val dataObject = jsonObj.getJSONObject("data")
                    val productImage = dataObject.getString("product_image")
                    val productName = dataObject.getString("product_name")
                    val productSupplier = dataObject.getString("product_supplier")
                    val supplierPrice = dataObject.getString("supplier_price")
                    val productPrice = dataObject.getString("product_price")

                    Picasso.get().load(AppConfig().IP_SERVER + "/beluxe/" + productImage).into(imageView)

                    // Set data ke UI element (contoh: imageView, productName, dll.)
                    this.productName.setText(productName)
                    this.productSupplier.setText(productSupplier)
                    this.supplierPrice.setText(supplierPrice)
                    this.productPrice.setText(productPrice)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { _ ->
                Toast.makeText(this, "Gagal Terhubung", Toast.LENGTH_SHORT).show()
            }
        ){
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["product_id"] = id!!
                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun saveDataWithImage() {
        // Kode untuk menyimpan perubahan dengan gambar baru
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)
        bitmap.recycle()
        val url1: String = AppConfig().IP_SERVER + "/beluxe/update_ProductWithImage.php"
        val stringRequest = object : StringRequest(Method.POST, url1,
            Response.Listener { response ->
                val jsonObj = JSONObject(response)
                Toast.makeText(this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()
                resId = 0
            },
            Response.ErrorListener { _ ->
                Toast.makeText(this, "Gagal Terhubung", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): HashMap<String, String> {
                val params = HashMap<String, String>()
                params["product_id"] = id!!
                params["product_image"] = base64Image
                params["product_name"] = productName.text.toString()
                params["product_supplier"] = productSupplier.text.toString()
                params["supplier_price"] = supplierPrice.text.toString()
                params["product_price"] = productPrice.text.toString()
                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun saveDataWithoutImage() {
        // Kode untuk menyimpan perubahan tanpa mengganti gambar
        val url2: String = AppConfig().IP_SERVER + "/beluxe/updateProduct.php"
        val stringRequest = object : StringRequest(Method.POST, url2,
            Response.Listener { response ->
                val jsonObj = JSONObject(response)
                Toast.makeText(this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()
                resId = 0
                val intent = Intent(this@UpdateProductActivity, ProductActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                bitmap.recycle()
            },
            Response.ErrorListener { _ ->
                Toast.makeText(this, "Gagal Terhubung", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): HashMap<String, String> {
                val params = HashMap<String, String>()
                params["product_id"] = id!!
                params["product_name"] = productName.text.toString()
                params["product_supplier"] = productSupplier.text.toString()
                params["supplier_price"] = supplierPrice.text.toString()
                params["product_price"] = productPrice.text.toString()
                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun deleteData() {
        val url: String = AppConfig().IP_SERVER + "/beluxe/deleteProduct.php"
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonObj = JSONObject(response)
                    Toast.makeText(this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()
                    resId = 0
                    val intent = Intent(this@UpdateProductActivity, ProductActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { _ ->
                Toast.makeText(this, "Gagal Terhubung", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): HashMap<String, String> {
                val params = HashMap<String, String>()
                params["product_id"] = id!!
                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }


}