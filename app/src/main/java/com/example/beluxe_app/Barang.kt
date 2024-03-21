package com.example.beluxe_app

class Barang {
    var id: String = ""
        get() = field
        set(value) { field = value }

    var imageView: String = ""
        get() = field
        set(value) { field = value }

    var productName: String = ""
        get() = field
        set(value) { field = value }

    var productPrice: Double = 0.0
        get() = field
        set(value) { field = value }
}