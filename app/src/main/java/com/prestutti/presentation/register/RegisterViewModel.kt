package com.prestutti.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prestutti.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(private val sessionManager: SessionManager) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, emailError = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, passwordError = null, confirmError = null)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, confirmError = null)
    }

    fun onRegisterClick() {
        val state = _uiState.value
        var emailError: String? = null
        var passwordError: String? = null
        var confirmError: String? = null

        if (state.email.isBlank()) {
            emailError = "Ingresá tu email"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            emailError = "Email inválido"
        }

        if (state.password.isBlank()) {
            passwordError = "Ingresá una contraseña"
        } else if (state.password.length < 6) {
            passwordError = "Mínimo 6 caracteres"
        }

        if (state.confirmPassword.isBlank()) {
            confirmError = "Repetí tu contraseña"
        } else if (state.confirmPassword != state.password) {
            confirmError = "Las contraseñas no coinciden"
        }

        if (emailError != null || passwordError != null || confirmError != null) {
            _uiState.value = state.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmError = confirmError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)
            sessionManager.registrarUsuario(
                usuario = state.email,
                password = state.password
            )
            sessionManager.guardarLoginState(true)
            // TODO: integrar registro real (Firebase Auth / API)
            _uiState.value = _uiState.value.copy(isLoading = false, isRegistered = true)
        }
    }
}