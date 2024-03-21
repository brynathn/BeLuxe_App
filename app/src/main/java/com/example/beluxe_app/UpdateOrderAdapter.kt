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

class UpdateOrderAdapter(
    private val context: Context,
    private val dataOrder: List<OrderList>,
) : RecyclerView.Adapter<UpdateOrderAdapter.ImageViewHolder>() {

    interface TotalAmountListener {
        fun onTotalAmountChanged(subtotal: Double)
    }


    var totalAmountListener: TotalAmountListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.card_edit_purchased_design, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val data = dataOrder[position]

        Picasso.get().load(data.imageView).into(holder.imageView)

        holder.productName.text = data.productName
        holder.productPrice.text = data.productPrice.toString()

        holder.editTextQuantity.removeTextChangedListener(holder.textWatcher)
        holder.editTextQuantity.setText(data.quantityToBuy.toString())
        holder.editTextQuantity.addTextChangedListener(holder.textWatcher)

        holder.totalAmount.text = data.totalAmount.toString()
    }

    override fun getItemCount(): Int {
        return dataOrder.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.productImage)
        var productName: TextView = itemView.findViewById(R.id.purchasedProductNameText)
        var productPrice: TextView = itemView.findViewById(R.id.productPriceText)
        var editTextQuantity: EditText = itemView.findViewById(R.id.quantityCounter)
        var totalAmount: TextView = itemView.findViewById(R.id.productTotalPriceText)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val adapterPosition = absoluteAdapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val quantity = s.toString().toIntOrNull() ?: 0
                    dataOrder[adapterPosition].quantityToBuy = quantity
                    updateTotalAmount(quantity)

                    dataOrder[adapterPosition].totalAmount = dataOrder[adapterPosition].productPrice * quantity.toDouble()
                    updateTotal()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        init {
            editTextQuantity.addTextChangedListener(textWatcher)
        }

        private fun updateTotalAmount(quantity: Int) {
            val total = dataOrder[absoluteAdapterPosition].productPrice * quantity
            totalAmount.text = total.toString()
        }

        private fun updateTotal() {
            var newSubtotal = 0.0
            for (data in dataOrder) {
                newSubtotal += data.totalAmount
            }
            totalAmountListener?.onTotalAmountChanged(newSubtotal)
        }
    }
}
