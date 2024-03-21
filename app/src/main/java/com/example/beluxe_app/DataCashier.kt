package com.example.beluxe_app
import android.os.Parcel
import android.os.Parcelable

class DataCashier() : Parcelable {
    // Properti yang sudah ada
    var id: String = ""

    var imageView: String = ""

    var productName: String = ""

    var productPrice: Double = 0.0

    var quantityToBuy: Int = 0

    // Implementasi Parcelable
    constructor(parcel: Parcel) : this() {
        id = parcel.readString() ?: ""
        imageView = parcel.readString() ?: ""
        productName = parcel.readString() ?: ""
        productPrice = parcel.readDouble()
        quantityToBuy = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(imageView)
        parcel.writeString(productName)
        parcel.writeDouble(productPrice)
        parcel.writeInt(quantityToBuy)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataCashier> {
        override fun createFromParcel(parcel: Parcel): DataCashier {
            return DataCashier(parcel)
        }

        override fun newArray(size: Int): Array<DataCashier?> {
            return arrayOfNulls(size)
        }
    }
}
