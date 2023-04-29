package net.bosowski.keyboard.stats

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import net.bosowski.keyboard.CallbackTarget
import timber.log.Timber

object FirebaseStatsStore : StatsStore {

    private val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://chattergpt-default-rtdb.europe-west1.firebasedatabase.app/").reference

    val callbackTargets = HashSet<CallbackTarget>()

    fun registerCallbackTarget(callbackTarget: CallbackTarget) {
        callbackTargets.add(callbackTarget)
    }

    private var statsModels: HashMap<String, StatsModel> = HashMap()
//    private var statsModels: ArrayList<StatsModel> = ArrayList()

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            if (dataSnapshot.exists()) {
//                dataSnapshot.children.mapNotNullTo(statsModels) { it.getValue<StatsModel>() }
                dataSnapshot.children.mapNotNull { it.getValue(StatsModel::class.java) }.forEach {
                    statsModels[it.userId] = it
                }
                Timber.i("Firebase Success : StatsModel Added")
                callbackTargets.forEach { it.onDataChanged() }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Timber.i("loadPost:onCancelled ${databaseError.toException()}")
        }
    }

    init {
        database.child("stats").addValueEventListener(postListener)
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

    override fun find(userId: String): StatsModel? {
        return statsModels[userId]
    }
}