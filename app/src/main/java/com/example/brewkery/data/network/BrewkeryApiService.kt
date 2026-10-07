package com.example.brewkery.data.network

import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.StoreResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface BrewkeryApiService {
    @GET("data.json")
    suspend fun getStoreData(): StoreResponse

    @GET("api/items/{id}.json")
    suspend fun getItemDetail(@Path("id") id: Int): MenuItem
}
