package com.example.beluxe_app

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CashierAdapter(var context: Context, private var listBarang: List<DataCashier>) : RecyclerView.Adapter<CashierAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.card_cashier_design, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val barang = listBarang[position]
        Picasso.get().load(barang.imageView).into(holder.imageView)
        holder.productName.text = listBarang[position].productName
        holder.productPrice.text = listBarang[position].productPrice.toString()

        holder.editTextQuantity.setText(barang.quantityToBuy.toString())
        holder.editTextQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update quantityToBuy saat nilai berubah
                barang.quantityToBuy = s.toString().toIntOrNull() ?: 0
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun getItemCount(): Int {
        return listBarang.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.cashierProductImage)
        var productName: TextView = itemView.findViewById(R.id.cashierProductNameText)
        var productPrice: TextView = itemView.findViewById(R.id.cashierProductPriceText)
        var editTextQuantity: EditText = itemView.findViewById(R.id.editCashierProductQty)
    }
}
