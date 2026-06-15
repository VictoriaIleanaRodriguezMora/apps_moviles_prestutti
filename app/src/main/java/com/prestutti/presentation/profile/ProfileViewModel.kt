package com.prestutti.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prestutti.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val displayName: String = "",
    val lastName: String = "",
    val email: String = "",
    val nickname: String = "",
    val photoUri: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(private val sessionManager: SessionManager) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        val perfilGuardado = sessionManager.obtenerPerfil()
        val emailRegistrado = sessionManager.obtenerUsuarioRegistrado() ?: ""

        _uiState.value = _uiState.value.copy(
            displayName = perfilGuardado["nombre"] ?: "",
            lastName = perfilGuardado["apellido"] ?: "",
            nickname = perfilGuardado["nickname"] ?: "",
            photoUri = perfilGuardado["photo"],
            email = emailRegistrado
        )
    }

    fun onNameChange(value: String)   { _uiState.value = _uiState.value.copy(displayName = value, isSaved = false) }
    fun onLastNameChange(value: String) { _uiState.value = _uiState.value.copy(lastName = value, isSaved = false) }
    fun onNicknameChange(value: String) { _uiState.value = _uiState.value.copy(nickname = value, isSaved = false) }
    fun onPhotoSelected(uri: String)  { _uiState.value = _uiState.value.copy(photoUri = uri, isSaved = false) }

    fun onSaveProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val state = _uiState.value

            sessionManager.guardarPerfil(
                nombre = state.displayName,
                apellido = state.lastName,
                nickname = state.nickname,
                photoUri = state.photoUri
            )

            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
        }
    }

    fun onDeleteAccountRequest()  { _uiState.value = _uiState.value.copy(showDeleteDialog = true) }
    fun onDeleteAccountCancel()   { _uiState.value = _uiState.value.copy(showDeleteDialog = false) }

    fun onDeleteAccountConfirm() {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(showDeleteDialog = false, isDeleted = true)
        }
    }

    fun cerrarSesion() {
        sessionManager.guardarLoginState(false)
    }
}
