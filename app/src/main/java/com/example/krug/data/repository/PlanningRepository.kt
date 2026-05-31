package com.example.krug.data.repository

import com.example.krug.data.model.DataResult
import com.example.krug.data.model.planning.*

interface PlanningRepository {
    suspend fun getPlanningModules(eventId: String): DataResult<PlanningModulesResponse>
    suspend fun createPoll(eventId: String, request: CreatePollRequest): DataResult<PlanningModule>
    suspend fun createItemList(eventId: String, request: CreateItemListRequest): DataResult<PlanningModule>
    suspend fun createTaskList(eventId: String, request: CreateTaskListRequest): DataResult<PlanningModule>
    suspend fun votePoll(eventId: String, pollId: String, optionIndexes: List<Int>): DataResult<Unit>
    suspend fun assignItem(eventId: String, type: String, moduleId: String, itemId: String, assign: Boolean): DataResult<Unit>
    suspend fun completeTask(eventId: String, moduleId: String, itemId: String, completed: Boolean): DataResult<Unit>
}