package com.prestutti.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prestutti.presentation.home.borrowed_list.BorrowedListScreen
import com.prestutti.presentation.home.lent_list.LentListScreen
import com.prestutti.ui.theme.PrestuttiPink
import com.prestutti.ui.theme.PrestuttiPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddLoan: (isLent: Boolean) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToLoanDetail: (loanId: Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Prestutti",
                        fontWeight = FontWeight.Bold,
                        color = PrestuttiPurple
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil", tint = PrestuttiPurple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddLoan(uiState.activeTab == HomeTab.LENT) },
                containerColor = if (uiState.activeTab == HomeTab.LENT) PrestuttiPurple else PrestuttiPink,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar préstamo", tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // ── Tabs PRESTÉ / ME PRESTARON ───────────────────────────────────
            TabRow(
                selectedTabIndex = if (uiState.activeTab == HomeTab.LENT) 0 else 1,
                containerColor = Color.White,
                contentColor = PrestuttiPurple
            ) {
                Tab(
                    selected = uiState.activeTab == HomeTab.LENT,
                    onClick = { viewModel.onTabSelected(HomeTab.LENT) },
                    text = {
                        Text(
                            "PRESTÉ",
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.activeTab == HomeTab.LENT) PrestuttiPurple else Color.Gray
                        )
                    }
                )
                Tab(
                    selected = uiState.activeTab == HomeTab.BORROWED,
                    onClick = { viewModel.onTabSelected(HomeTab.BORROWED) },
                    text = {
                        Text(
                            "ME PRESTARON",
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.activeTab == HomeTab.BORROWED) PrestuttiPink else Color.Gray
                        )
                    }
                )
            }

            // ── Contenido de la tab activa ────────────────────────────────────
            when (uiState.activeTab) {
                HomeTab.LENT -> LentListScreen(
                    onLoanClick = onNavigateToLoanDetail,
                    onAddLoan = { onNavigateToAddLoan(true) }
                )
                HomeTab.BORROWED -> BorrowedListScreen(
                    onLoanClick = onNavigateToLoanDetail,
                    onAddLoan = { onNavigateToAddLoan(false) }
                )
            }
        }
    }
}
