package net.bosowski.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import net.bosowski.KeyboardGPTApp
import net.bosowski.R
import net.bosowski.adapters.PredictionSettingsAdapter
import net.bosowski.databinding.PredictionSettingsListBinding
import net.bosowski.models.PredictionSettingModel

class PredictionSettingsListActivity: AppCompatActivity(){

    private lateinit var binding: PredictionSettingsListBinding
    private lateinit var app: KeyboardGPTApp

    private lateinit var adapter: PredictionSettingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PredictionSettingsListBinding.inflate(layoutInflater)

        setContentView(binding.root)

        app = application as KeyboardGPTApp

        val layoutManager = LinearLayoutManager(this)
        binding.settingsRecyclerView.layoutManager = layoutManager
        adapter = PredictionSettingsAdapter(app.predictionSettingsStore.findAll())
        binding.settingsRecyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_prediction_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val predictionSetting = PredictionSettingModel(text = "Rephrase the text")
        app.predictionSettingsStore.create(predictionSetting)
        adapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}