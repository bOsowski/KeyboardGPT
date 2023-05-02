package net.bosowski.stores

import net.bosowski.models.PredictionSettingModel

interface PredictionSettingsStore {

    fun create(predictionSettingModel: PredictionSettingModel)
    fun update(predictionSettingModel: PredictionSettingModel)
    fun delete(predictionSettingModel: PredictionSettingModel)
    fun deleteAll()
    fun findAll(): List<PredictionSettingModel>
    fun find(id: String): PredictionSettingModel?
}