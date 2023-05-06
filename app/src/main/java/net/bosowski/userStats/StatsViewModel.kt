package net.bosowski.userStats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StatsViewModel : ViewModel() {

    private val _statsModel = MutableLiveData<StatsModel?>()
    val statsModel: LiveData<StatsModel?> get() = _statsModel

    private val statsStore = FirebaseStatsStore(this)

    fun setStatsModel(stats: StatsModel?) {
        _statsModel.value = stats
    }

    fun incrementCompletions() {
        if(_statsModel.value != null){
            _statsModel.value!!.completionsUsed++
        }
        else{
            _statsModel.value = StatsModel()
            _statsModel.value!!.completionsUsed++
        }
        statsStore.set(statsModel.value!!)
    }

    fun incrementKeyStrokes(key: String) {
        if(_statsModel.value != null){
            if(_statsModel.value!!.buttonClicks.containsKey(key)){
                _statsModel.value!!.buttonClicks[key] = _statsModel.value!!.buttonClicks[key]!! + 1
            }
            else{
                _statsModel.value!!.buttonClicks[key] = 1
            }
        }
        else{
            _statsModel.value = StatsModel()
            _statsModel.value!!.buttonClicks[key] = 1
        }
        statsStore.set(statsModel.value!!)
    }
}