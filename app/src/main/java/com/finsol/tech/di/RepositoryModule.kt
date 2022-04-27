package com.jukti.clearscoredemo.di

import com.finsol.tech.data.remote.AllContractsDataSource
import com.finsol.tech.data.remote.LoginDataSource
import com.finsol.tech.data.remote.MarketDataSource
import com.finsol.tech.data.remote.ProfileDataSource
import com.finsol.tech.data.repository.AllContractsRepositoryImp
import com.finsol.tech.data.repository.LoginRepositoryImp
import com.finsol.tech.data.repository.MarketDataRepositoryImp
import com.finsol.tech.data.repository.ProfileRepositoryImp
import com.finsol.tech.domain.AllContractsResponseDataRepository
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
    fun provideProfileRepository(dataSource: ProfileDataSource) : ProfileResponseDataRepository {
        return ProfileRepositoryImp(dataSource)
    }

    @Singleton
    @Provides
    fun provideAllContractsRepository(dataSource: AllContractsDataSource) : AllContractsResponseDataRepository {
        return AllContractsRepositoryImp(dataSource)
    }
}