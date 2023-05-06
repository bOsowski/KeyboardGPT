package net.bosowski.predictionSettings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import net.bosowski.databinding.PredictionSettingCardBinding


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