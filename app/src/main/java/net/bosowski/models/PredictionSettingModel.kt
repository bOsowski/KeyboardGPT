package net.bosowski.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PredictionSettingModel(
    var id: String = "",
    var userId: String = "",
    var text: String = "Rephrase the text",
    var isOn: Boolean = true,
): Parcelable
