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
    val email: String = "",
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

    fun onNameChange(value: String)   { _uiState.value = _uiState.value.copy(displayName = value) }
    fun onPhotoSelected(uri: String)  { _uiState.value = _uiState.value.copy(photoUri = uri) }

    fun onSaveProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            // TODO: persistir en DataStore / Room / Firebase
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
        }
    }

    fun onDeleteAccountRequest()  { _uiState.value = _uiState.value.copy(showDeleteDialog = true) }
    fun onDeleteAccountCancel()   { _uiState.value = _uiState.value.copy(showDeleteDialog = false) }

    fun onDeleteAccountConfirm() {
        viewModelScope.launch {
            // TODO: eliminar cuenta en Firebase Auth / API
            _uiState.value = _uiState.value.copy(showDeleteDialog = false, isDeleted = true)
        }
    }

    fun cerrarSesion() {
        sessionManager.guardarLoginState(false)
    }
}
