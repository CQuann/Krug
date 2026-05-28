package com.example.krug.data.model.planning

import com.google.gson.*
import java.lang.reflect.Type

class PlanningModuleDeserializer : JsonDeserializer<PlanningModule> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): PlanningModule {
        val obj = json.asJsonObject
        val id = obj.get("id").asString
        val moduleType = obj.get("type").asString
        val title = obj.get("title").asString
        val dataElement = obj.get("data")

        val data: ModuleData? = when (moduleType) {
            "poll" -> context.deserialize(dataElement, PollData::class.java)
            "item_list" -> context.deserialize(dataElement, ItemListData::class.java)
            "task_list" -> context.deserialize(dataElement, TaskListData::class.java)
            else -> null
        }
        return PlanningModule(id, moduleType, title, data)
    }
}