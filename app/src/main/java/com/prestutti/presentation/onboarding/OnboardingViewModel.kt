package com.prestutti.presentation.onboarding

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prestutti_onboarding")

private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

data class OnboardingUiState(
    val isLoading: Boolean = true,
    val onboardingCompleted: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            context.dataStore.data
                .map { prefs -> prefs[ONBOARDING_COMPLETED] ?: false }
                .collect { completed ->
                    _uiState.value = OnboardingUiState(
                        isLoading = false,
                        onboardingCompleted = completed
                    )
                }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[ONBOARDING_COMPLETED] = true
            }
        }
    }
}
