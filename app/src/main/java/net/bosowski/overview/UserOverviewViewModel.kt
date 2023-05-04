package net.bosowski.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bosowski.models.StatsModel
import net.bosowski.utlis.Constants
import java.text.DecimalFormat

class UserOverviewViewModel: ViewModel() {

    private val _availableCredits = MutableLiveData<String>()
    val availableCredits: LiveData<String> get() = _availableCredits

    private val _stats = MutableLiveData<StatsModel?>()
    val stats: LiveData<StatsModel?> get() = _stats

    fun fetchUserData(idToken: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    val response =
                        HttpClient().get("${Constants.CHATTERGPT_SERVER_URL}/api/user/info") {
                            bearerAuth(idToken ?: "")
                        }
                    var availableCredits = 0f
                    if (response.bodyAsText().isNotBlank()) {
                        val json = JsonParser.parseString(response.bodyAsText()).asJsonObject
                        availableCredits = json.get("availableCredits").asFloat
                    }
                    val df = DecimalFormat("#.####")
                    _availableCredits.value = df.format(availableCredits)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun updateStats(stats: StatsModel?) {
        _stats.value = stats
    }
}