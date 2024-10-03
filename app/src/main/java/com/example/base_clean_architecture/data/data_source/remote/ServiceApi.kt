package com.example.base_clean_architecture.data.data_source.remote

import retrofit2.http.*
import java.util.*

interface ServiceApi {

    @POST("app/login/")
    suspend fun loginOfOwner()
}