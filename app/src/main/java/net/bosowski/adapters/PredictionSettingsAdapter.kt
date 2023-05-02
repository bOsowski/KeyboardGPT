package net.bosowski.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.bosowski.KeyboardGPTApp
import net.bosowski.models.PredictionSettingModel
import net.bosowski.databinding.PredictionSettingCardBinding
import net.bosowski.stores.PredictionSettingsStore
import timber.log.Timber

class PredictionSettingsAdapter(private var predictionSettings: List<PredictionSettingModel>) :
    RecyclerView.Adapter<PredictionSettingsAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding =
            PredictionSettingCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(
            binding, (parent.context.applicationContext as KeyboardGPTApp).predictionSettingsStore, this
        )
    }

    override fun getItemCount(): Int = predictionSettings.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val predictionSetting = predictionSettings[holder.adapterPosition]
        holder.bind(predictionSetting)
    }

    class MainHolder(
        private val binding: PredictionSettingCardBinding,
        private val predictionSettingsStore: PredictionSettingsStore,
        private val adapter: PredictionSettingsAdapter
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(predictionSetting: PredictionSettingModel) {
            binding.predictionCard.text = predictionSetting.text

            val foundPredictionSetting = predictionSettingsStore.find(predictionSetting.id)

            binding.deleteButton.setOnClickListener {
                Timber.i("Delete button clicked for ${foundPredictionSetting?.id}")
                if (foundPredictionSetting != null) {
                    predictionSettingsStore.delete(foundPredictionSetting)
                }
                adapter.predictionSettings = predictionSettingsStore.findAll()
                adapter.notifyDataSetChanged()
            }

            binding.statusButton.setOnClickListener {
                if (foundPredictionSetting != null) {
                    foundPredictionSetting.isOn = !foundPredictionSetting.isOn
                    predictionSettingsStore.update(foundPredictionSetting)
                    adapter.notifyItemChanged(adapterPosition)
                }
            }

            binding.editButton.setOnClickListener {
                if (foundPredictionSetting != null) {
                    // todo
                }
            }
        }
    }

}