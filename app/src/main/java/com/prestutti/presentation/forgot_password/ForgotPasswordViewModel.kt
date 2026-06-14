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

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isEmailSent: Boolean = false,
    val emailError: String? = null,
    val error: String? = null
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val sessionManager: SessionManager) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, emailError = null, error = null)
    }

    fun onSendResetLinkClick() {
        val state = _uiState.value

        if (state.email.isBlank()) {
            _uiState.value = state.copy(emailError = "Ingresá tu email")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.value = state.copy(emailError = "Email inválido")
            return
        }

        val usuarioRegistrado = sessionManager.obtenerUsuarioRegistrado()

        if (usuarioRegistrado == null || state.email != usuarioRegistrado) {
            _uiState.value = state.copy(error = "No hay ninguna cuenta registrada con ese email")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            kotlinx.coroutines.delay(1000)  //Simulamos una demora de red
            _uiState.value = _uiState.value.copy(isLoading = false, isEmailSent = true)
        }

    }

}