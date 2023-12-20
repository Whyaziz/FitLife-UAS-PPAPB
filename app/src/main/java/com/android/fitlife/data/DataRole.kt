package com.android.fitlife.data

import com.google.firebase.firestore.Exclude

data class DataRole(
    var uid: String = "",
    var nama: String = "",
    var role: String = "user",
)
