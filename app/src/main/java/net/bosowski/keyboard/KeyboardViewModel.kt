package net.bosowski.keyboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.bosowski.models.StatsModel
import net.bosowski.models.TextCommandConfigModel

class KeyboardViewModel : ViewModel() {

    private val _textCommands = MutableLiveData<List<TextCommandConfigModel>>()
    val textCommands: LiveData<List<TextCommandConfigModel>> = _textCommands

    private val _statsModel = MutableLiveData<StatsModel>()
    val statsModel: LiveData<StatsModel> = _statsModel

    fun setTextCommands(textCommands: List<TextCommandConfigModel>) {
        _textCommands.value = textCommands
    }

    fun setStatsModel(statsModel: StatsModel) {
        _statsModel.value = statsModel
    }
}