package com.andreasgift.transactionsmsparser.di

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import com.andreasgift.transactionsmsparser.data.SMSDao
import com.andreasgift.transactionsmsparser.data.SMSDatabase
import com.andreasgift.transactionsmsparser.data.SMSRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApplicationModule {

    @Provides
    fun provideSMSDatabase(@ApplicationContext appContext: Context): SMSDatabase {
        return SMSDatabase.getInstance(appContext)
    }

    @Provides
    @Singleton
    fun provideSMSDao(db: SMSDatabase): SMSDao = db.smsDao

    @Provides
    @Singleton
    fun provideSMSRepository(@ApplicationContext appContext: Context, dao:SMSDao) = SMSRepository(appContext, dao)
}