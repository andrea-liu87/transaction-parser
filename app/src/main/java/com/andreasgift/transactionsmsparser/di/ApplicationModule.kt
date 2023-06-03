package com.andreasgift.transactionsmsparser.di

import android.content.Context
import com.andreasgift.transactionparser.TransactionParser
import com.andreasgift.transactionsmsparser.data.SMS.SMSDao
import com.andreasgift.transactionsmsparser.data.SMS.SMSDatabase
import com.andreasgift.transactionsmsparser.data.SMS.SMSRepository
import com.andreasgift.transactionsmsparser.data.transaction.TransactionDao
import com.andreasgift.transactionsmsparser.data.transaction.TransactionDatabase
import com.andreasgift.transactionsmsparser.data.transaction.TransactionRepository
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
    fun transactionParser() : TransactionParser{
        return TransactionParser()
    }

    @Provides
    fun provideSMSDatabase(@ApplicationContext appContext: Context): SMSDatabase {
        return SMSDatabase.getInstance(appContext)
    }

    @Provides
    @Singleton
    fun provideSMSDao(db: SMSDatabase): SMSDao = db.smsDao

    @Provides
    @Singleton
    fun provideSMSRepository(@ApplicationContext appContext: Context, dao: SMSDao, parser: TransactionParser) =
        SMSRepository(appContext, dao, parser)

    @Provides
    fun provideTransactionDatabase(@ApplicationContext appContext: Context): TransactionDatabase {
        return TransactionDatabase.getInstance(appContext)
    }

    @Provides
    @Singleton
    fun provideTransactionDao(db: TransactionDatabase): TransactionDao = db.dao

    @Provides
    @Singleton
    fun provideTransactionRepository(@ApplicationContext appContext: Context, dao: TransactionDao, parser: TransactionParser)
    = TransactionRepository(appContext, dao, parser)
}