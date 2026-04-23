package com.taller.tiorico.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.taller.tiorico.ui.screens.*
import com.taller.tiorico.ui.viewmodel.GameViewModel
import com.taller.tiorico.ui.viewmodel.LobbyViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Lobby : Screen("lobby")
    object Dashboard : Screen("dashboard")
    object Results : Screen("results")
    object History : Screen("history")
    object Stats : Screen("stats")
    object Profile : Screen("profile")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    gameViewModel: GameViewModel,
    lobbyViewModel: LobbyViewModel,
    modifier: Modifier = Modifier
) {
    val currentRoom by lobbyViewModel.currentRoom.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.Lobby.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Lobby.route) {
            LaunchedEffect(currentRoom?.status) {
                if (currentRoom?.status == "STARTED") {
                    // REINICIO CRÍTICO: Asegura que todos empiecen de cero
                    gameViewModel.resetGame()
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Lobby.route) { saveState = true }
                    }
                }
            }

            LobbyScreen(
                onRoomJoined = { /* Manejado por status observer */ },
                viewModel = lobbyViewModel
            )
        }

        composable(Screen.Dashboard.route) {
            LaunchedEffect(currentRoom?.status) {
                if (currentRoom?.status == "FINISHED") {
                    navController.navigate(Screen.Results.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            }

            DashboardScreen(gameViewModel, lobbyViewModel)
        }

        composable(Screen.Results.route) {
            ResultsScreen(
                room = currentRoom,
                onBackToLobby = {
                    lobbyViewModel.leaveRoom()
                    navController.navigate(Screen.Lobby.route) {
                        popUpTo(Screen.Results.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.History.route) { HistoryScreen(gameViewModel) }
        composable(Screen.Stats.route) { StatsScreen(gameViewModel) }
        composable(Screen.Profile.route) { ProfileScreen(gameViewModel) }
    }
}
