package io.github.merlin.assistant.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.merlin.assistant.data.network.service.JewelService
import io.github.merlin.assistant.data.network.service.LoginService
import io.github.merlin.assistant.repo.AccountRepo
import io.github.merlin.assistant.repo.JewelRepo
import io.github.merlin.assistant.data.local.LocalDataSource
import io.github.merlin.assistant.repo.LoginRepo
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {

    @Provides
    @Singleton
    fun provideLocalDataSource(@ApplicationContext context: Context): LocalDataSource {
        return LocalDataSource(context)
    }

    @Provides
    @Singleton
    fun provideAccountRepo(
        localDataSource: LocalDataSource,
        json: Json
    ): AccountRepo {
        return AccountRepo(localDataSource, json)
    }

    @Provides
    @Singleton
    fun provideJewelRepo(
        localDataSource: LocalDataSource,
        jewelService: JewelService,
        json: Json
    ): JewelRepo {
        return JewelRepo(localDataSource, jewelService, json)
    }

    @Provides
    @Singleton
    fun provideLoginRepo(
        accountRepo: AccountRepo,
        loginService: LoginService,
    ): LoginRepo {
        return LoginRepo(accountRepo, loginService)
    }
}