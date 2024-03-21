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
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class CreateProductActivity : AppCompatActivity() {
    private var resId     = 0
    private lateinit var imageView       : ImageView
    private lateinit var productName     : EditText
    private lateinit var productSupplier : EditText
    private lateinit var supplierPrice   : EditText
    private lateinit var productPrice    : EditText
    private lateinit var add             : Button
    private lateinit var bitmap          : Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_product)

        imageView           = findViewById(R.id.editProductImage)
        productName         = findViewById(R.id.editProductName)
        productSupplier     = findViewById(R.id.editProductSupplier)
        supplierPrice       = findViewById(R.id.editSupplierPrice)
        productPrice        = findViewById(R.id.editProductPrice)
        add                 = findViewById(R.id.addProductButton)

        imagePick()
        add.setOnClickListener {
            insertData()
        }
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

    private fun insertData() {
        if (resId == 1){
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val bytes = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)
            val url: String = AppConfig().IP_SERVER + "/beluxe/createProduct.php"
            val stringRequest = object : StringRequest(Method.POST,url,
                Response.Listener { response ->
                    val jsonObj = JSONObject(response)
                    Toast.makeText(this,jsonObj.getString("message"),Toast.LENGTH_SHORT).show()
                    imageView.setImageResource(R.drawable.icon_image)
                    productName.setText("")
                    productSupplier.setText("")
                    supplierPrice.setText("")
                    productPrice.setText("")
                    resId = 0
                    val intent = Intent(this@CreateProductActivity, ProductActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                Response.ErrorListener { _ ->
                    Toast.makeText(this,"Gagal Terhubung",Toast.LENGTH_SHORT).show()
                }
            ){
                override fun getParams(): HashMap<String,String>{
                    val params = HashMap<String,String>()
                    params["product_image"]     = base64Image
                    params["product_name"]      = productName.text.toString()
                    params["product_supplier"]  = productSupplier.text.toString()
                    params["supplier_price"]    = supplierPrice.text.toString()
                    params["product_price"]     = productPrice.text.toString()

                    return params
                }
            }
            Volley.newRequestQueue(this).add(stringRequest)
        }
        else{
            Toast.makeText(this,"Select the image first",Toast.LENGTH_SHORT).show()
        }
    }
}