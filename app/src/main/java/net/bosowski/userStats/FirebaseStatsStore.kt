package net.bosowski.userStats

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class FirebaseStatsStore(private val statsViewModel: StatsViewModel) : StatsStore {

    private val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://chattergpt-default-rtdb.europe-west1.firebasedatabase.app/").reference

    private var statsModel: StatsModel? = null

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            statsModel = dataSnapshot.getValue<StatsModel>()

            Timber.i("Firebase Success : StatsModel Loaded")
            statsViewModel.setStatsModel(statsModel)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Timber.i("loadPost:onCancelled ${databaseError.toException()}")
        }
    }

    init {
        Firebase.auth.addAuthStateListener {
            if (it.currentUser != null) {
                database.child("stats").child(it.currentUser!!.uid).addValueEventListener(postListener)
            }
        }
    }

    override fun set(statsModel: StatsModel) {
        val key = database.child("stats").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        statsModel.id = key
        database.child("stats").child(Firebase.auth.currentUser!!.uid).setValue(statsModel)
    }

    override fun find(): StatsModel? {
        return statsModel
    }
}