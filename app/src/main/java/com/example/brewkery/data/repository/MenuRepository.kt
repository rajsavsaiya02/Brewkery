package com.example.brewkery.data.repository

import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.StoreResponse
import com.example.brewkery.data.network.BrewkeryApiService
import com.example.brewkery.data.network.RetrofitClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface MenuRepository {
    suspend fun fetchStoreData(): Result<StoreResponse>
    suspend fun fetchItemDetail(id: Int): Result<MenuItem>
}

class MenuRepositoryImpl(
    private val apiService: BrewkeryApiService = RetrofitClient.apiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MenuRepository {

    private var cachedStoreResponse: StoreResponse? = null

    override suspend fun fetchStoreData(): Result<StoreResponse> = withContext(ioDispatcher) {
        runCatching {
            val response = apiService.getStoreData()
            cachedStoreResponse = response
            response
        }
    }

    override suspend fun fetchItemDetail(id: Int): Result<MenuItem> = withContext(ioDispatcher) {
        runCatching {
            val itemFromCache = cachedStoreResponse?.menuItems?.firstOrNull { it.id == id }
            if (itemFromCache?.customizations != null) {
                itemFromCache
            } else {
                apiService.getItemDetail(id)
            }
        }
    }
}
