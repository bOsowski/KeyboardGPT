package net.bosowski.predictionSettings

import net.bosowski.predictionSettings.PredictionSettingModel

interface PredictionSettingStore {
    fun create(predictionSettingModel: PredictionSettingModel)
    fun update(predictionSettingModel: PredictionSettingModel)
    fun update(predictionSettingModels: ArrayList<PredictionSettingModel>)
    fun delete(predictionSettingModel: PredictionSettingModel)
    fun deleteAll()
    fun findAll(): List<PredictionSettingModel>
    fun find(id: String): PredictionSettingModel?
}