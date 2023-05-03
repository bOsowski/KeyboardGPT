package net.bosowski.adapters

import android.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import net.bosowski.databinding.PredictionSettingCardBinding
import net.bosowski.models.TextCommandConfigModel
import net.bosowski.stores.FirebaseTextCommandStore


class PredictionSettingsRecyclerViewAdapter(var predictionSettings: ArrayList<TextCommandConfigModel>) :
    RecyclerView.Adapter<PredictionSettingsRecyclerViewAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding =
            PredictionSettingCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(
            binding,
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
        private val adapter: PredictionSettingsRecyclerViewAdapter
    ) : RecyclerView.ViewHolder(binding.root) {


        private fun setStatusImage(predictionSetting: TextCommandConfigModel) {
            if (predictionSetting.isOn) {
                binding.statusButton.setImageResource(R.drawable.star_big_on)
            } else {
                binding.statusButton.setImageResource(R.drawable.star_big_off)
            }
        }

        fun bind(predictionSetting: TextCommandConfigModel) {
            binding.predictionCard.setText(predictionSetting.text)

            setStatusImage(predictionSetting)

            binding.predictionCard.doOnTextChanged { text, start, before, count ->
                predictionSetting.text = text.toString()
            }

            binding.deleteButton.setOnClickListener {
                adapter.notifyItemRemoved(adapterPosition)
                adapter.predictionSettings.remove(predictionSetting)
            }

            binding.statusButton.setOnClickListener {
                predictionSetting.isOn = !predictionSetting.isOn
                FirebaseTextCommandStore.update(predictionSetting)
                setStatusImage(predictionSetting)
            }
        }
    }

}