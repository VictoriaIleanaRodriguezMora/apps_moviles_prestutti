package com.prestutti

import com.prestutti.domain.model.Loan
import com.prestutti.domain.model.LoanType
import com.prestutti.domain.repository.LoanRepository
import com.prestutti.domain.usecase.SaveLoanUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

// 1. Creamos un "Fake" (Doble de prueba) del repositorio para aislar el test de Room
class FakeLoanRepository : LoanRepository {
    var savedLoan: Loan? = null

    override suspend fun saveLoan(loan: Loan): Long {
        savedLoan = loan
        return 1L // Simulamos que se guardó en la base de datos con el ID 1
    }

    // Dejamos el resto vacío porque este test solo evalúa la función saveLoan
    override fun getLentLoans(): Flow<List<Loan>> = emptyFlow()
    override fun getBorrowedLoans(): Flow<List<Loan>> = emptyFlow()
    override suspend fun updateLoan(loan: Loan) {}
    override suspend fun deleteLoan(loan: Loan) {}
}

class SaveLoanUseCaseTest {

    private lateinit var fakeRepository: FakeLoanRepository
    private lateinit var saveLoanUseCase: SaveLoanUseCase

    @Before
    fun setUp() {
        // Antes de cada prueba, inicializamos nuestro entorno limpio
        fakeRepository = FakeLoanRepository()
        saveLoanUseCase = SaveLoanUseCase(fakeRepository)
    }

    @Test
    fun `cuando el prestamo es valido, se guarda en el repositorio y retorna success`() = runBlocking {
        // 1. Preparar (Arrange)
        val validLoan = Loan(
            id = 0,
            personName = "Juan",
            item = "Taladro",
            description = "",
            category = "Herramientas",
            date = Date(),
            dueDate = null,
            type = LoanType.LENT,
            isReturned = false
        )

        // 2. Ejecutar (Act)
        val result = saveLoanUseCase(validLoan)

        // 3. Comprobar (Assert)
        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull())
        assertEquals("Juan", fakeRepository.savedLoan?.personName) // Verificamos que llegó al repositorio
    }

    @Test
    fun `cuando el nombre esta vacio, no guarda y retorna error especifico`() = runBlocking {
        // 1. Preparar
        val invalidLoan = Loan(
            id = 0,
            personName = "   ", // <- El usuario mandó espacios vacíos
            item = "Taladro",
            description = "",
            category = "Herramientas",
            date = Date(),
            dueDate = null,
            type = LoanType.LENT,
            isReturned = false
        )

        // 2. Ejecutar
        val result = saveLoanUseCase(invalidLoan)

        // 3. Comprobar
        assertTrue(result.isFailure)
        assertEquals("El nombre de la persona no puede estar vacío", result.exceptionOrNull()?.message)
        assertTrue(fakeRepository.savedLoan == null) // Verificamos que el repositorio de mentira no guardó nada
    }

    @Test
    fun `cuando el item esta vacio, no guarda y retorna error especifico`() = runBlocking {
        // 1. Preparar
        val invalidLoan = Loan(
            id = 0,
            personName = "Juan",
            item = "", // <- El usuario no puso qué prestó
            description = "",
            category = "Herramientas",
            date = Date(),
            dueDate = null,
            type = LoanType.LENT,
            isReturned = false
        )

        // 2. Ejecutar
        val result = saveLoanUseCase(invalidLoan)

        // 3. Comprobar
        assertTrue(result.isFailure)
        assertEquals("El ítem prestado/recibido no puede estar vacío", result.exceptionOrNull()?.message)
    }
}