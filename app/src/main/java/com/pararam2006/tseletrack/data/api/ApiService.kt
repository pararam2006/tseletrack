package com.pararam2006.tseletrack.data.api

import com.pararam2006.tseletrack.data.api.dto.*
import retrofit2.http.*

interface ApiService {

    @POST("users/register")
    suspend fun register(@Body body: RegisterRequest): UserDto

    @FormUrlEncoded
    @POST("users/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("deals/")
    suspend fun getDeals(): List<DealDto>

    @GET("deals/{id}")
    suspend fun getDeal(@Path("id") id: Int): DealDto

    @POST("deals/")
    suspend fun createDeal(@Body body: CreateDealRequest): DealDto

    @POST("deals/{id}/advance")
    suspend fun advanceStage(
        @Path("id") id: Int,
        @Query("new_stage") newStage: Int
    ): AdvanceResponse

    @GET("deals/{id}/events")
    suspend fun getEvents(@Path("id") id: Int): List<EventDto>

    @POST("deals/{id}/events")
    suspend fun addEvent(
        @Path("id") id: Int,
        @Body body: CreateEventRequest
    ): EventDto

    @GET("analytics/funnel")
    suspend fun getFunnel(): FunnelResponse
}