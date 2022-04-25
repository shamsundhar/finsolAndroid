package com.jukti.clearscoredemo.di

import com.finsol.tech.data.remote.LoginDataSource
import com.finsol.tech.data.remote.MarketDataSource
import com.finsol.tech.data.repository.LoginRepositoryImp
import com.finsol.tech.data.repository.MarketDataRepositoryImp
import com.finsol.tech.domain.LoginResponseDataRepository
import com.finsol.tech.domain.MarketDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMarketDataRepository(dataSource: MarketDataSource) : MarketDataRepository {
        return MarketDataRepositoryImp(dataSource)
    }

    @Singleton
    @Provides
    fun provideLoginRepository(dataSource: LoginDataSource) : LoginResponseDataRepository {
        return LoginRepositoryImp(dataSource)
    }
}