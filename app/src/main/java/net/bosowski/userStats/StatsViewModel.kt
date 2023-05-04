package net.bosowski.userStats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.bosowski.models.StatsModel

class StatsViewModel : ViewModel() {
    private val _statsModel = MutableLiveData<StatsModel?>()
    val statsModel: LiveData<StatsModel?> = _statsModel

    fun setStatsModel(stats: StatsModel?) {
        _statsModel.value = stats
    }
}