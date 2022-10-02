package com.juvcarl.shoplist.database

import com.juvcarl.shoplist.database.dao.ItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DaosModule {
    @Provides
    fun providesItemDao(
        database: ShopListDatabase
    ): ItemDao = database.ItemsDao()
}