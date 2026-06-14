package com.prestutti.presentation.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prestutti.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewPasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NewPasswordViewModel @Inject constructor(
    private val sessionManager: SessionManager) : ViewModel() {

    private val _uiState = MutableStateFlow(NewPasswordUiState())
    val uiState: StateFlow<NewPasswordUiState> = _uiState.asStateFlow()

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, error = null)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible)
    }

    fun onSubmitNewPassword() {
        val state = _uiState.value

        // 1. Validar que no estén vacíos
        if (state.password.isBlank() || state.confirmPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Completa ambos campos")
            return
        }

        // 2. Validar longitud mínima
        if (state.password.length < 6) {
            _uiState.value = _uiState.value.copy(error = "La contraseña debe tener al menos 6 caracteres")
            return
        }

        // 3. Validar que coincidan
        if (state.password != state.confirmPassword) {
            _uiState.value = _uiState.value.copy(error = "Las contraseñas no coinciden")
            return
        }

        // Si todo está bien, guardamos la nueva contraseña
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)

            sessionManager.actualizarPassword(state.password)

            _uiState.value = _uiState.value.copy(isSaving = false, isSuccess = true)
        }
    }
}