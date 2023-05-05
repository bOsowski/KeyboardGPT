package net.bosowski.stores

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import net.bosowski.utlis.AbstractObserverNotifier
import net.bosowski.models.PredictionSettingModel
import timber.log.Timber

object FirebasePredictionSettingStore : PredictionSettingStore, AbstractObserverNotifier() {

    private val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://chattergpt-default-rtdb.europe-west1.firebasedatabase.app/").reference

    private var predictionSettings: ArrayList<PredictionSettingModel> = ArrayList()

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                val currentData = dataSnapshot.children.mapNotNull {
                    it.getValue(PredictionSettingModel::class.java)
                } as ArrayList<PredictionSettingModel>
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

    override fun create(predictionSettingModel: PredictionSettingModel) {
        val key = database.child("prediction_settings").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        predictionSettingModel.id = key
        predictionSettings.add(predictionSettingModel)
        database.child("prediction_settings").child(Firebase.auth.uid!!).child(key)
            .setValue(predictionSettingModel)
    }

    override fun update(predictionSettingModel: PredictionSettingModel) {
        database.child("prediction_settings").child(predictionSettingModel.userId)
            .child(predictionSettingModel.id).setValue(predictionSettingModel)
    }

    override fun delete(predictionSettingModel: PredictionSettingModel) {
        predictionSettings.remove(predictionSettingModel)
        database.child("prediction_settings").child(predictionSettingModel.userId)
            .child(predictionSettingModel.id).removeValue()
    }

    override fun deleteAll() {
        predictionSettings.clear()
        database.child("prediction_settings").child(Firebase.auth.uid!!).removeValue()
    }

    override fun findAll(): ArrayList<PredictionSettingModel> {
        return predictionSettings
    }

    override fun find(id: String): PredictionSettingModel? {
        return predictionSettings.find { it.id == id }
    }

}