package net.bosowski.models

data class PredictionSettingModel(
    var id: String = "",
    var userId: String = "",
    var text: String = "Rephrase the text",
    var isOn: Boolean = true,
)
