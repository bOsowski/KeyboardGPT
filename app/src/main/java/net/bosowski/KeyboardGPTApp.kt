package net.bosowski

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import net.bosowski.stores.FirebaseTextCommandStore
import net.bosowski.stores.FirebaseStatsStore
import timber.log.Timber

class KeyboardGPTApp: Application() {

    lateinit var user: GoogleSignInAccount

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        // Initialize early
        FirebaseStatsStore
        FirebaseTextCommandStore
    }

}