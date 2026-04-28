package com.example.chatbot.core.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.chatbot.presentation.chat.ChatScreen
import com.example.chatbot.presentation.menu.MenuScreen
import com.example.chatbot.presentation.onboarding.SplashScreen

/**
 * NavHost containing Splash, Menu and Chat screens.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.Splash
    ) {
        composable<Route.Splash>(
            exitTransition = { fadeOut() + scaleOut(targetScale = 0.9f) }
        ) {
            SplashScreen(
                onNavigateToMenu = {
                    navController.navigate(Route.Menu) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Menu>(
            enterTransition = { fadeIn() + scaleIn(initialScale = 1.1f) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() }
        ) {
            MenuScreen(
                onServiceClick = { service ->
                    navController.navigate(Route.Chat(serviceTitle = service.title))
                }
            )
        }

        composable<Route.Chat>(
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
        ) { backStackEntry ->
            val chatRoute: Route.Chat = backStackEntry.toRoute()
            ChatScreen(
                serviceTitle = chatRoute.serviceTitle,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
