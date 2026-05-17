package com.example.krug.data.repository

import com.example.krug.data.model.DataResult
import com.example.krug.data.model.planning.*

interface PlanningRepository {
    suspend fun getPlanningModules(eventId: String): DataResult<PlanningModulesResponse>
    suspend fun createPoll(eventId: String, request: CreatePollRequest): DataResult<Unit>
    suspend fun createItemList(eventId: String, request: CreateItemListRequest): DataResult<Unit>
    suspend fun createTaskList(eventId: String, request: CreateTaskListRequest): DataResult<Unit>
}