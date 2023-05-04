package net.bosowski.keyboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.bosowski.models.StatsModel
import net.bosowski.models.PredictionSettingModel

class KeyboardViewModel : ViewModel() {

    private val _textCommands = MutableLiveData<List<PredictionSettingModel>>()
    val textCommands: LiveData<List<PredictionSettingModel>> = _textCommands

    private val _statsModel = MutableLiveData<StatsModel>()
    val statsModel: LiveData<StatsModel> = _statsModel

    fun setTextCommands(textCommands: List<PredictionSettingModel>) {
        _textCommands.value = textCommands
    }

    fun setStatsModel(statsModel: StatsModel) {
        _statsModel.value = statsModel
    }
}