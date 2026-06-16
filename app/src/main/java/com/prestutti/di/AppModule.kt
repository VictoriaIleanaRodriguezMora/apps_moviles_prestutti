package com.prestutti.di

import android.content.Context
import androidx.room.Room
import com.prestutti.data.local.LoanDao
import com.prestutti.data.local.PrestuttiDatabase
import com.prestutti.data.remote.dto.PrestuttiApiService
import com.prestutti.data.repository.LoanRepositoryImplementation
import com.prestutti.domain.repository.LoanRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// Con Hilt, solo escribís @Inject y él lo arma solo. AppModule.kt le dice a Hilt cómo crear Room y cómo conectar LoanRepository con LoanRepositoryImpl.

// LoanRepository es una interfaz. No tiene código real, solo promesas, no implementación
// Hilt conecta la interfaz (LoanRepository) con la implementación real
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
    // ¿Cómo llega el DAO al Repository?
    // Hilt construye la cadena completa antes de que la app arranque:
    fun provideLoanDao(db: PrestuttiDatabase): LoanDao = db.loanDao()

    // Inyeccion de Retrofit

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        // Le enseñamos a Hilt ("el electricista") cómo construir la conexión a internet
        return Retrofit.Builder()
            // La URL base de la API pública que vamos a consumir
            .baseUrl("https://jsonplaceholder.typicode.com/")
            // Le decimos que use Gson para traducir el JSON de internet a Kotlin
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePrestuttiApiService(retrofit: Retrofit): PrestuttiApiService {
        // Hilt toma el Retrofit que creamos arriba y construye la interfaz de la API
        return retrofit.create(PrestuttiApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLoanRepository(impl: LoanRepositoryImplementation): LoanRepository
    // "Cada vez que alguien pida un LoanRepository, dale un LoanRepositoryImplementation"
    // "LoanRepository" → darle LoanRepositoryImpl
}
/*
* SaveLoanUseCase pide un LoanRepository (interfaz)
* Hilt le da un LoanRepositoryImplementation (sin que SaveLoanUseCase lo sepa)
* UseCase llama a  repository.saveLoan(loan)
    En realidad está llamando a  LoanRepositoryImpl.saveLoan(loan)
* LoanRepositoryImpl hace la conversión y llama al DAO

SaveLoanUseCase nunca sabe que existe LoanRepositoryImpl. Solo conoce la interfaz LoanRepositor

! Es como enchufar algo a la pared: uno usa el enchufe (la interfaz), sin saber cómo está cableado adentro (la implementación).
* Hilt es el electricista que conecta todo antes de que uno llegue.
 */