package net.bosowski.userStats

import java.util.Date

data class StatsModel(
    var id: String? = null,
    var userId: String = "",
    var buttonClicks: HashMap<String, Long> = HashMap(),
    var completionsUsed: Long = 0,
    var date: Date = Date()
)
