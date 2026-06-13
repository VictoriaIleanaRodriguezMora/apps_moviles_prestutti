package com.prestutti.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prestutti.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(private val sessionManager: SessionManager) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, error = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun onLoginClick() {
        val state = _uiState.value
        //Validación de campos vacios
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "Completá email y contraseña")
            return
        }

        //Validación de seguridad(ej: Contraseña minimo 6 caracteres)
        if (state.password.length < 6) {
            _uiState.value = state.copy(error = "Contraseña debe tener al menos 6 caracteres")
            return
        }

        //Buscamos en nuestra "base de datos" local (sessionManager)
        val usuarioRegistrado = sessionManager.obtenerUsuarioRegistrado()
        val passwordRegistrada = sessionManager.obtenerPasswordRegistrada()

        //Verificamos si el usuario no está registrado aún
        if (usuarioRegistrado == null) {
            _uiState.value = state.copy(error = "No hay ninguna cuenta registrada. Por favor, registrate primero.")
            return
        }

        //Verificamos si los datos ingresados NO coinciden con los guardados
        if (state.email != usuarioRegistrado || state.password != passwordRegistrada) {
            _uiState.value = state.copy(error = "Usuario o contraseña incorrectos")
            return
        }

        //Login exitoso
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            // Guardamos el estado exitoso en SharedPreferences
            sessionManager.guardarLoginState(true)
            _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
        }
    }
}
