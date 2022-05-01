package com.jukti.clearscoredemo.di

import com.finsol.tech.data.remote.LoginDataSource
import com.finsol.tech.data.remote.MarketDataSource

import com.finsol.tech.data.remote.ProfileDataSource
import com.finsol.tech.data.repository.LoginRepositoryImp
import com.finsol.tech.data.repository.MarketDataRepositoryImp
import com.finsol.tech.data.repository.PortfolioRepositoryImp

import com.finsol.tech.domain.LoginResponseDataRepository
import com.finsol.tech.domain.MarketDataRepository
import com.finsol.tech.domain.ProfileResponseDataRepository
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


    @Singleton
    @Provides
    fun providePortfolioRepository(dataSource: PortfolioDataSource) : PortfolioResponseDataRepository {
        return PortfolioRepositoryImp(dataSource)
    }
}