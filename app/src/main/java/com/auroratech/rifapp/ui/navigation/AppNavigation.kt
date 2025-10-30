package com.auroratech.rifapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.auroratech.rifapp.ui.auth.*
import com.auroratech.rifapp.ui.screens.* // 👈 Aquí se importan las pantallas de rifas, home, etc.
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    val user = FirebaseAuth.getInstance().currentUser

    // 🔹 Si hay usuario autenticado, lo llevamos directo a Home
    val startDestination = if (user != null) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 🔹 LOGIN
        composable(route = "login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onPhoneLogin = {
                    navController.navigate("phone_login")
                }
            )
        }

        // 🔹 REGISTRO
        composable(route = "register") {
            RegisterScreen(navController = navController)
        }

        // 🔹 LOGIN POR TELÉFONO
        composable(route = "phone_login") {
            PhoneLoginScreen(navController = navController)
        }

        // 🔹 VERIFICAR CÓDIGO
        composable(
            route = "verify_code/{verificationId}",
            arguments = listOf(navArgument("verificationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
            VerifyCodeScreen(navController = navController, verificationId = verificationId)
        }

        // 🔹 HOME
        composable(route = "home") {
            HomeScreen(navController = navController)
        }

        // 🔹 PERFIL
        composable(route = "profile") {
            UserProfileScreen(navController = navController)
        }

        // 🔹 CONFIGURACIÓN
        composable(route = "settings") {
            SettingsScreen(navController = navController)
        }

        // 🔹 MIS RIFAS
        composable(route = "my_raffles") {
            MisRifasScreen(navController = navController)
        }

        // 🔹 CREAR NUEVA RIFA
        composable(route = "add_raffle") {
            AddRifaScreen(navController = navController)
        }
        // 🔹 Participantes de una rifa
        composable(
            route = "raffle_participants/{raffleId}/{raffleTitle}",
            arguments = listOf(
                navArgument("raffleId") { type = NavType.StringType },
                navArgument("raffleTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val raffleId = backStackEntry.arguments?.getString("raffleId") ?: ""
            val raffleTitle = backStackEntry.arguments?.getString("raffleTitle") ?: ""
            RaffleParticipantsScreen(navController, raffleId, raffleTitle)
        }
    }
}
