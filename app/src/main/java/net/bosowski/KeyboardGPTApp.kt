package net.bosowski

import android.app.Application
import net.bosowski.stores.FirebasePredictionSettingsStore
import net.bosowski.stores.FirebaseStatsStore
import net.bosowski.stores.PredictionSettingsStore
import net.bosowski.stores.StatsStore
import timber.log.Timber

class KeyboardGPTApp: Application() {

    var idToken: String? = null
    lateinit var userId: String
    lateinit var statsStore: FirebaseStatsStore
    lateinit var predictionSettingsStore: FirebasePredictionSettingsStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}