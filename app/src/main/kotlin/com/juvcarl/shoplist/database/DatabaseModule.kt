package com.juvcarl.shoplist.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun providesShopListDatabase(
        @ApplicationContext context: Context
    ): ShopListDatabase = Room.databaseBuilder(
        context,ShopListDatabase::class.java, "shoplist database"
    ).addMigrations(MIGRATION_3_4).build()
}