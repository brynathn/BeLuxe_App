package com.example.beluxe_app


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class BarangAdapter(var context: Context, private var listBarang: List<Barang>, private var itemClickListener: OnItemClickListener) : RecyclerView.Adapter<BarangAdapter.ImageViewHolder>() {
    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.card_product_design, null)
        return ImageViewHolder(view)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }


    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val barang = listBarang[position]
        Picasso.get().load(barang.imageView).into(holder.imageView)
        holder.productName.text = listBarang[position].productName
        holder.productPrice.text = listBarang[position].productPrice.toString()

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(barang)
        }
    }

    override fun getItemCount(): Int {
        return listBarang.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView       : ImageView
        var productName     : TextView
        var productPrice    : TextView


        init {
            imageView    = itemView.findViewById(R.id.productImage)
            productName  = itemView.findViewById(R.id.productNameText)
            productPrice = itemView.findViewById(R.id.productSoldText)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(barang: Barang)
    }
}