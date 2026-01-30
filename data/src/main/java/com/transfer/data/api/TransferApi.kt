package com.transfer.data.api

import com.transfer.data.dto.FxRateDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TransferApi {
    @GET("api/fx-rates")
    suspend fun getFxRate(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ): FxRateDto
}
