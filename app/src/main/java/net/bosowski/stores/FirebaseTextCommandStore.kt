package net.bosowski.stores

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import net.bosowski.utlis.AbstractObserverNotifier
import net.bosowski.models.TextCommandConfigModel
import timber.log.Timber

object FirebaseTextCommandStore : TextCommandStore, AbstractObserverNotifier() {

    private val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://chattergpt-default-rtdb.europe-west1.firebasedatabase.app/").reference

    private var predictionSettings: ArrayList<TextCommandConfigModel> = ArrayList()

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                val currentData = dataSnapshot.children.mapNotNull {
                    it.getValue(TextCommandConfigModel::class.java)
                } as ArrayList<TextCommandConfigModel>
                predictionSettings.clear()
                predictionSettings.addAll(currentData)
                Timber.i("Firebase Success : StatsModel Added")
                observers.forEach { it.onDataChanged() }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Timber.i("loadPost:onCancelled ${databaseError.toException()}")
        }
    }

    init {
        Firebase.auth.addAuthStateListener {
            if (it.currentUser != null) {
                database.child("prediction_settings").child(it.currentUser!!.uid).addValueEventListener(postListener)
            }
        }
    }

    override fun create(textCommandConfigModel: TextCommandConfigModel) {
        val key = database.child("prediction_settings").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        textCommandConfigModel.id = key
        predictionSettings.add(textCommandConfigModel)
        database.child("prediction_settings").child(Firebase.auth.uid!!).child(key)
            .setValue(textCommandConfigModel)
    }

    override fun update(textCommandConfigModel: TextCommandConfigModel) {
        database.child("prediction_settings").child(textCommandConfigModel.userId)
            .child(textCommandConfigModel.id).setValue(textCommandConfigModel)
    }

    override fun delete(textCommandConfigModel: TextCommandConfigModel) {
        predictionSettings.remove(textCommandConfigModel)
        database.child("prediction_settings").child(textCommandConfigModel.userId)
            .child(textCommandConfigModel.id).removeValue()
    }

    override fun deleteAll() {
        predictionSettings.clear()
        database.child("prediction_settings").child(Firebase.auth.uid!!).removeValue()
    }

    override fun findAll(): ArrayList<TextCommandConfigModel> {
        return predictionSettings
    }

    override fun find(id: String): TextCommandConfigModel? {
        return predictionSettings.find { it.id == id }
    }

}