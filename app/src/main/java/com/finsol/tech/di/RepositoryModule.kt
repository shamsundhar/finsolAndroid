package com.jukti.clearscoredemo.di

import com.finsol.tech.data.remote.LoginDataSource
import com.finsol.tech.data.repository.LoginRepositoryImp
import com.finsol.tech.domain.LoginResponseDataRepository
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
    fun provideLoginRepository(dataSource: LoginDataSource) : LoginResponseDataRepository {
        return LoginRepositoryImp(dataSource)
    }
}