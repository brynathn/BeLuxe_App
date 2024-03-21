package com.example.beluxe_app

import android.os.Parcel
import android.os.Parcelable

class OrderList(
    var id: String = "",
    var imageView: String = "",
    var productName: String = "",
    var productPrice: Double = 0.0,
    var quantityToBuy: Int = 0,
    var totalAmount: Double = 0.0  // Add this property
) : Parcelable {
    // Existing code...

    // Parcelable implementation
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(imageView)
        parcel.writeString(productName)
        parcel.writeDouble(productPrice)
        parcel.writeInt(quantityToBuy)
        parcel.writeDouble(totalAmount)  // Write totalAmount to Parcel
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CheckoutData> {
        override fun createFromParcel(parcel: Parcel): CheckoutData {
            return CheckoutData(parcel)
        }

        override fun newArray(size: Int): Array<CheckoutData?> {
            return arrayOfNulls(size)
        }
    }
}
