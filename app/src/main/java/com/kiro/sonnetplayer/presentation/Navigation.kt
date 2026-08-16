package com.kiro.sonnetplayer.presentation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kiro.sonnetplayer.presentation.player.PlayerScreen
import com.kiro.sonnetplayer.presentation.settings.SettingsScreen

sealed class Screen(val route: String) {
    data object Player : Screen("player")
    data object Settings : Screen("settings")
}

@Composable
fun SonnetPlayerNavigation(
    navController: NavHostController = rememberNavController()
) {
    var showBookmarks by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = Screen.Player.route
    ) {
        composable(Screen.Player.route) {
            PlayerScreen(
                videoUri = null, // TODO: Pass actual video URI
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onShowBookmarks = {
                    showBookmarks = true
                }
            )

            // Bookmarks sheet overlay
            if (showBookmarks) {
                com.kiro.sonnetplayer.presentation.bookmarks.BookmarksSheet(
                    currentPosition = 0L, // TODO: Get from PlayerViewModel
                    onDismiss = { showBookmarks = false },
                    onJumpToTimestamp = { timestamp ->
                        // TODO: Communicate with PlayerViewModel
                    }
                )
            }
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
