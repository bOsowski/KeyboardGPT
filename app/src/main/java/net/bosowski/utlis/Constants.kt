package net.bosowski.utlis

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import net.bosowski.BuildConfig

object Constants {
    const val CHATTERGPT_SERVER_URL = BuildConfig.SERVER_URL

    val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://chattergpt-default-rtdb.europe-west1.firebasedatabase.app/").reference
}