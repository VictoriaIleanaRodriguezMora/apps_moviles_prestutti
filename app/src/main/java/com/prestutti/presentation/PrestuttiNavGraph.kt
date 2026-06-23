package com.prestutti.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.prestutti.data.local.SessionManager
import com.prestutti.presentation.add_loan.AddLoanScreen
import com.prestutti.presentation.forgot_password.ForgotPasswordScreen
import com.prestutti.presentation.forgot_password.NewPasswordScreen
import com.prestutti.presentation.home.HomeScreen
import com.prestutti.presentation.loan_detail.LoanDetailScreen
import com.prestutti.presentation.login.LoginScreen
import com.prestutti.presentation.onboarding.OnboardingScreen
import com.prestutti.presentation.onboarding.OnboardingViewModel
import com.prestutti.presentation.profile.ProfileScreen
import com.prestutti.presentation.register.RegisterScreen
import com.prestutti.ui.theme.PrestuttiPurple

object Routes {
    const val ONBOARDING      = "onboarding"
    const val LOGIN           = "login"
    const val REGISTER        = "register"
    const val HOME            = "home"
    const val ADD_LOAN        = "add_loan/{isLent}"
    const val LOAN_DETAIL     = "loan_detail/{loanId}"
    const val PROFILE         = "profile"
    const val FORGOT_PASSWORD = "forgot_password"
    const val NEW_PASSWORD    = "new_password"

    fun addLoan(isLent: Boolean) = "add_loan/$isLent"
    fun loanDetail(id: Long)     = "loan_detail/$id"
}

@Composable
fun PrestuttiNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val onboardingState by onboardingViewModel.uiState.collectAsStateWithLifecycle()

    // Mientras DataStore carga mostramos un spinner en lugar de navegar
    if (onboardingState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrestuttiPurple)
        }
        return
    }

    val startDestination = when {
        !onboardingState.onboardingCompleted -> Routes.ONBOARDING
        sessionManager.estaLogueado()        -> Routes.HOME
        else                                 -> Routes.LOGIN
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToHome           = { navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onNavigateToRegister       = { navController.navigate(Routes.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToHome  = { navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToAddLoan    = { isLent -> navController.navigate(Routes.addLoan(isLent)) },
                onNavigateToProfile    = { navController.navigate(Routes.PROFILE) },
                onNavigateToLoanDetail = { id -> navController.navigate(Routes.loanDetail(id)) }
            )
        }

        composable(
            route = Routes.ADD_LOAN,
            arguments = listOf(navArgument("isLent") { type = NavType.BoolType })
        ) {
            AddLoanScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.LOAN_DETAIL,
            arguments = listOf(navArgument("loanId") { type = NavType.LongType })
        ) {
            LoanDetailScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack   = { navController.popBackStack() },
                onAccountDeleted = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateBack     = { navController.popBackStack() },
                onInstructionsSent = { navController.navigate(Routes.NEW_PASSWORD) }
            )
        }

        composable(Routes.NEW_PASSWORD) {
            NewPasswordScreen(
                onNavigateBack         = { navController.popBackStack() },
                onPasswordResetSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
    }
}
