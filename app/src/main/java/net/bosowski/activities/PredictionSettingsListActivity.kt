package net.bosowski.activities

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.bosowski.KeyboardGPTApp
import net.bosowski.R
import net.bosowski.adapters.PredictionSettingsRecyclerViewAdapter
import net.bosowski.databinding.PredictionSettingsListBinding
import net.bosowski.models.PredictionSettingModel
import net.bosowski.stores.FirebasePredictionSettingStore
import net.bosowski.utlis.SwipeToDeleteCallback

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

        currentPredictionSettings.addAll(FirebasePredictionSettingStore.findAll())
        adapter = PredictionSettingsRecyclerViewAdapter(currentPredictionSettings)
        binding.settingsRecyclerView.adapter = adapter

        binding.floatingActionButton.setOnClickListener{ view ->
            val predictionSetting = PredictionSettingModel()
            currentPredictionSettings.add(predictionSetting)
            adapter.notifyItemInserted(adapter.itemCount - 1)
        }

        val swipeDeleteHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.settingsRecyclerView.adapter as PredictionSettingsRecyclerViewAdapter
                adapter.removeAt(viewHolder.adapterPosition)

            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(binding.settingsRecyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_prediction_settings, menu)

        menu!!.findItem(R.id.save_button).setOnMenuItemClickListener { item ->
            FirebasePredictionSettingStore.deleteAll()
            currentPredictionSettings.forEach {
                FirebasePredictionSettingStore.create(it)
            }
            finish()
            true
        }
        menu.findItem(R.id.cancel_button).setOnMenuItemClickListener { item ->
            currentPredictionSettings.clear()
            currentPredictionSettings.addAll(FirebasePredictionSettingStore.findAll())
            finish()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }
}