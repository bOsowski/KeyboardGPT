package net.bosowski.predictionSettings

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.bosowski.KeyboardGPTApp
import net.bosowski.R
import net.bosowski.databinding.PredictionSettingsListBinding
import net.bosowski.utlis.SwipeToDeleteCallback

class PredictionSettingsListActivity : AppCompatActivity() {

    private lateinit var binding: PredictionSettingsListBinding
    private lateinit var app: KeyboardGPTApp

    private lateinit var adapter: PredictionSettingsRecyclerViewAdapter

    private lateinit var predictionsSettingsViewModel: PredictionSettingsViewModel
    private var currentPredictionSettings = ArrayList<PredictionSettingModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PredictionSettingsListBinding.inflate(layoutInflater)

        setContentView(binding.root)

        app = application as KeyboardGPTApp

        predictionsSettingsViewModel = app.predictionSettingsViewModel

        currentPredictionSettings.clear()
        if(predictionsSettingsViewModel.predictionSettings.value != null){
            currentPredictionSettings.addAll(predictionsSettingsViewModel.predictionSettings.value!!)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.settingsRecyclerView.layoutManager = layoutManager

        adapter = PredictionSettingsRecyclerViewAdapter(currentPredictionSettings)
        binding.settingsRecyclerView.adapter = adapter

        binding.floatingActionButton.setOnClickListener { view ->
            val predictionSetting = PredictionSettingModel()
            currentPredictionSettings.add(predictionSetting)
            adapter.notifyItemInserted(adapter.itemCount - 1)
        }

        val swipeDeleteHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(binding.settingsRecyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_prediction_settings, menu)

        menu!!.findItem(R.id.save_button).setOnMenuItemClickListener { item ->
            predictionsSettingsViewModel.update(currentPredictionSettings.filter { it.text != "" } as ArrayList<PredictionSettingModel>)
            finish()
            true
        }
        menu.findItem(R.id.cancel_button).setOnMenuItemClickListener { item ->
            finish()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }
}