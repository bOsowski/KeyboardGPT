package net.bosowski

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import net.bosowski.authentication.LoginViewModel
import net.bosowski.stores.FirebasePredictionSettingStore
import net.bosowski.userStats.StatsViewModel
import timber.log.Timber

class KeyboardGPTApp: Application() {

    private lateinit var viewModelProvider: ViewModelProvider

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        // Initialize early
        FirebasePredictionSettingStore
    }

    fun getViewModelProvider(): ViewModelProvider {
        if (!::viewModelProvider.isInitialized) {
            viewModelProvider = ViewModelProvider(ViewModelStore(), ViewModelProvider.NewInstanceFactory())
        }
        return viewModelProvider
    }

    fun getLoginViewModel(): LoginViewModel {
        return getViewModelProvider()[LoginViewModel::class.java]
    }

    fun getStatsViewModel(): StatsViewModel {
        return getViewModelProvider()[StatsViewModel::class.java]
    }

}