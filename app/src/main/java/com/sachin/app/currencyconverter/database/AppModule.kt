package com.sachin.app.currencyconverter.database

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class AppModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = AppDatabase.invoke(context)

    @Provides
    fun provideDao(@ApplicationContext context: Context): CountriesDao =
        AppDatabase.invoke(context).countriesDao()
}