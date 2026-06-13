package com.prestutti.di

import android.content.Context
import androidx.room.Room
import com.prestutti.data.local.LoanDao
import com.prestutti.data.local.PrestuttiDatabase
import com.prestutti.data.repository.LoanRepositoryImpl
import com.prestutti.domain.repository.LoanRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PrestuttiDatabase =
        Room.databaseBuilder(
            context,
            PrestuttiDatabase::class.java,
            PrestuttiDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // TODO: reemplazar por una Migration real antes de producción
            .build()

    @Provides
    fun provideLoanDao(db: PrestuttiDatabase): LoanDao = db.loanDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLoanRepository(impl: LoanRepositoryImpl): LoanRepository
}