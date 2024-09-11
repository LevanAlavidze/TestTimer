package com.example.testforsmokers.modules

import com.example.testforsmokers.TimerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object TimerManagerModule {

    @Provides
    fun provideDispatcher(): CoroutineDispatcher {
        return Dispatchers.Default // Or any other dispatcher you need
    }

    @Provides
    fun provideTimerManager(dispatcher: CoroutineDispatcher): TimerManager {
        return TimerManager(dispatcher)
    }
}