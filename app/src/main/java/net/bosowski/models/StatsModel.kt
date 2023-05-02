package net.bosowski.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class StatsModel(
    var id: String? = null,
    var userId: String = "",
    var buttonClicks: HashMap<String, Long> = HashMap(),
    var completionsUsed: Long = 0,
    var date: Date = Date()
) : Parcelable
