package net.bosowski.stores

import net.bosowski.models.TextCommandConfigModel

interface TextCommandStore {
    fun create(textCommandConfigModel: TextCommandConfigModel)
    fun update(textCommandConfigModel: TextCommandConfigModel)
    fun delete(textCommandConfigModel: TextCommandConfigModel)
    fun deleteAll()
    fun findAll(): List<TextCommandConfigModel>
    fun find(id: String): TextCommandConfigModel?
}