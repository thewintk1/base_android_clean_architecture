package com.example.base_clean_architecture.di

import com.example.base_clean_architecture.data.data_source.remote.ServiceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //Taking base_url and sharing it with Retrofit
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://base_url/") //Replace with your base url
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Sharing Retrofit instance with Api Service to prepare ApiService
    @Provides
    @Singleton
    fun provideRetrofitInstance(baseUrl: String): ServiceApi =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServiceApi::class.java)

}