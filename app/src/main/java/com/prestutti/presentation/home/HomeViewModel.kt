package com.prestutti.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
//  HomeViewModel solo maneja qué tab está activa. Según la tab activa, muestra una pantalla diferente:
enum class HomeTab { LENT, BORROWED }

data class HomeUiState(
    val activeTab: HomeTab = HomeTab.LENT // por defecto
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onTabSelected(tab: HomeTab) { // acá lee que tab se está usando
        _uiState.value = _uiState.value.copy(activeTab = tab)
    }
}
