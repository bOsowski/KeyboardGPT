package net.bosowski.predictionSettings

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import net.bosowski.utlis.Constants
import timber.log.Timber

class FirebasePredictionSettingStore(private val predictionSettingsViewModel: PredictionSettingsViewModel) :
    PredictionSettingStore {

    private val database: DatabaseReference = Constants.database

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                val currentData = dataSnapshot.children.mapNotNull {
                    it.getValue(PredictionSettingModel::class.java)
                } as ArrayList<PredictionSettingModel>
                predictionSettingsViewModel.setPredictionSettings(currentData)
                Timber.i("Firebase Success : StatsModel Added")
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Timber.i("loadPost:onCancelled ${databaseError.toException()}")
        }
    }

    init {
        Firebase.auth.addAuthStateListener {
            if (it.currentUser != null) {
                database.child("prediction_settings").child(it.currentUser!!.uid)
                    .addValueEventListener(postListener)
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
        database.child("prediction_settings").child(Firebase.auth.uid!!).child(key)
            .setValue(predictionSettingModel)
    }

    override fun update(predictionSettingModel: PredictionSettingModel) {
        database.child("prediction_settings").child(predictionSettingModel.userId)
            .child(predictionSettingModel.id).setValue(predictionSettingModel)
    }

    override fun delete(predictionSettingModel: PredictionSettingModel) {
        database.child("prediction_settings").child(predictionSettingModel.userId)
            .child(predictionSettingModel.id).removeValue()
    }

    override fun deleteAll() {
        database.child("prediction_settings").child(Firebase.auth.uid!!).removeValue()
    }

    override fun update(predictionSettingModels: ArrayList<PredictionSettingModel>) {
        database.child("prediction_settings").child(Firebase.auth.uid!!)
            .setValue(predictionSettingModels)
    }

    override fun findAll(): ArrayList<PredictionSettingModel> {
        return predictionSettingsViewModel.predictionSettings.value!!
    }

    override fun find(id: String): PredictionSettingModel? {
        return predictionSettingsViewModel.predictionSettings.value?.find { it.id == id }
    }

}