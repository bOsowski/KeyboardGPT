package net.bosowski.stores

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import net.bosowski.utlis.AbstractObserverNotifier
import net.bosowski.models.StatsModel
import timber.log.Timber

class FirebaseStatsStore(private val userId: String) : StatsStore, AbstractObserverNotifier() {

    private val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://chattergpt-default-rtdb.europe-west1.firebasedatabase.app/").reference

    private var statsModel: StatsModel? = null

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            if (dataSnapshot.exists()) {
                statsModel = dataSnapshot.getValue<StatsModel>()!!
            }
            else{
                statsModel = StatsModel(userId = userId)
            }

            Timber.i("Firebase Success : StatsModel Added")
            observers.forEach { it.onDataChanged() }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Timber.i("loadPost:onCancelled ${databaseError.toException()}")
        }
    }

    init {
        database.child("stats").child(userId).addValueEventListener(postListener)
    }

    override fun set(statsModel: StatsModel) {
        val key = database.child("stats").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        statsModel.id = key
        database.child("stats").child(statsModel.userId).setValue(statsModel)
    }

    override fun find(): StatsModel? {
        return statsModel
    }

}