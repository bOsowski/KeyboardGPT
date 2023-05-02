package net.bosowski.adapters

import android.R
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import net.bosowski.KeyboardGPTApp
import net.bosowski.databinding.PredictionSettingCardBinding
import net.bosowski.models.PredictionSettingModel
import net.bosowski.stores.PredictionSettingsStore
import timber.log.Timber


class PredictionSettingsAdapter(private var predictionSettings: List<PredictionSettingModel>) :
    RecyclerView.Adapter<PredictionSettingsAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding =
            PredictionSettingCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(
            binding,
            (parent.context.applicationContext as KeyboardGPTApp).predictionSettingsStore,
            this
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


        private fun setStatusImage(predictionSetting: PredictionSettingModel) {
            if (predictionSetting.isOn) {
                binding.statusButton.setImageResource(R.drawable.star_big_on)
            } else {
                binding.statusButton.setImageResource(R.drawable.star_big_off)
            }
        }

        fun bind(predictionSetting: PredictionSettingModel) {
            binding.predictionCard.setText(predictionSetting.text)

            setStatusImage(predictionSetting)

            binding.predictionCard.doOnTextChanged { text, start, before, count ->
                predictionSetting.text = text.toString()
                predictionSettingsStore.update(predictionSetting)
            }

            binding.deleteButton.setOnClickListener {
//                Timber.i("Delete button clicked for ${foundPredictionSetting?.id}")
                if (predictionSetting != null) {
                    predictionSettingsStore.delete(predictionSetting)
                }
                adapter.notifyDataSetChanged()
            }

            binding.statusButton.setOnClickListener {
                predictionSetting.isOn = !predictionSetting.isOn
                predictionSettingsStore.update(predictionSetting)
                setStatusImage(predictionSetting)
            }
        }
    }

}