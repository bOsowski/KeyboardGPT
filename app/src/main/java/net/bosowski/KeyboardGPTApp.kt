package net.bosowski

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import net.bosowski.authentication.LoginViewModel
import net.bosowski.predictionSettings.PredictionSettingsViewModel
import net.bosowski.userStats.StatsViewModel
import timber.log.Timber

class KeyboardGPTApp: Application() {

    private lateinit var viewModelProvider: ViewModelProvider

    lateinit var predictionSettingsViewModel: PredictionSettingsViewModel
    lateinit var statsViewModel: StatsViewModel
    lateinit var loginViewModel: LoginViewModel

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        // Instantiating here to allow for the earliest possible instantiation time
        predictionSettingsViewModel = getViewModelProvider()[PredictionSettingsViewModel::class.java]
        statsViewModel = getViewModelProvider()[StatsViewModel::class.java]
        loginViewModel = getViewModelProvider()[LoginViewModel::class.java]
    }

    private fun getViewModelProvider(): ViewModelProvider {
        if (!::viewModelProvider.isInitialized) {
            viewModelProvider = ViewModelProvider(ViewModelStore(), ViewModelProvider.NewInstanceFactory())
        }
        return viewModelProvider
    }

}