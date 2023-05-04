package net.bosowski

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import net.bosowski.authentication.LoginViewModel
import net.bosowski.stores.FirebaseTextCommandStore
import net.bosowski.stores.FirebaseStatsStore
import timber.log.Timber

class KeyboardGPTApp: Application() {

//    lateinit var user: GoogleSignInAccount
    private lateinit var viewModelProvider: ViewModelProvider

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        // Initialize early
        FirebaseTextCommandStore
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

}