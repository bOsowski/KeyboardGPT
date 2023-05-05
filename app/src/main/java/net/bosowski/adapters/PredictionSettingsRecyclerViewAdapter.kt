package net.bosowski.adapters

import android.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.NonDisposableHandle.parent
import net.bosowski.databinding.PredictionSettingCardBinding
import net.bosowski.models.PredictionSettingModel
import net.bosowski.stores.FirebasePredictionSettingStore


class PredictionSettingsRecyclerViewAdapter(var predictionSettings: ArrayList<PredictionSettingModel>) :
    RecyclerView.Adapter<PredictionSettingsRecyclerViewAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding =
            PredictionSettingCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(
            binding
        )
    }

    override fun getItemCount(): Int = predictionSettings.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val predictionSetting = predictionSettings[holder.adapterPosition]
        holder.bind(predictionSetting)
    }

    fun removeAt(position: Int) {
        predictionSettings.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder(
        private val binding: PredictionSettingCardBinding,
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(predictionSetting: PredictionSettingModel) {
            binding.predictionCard.setText(predictionSetting.text)

            binding.predictionCard.doOnTextChanged { text, start, before, count ->
                predictionSetting.text = text.toString()
            }
        }
    }

}