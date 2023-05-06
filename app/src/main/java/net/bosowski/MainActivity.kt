package net.bosowski

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.bosowski.R
import net.bosowski.authentication.LoginFragment
import net.bosowski.databinding.ActivityMainBinding
import net.bosowski.overview.UserOverviewFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, LoginFragment())
            .commit()
    }
}