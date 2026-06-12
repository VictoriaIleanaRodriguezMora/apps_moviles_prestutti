package com.prestutti.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.prestutti.presentation.add_loan.AddLoanScreen
import com.prestutti.presentation.home.HomeScreen
import com.prestutti.presentation.loan_detail.LoanDetailScreen
import com.prestutti.presentation.login.LoginScreen
import com.prestutti.presentation.profile.ProfileScreen
import com.prestutti.presentation.register.RegisterScreen

object Routes {
    const val LOGIN        = "login"
    const val HOME         = "home"
    const val ADD_LOAN     = "add_loan/{isLent}"
    const val LOAN_DETAIL  = "loan_detail/{loanId}"
    const val PROFILE      = "profile"

    const val REGISTER = "register"

    fun addLoan(isLent: Boolean) = "add_loan/$isLent"
    fun loanDetail(id: Long)     = "loan_detail/$id"
}

@Composable
fun PrestuttiNavGraph(startDestination: String = Routes.LOGIN) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToHome     = { navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToForgotPassword = { /* TODO: ForgotPasswordScreen */ }
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
                onAccountDeleted  = { navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } } }
            )
        }
    }
}

