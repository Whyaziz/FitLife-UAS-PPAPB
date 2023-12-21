package com.android.fitlife.data

data class DataUser(
    var uid: String = "",
    var nama: String = "",
    var program: String = "",
    var usia: Int = 0,
    var height: Float = 0.0F,
    var weight: Float = 0.0F,
    var targetWeight: Float = 0.0F,
    var kaloriHarian: Int = 0,
)
