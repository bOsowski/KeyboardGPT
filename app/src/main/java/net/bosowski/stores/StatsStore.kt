package net.bosowski.stores

import net.bosowski.utlis.ObserverNotifier
import net.bosowski.models.StatsModel

interface StatsStore: ObserverNotifier {
    fun set(statsModel: StatsModel)
    fun find(): StatsModel?
}