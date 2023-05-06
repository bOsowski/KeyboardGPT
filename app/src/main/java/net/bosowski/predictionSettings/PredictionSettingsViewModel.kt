package net.bosowski.predictionSettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PredictionSettingsViewModel : ViewModel() {

    private val db = FirebasePredictionSettingStore(this)
    private val _predictionSettings = MutableLiveData<ArrayList<PredictionSettingModel>>()

    val predictionSettings: LiveData<ArrayList<PredictionSettingModel>>
        get() = _predictionSettings


    fun add(predictionSetting: PredictionSettingModel) {
        db.create(predictionSetting)
    }

    fun update(currentPredictionSettings: ArrayList<PredictionSettingModel>) {
        db.deleteAll()
        currentPredictionSettings.forEach {
            db.create(it)
        }
    }

    fun setPredictionSettings(currentData: ArrayList<PredictionSettingModel>) {
        _predictionSettings.value = currentData
    }

}
