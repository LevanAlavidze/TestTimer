package com.example.testforsmokers.modules

import com.example.testforsmokers.CigaretteRepository
import com.example.testforsmokers.CigaretteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindCigaretteRepository(
        impl: CigaretteRepositoryImpl
    ): CigaretteRepository
}
