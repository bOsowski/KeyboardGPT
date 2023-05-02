package net.bosowski.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import net.bosowski.KeyboardGPTApp
import net.bosowski.R
import net.bosowski.adapters.PredictionSettingsRecyclerViewAdapter
import net.bosowski.databinding.PredictionSettingsListBinding
import net.bosowski.models.PredictionSettingModel

class PredictionSettingsListActivity: AppCompatActivity(){

    private lateinit var binding: PredictionSettingsListBinding
    private lateinit var app: KeyboardGPTApp

    private lateinit var adapter: PredictionSettingsRecyclerViewAdapter

    private val currentPredictionSettings = ArrayList<PredictionSettingModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PredictionSettingsListBinding.inflate(layoutInflater)

        setContentView(binding.root)

        app = application as KeyboardGPTApp

        val layoutManager = LinearLayoutManager(this)
        binding.settingsRecyclerView.layoutManager = layoutManager

        currentPredictionSettings.addAll(app.predictionSettingsStore.findAll())
        adapter = PredictionSettingsRecyclerViewAdapter(currentPredictionSettings)
        binding.settingsRecyclerView.adapter = adapter

        binding.floatingActionButton.setOnClickListener{ view ->
            val predictionSetting = PredictionSettingModel(text = "Rephrase the text")
            currentPredictionSettings.add(predictionSetting)
            adapter.notifyItemInserted(adapter.itemCount - 1)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_prediction_settings, menu)

        menu!!.findItem(R.id.save_button).setOnMenuItemClickListener { item ->
            app.predictionSettingsStore.deleteAll()
            currentPredictionSettings.forEach {
                app.predictionSettingsStore.create(it)
            }
            finish()
            true
        }
        menu.findItem(R.id.cancel_button).setOnMenuItemClickListener { item ->
            currentPredictionSettings.clear()
            currentPredictionSettings.addAll(app.predictionSettingsStore.findAll())
            finish()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val predictionSetting = PredictionSettingModel(text = "Rephrase the text")
        app.predictionSettingsStore.create(predictionSetting)
        adapter.notifyItemInserted(adapter.itemCount - 1)
        return super.onOptionsItemSelected(item)
    }
}