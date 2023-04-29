package net.bosowski.keyboard.stats

interface StatsStore {
    fun set(statsModel: StatsModel)
    fun find(userId: String): StatsModel?
}