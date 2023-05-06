package net.bosowski.userStats

import net.bosowski.models.StatsModel

interface StatsStore {
    fun set(statsModel: StatsModel)
    fun find(): StatsModel?
}