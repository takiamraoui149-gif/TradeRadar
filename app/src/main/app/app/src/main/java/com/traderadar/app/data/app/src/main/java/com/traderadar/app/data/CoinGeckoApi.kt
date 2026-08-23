package com.traderadar.app.data

import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoApi {

    @GET("api/v3/coins/markets")
    suspend fun getTopCoins(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 25,
        @Query("page") page: Int = 1,
        @Query("price_change_percentage") priceChange: String = "24h"
    ): List<CoinPrice>
}
