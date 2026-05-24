package com.example.krug.data.model.event

import com.google.gson.annotations.SerializedName

data class Event(
    @SerializedName("event_id") val eventId: String,
    val title: String,
    @SerializedName("description_event") val description: String?,
    val location: String?,
    @SerializedName("start_date_time") val startDateTime: String?,
    @SerializedName("end_date_time") val endDateTime: String?,
    val color: String,
    @SerializedName("status_event") val status: String
)

data class Member(
    @SerializedName("user_id") val userId: String,
    @SerializedName("display_name") val displayName: String,
    val permissions: String
)

data class DetailedEvent(
    val event: Event,
    @SerializedName("invite_link") val inviteLink: String?,
    val members: List<Member>,
    val permissions: String
)

data class EventsListResponse(
    val items: List<Event>,
    val total: Int
)

data class CreateEventRequest(
    val title: String,
    val location: String? = null,
    @SerializedName("description_event") val description: String? = null,
    @SerializedName("start_date_time") val startDateTime: String? = null,
    @SerializedName("end_date_time") val endDateTime: String? = null,
    val color: String
)

data class UpdateEventRequest(
    val title: String? = null,
    val location: String? = null,
    @SerializedName("description_event") val description: String? = null,
    @SerializedName("start_date_time") val startDateTime: String? = null,
    @SerializedName("end_date_time") val endDateTime: String? = null,
    val color: String? = null
)

data class StatusUpdateRequest(val status: String)
data class JoinEventRequest(@SerializedName("invite_token") val inviteToken: String)
data class UpdateMemberPermissionsRequest(val permissions: String)