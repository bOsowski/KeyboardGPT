package net.bosowski.userStats

interface StatsStore {
    fun set(statsModel: StatsModel)
    fun find(): StatsModel?
}