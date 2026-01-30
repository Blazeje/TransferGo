package com.transfer.data.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.transfer.data.api.TransferApi
import com.transfer.data.repository.TransferRepositoryImpl
import com.transfer.domain.TransferRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://my.transfergo.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideTransferApi(retrofit: Retrofit): TransferApi =
        retrofit.create(TransferApi::class.java)

    @Provides
    @Singleton
    fun provideTransferRepository(
        api: TransferApi
    ): TransferRepository = TransferRepositoryImpl(api)
}


