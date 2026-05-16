package com.example.krug.data.repository

import com.example.krug.data.model.DataResult
import com.example.krug.data.model.planning.CreatePollRequest

interface PlanningRepository {
    suspend fun createPoll(request: CreatePollRequest): DataResult<Unit>
}