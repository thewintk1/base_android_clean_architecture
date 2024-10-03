package com.example.base_clean_architecture.di

import com.example.base_clean_architecture.ui.MainViewModelDelegate
import com.example.base_clean_architecture.ui.MainViewModelDelegateImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class ViewModelModule {
    @Singleton
    @Provides
    fun provideMainViewModelDelegate(): MainViewModelDelegate = MainViewModelDelegateImpl()
}
