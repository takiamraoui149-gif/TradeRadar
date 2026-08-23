package com.traderadar.app.data

import com.google.gson.annotations.SerializedName

data class CoinPrice(
    val id: String,
    val symbol: String,
    val name: String,

    @SerializedName("current_price")
    val currentPrice: Double,

    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Double?,

    @SerializedName("image")
    val imageUrl: String,

    @SerializedName("market_cap_rank")
    val marketCapRank: Int?
)
