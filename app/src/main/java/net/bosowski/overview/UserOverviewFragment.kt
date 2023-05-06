package net.bosowski.overview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.bosowski.predictionSettings.PredictionSettingsListActivity
import net.bosowski.databinding.FragmentUserOverviewBinding
import android.net.Uri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import net.bosowski.BuildConfig.SERVER_URL
import net.bosowski.KeyboardGPTApp
import net.bosowski.R
import net.bosowski.authentication.LoginViewModel
import net.bosowski.userStats.StatsViewModel

class UserOverviewFragment : Fragment() {

    private lateinit var app: KeyboardGPTApp
    private lateinit var binding: FragmentUserOverviewBinding

    private val userOverviewViewModel: UserOverviewViewModel by viewModels()
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var statsViewModel: StatsViewModel

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        app = requireActivity().application as KeyboardGPTApp
        statsViewModel = app.statsViewModel
        loginViewModel = app.loginViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserOverviewBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userOverviewViewModel.availableCredits.observe(viewLifecycleOwner, Observer { credits ->
            binding.availableCredits.text = getString(R.string.available_credits, credits)
        })

        statsViewModel.statsModel.observe(viewLifecycleOwner, Observer { stats ->
            binding.keystrokes.text =
                getString(R.string.total_keystrokes, stats?.buttonClicks?.map { it.value }?.sum() ?: 0)
            binding.completionClicks.text =
                getString(R.string.completion_clicks, stats?.completionsUsed ?: 0)
        })

        binding.buyCredits.setOnClickListener {
            buyCredits()
        }

        loginViewModel.idToken.observe(viewLifecycleOwner) { token: String ->
            userOverviewViewModel.fetchUserData(token)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_user_overview, menu)

        menu.findItem(R.id.settings_button).setOnMenuItemClickListener { _ ->
            val launcherIntent = Intent(requireContext(), PredictionSettingsListActivity::class.java)
            startActivity(launcherIntent)
            true
        }
    }

    private fun buyCredits() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(SERVER_URL))
        startActivity(browserIntent)
    }
}
