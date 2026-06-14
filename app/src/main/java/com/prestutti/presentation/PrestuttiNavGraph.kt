package com.prestutti.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.prestutti.data.local.SessionManager
import com.prestutti.presentation.add_loan.AddLoanScreen
import com.prestutti.presentation.forgot_password.ForgotPasswordScreen
import com.prestutti.presentation.home.HomeScreen
import com.prestutti.presentation.loan_detail.LoanDetailScreen
import com.prestutti.presentation.login.LoginScreen
import com.prestutti.presentation.profile.ProfileScreen
import com.prestutti.presentation.register.RegisterScreen
import com.prestutti.presentation.forgot_password.NewPasswordScreen

object Routes {
    const val LOGIN        = "login"
    const val HOME         = "home"
    const val ADD_LOAN     = "add_loan/{isLent}"
    const val LOAN_DETAIL  = "loan_detail/{loanId}"
    const val PROFILE      = "profile"

    const val REGISTER = "register"

    const val FORGOT_PASSWORD = "forgot_password"

    const val NEW_PASSWORD = "new_password"

    fun addLoan(isLent: Boolean) = "add_loan/$isLent"
    fun loanDetail(id: Long)     = "loan_detail/$id"
}

@Composable
fun PrestuttiNavGraph() {
    //Obtenemos el contexto de Android para abri SharedPreferences
    val context = LocalContext.current
    //Iniciamos el SessionManager
    val sessionManager = remember{ SessionManager(context) }
    //La lógica del portero, elegimos dinámicamente dónde arrancar
    val startDestination = if (sessionManager.estaLogueado()) {
        Routes.HOME
    } else {
        Routes.LOGIN
    }

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToHome     = { navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
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
                onNavigateToAddLoan   = { isLent -> navController.navigate(Routes.addLoan(isLent)) },
                onNavigateToProfile   = { navController.navigate(Routes.PROFILE) },
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
                onNavigateBack    = { navController.popBackStack() },
                onAccountDeleted  = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                onLogout          = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onInstructionsSent = { navController.navigate(Routes.NEW_PASSWORD) }
            )
        }

        composable(Routes.NEW_PASSWORD) {
            NewPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onPasswordResetSuccess = {
                    // Si cambia la contraseña, lo mandamos al Login y borramos el historial
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
    }
}

