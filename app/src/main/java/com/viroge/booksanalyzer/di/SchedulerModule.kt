package com.viroge.booksanalyzer.di

import com.viroge.booksanalyzer.domain.delete.DeleteBooksScheduler
import com.viroge.booksanalyzer.domain.delete.DeleteBooksSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SchedulerModule {

    @Binds
    abstract fun bindDeleteScheduler(
        impl: DeleteBooksSchedulerImpl,
    ): DeleteBooksScheduler
}
