package com.example.krug.data.model.planning

data class PlanningModulesResponse(
    val modules: List<PlanningModule>
)

data class PlanningModule(
    val id: String,
    val type: String,        // "poll", "item_list", "task_list"
    val title: String,
    val data: ModuleData?
)

sealed class ModuleData

data class PollData(
    val options: List<String>,
    val multiple_choice: Boolean,
    val votes: List<Vote>? = null,
    val votes_count: List<Int>? = null,
    val own_vote: List<Int> = emptyList()
) : ModuleData()

data class Vote(
    val option_index: Int,
    val user_id: String,
    val display_name: String
)

data class ItemListData(
    val items: List<PlanItem>
) : ModuleData()

data class TaskListData(
    val items: List<PlanItem>
) : ModuleData()

data class PlanItem(
    val id: String,
    val text: String,
    val assigned_user_id: String? = null,
    val assigned_user_name: String? = null,
    val completed: Boolean? = null   // только для задач
)

// Запросы на создание
data class CreatePollRequest(
    val title: String,
    val options: List<String>,
    val multiple_choice: Boolean
)

data class CreateItemListRequest(
    val title: String,
    val items: List<String>
)

data class CreateTaskListRequest(
    val title: String,
    val items: List<String>
)