package net.bosowski

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.bosowski.models.TextCommandConfigModel

class TextCommandConfigViewModel : ViewModel() {

    private val textCommandConfigs = MutableLiveData<List<TextCommandConfigModel>>()

    var observableCommandConfigs: LiveData<List<TextCommandConfigModel>>
        get() = textCommandConfigs
        set(value) {textCommandConfigs.value = value.value}

    fun load(){
        textCommandConfigs.value = TextCommandConfigManager.findAll()
    }
}