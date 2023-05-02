package net.bosowski

import android.app.Application
import net.bosowski.stores.PredictionSettingsStore
import net.bosowski.stores.StatsStore
import timber.log.Timber

class KeyboardGPTApp: Application() {

    var idToken: String? = null
    lateinit var userId: String
    lateinit var statsStore: StatsStore
    lateinit var predictionSettingsStore: PredictionSettingsStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}